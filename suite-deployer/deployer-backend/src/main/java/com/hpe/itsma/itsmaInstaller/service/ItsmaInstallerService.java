package com.hpe.itsma.itsmaInstaller.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hpe.itsma.dbservice.DbConn;
import com.hpe.itsma.itsmaInstaller.bean.*;
import com.hpe.itsma.itsmaInstaller.constants.ItsmaInstallerConstants;
import com.hpe.itsma.itsmaInstaller.constants.KubeResType;
import com.hpe.itsma.itsmaInstaller.controller.ItsmaInstallerController;
import com.hpe.itsma.itsmaInstaller.exception.InvalidStateException;
import com.hpe.itsma.itsmaInstaller.exception.K8sResTypeUnsupportedException;
import com.hpe.itsma.itsmaInstaller.util.ItsmaUtil;
import com.hpe.itsma.itsmaInstaller.util.VelocityWrapper;
import com.hpe.itsma.ldapservice.LdapConn;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The concrete class to do the installation of itsma suite
 * <p>
 * <p>Itsma suite is installed/deployed by kubernetes, which depends on yaml files,
 * so this class actually does two things:
 * <p>
 * <ol>
 * <li>Prepare the configuration parameters, which includes two parts:
 * <ul>
 * <li>Parameters from user input</li>
 * <li>Parameters from the os environment</li>
 * </ul>
 * </li>
 * <li>Send install request to SharedService Suite Installer</li>
 * <li>Create and test an Oracle DB connection by user input</li>
 * </ol>
 * </p>
 *
 * @author danny.tian
 * @since 11/2/2016.
 */
@Component
public class ItsmaInstallerService {
  private static Log logger = LogFactory.getLog(ItsmaInstallerController.class);

  private static Thread statusRefreshThread = null;
  private static String installStatus;

  @Autowired
  ItsmaSuite itsmaSuite;

  @Autowired
  DeployerContext deployerContext;

  @Autowired
  private K8sRestClient k8sRestClient;

  @Autowired
  private SSRestClient ssRestClient;

  @Autowired
  private RuntimeProfile runtimeProfile;

  @Autowired
  private DbOperatorService dbOperatorService;

  @Autowired
  private ProductsService productsService;

  @Autowired
  private VaultService vaultService;

  @Autowired
  private StorageService storageService;

  @Autowired
  private BackupInstallerService backupInstallerService;

  public KubeResourceStatus getPodStatusByName(String podName) throws Exception {
    return k8sRestClient.getPodStatusByName(runtimeProfile.getNamespace(), podName);
  }

  public KubeResourceStatus getServiceStatusByName(String podName) throws Exception {
    return k8sRestClient.getServiceStatusByName(runtimeProfile.getNamespace(), podName);
  }

  public KubeInstanceInfo.K8sResponseBody.Status getResourceStatus(String installationId) throws Exception {
    return ItsmaUtil.unmarshallJson(ssRestClient.getResourceStatus(runtimeProfile.getDeploymentUuid(), installationId), SSRestResponseBody.class)
      .getBatchInfo()
      .getKubeInstanceInfoList()
      .get(0)
      .unmarshallResponseBody()
      .getStatus();
  }

