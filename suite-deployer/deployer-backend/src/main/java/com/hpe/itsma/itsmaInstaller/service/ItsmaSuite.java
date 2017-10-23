package com.hpe.itsma.itsmaInstaller.service;

import com.hpe.itsma.itsmaInstaller.bean.*;
import com.hpe.itsma.itsmaInstaller.constants.ItsmaInstallerConstants;
import com.hpe.itsma.itsmaInstaller.constants.KubeResType;
import com.hpe.itsma.itsmaInstaller.exception.ItsmaServiceNotReadyToInstallException;
import com.hpe.itsma.itsmaInstaller.exception.K8sResourceFailToCreateException;
import com.hpe.itsma.itsmaInstaller.util.ItsmaUtil;
import com.hpe.itsma.itsmaInstaller.util.VelocityWrapper;
import com.hpe.itsma.itsmaInstaller.util.YamlParser;
import io.fabric8.kubernetes.client.KubernetesClientException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by tianlib on 8/28/2017.
 */
@Service
public class ItsmaSuite {
  private Log logger = LogFactory.getLog(ItsmaSuite.class);

  @Autowired
  DeployerContext deployerContext;

  @Autowired
  private RuntimeProfile runtimeProfile;

  @Autowired
  K8sRestClient k8sRestClient;

  @Autowired
  private SSRestClient ssRestClient;

  @Autowired
  private ProductsService productsService;

  @Autowired
  YamlService yamlService;

  @Autowired
  BackupInstallerService backupInstallerService;

  public DeployerStatus getDeployerStatus() {
    return deployerContext.getDeployerStatus();
  }

  /**
   * Save the manifest.yaml in dir: ${itsma_services_path}/${servicename}-${service-version}
   * itsma_service_path has been defined in application.properties
   *
   * @param itsmaServiceName
   * @param manifest
   * @throws Exception
   */
  public void saveManifest(String itsmaServiceName, String manifest) throws Exception {
    ItsmaService itsmaService = findItsmaServiceByName(itsmaServiceName);

    ItsmaService itsmaServiceReal = deserializeItsmaService(new ByteArrayInputStream(manifest.getBytes()));
    if (!itsmaServiceName.equalsIgnoreCase(itsmaService.getName())) {
      throw new IllegalArgumentException("Service name not equal to the one in manifest");
    }

    itsmaService.copy(itsmaServiceReal);

    String suiteSize = ItsmaUtil.getPropertyEx(deployerContext.getSuiteProperties(), "itom_suite_size").toString();
    Map<String, String> profileParameter = this.deserializeServiceParameters(suiteSize, new ByteArrayInputStream(manifest.getBytes()));
    if (!ItsmaUtil.isNullOrEmpty(profileParameter)) {
      deployerContext.getSuiteProperties().putAll(profileParameter);
    }

    String serviceDir = runtimeProfile.getItsma_services_path() + "/" + itsmaServiceName + "-" + itsmaService.getVersion();
    File f = new File(serviceDir);
    if (!f.exists()) {
      f.mkdir();
    }

    FileOutputStream fo = new FileOutputStream(serviceDir + "/manifest.yaml", false);
    fo.write(manifest.getBytes());
    fo.close();

    // Set the status of service to be READY to install if it depends on nothing
    // Or Set to WAIT, createStatusRefreshThread will update the status periodically
    if (itsmaService.getDeployDeps() == null) {
      synchronized (deployerContext.getItsmaServiceStatusLock()) {
        itsmaService.setStatus(ItsmaService.STATUS.READY);
      }
    } else {
      synchronized (deployerContext.getItsmaServiceStatusLock()) {
        logger.info("Itsma service [" + itsmaService.getName() + "] is waiting to deploy.");
        itsmaService.setStatus(ItsmaService.STATUS.WAIT);
      }
    }
  }

  public ItsmaServiceStatus getItsmaServiceStatus(String itsmaServiceName) throws Exception {
    ItsmaService itsmaService = findItsmaServiceByName(itsmaServiceName);
    return new ItsmaServiceStatus(itsmaServiceName, itsmaService.getStatus().toString());
  }

