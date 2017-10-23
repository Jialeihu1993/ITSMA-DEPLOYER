package com.hpe.itsma.itsmaInstaller.service;

import com.hpe.itsma.itsmaInstaller.bean.*;
import com.hpe.itsma.itsmaInstaller.constants.ItsmaInstallerConstants;
import com.hpe.itsma.itsmaInstaller.exception.InvalidStateException;
import com.hpe.itsma.itsmaInstaller.util.ItsmaUtil;
import com.hpe.itsma.itsmaInstaller.util.VelocityWrapper;
import io.fabric8.kubernetes.api.model.ConfigMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by tianlib on 8/29/2017.
 */
@Component
public class UpdateService {
  private Log logger = LogFactory.getLog(UpdateService.class);

  @Autowired
  DeployerContext deployerContext;
  @Autowired
  RuntimeProfile runtimeProfile;
  @Autowired
  ItsmaSuite itsmaSuite;
  @Autowired
  ProductsService productsService;
  @Autowired
  StorageService storageService;
  @Autowired
  SSRestClient ssRestClient;
  @Autowired
  K8sRestClient k8sRestClient;
  @Autowired
  VaultService vaultService;
  @Autowired
  SuiteVersionService suiteVersionService;
  @Autowired
  UpdateProcessStatus updateProcessStatus;

  private String currentSuiteVersion;
  private List<ConfigMap> oldConfigMaps;

  public void update() throws Exception {
    if (deployerContext.getDeployerStatus().getPhase().getName() == DeployerStatus.PhaseType.IDLE) {
      createUpdateThread();
      createStatusRefreshThread();
    } else {
      throw new InvalidStateException("The service is too busy to do update.");
    }
    deployerContext.updateDeployerStatus(DeployerStatus.PhaseType.UPDATING, "");
  }

  /**
   * newSecrete return all secrets of all services of new suite compared to the current suite
   * <p>
   * The delta secrets can be computed by comparing two files:
   * 1. ../config/{currentSuiteVersion}/itsmaProducts.json  e.g ../config/2017.07/itsmaProducts.json
   * 2. ../config/itsmaProducts.json  the new one
   *
   * @return
   * @throws Exception
   */
  public Map<String, List<ItsmaProduct.Vault.Secret>> newSecrets() throws Exception {
    ProductsService oldProductsService = new ProductsService();
    InputStream is = new FileSystemResource(runtimeProfile.getBuiltin_config_path() + "/" + currentSuiteVersion + "/" + ItsmaInstallerConstants.ITSMA_PRODUCTS).getInputStream();
    oldProductsService.loadProducts(is);
    is = new FileSystemResource(runtimeProfile.getBuiltin_config_path() + "/" + ItsmaInstallerConstants.ITSMA_PRODUCTS).getInputStream();
    ProductsService newProductsService = new ProductsService();
    newProductsService.loadProducts(is);

    Map<String, List<ItsmaProduct.Vault.Secret>> secrets = new HashMap<>();
    for (ItsmaProduct itsmaProduct : newProductsService.getItsmaProducts()) {
      ItsmaProduct product = productsService.getItsmaProduct(itsmaProduct.getName());
      if (product != null && product.isActive()) {
        List<ItsmaProduct.Vault.Secret> newProductSecrets = new ArrayList<>();
        ItsmaProduct oldProduct = oldProductsService.getItsmaProduct(itsmaProduct.getName());
        if (oldProduct == null) {
          continue;
        }
        if (itsmaProduct.getVault() != null) {
          if (oldProduct.getVault() == null) {
            secrets.put(itsmaProduct.getName(), itsmaProduct.getVault().getSecrets());
          } else {
            itsmaProduct.getVault().getSecrets().stream().filter(
              secret -> !oldProduct.getVault().getSecrets().stream().filter(
                secret2 -> secret.getSecretKey().equals(secret2.getSecretKey())
              ).findAny().isPresent()
            ).forEach(
              secret1 -> newProductSecrets.add(secret1)
            );
          }
        }
        if (!newProductSecrets.isEmpty()) {
          secrets.put(itsmaProduct.getName(), newProductSecrets);
        }
      }
    }
    return secrets;
  }