  public void install(Map<String, Object> properties) throws Exception {
    if (deployerContext.getDeployerStatus().getPhase().getName() != DeployerStatus.PhaseType.IDLE) {
      throw new InvalidStateException("Deployer is too busy to install");
    }
    deployerContext.updateDeployerStatus(DeployerStatus.PhaseType.INSTALLING, "");
    properties.putAll(runtimeProfile.toMap());

    InputStream is = new FileSystemResource(
      runtimeProfile.getBuiltin_config_path() + "/" + ItsmaInstallerConstants.ITSMA_PRODUCTS).getInputStream();
    productsService.loadProducts(is);
    productsService.deactiveProducts(properties);

    properties.put(ItsmaInstallerConstants.FULLSERVICELIST, productsService.getItsmaProductsAsString());
    properties.put(ItsmaInstallerConstants.INSTALLEDSERVICELIST, productsService.getActivatedItsmaProductsAsString());

    deployerContext.getSuiteProperties().clear();
    deployerContext.getSuiteProperties().putAll(properties);

    if (ItsmaUtil.getProperty(deployerContext.getSuiteProperties(), "itom_suite_install_type").toString().equalsIgnoreCase("install_from_backup")) {
      logger.info("restore volumes");
      backupInstallerService.restoreVolumes(deployerContext.getSuiteProperties());
    }

    // construct all active services
    deployerContext.getItsmaServices().clear();
    productsService.getItsmaProducts().stream().filter(
      itsmaProduct -> itsmaProduct.isActive()
    ).forEach(
      itsmaProduct1 -> {
        ItsmaService itsmaService = new ItsmaService();
        itsmaService.setName(itsmaProduct1.getName());
        itsmaService.setVersion(itsmaProduct1.getVersion());
        itsmaService.setStatus(ItsmaService.STATUS.DEPLOYED);

        itsmaService.setRegistryUrl(itsmaSuite.getServiceRegistryUrl(itsmaProduct1.getName()));
        itsmaService.setControllerImgTag(itsmaSuite.getServiceControllerImgTag(itsmaProduct1));
        deployerContext.getItsmaServices().add(itsmaService);
      }
    );

    addExtraConfig(deployerContext.getSuiteProperties());

    Map<String, String> appRoles = vaultService.saveSecret(runtimeProfile.getDeploymentUuid(), deployerContext.getSuiteProperties());
    vaultService.updateVaultExtendApproles(runtimeProfile.getDeploymentUuid(), deployerContext.getSuiteProperties());
    deployerContext.getSuiteProperties().putAll(appRoles);

    deployerContext.getSuiteProperties().put("namespace", properties.get(ItsmaInstallerConstants.SUITE_NAMESPACE));

    installBuiltinKubeResources();

    logger.debug("save configuration to etcd.");
    storageService.createConfiguration(deployerContext.getSuiteProperties());

    itsmaSuite.createAllServiceDeployController(ItsmaUtil.getProperty(deployerContext.getSuiteProperties(), "itom_suite_install_type").toString());

    createStatusRefreshThread();

    ssRestClient.registerAutoPassUrl();
  }

  public void installBuiltinKubeResources() throws Exception {
    String builtinConfigPath = runtimeProfile.getBuiltin_config_path();
    String itsmaServicesPath = runtimeProfile.getItsma_services_path();

    VelocityWrapper velocityWrapper = new VelocityWrapper();
    velocityWrapper.transformFile(builtinConfigPath + "/yaml_templates/configmap.yaml", builtinConfigPath + "/yamls/configmap.yaml", deployerContext.getSuiteProperties());

    String deploymentUuid = runtimeProfile.getDeploymentUuid();

    if (runtimeProfile.isStandlone()) {
      this.createDefaultPV(deploymentUuid);
    }

    // create configmap
    ssRestClient.createKubeResource(deploymentUuid, builtinConfigPath + "/yamls/configmap.yaml");

    // create suite_secret.yaml
    // Note: please put the suite_secret.yaml under 'suite-install/itsma/output/yaml_templates' before install.
    FileSystemResource fileSystemResource = new FileSystemResource(itsmaServicesPath + "/yaml_templates/suite_secret.yaml");
    if (fileSystemResource.exists()) {
      velocityWrapper.transformFile(itsmaServicesPath + "/yaml_templates/suite_secret.yaml", builtinConfigPath + "/yamls/suite_secret.yaml", deployerContext.getSuiteProperties());
      logger.debug("Try to createKubeResource " + builtinConfigPath + "/yamls/suite_secret.yaml");
      ssRestClient.createKubeResource(deploymentUuid, builtinConfigPath + "/yamls/suite_secret.yaml");
    } else {
      logger.info(itsmaServicesPath + "/yaml_templates/suite_secret.yaml does NOT exist.");
    }
  }