  public void setItsmaServiceStatus(String itsmaServiceName, ItsmaServiceStatus itsmaServiceStatus) throws Exception {
    ItsmaService itsmaService = findItsmaServiceByName(itsmaServiceName);

    String serviceStatus = itsmaServiceStatus.getStatus();
    logger.info("Update Itsma service [" + itsmaService.getName() + "] to: " + serviceStatus);
    if (serviceStatus.equalsIgnoreCase("success")) {
      synchronized (deployerContext.getItsmaServiceStatusLock()) {
        itsmaService.setStatus(ItsmaService.STATUS.SUCCESS);
      }
    } else if (serviceStatus.equalsIgnoreCase("failed")) {
      ssRestClient.updateDeploymentStatus(runtimeProfile.getDeploymentUuid(), "INSTALL_FINISHED");
      synchronized (deployerContext.getItsmaServiceStatusLock()) {
        itsmaService.setStatus(ItsmaService.STATUS.FAILED);
      }
    } else {
      String warnMsg = "Unknown status. Only accept: success/failed";
      logger.warn(warnMsg);
      throw new IllegalArgumentException(warnMsg);
    }
  }

  /**
   * Write the kube resource content to file /temp/yaml_templates/ named {itsmaServiceName}-{TimeStamp}.yaml
   * Then generate the final yaml in /temp/yamls/ with the same file name, send to SS to create at last.
   *
   * @param itsmaServiceName
   * @param kubeResource
   * @return
   * @throws Exception
   */
  public void createKubeResourceByContent(String itsmaServiceName, String resFileName, String kubeResource) throws Exception {
    ItsmaService itsmaService = findItsmaServiceByName(itsmaServiceName);

    // Only try to create k8s resource when the service status is READY or SUCCESS
    // The reason for SUCCESS is some itsma services need to do scale out after installation. e.g smarta
    synchronized (deployerContext.getItsmaServiceStatusLock()) {
      if (itsmaService.getStatus() != ItsmaService.STATUS.READY && itsmaService.getStatus() != ItsmaService.STATUS.SUCCESS) {
        throw new ItsmaServiceNotReadyToInstallException(itsmaServiceName + " is not ready to deploy, try again later.");
      }
    }

    String serviceDir = runtimeProfile.getItsma_services_path() + "/" + itsmaServiceName + "-" + itsmaService.getVersion();
    String tempYamlTemplatesDir = serviceDir + "/yaml_templates";
    String tempYamls = serviceDir + "/yamls";
    File f = new File(tempYamlTemplatesDir);
    if (!f.exists()) {
      f.mkdir();
    }

    Map<String, Object> serviceProperties = new HashMap<>();
    serviceProperties.putAll(deployerContext.getSuiteProperties());
    serviceProperties.put("docker_image_registry", itsmaService.getRegistryUrl());
    FileOutputStream fo = new FileOutputStream(tempYamlTemplatesDir + "/" + resFileName, false);
    fo.write(kubeResource.getBytes());
    fo.close();
    VelocityWrapper vw = new VelocityWrapper(tempYamlTemplatesDir, tempYamls, serviceProperties);
    vw.init();
    vw.transformFiles();

    //try 3 times in case failure
    for (int tryTimes = 0; tryTimes < 3; tryTimes++) {
      try {
        String responseBody = ssRestClient.createKubeResource(runtimeProfile.getDeploymentUuid(), tempYamls + "/" + resFileName);
        SSRestResponseBody ssRestResponseBody = ItsmaUtil.unmarshallJson(responseBody, SSRestResponseBody.class);
        BatchInfo batchInfo = ssRestResponseBody.getBatchInfo();
        if (!batchInfo.getKubeInstanceInfoList().isEmpty() && !batchInfo.isFoundInvalidate()) {
          if (ItsmaUtil.getProperty(deployerContext.getSuiteProperties(), "itom_suite_install_type").toString().equalsIgnoreCase("install_from_backup")) {
            Yaml yaml = new Yaml();
            List<String> yamlList = YamlParser.split(new ByteArrayInputStream(kubeResource.getBytes()));
            for (String yamlStr : yamlList) {
              Map<String, Object> yamlMap = yaml.loadAs(yamlStr, Map.class);
              String kind = yamlMap.get("kind").toString();
              Map<String, Object> metadata = (Map<String, Object>) yamlMap.get("metadata");
              String configmapName = metadata.get("name").toString().trim();
              if (kind.equalsIgnoreCase("ConfigMap")) {
                //get configmap name & configmap key from manifest.yaml
                String manifestYamlFile = serviceDir + "/manifest.yaml";
                Map<String, Object> configmapRetained = this.deserializeConfigmapRetained(new FileInputStream(new File(manifestYamlFile)));
                logger.debug(String.format("itsmaServiceName = [%s], configmapRetained = [%s]", itsmaServiceName, configmapRetained));
                backupInstallerService.restoreConfigMapByName(configmapName, configmapRetained);
              }
            }
          }
          return;
        }

        logger.error(String.format("===CreateK8sResource with(service=%s, resourceName=%s, resourceContent=%s): Failed. " +
          "SS return 200 but nothing has been created. Will retry...", itsmaServiceName, tempYamls + "/" + resFileName, kubeResource));
      } catch (Exception e) {
        logger.error(String.format("===CreateK8sResource with(service=%s, resourceName=%s, resourceContent=%s): Failed. Exception: %s. Will retry...",
          itsmaServiceName, tempYamls + "/" + resFileName, kubeResource, e.getMessage()));
      }
    }

    String errMsg = String.format("Fatal Error. Failed to create %s. Set service %s status as 'Failed'.",
      tempYamls + "/" + resFileName, itsmaService);
    synchronized (deployerContext.getItsmaServiceStatusLock()) {
      logger.error(errMsg);
      itsmaService.setStatus(ItsmaService.STATUS.FAILED);
    }

    throw new K8sResourceFailToCreateException(errMsg);
  }

