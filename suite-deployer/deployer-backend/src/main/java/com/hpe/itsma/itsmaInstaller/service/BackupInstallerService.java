package com.hpe.itsma.itsmaInstaller.service;

import com.hpe.itsma.itsmaInstaller.bean.KubeInstanceInfo;
import com.hpe.itsma.itsmaInstaller.bean.KubeResourceStatus;
import com.hpe.itsma.itsmaInstaller.bean.RuntimeProfile;
import com.hpe.itsma.itsmaInstaller.bean.SSRestResponseBody;
import com.hpe.itsma.itsmaInstaller.constants.ItsmaInstallerConstants;
import com.hpe.itsma.itsmaInstaller.util.ItsmaUtil;
import com.hpe.itsma.itsmaInstaller.util.VelocityWrapper;
import com.hpe.itsma.itsmaInstaller.util.YamlParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhongtao on 8/9/2017.
 */
@Component
public class BackupInstallerService extends ItsmaInstallerService {
  private static Log logger = LogFactory.getLog(BackupInstallerService.class);

  @Autowired
  ItsmaSuite itsmaSuite;

  @Autowired
  private K8sRestClient k8sRestClient;

  @Autowired
  private RuntimeProfile runtimeProfile;

  @Autowired
  private SSRestClient ssRestClient;

  @Autowired
  private ProductsService productsService;

  private List<Map<String, Object>> backupedConfigMapList;

  public void createBackupSVC() throws Exception {
    logger.info("Creating Service [itom-itsma-backup-svc]...");
    Map<String, Object> properties = new HashMap<>();
    String namespace = runtimeProfile.getNamespace();
    properties.put("namespace", namespace);
    properties.put(ItsmaInstallerConstants.NODE_LABEL_KEY, runtimeProfile.getSuite_label_key());
    properties.put(ItsmaInstallerConstants.NODE_LABEL_VALUE, runtimeProfile.getSuite_label_value());
    properties.put("docker_image_registry", runtimeProfile.getDefault_registry_url());
    properties.put("time_zone", runtimeProfile.getTimezone());

    String builtinConfigPath = runtimeProfile.getBuiltin_config_path();
    String deploymentUuid = runtimeProfile.getDeploymentUuid();

    VelocityWrapper velocityWrapper = new VelocityWrapper();
    velocityWrapper.transformFile(builtinConfigPath + "/yaml_templates/namespace.yaml", builtinConfigPath + "/yamls/namespace.yaml", properties);
    velocityWrapper.transformFile(builtinConfigPath + "/yaml_templates/itom-itsma-backup.yaml", builtinConfigPath + "/yamls/itom-itsma-backup.yaml", properties);

    // create namespace and block until it's active
    String responseBody = ssRestClient.createKubeResource(deploymentUuid, builtinConfigPath + "/yamls/namespace.yaml");
    SSRestResponseBody ssRestResponseBody = ItsmaUtil.unmarshallJson(responseBody, SSRestResponseBody.class);
    while (true) {
      KubeInstanceInfo.K8sResponseBody.Status status = getResourceStatus(ssRestResponseBody.getInstallationId());
      if (status.getPhase() != null && status.getPhase().equalsIgnoreCase("Active")) {
        break;
      }
      TimeUnit.MILLISECONDS.sleep(ItsmaInstallerConstants.STATUS_CHECK_INTERVAL);
    }

    if (runtimeProfile.isStandlone()) {
      this.createDefaultPV(deploymentUuid);
    }

    ssRestClient.createKubeResource(deploymentUuid, builtinConfigPath + "/yamls/itom-itsma-backup.yaml");
    logger.debug("Going to check the status of [itom-itsma-backup.yaml] and block until it's 'Running'.");
    // create itom-itsma-backup.yaml and block until it's Running
    while (true) {
      KubeResourceStatus status = k8sRestClient.getServiceStatusByName(namespace, "itom-itsma-backup-svc");
      if (status.getStatus().equalsIgnoreCase("Running")) {
        logger.info("[itom-itsma-backup-svc] is Running now.");
        break;
      }
      TimeUnit.MILLISECONDS.sleep(ItsmaInstallerConstants.STATUS_CHECK_INTERVAL);
    }
  }

  public List<Map<String, Object>> getConfigMapFromBackupService() throws Exception {
    if (ItsmaUtil.isNullOrEmpty(this.backupedConfigMapList)) {
      String itom_suite_backup_package_dir = ItsmaUtil.getPropertyEx(deployerContext.getSuiteProperties(), "itom_suite_backup_package_dir").toString();
      String itom_suite_backup_package_name = ItsmaUtil.getPropertyEx(deployerContext.getSuiteProperties(), "itom_suite_backup_package_name").toString();
      String url = runtimeProfile.getBackup_svc_url() + "/suitebackup/restore/yamls/" + itom_suite_backup_package_dir + "/" + itom_suite_backup_package_name + "/configmap";
      ResponseEntity<String> response = ssRestClient.sendRequestToSS(url, HttpMethod.GET, new HttpEntity(""), true);
      logger.debug("Response of [" + url + "]: " + response.getBody());
      String configmapDir = runtimeProfile.getItsma_services_path() + "/suitebackup/yamls/configmap";
      File f = new File(configmapDir);
      if (!f.exists()) {
        f.mkdirs();
      }

      FileOutputStream fo = new FileOutputStream(configmapDir + "/configmap.yaml", false);
      fo.write(response.getBody().getBytes());
      fo.close();

      this.backupedConfigMapList = (List<Map<String, Object>>) YamlParser.parseYamlFileToList(configmapDir + "/configmap.yaml");
    }
    return this.backupedConfigMapList;
  }