  public String getInstallStatus() throws Exception {
    synchronized (deployerContext.getInstallStatusResultLock()) {
      logger.info("Installation Status: " + installStatus);
      return installStatus;
    }
  }

  /**
   * Create and test an Oracle DB connection
   *
   * @param properties
   * @throws Exception
   */
  public ResponseEntity<String> testDatabase(Map<String, Object> properties) throws Exception {

    return DbConn.testDB(properties);
  }

  /**
   * Create and test an Ldap connection
   *
   * @param properties
   * @throws Exception
   */
  public ResponseEntity<String> connLdap(Map<String, Object> properties) throws Exception {

    return LdapConn.connLdap(properties);
  }

  private void addExtraConfig(Map<String, Object> properties) throws Exception {
    logger.debug("Add extra configuration.");
    String domainName = (String) ItsmaUtil.getPropertyEx(properties, ItsmaInstallerConstants.DOMAIN_NAME);
    if (domainName.isEmpty()) {
      throw new IllegalArgumentException("Parameter " + ItsmaInstallerConstants.DOMAIN_NAME + " cannot be empty.");
    }

    transformDBConfig(properties);

//    properties.put(ItsmaInstallerConstants.AM_QUICKSEARCH_URL,  domainName+":30013");
//    properties.put(ItsmaInstallerConstants.AM_USER,             "Admin");
//    properties.put(ItsmaInstallerConstants.AM_PASSWORD,         "");

    properties.put(ItsmaInstallerConstants.LWSSO_DOMAIN, domainName.substring(domainName.indexOf('.') + 1));
    properties.put(ItsmaInstallerConstants.LWSSO_USER, "SM_USER");
    properties.put(ItsmaInstallerConstants.LWSSO_INITSTRING, new BigInteger(130, new SecureRandom()).toString(28));

    // add ldap parameters
    ItsmaUtil.putPropIfNotExist(properties,
      ItsmaInstallerConstants.LDAP_SERVER_IP,
      "openldap-svc");

    // set the default node label as Worker
    properties.put(ItsmaInstallerConstants.NODE_LABEL_KEY, properties.get(ItsmaInstallerConstants.SUITE_LABEL_KEY));
    properties.put(ItsmaInstallerConstants.NODE_LABEL_VALUE, properties.get(ItsmaInstallerConstants.SUITE_LABEL_VALUE));

    appendDefaultConfig(properties);
    appendSMRTEConfig(properties);
    appendItsamCommonConfig(properties);
    dbOperatorService.createInstanceAndResetPassword(properties);
  }