  /**
   * stopService stops all kubernetes resources belong to the specified Itsma service
   *
   * @param servicName name of the Itsma service
   * @throws Exception
   */
  public void stopService(String servicName) throws Exception {
    ItsmaService itsmaService = null;
    if (!servicName.equalsIgnoreCase("deployer")) {
      itsmaService = findItsmaServiceByName(servicName);
    }
    if (itsmaService != null) {
      synchronized (deployerContext.getItsmaServiceStatusLock()) {
        itsmaService.setStatus(ItsmaService.STATUS.TERMINATING);
      }
    }

    for (KubeResType type : KubeResType.values()) {
      // never delete any pv and pvc of any itsma service
      // ignore Pod, which should be deleted at last, or if it belongs to a deployment, k8s will try to
      // re-create it
      if (type == KubeResType.PV ||
        type == KubeResType.PVC ||
        type == KubeResType.POD) {
        continue;
      }

      k8sRestClient.deleteResource(runtimeProfile.getNamespace(), "itsmaService=" + servicName, type);
    }

    // delete Pod at last if any
    k8sRestClient.deleteResource(runtimeProfile.getNamespace(), "itsmaService=" + servicName, KubeResType.POD);

    if (itsmaService != null) {
      synchronized (deployerContext.getItsmaServiceStatusLock()) {
        itsmaService.setStatus(ItsmaService.STATUS.TERMINATED);
      }
    }
  }

  /**
   * stopAllServices stops all Itsma services
   * <p>
   * This might be a time consuming operation, so spawn a thread to do it.
   * The deployer status is set to 'SHUTTINGDOWN' at the begging, and will be set to 'IDLE' until all Itsam services have been terminated.
   */
  public void stopAllServices() {
    deployerContext.updateDeployerStatus(DeployerStatus.PhaseType.SHUTTINGDOWN, "");
    new Thread() {
      @Override
      public void run() {
        try {
          for (ItsmaService itsmaService : deployerContext.getItsmaServices()) {
            stopService(itsmaService.getName());
          }

          // update the status of deployer to be IDLE when all Itsma services are terminated
          while (true) {
            if (deployerContext.getDeployerStatus().getItsmaServiceStatuses().stream().allMatch(
              itsmaServiceStatus -> itsmaServiceStatus.getStatus().equalsIgnoreCase("Terminated")
            )) {
              deployerContext.updateDeployerStatus(DeployerStatus.PhaseType.IDLE, "");
              break;
            } else {
              try {
                TimeUnit.MILLISECONDS.sleep(ItsmaInstallerConstants.STATUS_CHECK_INTERVAL);
              } catch (InterruptedException e) {
                logger.warn("Failed to sleep... " + e.getMessage());
              }
            }
          }
        } catch (Exception e) {
          logger.error("===Stop Service Failed. Found exception: " + e.getMessage());
        }
      }
    }.start();
  }

  public Map<String, String> deserializeServiceParameters(String suiteSize, InputStream in) throws Exception {
    Map<String, String> profileParameter = null;
    ItsmaServiceParameters itsmaServiceParameters = yamlService.convertYamlToItsmaServiceParameters(in);
    logger.debug("itsmaServiceParameters: " + itsmaServiceParameters.toString());
    if (!ItsmaUtil.isNullOrEmpty(itsmaServiceParameters)) {
      ItsmaProfileParameters itsmaProfileParameters = itsmaServiceParameters.getProfile_parameters();
      logger.debug("itsmaProfileParameters: " + itsmaProfileParameters);
      if (!ItsmaUtil.isNullOrEmpty(itsmaProfileParameters)) {
        switch (suiteSize) {
          case "demo":
            profileParameter = itsmaProfileParameters.getDemo();
            break;
          case "xsmall":
            profileParameter = itsmaProfileParameters.getXsmall();
            if (ItsmaUtil.isNullOrEmpty(profileParameter)) {
              profileParameter = itsmaProfileParameters.getExtrasmall();
            }
            break;
          case "small":
            profileParameter = itsmaProfileParameters.getSmall();
            break;
          case "medium":
            profileParameter = itsmaProfileParameters.getMedium();
            break;
          case "large":
            profileParameter = itsmaProfileParameters.getLarge();
            break;
          default:
            profileParameter = itsmaProfileParameters.getDefaultVal();
            break;
        }
        if (ItsmaUtil.isNullOrEmpty(profileParameter)) {
          profileParameter = itsmaProfileParameters.getDefaultVal();
        }
        logger.debug("profileParameter: " + profileParameter);
      }
    }
    return profileParameter;
  }