  private void createUpdateThread() {
    logger.info("create the thread for suite update...");
    new Thread() {
      @Override
      public void run() {
        try {
          deployerContext.getSuiteProperties().put("itom_suite_install_type", "update");
          deployerContext.updateDeployerStatus(DeployerStatus.PhaseType.UPDATING, "statistic");
          oldConfigMaps = k8sRestClient.listConfigMapByNamespace(runtimeProfile.getNamespace());

          String deploymentuuid = getFromConfigMapCache(oldConfigMaps, "itsma-common-configmap", ItsmaInstallerConstants.DEPLOYMENTUUID);
          if (deploymentuuid == null || deploymentuuid.isEmpty()) {
            logger.error("===UpdateThread exited! Cannot get deploymentuuid from configmap");
            return;
          }
          runtimeProfile.setDeploymentUuid(deploymentuuid);

          ssRestClient.updateDeploymentStatus(runtimeProfile.getDeploymentUuid(), "INSTALL_FINISHED");

          currentSuiteVersion = suiteVersionService.currentSimpleVersion();

          prepareProperties();

          String itsmaSuiteSize = getFromConfigMapCache(oldConfigMaps, "itsma-common-configmap", "itom_suite_size");
          if (itsmaSuiteSize == null || itsmaSuiteSize.isEmpty()) {
            logger.error("===UpdateThread exited! Cannot get itom_suite_size from configmap");
            return;
          }
          deployerContext.getSuiteProperties().put("itom_suite_size", itsmaSuiteSize);

          deployerContext.updateDeployerStatus(DeployerStatus.PhaseType.UPDATING, "vault");
          //Create new secrets if any
          Map<String, List<ItsmaProduct.Vault.Secret>> secrets = newSecrets();
          for (String product : secrets.keySet()) {
            vaultService.saveSecret(runtimeProfile.getNamespace(),
              runtimeProfile.getDeploymentUuid(),
              (String) deployerContext.getSuiteProperties().get(product.toLowerCase() + "_vault_approle"),
              (String) deployerContext.getSuiteProperties().get(product.toLowerCase() + "_vault_approle_id"),
              secrets.get(product));
          }

          itsmaSuite.stopService("deployer");

          deployerContext.updateDeployerStatus(DeployerStatus.PhaseType.UPDATING, "install");
          installBuiltinKubeResources();

          //Merge some key/value of old configmap in new configmap
          applyRetainedConfigMap(oldConfigMaps);

          itsmaSuite.createAllServiceDeployController("update");
        } catch (Exception e) {
          logger.error("===UpdateThread exited unexpectedly!!! exception: " + e.getMessage());
          e.printStackTrace();
        }
      }
    }.start();
  }

  private void createStatusRefreshThread() {
    logger.info("create a thread for status refreshing...");
    new Thread() {
      @Override
      public void run() {
        while (true) {
          // Installation complete if all itsma services are ready
          if (!deployerContext.getItsmaServices().isEmpty() &&
            deployerContext.getItsmaServices().stream().allMatch(itsmaService -> itsmaService.getStatus().compareTo(ItsmaService.STATUS.SUCCESS) == 0)) {
            ssRestClient.updateDeploymentStatus(runtimeProfile.getDeploymentUuid(), "INSTALL_FINISHED");
            deployerContext.updateDeployerStatus(DeployerStatus.PhaseType.IDLE, "");
            System.exit(0);
            break;
          }

          deployerContext.getItsmaServices().stream().filter(
            itsmaService -> itsmaService.getStatus() == ItsmaService.STATUS.WAIT && itsmaService.getDeployDeps() != null
          ).forEach(
            itsmaService1 -> {
              if (itsmaService1.getDeployDeps().stream().allMatch(
                itsmaServiceDep -> {
                  try {
                    return itsmaSuite.findItsmaServiceByName(itsmaServiceDep.getName()).getStatus() == ItsmaService.STATUS.SUCCESS;
                  } catch (Exception e) {
                    return false;
                  }
                }
              )) {
                synchronized (deployerContext.getItsmaServiceStatusLock()) {
                  itsmaService1.setStatus(ItsmaService.STATUS.READY);
                }
              }
            }
          );

          try {
            TimeUnit.MILLISECONDS.sleep(ItsmaInstallerConstants.STATUS_CHECK_INTERVAL);
          } catch (InterruptedException e) {
            logger.warn("Failed to sleep... " + e.getMessage());
          }
        }
      }
    }.start();
  }

  /**
   * applyRetainedConfigMap update the current ConfigMap with the retained one
   * <p>
   * when updating Itsma suite, some configurations in ConfigMap need be retained, which is listed in
   * file retainedSuiteConfigMap.json, this method get all the old values of retained ConfigMap, and
   * apply them to the current ConfigMap
   *
   * @param configMaps the list of old ConfigMap
   * @throws Exception
   */
  public void applyRetainedConfigMap(List<ConfigMap> configMaps) throws Exception {
    InputStream is = new FileSystemResource(runtimeProfile.getBuiltin_config_path() + "/" + currentSuiteVersion + "/" + ItsmaInstallerConstants.RETAINEDCONFIGMAP).getInputStream();
    JSONObject jsonObject = new JSONObject(IOUtils.toString(is, "UTF-8"));
    Map<String, Object> retainedConfigMapKeys = jsonObject.toMap();
    for (String configmap : retainedConfigMapKeys.keySet()) {
      Map<String, String> retainedConfigMap = new HashMap<>();

      jsonObject.getJSONArray(configmap).toList().stream().forEach(
        cmKey -> {
          String cmValue = configMaps.stream().filter(
            cm -> cm.getMetadata().getName().equalsIgnoreCase(configmap)
          ).findFirst().get().getData().get(cmKey);
          retainedConfigMap.put((String) cmKey, cmValue);
        }
      );
      k8sRestClient.updateOrNewDataOfConfigMap(runtimeProfile.getNamespace(), configmap, retainedConfigMap);
    }
  }