  /**
   * Transform the database configuration input by restful client to the format needed by itsma suite.
   * <p>
   * When the json array 'database' is null or empty, guess using internal database(Postgres), or guess
   * using external database.
   *
   * @param properties
   */
  public void transformDBConfig(Map<String, Object> properties) throws Exception{
    if (ItsmaUtil.isNullOrEmpty(ItsmaUtil.getProperty(properties, "database"))) {
      properties.put("sm_" + ItsmaInstallerConstants.INTERNAL + "_db", true);
      properties.put("ucmdb_" + ItsmaInstallerConstants.INTERNAL + "_db", true);
      properties.put("sp_" + ItsmaInstallerConstants.INTERNAL + "_db", true);
      properties.put("idm_" + ItsmaInstallerConstants.INTERNAL + "_db", true);
      properties.put("xservices_" + ItsmaInstallerConstants.INTERNAL + "_db", true);
      properties.put("xruntime_" + ItsmaInstallerConstants.INTERNAL + "_db", true);
      properties.put("smarta_" + ItsmaInstallerConstants.INTERNAL + "_db", true);
    } else {
      List<Map<String, Object>> databases = (List<Map<String, Object>>) ItsmaUtil.getProperty(properties, "database");
      String installType = ItsmaUtil.getProperty(properties, ItsmaInstallerConstants.ITOM_SUITE_INSTALL_TYPE).toString();
      String suiteMode = ItsmaUtil.getProperty(properties, ItsmaInstallerConstants.ITOM_SUITE_MODE).toString();
      properties.put("external_db", "TRUE");
      for (Map<String, Object> db : databases) {
        String productName = (String) ItsmaUtil.getPropertyEx(db, ItsmaInstallerConstants.PRODUCT_NAME);
        properties.put(productName + "_" + ItsmaInstallerConstants.INTERNAL + "_db", ItsmaUtil.getPropertyEx(db, ItsmaInstallerConstants.INTERNAL));
        properties.put(productName + "_" + ItsmaInstallerConstants.DB_SERVER, ItsmaUtil.getPropertyEx(db, ItsmaInstallerConstants.DB_SERVER));
        properties.put(productName + "_" + ItsmaInstallerConstants.DB_PORT, ItsmaUtil.getPropertyEx(db, ItsmaInstallerConstants.DB_PORT));
        properties.put(productName + "_" + ItsmaInstallerConstants.DB_ENGINE, ItsmaUtil.getPropertyEx(db, ItsmaInstallerConstants.DB_ENGINE));
        properties.put(productName + "_" + ItsmaInstallerConstants.DB_INST, ItsmaUtil.getPropertyEx(db, ItsmaInstallerConstants.DB_INST));

        //if oracle or install from backup, not provide db admin
        if (db.get(ItsmaInstallerConstants.DB_ENGINE).toString().equalsIgnoreCase("oracle")
          || (installType.equalsIgnoreCase(ItsmaInstallerConstants.INSTALL_FROM_BACKUP) && suiteMode.equalsIgnoreCase(ItsmaInstallerConstants.ITOM_H_MODE))) {
          properties.put(productName + "_db_username", ItsmaUtil.getPropertyEx(db, ItsmaInstallerConstants.DB_LOGIN));
          properties.put(productName + "_db_password", ItsmaUtil.getPropertyEx(db, ItsmaInstallerConstants.DB_PASSWORD));
        } else {
          properties.put(productName + "_"+ItsmaInstallerConstants.DBA_USERNAME, ItsmaUtil.getPropertyEx(db, ItsmaInstallerConstants.DB_LOGIN));
          properties.put(productName + "_"+ItsmaInstallerConstants.DBA_PASSWORD, ItsmaUtil.getPropertyEx(db, ItsmaInstallerConstants.DB_PASSWORD));
          if (productName.equalsIgnoreCase("sm")) {
            properties.put(productName + "_" + ItsmaInstallerConstants.DB_INST, "servicemanagement");
            properties.put(productName + "_db_username", "servicemanagement"); //for now use the same name and pass
            properties.put(productName + "_db_password", "servicemanagement");
          }
          if (productName.equalsIgnoreCase("idm")) {
            properties.put(productName + "_" + ItsmaInstallerConstants.DB_INST, "idm");
            properties.put(productName + "_db_username", "idm"); //for now use the same name and pass
            properties.put(productName + "_db_password", "idm");
          }
          if (productName.equalsIgnoreCase("ucmdb")) {
            properties.put(productName + "_" + ItsmaInstallerConstants.DB_INST, "ucmdb");
            properties.put(productName + "_db_username", "ucmdb");
            properties.put(productName + "_db_password", "ucmdb");
          }
        }

        if (productName.equalsIgnoreCase("ucmdb")) {
          properties.put(productName + "_" + "db_login1", ItsmaUtil.getPropertyEx(db, ItsmaInstallerConstants.DB_LOGIN));
          properties.put(productName + "_" + "db_login2", ItsmaUtil.getPropertyEx(db, ItsmaInstallerConstants.DB_LOGIN));
        }
        if (productName.equalsIgnoreCase("sm")) {
          properties.put("sm_dba_password_key", "itom_itsma_dba_password_secret_key");
          properties.put("sm_db_password_key", "itom_itsma_db_password_secret_key");
        }
        appendDefaultConfigForEmptyValue(properties,productName + "_" + ItsmaInstallerConstants.DB_INST);
      }
    }
  }