  public Map<String, Object> deserializeConfigmapRetained(InputStream in) throws Exception {
    YamlService yamlService = new YamlService();
    Map<String, Object> manifest = yamlService.convertYamlToMap(in);
    Map<String, Object> configmapRetained = (Map<String, Object>) manifest.get("configmap_keeper");
    return configmapRetained;
  }

  public ItsmaService deserializeItsmaService(InputStream is) throws Exception {
    Constructor constructor = new Constructor(ItsmaService.class);
    TypeDescription typeDescription = new TypeDescription(ItsmaService.class);
    constructor.addTypeDescription(typeDescription);
    Representer representer = new Representer();
    representer.getPropertyUtils().setSkipMissingProperties(true);
    Yaml yaml = new Yaml(constructor, representer);

    return yaml.loadAs(IOUtils.toString(is, "UTF-8"), ItsmaService.class);
  }

  public ItsmaService findItsmaServiceByName(String serviceName) throws Exception {
    for (ItsmaService itsmaService : deployerContext.getItsmaServices()) {
      if (itsmaService.getName().equalsIgnoreCase(serviceName)) {
        return itsmaService;
      }
    }
    throw new ItsmaServiceNotReadyToInstallException("Not found itsma service by name: " + serviceName);
  }

  /**
   * createAllServiceDeployController create all deploy controllers of active services
   *
   * @param installType specify the install type: new_install, install_from_backup, update
   * @throws Exception
   */
  public void createAllServiceDeployController(String installType) throws Exception {
    InputStream is = this.getClass().getClassLoader().getResourceAsStream("deploy-controller-template.yaml");
    String deployControllerTpl = IOUtils.toString(is);
    String deployerBackendUrl = runtimeProfile.getSuite_install_backend_url();
    if (installType.equalsIgnoreCase("update")) {
      deployerBackendUrl = runtimeProfile.getSuite_update_backend_url();
    }
    for (ItsmaService itsmaService : deployerContext.getItsmaServices()) {
      String deployControllerYaml = deployControllerTpl.replaceAll("SERVICENAME", itsmaService.getName());
      deployControllerYaml = deployControllerYaml.replaceAll("NAMESPACE", runtimeProfile.getNamespace());
      deployControllerYaml = deployControllerYaml.replaceAll("TIMEZONE", runtimeProfile.getTimezone());
      deployControllerYaml = deployControllerYaml.replace("SUITE_DEPLOYER_BACKEND_URL", deployerBackendUrl);
      deployControllerYaml = deployControllerYaml.replace("REGISTRY_URL", itsmaService.getRegistryUrl());
      deployControllerYaml = deployControllerYaml.replace("CONTROLLER_TAG", itsmaService.getControllerImgTag());
      deployControllerYaml = deployControllerYaml.replace("INSTALL_TYPE", installType);

      String tempYamls = runtimeProfile.getItsma_services_path() + "/controller_yamls";
      File f = new File(tempYamls);
      if (!f.exists()) {
        f.mkdir();
      }

      String yamlFileName = itsmaService.getName() + "-deploy-controller.yaml";
      FileOutputStream fo = new FileOutputStream(tempYamls + "/" + yamlFileName, false);
      fo.write(deployControllerYaml.getBytes());
      fo.close();
      ssRestClient.createKubeResource(runtimeProfile.getDeploymentUuid(), tempYamls + "/" + yamlFileName);
    }
  }