  public Map<String, Object> getConfigMapByName(String configMapMame) throws Exception {
    for (Map<String, Object> configmap : this.getConfigMapFromBackupService()) {
      Map<String, Object> metadata = (Map<String, Object>) configmap.get("metadata");
      String name = metadata.get("name").toString().trim();
      if (name.equalsIgnoreCase(configMapMame)) {
        return configmap;
      }
    }
    return null;
  }

  public void restoreConfigMapByName(String configmapName, Map<String, Object> configmapRetained) throws Exception {
    logger.debug(String.format("Trying to restore configmap [%s]", configmapName));
    if (ItsmaUtil.isNullOrEmpty(configmapRetained)) {
      logger.info("configmapRetained is empty, nothing to restore.");
      return;
    }

    Iterator<Map.Entry<String, Object>> iterator = configmapRetained.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, Object> configmapRetainedItem = iterator.next();
      String configmapRetainedItemName = configmapRetainedItem.getKey();
      if (configmapRetainedItemName.equalsIgnoreCase(configmapName)) {
        logger.debug("The values [" + configmapRetainedItem.getValue() + "] in configmap [" + configmapRetainedItemName + "] will be restored.");
        Map<String, String> data = new HashMap<>();
        data.put("configmapRetainedItemName", configmapRetainedItemName);
        Map<String, Object> configmap = this.getConfigMapByName(configmapRetainedItemName);
        logger.debug("Found configmap [" + configmap + "]");
        Map<String, String> configmapData = (Map<String, String>) configmap.get("data");
        for (String key : (List<String>) configmapRetainedItem.getValue()) {
          data.put(key, configmapData.get(key));
        }
        try {
          k8sRestClient.updateOrNewDataOfConfigMap(runtimeProfile.getNamespace(), configmapRetainedItemName, data);
          logger.info("ConfigMap [" + configmapRetainedItemName + "] restored successfully.");
          return;
        } catch (Exception e) {
          logger.info("Exception on updating config map [" + configmapRetainedItemName + "]. " + e);
          throw new Exception("Exception on updating config map [" + configmapRetainedItemName + "]. " + e);
        }
      }
    }
  }

  public void restoreVolumes(Map<String, Object> properties) throws Exception {
    String itom_suite_backup_package_dir = ItsmaUtil.getPropertyEx(properties, "itom_suite_backup_package_dir").toString();
    String itom_suite_backup_package_name = ItsmaUtil.getPropertyEx(properties, "itom_suite_backup_package_name").toString();

    String url = runtimeProfile.getBackup_svc_url() + "/suitebackup/restore/volumes";

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("itom_suite_backup_package_dir", itom_suite_backup_package_dir);
    jsonObject.put("itom_suite_backup_package_name", itom_suite_backup_package_name);
    jsonObject.put("activated_services", productsService.getActivatedServices());
    logger.debug("restoreVolumes, Request body: " + jsonObject.toString());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity = new HttpEntity<String>(jsonObject.toString(), headers);

    ResponseEntity<String> response = ssRestClient.sendRequestToSS(url, HttpMethod.POST, entity, false);
    logger.debug("Response of [" + url + "]: " + response);
    if (response.getStatusCode().equals(HttpStatus.CREATED)) {
      while (true) {
        response = ssRestClient.sendRequestToSS(url, HttpMethod.GET, entity, false);
        Map result = ItsmaUtil.unmarshallJson(response.getBody(), Map.class);
        String restoreVolumeStatus = result.get("restoreVolumeStatus").toString();
        if (restoreVolumeStatus.equalsIgnoreCase(ItsmaInstallerConstants.RESTORING)) {
          logger.info("Restoring volumes..., please wait");
          TimeUnit.MILLISECONDS.sleep(ItsmaInstallerConstants.STATUS_CHECK_INTERVAL);
        } else if (restoreVolumeStatus.equalsIgnoreCase(ItsmaInstallerConstants.FAILED)) {
          logger.error("Fail to restore volumes");
          throw new Exception("Fail to restore volumes");
        } else if (restoreVolumeStatus.equalsIgnoreCase(ItsmaInstallerConstants.SUCCESS)) {
          logger.error("Restored volumes successfully.");
          break;
        }
      }
    } else if (response.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
      logger.error("Already have backup process. Throw Exception.");
      throw new Exception("Fail to restore volumes");
    } else {
      logger.error("Fail to restore volumes");
      throw new Exception("Fail to restore volumes");
    }
  }

  public void backup() throws Exception {
    String url = runtimeProfile.getBackup_svc_url() + "/suitebackup/backup";

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("backup_namespace", runtimeProfile.getNamespace());
    JSONArray jsonArray = new JSONArray();
    deployerContext.getItsmaServices().stream().forEach(
      itsmaService -> jsonArray.put(new JSONObject().put("name", itsmaService.getName()))
    );

    jsonObject.put("backup_services", jsonArray);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity = new HttpEntity<String>(jsonObject.toString(), headers);

    ssRestClient.sendRequestToSS(url, HttpMethod.POST, entity, false);
  }

  public String backupStatus() throws Exception {
    String url = runtimeProfile.getBackup_svc_url() + "/suitebackup/backup";

    return ssRestClient.sendRequestToSS(url, HttpMethod.GET, new HttpEntity(""), false).getBody();
  }
}