  /**
   * Append default configuration for specify key
   * All the configurations are defined in resources/configurationProperties.json
   *
   * @param properties
   * @param key   the key which is empty and use default value
   * @throws Exception
   */
  public void appendDefaultConfigForEmptyValue(Map<String, Object> properties,String key) throws Exception {
    try {
      Map<String, Object> defaultProperties = ItsmaUtil.readResourceJson2Map(ItsmaInstallerConstants.CONFIGURATION_PROPERTIES);
      if(properties.get(key).toString().equalsIgnoreCase("")){
        properties.put(key,defaultProperties.get(key));
      }
    } catch (Exception e) {
      logger.error("Failed to append default configuration for key <"+key+">. Cause: " + e.getMessage());
      throw new Exception("Failed to append default configuration. Cause: " + e.getMessage());
    }
  }

  /**
   * Append all the default configuration used by itsma suite.
   * All the configurations are defined in resources/configurationProperties.json
   *
   * @param properties
   * @throws Exception
   */
  public void appendDefaultConfig(Map<String, Object> properties) throws Exception {
    try {
      Map<String, Object> defaultProperties = ItsmaUtil.readResourceJson2Map(ItsmaInstallerConstants.CONFIGURATION_PROPERTIES);
      for (String key : defaultProperties.keySet()) {
        ItsmaUtil.putPropIfNotExist(properties, key, defaultProperties.get(key));
      }
    } catch (Exception e) {
      logger.error("Failed to append default configuration. Cause: " + e.getMessage());
      throw new Exception("Failed to append default configuration. Cause: " + e.getMessage());
    }
  }

  /**
   * Append all the sm_rte configuration used by itsma suite.
   *
   * @param properties
   * @throws Exception
   */
  public void appendSMRTEConfig(Map<String, Object> properties) throws Exception {
    try {
      ItsmaProduct sm = productsService.getItsmaProduct("itom-sm");
      // if SM is internal installed.
      if (sm != null && sm.isActive()) {
        String currentDate = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        properties.put("sm_rte_url", "http://sm-rte-integration-svc:13090");
        properties.put("sm_integration_username", "intgAdmin");
        properties.put("sm_integration_password", "sm_integration_password_secret_key");
        String dbType = (String) properties.get("sm_" + ItsmaInstallerConstants.DB_ENGINE);
        if (dbType != null) {
          properties.put("sm_database_type", dbType);
        } else {
          properties.put("sm_database_type", "postgres");
        }
        String dbIp = (String) properties.get("sm_" + ItsmaInstallerConstants.DB_SERVER);
        if (dbIp != null) {
          properties.put("sm_database_ip", dbIp);
        } else {
          properties.put("sm_database_ip", "sm-postgres-svc");
        }
        String dbPort = (String) properties.get("sm_" + ItsmaInstallerConstants.DB_PORT);
        if (dbPort != null) {
          properties.put("sm_database_port", dbPort);
        } else {
          properties.put("sm_database_port", "5432");
        }
        String dbInst = (String) properties.get("sm_" + ItsmaInstallerConstants.DB_INST);
        if (dbInst != null) {
          properties.put("sm_database_instance", dbInst);
        } else {
          properties.put("sm_database_instance", "sm");
        }
//        String dbUser = (String)properties.get("sm_" + ItsmaInstallerConstants.DB_LOGIN);
        String dbUser = (String) properties.get("sm_db_username");
        if (dbUser != null) {
          properties.put("sm_database_user", dbUser);
        } else {
          properties.put("sm_database_user", "sm");
        }
        properties.put("sm_database_password", "sm_database_password_secret_key");
        properties.put("sm_apps_version", "201707");
        properties.put("sm_process_designer", "1");
        properties.put("sm_smarta_enabled", "4");
        properties.put("sm_smarta_ts", currentDate);
        properties.put("sm_chat_enabled", "1");
        properties.put("sm_chat_ts", currentDate);
        properties.put("sm_portal_enabled", "1");
        properties.put("sm_portal_ts", currentDate);
        properties.put("sm_survey_enabled", "1");
      }
    } catch (Exception e) {
      logger.error("Failed to append sm rte configuration. Cause: " + e.getMessage());
      throw new Exception("Failed to append sm rte configuration. Cause: " + e.getMessage());
    }
  }