  public String getServiceRegistryUrl(String serviceName) {
    String defaultRegistryUrl = (String) deployerContext.getSuiteProperties().get("default_registry_url");
    if (defaultRegistryUrl == null || defaultRegistryUrl.isEmpty()) {
      defaultRegistryUrl = runtimeProfile.getDefault_registry_url();
    }

    Object oActiveServices = deployerContext.getSuiteProperties().get("activated_services");
    if (oActiveServices != null) {
      try {
        List<Map<String, String>> activatedServices = (List<Map<String, String>>) oActiveServices;
        String registryUrl = activatedServices.stream().filter(
          service -> service.get("name").equalsIgnoreCase(serviceName)
        ).findFirst().get().get("registry_url");
        return registryUrl == null || registryUrl.isEmpty() ? defaultRegistryUrl : registryUrl;
      } catch (Exception e) {
        logger.warn("Get registry_url of service " + serviceName +
          " from payload failed. Using default: " + defaultRegistryUrl);
        return defaultRegistryUrl;
      }
    }

    return defaultRegistryUrl;
  }

  public String getServiceControllerImgTag(ItsmaProduct itsmaProduct) {
    String defaultTag = itsmaProduct.getVersion();
    Object oActiveServices = deployerContext.getSuiteProperties().get("activated_services");
    if (oActiveServices != null) {
      try {
        List<Map<String, String>> activatedServices = (List<Map<String, String>>) oActiveServices;
        String controllerImgTag = activatedServices.stream().filter(
          service -> service.get("name").equalsIgnoreCase(itsmaProduct.getName())
        ).findFirst().get().get("controller_img_tag");
        return controllerImgTag == null || controllerImgTag.isEmpty() ? defaultTag : controllerImgTag;
      } catch (Exception e) {
        logger.error("Get controller_img_tag of service " + itsmaProduct.getName() +
          " from payload failed. Using default: " + defaultTag);
        return defaultTag;
      }
    }
    return defaultTag;
  }

  /**
   * restoreDeployerContext try to restore the deployer context if the Itsma suite is running(e.g. when updating the suite)
   *
   * @throws Exception
   */
  public void restoreDeployerContext() {
    try {
      //String suiteMode = k8sRestClient.queryConfigMap(runtimeProfile.getNamespace(), "itsma-common-configmap", "itom_suite_mode");
      String activatedServices = k8sRestClient.queryConfigMap(runtimeProfile.getNamespace(), "itsma-common-configmap", ItsmaInstallerConstants.INSTALLEDSERVICELIST);
      if (activatedServices == null || activatedServices.isEmpty()) {
        logger.info("===QueryConfigMap. activatedServices is empty. maybe no Itsma suite is running. do nothing");
        return;
      }

      InputStream is = new FileSystemResource(runtimeProfile.getBuiltin_config_path() + "/" + ItsmaInstallerConstants.ITSMA_PRODUCTS).getInputStream();
      productsService.loadProducts(is);

      for (ItsmaProduct itsmaProduct : productsService.getItsmaProducts()) {
        if (activatedServices.contains(itsmaProduct.getName())) {
          itsmaProduct.setActive(true);
          ItsmaService itsmaService = new ItsmaService();
          itsmaService.setName(itsmaProduct.getName());
          itsmaService.setVersion(itsmaProduct.getVersion());
          itsmaService.setStatus(ItsmaService.STATUS.RUNNING);
          itsmaService.setRegistryUrl(getServiceRegistryUrl(itsmaProduct.getName()));
          itsmaService.setControllerImgTag(getServiceControllerImgTag(itsmaProduct));
          deployerContext.getItsmaServices().add(itsmaService);
        } else {
          itsmaProduct.setActive(false);
        }
      }
    } catch (Exception e) {
      if (e instanceof KubernetesClientException) {
        logger.info("===QueryConfigMap failed. maybe no Itsma suite is running. do nothing");
      } else {
        logger.error("===RestoreDeployerContext failed. This is a fatal error, deployer exits now. Exception: " + e.getMessage());
        System.exit(1);
      }
    }
  }

  /**
   * createStatusRefreshThread creates a thread for refreshing the deployer status
   * It should not exit until the whole application exit
   */
  public void createStatusRefreshThread() {
    logger.info("create a thread for status refreshing...");
    new Thread() {
      @Override
      public void run() {
        while (true) {
          List<ItsmaServiceStatus> itsmaServiceStatuses = new ArrayList<>();
          for (ItsmaService itsmaService : deployerContext.getItsmaServices()) {
            itsmaServiceStatuses.add(new ItsmaServiceStatus(itsmaService.getName(), itsmaService.getStatus().toString()));
          }

          deployerContext.updateDeployerStatus(itsmaServiceStatuses);
          logger.info("Update install status: " + deployerContext.getDeployerStatus().toJson());

          try {
            TimeUnit.MILLISECONDS.sleep(ItsmaInstallerConstants.STATUS_CHECK_INTERVAL);
          } catch (InterruptedException e) {
            logger.warn("Failed to sleep... " + e.getMessage());
          }
        }
      }
    }.start();
  }
}