  public void prepareProperties() throws Exception {
    deployerContext.getSuiteProperties().clear();
    deployerContext.getSuiteProperties().putAll(runtimeProfile.toMap());
    List<String> deactivatedPP = productsService.getDeactivatedProducts();
    if (!ItsmaUtil.isNullOrEmpty(deactivatedPP)) {
      deployerContext.getSuiteProperties().put("deactivated_pp", deactivatedPP.toArray(new String[deactivatedPP.size()]));
    } else {
      deployerContext.getSuiteProperties().put("deactivated_pp", new String[0]);
    }

        /* get configuration in etcd. which looks like:
            {
                "configuration": [
                    {
                        "key": "/suite-installer/v1.1/suite-storage/bdcf6ddb-a890-4d9a-b30c-dc5da079e243/ATUOPASSURL",
                        "value": {
                            "protocol": "https",
                            "ip": "shc-itsma-suite-cd-81.hpeswlab.net",
                            "port": "443",
                            "path": "autopass"
                        }
                    },
                    {
                        "key": "/suite-installer/v1.1/suite-storage/bdcf6ddb-a890-4d9a-b30c-dc5da079e243/itsma8",
                        "value": {
                            "applied": {
                                "itom-idm": true,
                                "itom-itsma-config_vault_approle": "itom-itsma-config",
                                "cmdb_browser_loglevel_log4j_statistics": "INFO",
                                "sm_smarta_enabled": "4",
                                "sm_webtier_loglevel_query_security": "true",
                                "sm_integration_username": "intgAdmin",
                                "sm_database_ip": "sm-postgres-svc",
                                "saml2_enable": "FALSE",
                                "idm_tenant": "ITSMA"
                            }
                        }
                    }
                 ]
            }
         */
    boolean failedToRetrieveConfiguration = true;
    String appliedConfiguration = storageService.getConfiguration(null);
    JSONObject jsonObject = new JSONObject(appliedConfiguration);
    org.json.JSONArray jsonArray = jsonObject.getJSONArray("configuration");
    for (int i = 0; i < jsonArray.length(); i++) {
      try {
        Map<String, Object> appliedProperties = jsonArray.getJSONObject(i).getJSONObject("value").getJSONObject("applied").toMap();
        if (!appliedProperties.isEmpty()) {
          failedToRetrieveConfiguration = false;
          deployerContext.getSuiteProperties().putAll(appliedProperties);
        }
      } catch (Exception e) {
        continue;
      }
    }

    if (failedToRetrieveConfiguration) {
      throw new Exception("Failed to get configuration from etcd.");
    }
  }

  public void installBuiltinKubeResources() throws Exception {
    String builtinConfigPath = runtimeProfile.getBuiltin_config_path();
    VelocityWrapper vw = new VelocityWrapper(builtinConfigPath + "/yaml_templates",
      builtinConfigPath + "/yamls", deployerContext.getSuiteProperties());
    vw.init();
    vw.transformFiles();

    // create configmap
    ssRestClient.createKubeResource(runtimeProfile.getDeploymentUuid(), builtinConfigPath + "/yamls/configmap.yaml");
  }

  /**
   * getFromConfigMapCache get the value of given key and configmap name in the cached configmap
   *
   * @param configMaps
   * @param configMap
   * @param key
   * @return value if found, or return null
   */
  public String getFromConfigMapCache(List<ConfigMap> configMaps, String configMap, String key) {
    if (configMaps != null) {
      Optional<ConfigMap> cm = configMaps.stream().filter(
        configMap1 -> configMap1.getMetadata().getName().equalsIgnoreCase(configMap)
      ).findFirst();
      if (cm.isPresent()) {
        return cm.get().getData().get(key);
      }
    }
    return null;
  }

  public void createKubeResource(String yamlFile, String namespace) {
    //try 3 times in case failure
    int maxTryTimes = 3;
    for (int tryTimes = 0; tryTimes < maxTryTimes; tryTimes++) {
      try {
        k8sRestClient.createResourceByYaml(yamlFile, namespace);
        logger.info(String.format("Create k8s resource successfully. (yaml=%s)", yamlFile));
        return;
      } catch (Exception e) {
        logger.error(String.format("Fail to create k8s resource by yaml. (yaml=%s). Found Exception: %s. Will retry %s times. This is No.%s", yamlFile, e.getMessage(), maxTryTimes, tryTimes));
      }
    }
  }
}