  /**
   * Append the itsma common configuration
   *
   * @param properties
   * @throws Exception
   */
  public void appendItsamCommonConfig(Map<String, Object> properties) throws Exception {
    try {
      ItsmaProduct sm = productsService.getItsmaProduct("itom-sm");
      // if SM is internal installed.
      if (sm != null && sm.isActive()) {
        properties.put("chat_config_ready", "true");
      } else {
        properties.put("chat_config_ready", "false");
      }
    } catch (Exception e) {
      logger.error("Failed to append itsma common configuration. Cause: " + e.getMessage() + " Append default value.");
      throw new Exception("Failed to append itsma common configuration. Cause: " + e.getMessage() + " Append default value.");
    }

    for (ItsmaProduct product : productsService.getItsmaProducts()) {
      properties.put(product.getName(), product.isActive());
    }
    List<SuiteDeploymentInfo> suiteDeploymentInfoList = ssRestClient.getSuiteDeploymentInfo("");
    properties.put(ItsmaInstallerConstants.SUITE_VERSION, suiteDeploymentInfoList.get(0).getVersion());
  }

  private void createStatusRefreshThread() {
    logger.info("create a thread for status refreshing...");
    if (statusRefreshThread == null) {
      statusRefreshThread = new Thread() {
        @Override
        public void run() {
          while (true) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            synchronized (deployerContext.getInstallStatusResultLock()) {
              final JsonArray jsonArray = new JsonArray();
              for (ItsmaService itsmaService : deployerContext.getItsmaServices()) {
                final JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", itsmaService.getName());
                jsonObject.addProperty("status", itsmaService.getStatus().name());
                jsonArray.add(jsonObject);
              }
              final JsonObject servicestatus = new JsonObject();
              servicestatus.add("servicestatus", jsonArray);
              installStatus = gson.toJson(servicestatus);
            }
            logger.info("Update install status: " + installStatus);

            // Installation complete if all itsma services are SUCCESS
            if (!deployerContext.getItsmaServices().isEmpty() &&
              deployerContext.getItsmaServices().stream().allMatch(itsmaService -> itsmaService.getStatus().compareTo(ItsmaService.STATUS.SUCCESS) == 0)) {
              deployerContext.updateDeployerStatus(DeployerStatus.PhaseType.IDLE, "");

              //Remove internal DBs when external DBs are used
              if (ItsmaUtil.getPropertyEx(deployerContext.getSuiteProperties(), "external_db").toString().equalsIgnoreCase("true")) {
                deleteInternalDB();
              }

              Map<String, String> data = new HashMap<>();
              data.put("itom_install_phase", ItsmaInstallerConstants.RUNNING);
              try {
                k8sRestClient.updateOrNewDataOfConfigMap(runtimeProfile.getNamespace(), "itsma-common-configmap", data);
              } catch (Exception e) {
                logger.error(String.format("===Fail to update 'itom_install_phase' to 'Running'. Found Exception: %s.", e.getMessage()));
              }

              ssRestClient.updateDeploymentStatus(runtimeProfile.getDeploymentUuid(), "INSTALL_FINISHED");
              break;
            }

            deployerContext.getItsmaServices().stream().filter(
              itsmaService ->
                itsmaService.getStatus() == ItsmaService.STATUS.WAIT &&
                  itsmaService.getDeployDeps() != null
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
      };
    }

    if (statusRefreshThread != null) {
      statusRefreshThread.start();
    }
  }

  protected void createDefaultPV(String deploymentUuid) throws Exception {
    JSONObject globalVolume = new JSONObject();
    globalVolume.put("accessModes", "ReadWriteMany");
    globalVolume.put("nfsIp", runtimeProfile.getNfs_server());
    globalVolume.put("nfsOutputPath", runtimeProfile.getGlobal_nfs_expose());
    globalVolume.put("persistentVolumeReclaimPolicy", "Delete");
    globalVolume.put("pvName", "global-volume");
    globalVolume.put("pvPvcStorage", "80Gi");
    Map<String, Object> response = ssRestClient.createPV(deploymentUuid, globalVolume.toString());
    if (response.get("status").equals(true) || response.get("message").equals("the same ip can not set two different volums")) {
      logger.info("global-volume created successfully.");
    } else {
      logger.error("Failed to create persistent volume... " + globalVolume.toString());
      throw new Exception();
    }

    JSONObject dbVolume = new JSONObject();
    dbVolume.put("accessModes", "ReadWriteMany");
    dbVolume.put("nfsIp", runtimeProfile.getNfs_server());
    dbVolume.put("nfsOutputPath", runtimeProfile.getDb_nfs_expose());
    dbVolume.put("persistentVolumeReclaimPolicy", "Delete");
    dbVolume.put("pvName", "db-volume");
    dbVolume.put("pvPvcStorage", "80Gi");
    response = ssRestClient.createPV(deploymentUuid, dbVolume.toString());
    if (response.get("status").equals(true) || response.get("message").equals("the same ip can not set two different volums")) {
      logger.info("db-volume created successfully.");
    } else {
      logger.error("Failed to create persistent volume... " + dbVolume.toString());
      throw new Exception();
    }

    JSONObject smaVolume = new JSONObject();
    smaVolume.put("accessModes", "ReadWriteMany");
    smaVolume.put("nfsIp", runtimeProfile.getNfs_server());
    smaVolume.put("nfsOutputPath", runtimeProfile.getSmartanalytics_nfs_expose());
    smaVolume.put("persistentVolumeReclaimPolicy", "Delete");
    smaVolume.put("pvName", "smartanalytics-volume");
    smaVolume.put("pvPvcStorage", "80Gi");
    response = ssRestClient.createPV(deploymentUuid, smaVolume.toString());
    if (response.get("status").equals(true) || response.get("message").equals("the same ip can not set two different volums")) {
      logger.info("smartanalytics-volume created successfully.");
    } else {
      logger.error("Failed to create persistent volume... " + smaVolume.toString());
      throw new Exception();
    }
  }

  private void deleteInternalDB() {
    try {
      logger.info("Remove internal DBs when external DBs are used.");

      InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(ItsmaInstallerConstants.DELETED_INTERNAL_DBS);
      JSONArray jsonArray = new JSONArray(IOUtils.toString(is, "UTF-8"));
      List<Map<String, Object>> deletedInternalDBs = ItsmaUtil.unmarshallJson(jsonArray.toString(), List.class);

      for (Map<String, Object> productDBs : deletedInternalDBs) {
        String product_name = productDBs.get("product_name").toString();
        if (ItsmaUtil.getPropertyEx(deployerContext.getSuiteProperties(), product_name).toString().equalsIgnoreCase("true")) {
          List<Map<String, String>> k8s_resources = (List<Map<String, String>>) productDBs.get("k8s_resources");
          String name = null;
          String kind = null;
          for (Map<String, String> k8s_resource : k8s_resources) {
            name = k8s_resource.get("name");
            kind = k8s_resource.get("kind");
            KubeResType type = k8sRestClient.getKubeResType(kind);
            k8sRestClient.deleteResourceByName(runtimeProfile.getNamespace(), name, type);
            logger.info(String.format("Remove internal DBs when external DBs are used. Deployment [%s] Removed.", name));
          }
        }
      }
    } catch (K8sResTypeUnsupportedException e) {
      logger.error(String.format("Remove internal DBs when external DBs are used: Failed. Found K8sResTypeUnsupportedException: %s.", e.getMessage()));
    } catch (IOException e) {
      logger.error(String.format("Remove internal DBs when external DBs are used: Failed. Found IOException: %s.", e.getMessage()));
    }
  }
}
