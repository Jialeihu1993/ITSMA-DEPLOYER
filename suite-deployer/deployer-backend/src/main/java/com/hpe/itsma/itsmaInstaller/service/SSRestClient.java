package com.hpe.itsma.itsmaInstaller.service;

import com.google.gson.Gson;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;
import com.hpe.itsma.itsmaInstaller.bean.*;
import com.hpe.itsma.itsmaInstaller.constants.ItsmaInstallerConstants;
import com.hpe.itsma.itsmaInstaller.exception.K8sResourceNotFoundException;
import com.hpe.itsma.itsmaInstaller.util.ItsmaUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by tianlib on 12/12/2016.
 */
@Component
public class SSRestClient {
  private static Log logger = LogFactory.getLog(SSRestClient.class);

  private RuntimeProfile runtimeProfile;
  private Map<String, String> security = new HashMap<>();

  @Autowired
  public SSRestClient(RuntimeProfile runtimeProfile) {
    this.runtimeProfile = runtimeProfile;
  }

  public String getIdmToken() throws Exception {
    String url = runtimeProfile.getSs_base_url() + "/tokens";
    // set headers
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    String requestBody = "{\"passwordCredentials\": {\"password\": \"" +
      runtimeProfile.getItsma_admin_pwd() +
      "\", \"username\": \"itsma_admin\"}, \"tenantName\": \"provider\"}";
    HttpEntity<String> entity = new HttpEntity<String>(requestBody, headers);
    JSONObject jsonObject = new JSONObject(sendRequestToSS(url, HttpMethod.POST, entity, true).getBody());
    logger.debug("Got idm token: " + jsonObject);

    return jsonObject.getString("token");
  }

  public IdmSigningKey getIdmSigningKey() throws Exception {
    final String url = "https://" + runtimeProfile.getIngress_fqdn() + ":5443";
    final String pathSigningKey = "/idm-service/api/system/configurations/items/idm.encryptedSigningKey";

    String plainCreds = "idmTransportUser:idmTransportUser";
    byte[] plainCredsBytes = plainCreds.getBytes();
    byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
    String base64Creds = new String(base64CredsBytes);

    String idmToken = getIdmToken();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.set(ItsmaInstallerConstants.X_AUTH_TOKEN, idmToken);
    headers.add("Authorization", "Basic " + base64Creds);

    HttpEntity<String> entity = new HttpEntity<>(headers);
    try {
      // send request and parse result
      RestTemplate restTemplate = new HttpsClientTemplate().getRestTemplate();
      ResponseEntity<String> response = restTemplate.exchange(url + pathSigningKey, HttpMethod.GET, entity, String.class);
      if (response.getStatusCode() != HttpStatus.OK) {
        String message = String.format("===GetIdmSigningKey with (url=%s): Failed. HttpResponseCode: %s, HttpResponseBody: %s",
          url + pathSigningKey, response.getStatusCode(), response.getBody());
        logger.error(message);

        throw new Exception(message);
      }

      String signingKey = response.getBody();
      logger.info("===GetIdmSigningKey: Success. signingKey: " + signingKey);
      Gson gson = new Gson();
      return gson.fromJson(signingKey, IdmSigningKey.class);
    } catch (Exception ex) {
      String message = String.format("===GetIdmSigningKey with (url=%s): Failed. Exception Message:: %s",
        url, ex.getMessage());
      logger.error(message);
      throw ex;
    }
  }

  /**
   * Get a new CRSF token from SharedService
   * Request URL: http://host:port/urest/v1.1/csrf-token
   *
   * @return a json string e.g {"csrfToken": "ad6b2644-107b-4b7d-b60a-1f35d7d0f235"}
   * @throws Exception
   */
  public Map<String, String> getSecurity(boolean refresh) throws Exception {
    if (security.containsKey(ItsmaInstallerConstants.X_CSRF_TOKEN) &&
      security.containsKey(ItsmaInstallerConstants.X_AUTH_TOKEN) &&
      security.containsKey(ItsmaInstallerConstants.JSESSIONID) && !refresh) {
      return security;
    }

    security.clear();

    String url = runtimeProfile.getSs_base_url() + "/csrf-token";
    // set headers
    String idmAuthToken = getIdmToken();
    HttpHeaders headers = new HttpHeaders();
    headers.add(ItsmaInstallerConstants.X_AUTH_TOKEN, idmAuthToken);

    logger.debug("Send request of Getting CSRF token to SS at: " + url + " with headers: " + headers);

    HttpEntity entity = new HttpEntity(headers);
    ResponseEntity<String> response = sendRequestToSS(url, HttpMethod.GET, entity, true);
    JSONObject jsonObject = new JSONObject(response.getBody());
    logger.debug("Got CSRF token: " + jsonObject);

    String csrfToken = jsonObject.getString("csrfToken");
    if (csrfToken == null || csrfToken.isEmpty())
      throw new Exception("Invalid csrf token from SS.");

    security.put(ItsmaInstallerConstants.X_CSRF_TOKEN, csrfToken);
    security.put(ItsmaInstallerConstants.X_AUTH_TOKEN, idmAuthToken);

    String cookie = response.getHeaders().get("Set-Cookie").get(0);
    if (cookie != null && !cookie.isEmpty()) {
      String jsessionId = cookie.substring(cookie.indexOf("JSESSIONID="), cookie.indexOf(";"));
      if (jsessionId != null && !jsessionId.isEmpty()) {
        logger.debug("Got jsessionId: " + jsessionId);
        security.put(ItsmaInstallerConstants.JSESSIONID, jsessionId);
      } else {
        throw new Exception("Cannot get sessionId from SS.");
      }
    } else {
      throw new Exception("Cannot get sessionId from SS.");
    }

    return security;
  }

  public String createDeployment() throws Exception {
    String url = runtimeProfile.getSs_base_url() + "/deployment/";

    HttpHeaders headers = newHttpHeaders();
    HttpEntity entity = new HttpEntity(headers);

    String responseBody = sendRequestToSS(url, HttpMethod.POST, entity, false).getBody();
    JSONObject jsonObject = new JSONObject(responseBody);
    logger.debug("Got deployment: " + jsonObject);

    String uuid = jsonObject.getString("uuid");
    if (uuid == null || uuid.isEmpty())
      throw new Exception("Invalid uuid from SS.");

    return uuid;
  }

  public String createFeature(String deploymentUuid) throws Exception {
    String url = runtimeProfile.getSs_base_url() + "/deployment/" + deploymentUuid + "/feature";

    HttpHeaders headers = newHttpHeaders();

    String requestBody = "{\n" +
      "  \"allow_custom_selection\": true,\n" +
      "  \"description\": \"string\",\n" +
      "  \"editions\": [\n" +
      "    {\n" +
      "      \"description\": \"string\",\n" +
      "      \"display\": true,\n" +
      "      \"has_feature_sets\": [\n" +
      "        \"string\"\n" +
      "      ],\n" +
      "      \"id\": \"string\",\n" +
      "      \"id_defined_in_autopass\": \"string\",\n" +
      "      \"name\": \"string\",\n" +
      "      \"selected\": true\n" +
      "    }\n" +
      "  ],\n" +
      "  \"feature_sets\": [\n" +
      "    {\n" +
      "      \"description\": \"string\",\n" +
      "      \"display\": \"string\",\n" +
      "      \"has_features\": [\n" +
      "        \"string\"\n" +
      "      ],\n" +
      "      \"id\": \"string\",\n" +
      "      \"name\": \"string\",\n" +
      "      \"selected\": true\n" +
      "    }\n" +
      "  ],\n" +
      "  \"features\": [\n" +
      "    {\n" +
      "      \"also_select\": [\n" +
      "        \"string\"\n" +
      "      ],\n" +
      "      \"description\": \"string\",\n" +
      "      \"display\": true,\n" +
      "      \"id\": \"string\",\n" +
      "      \"name\": \"string\",\n" +
      "      \"selected\": true\n" +
      "    }\n" +
      "  ],\n" +
      "  \"i18n\": [\n" +
      "    \"string\"\n" +
      "  ],\n" +
      "  \"images\": [\n" +
      "    {\n" +
      "      \"image\": \"string\"\n" +
      "    }\n" +
      "  ],\n" +
      "  \"initial_edition_selected\": \"string\",\n" +
      "  \"suite\": \"itsma\",\n" +
      "  \"version\": \"string\"\n" +
      "}";
    HttpEntity<String> entity = new HttpEntity(requestBody, headers);

    return sendRequestToSS(url, HttpMethod.POST, entity, false).getBody();
  }

  public String suiteConfig(String deploymentUuid, String namespace) throws Exception {
    logger.info("In suiteConfig... deploymentUuid: " + deploymentUuid);
    String url = runtimeProfile.getSs_base_url() + "/deployment/" + deploymentUuid + "/suite-configuration";

    HttpHeaders headers = newHttpHeaders();

    String requestBody = "{\n" +
      "    \"labelKey\": \"Worker\",\n" +
      "    \"labelValue\": \"label\",\n" +
      "    \"namespace\": \"" + namespace + "\",\n" +
      "    \"suite\": \"itsma\",\n" +
      "    \"version\": \"2017.07\" \n" +
      "}";

    HttpEntity<String> entity = new HttpEntity(requestBody, headers);

    return sendRequestToSS(url, HttpMethod.POST, entity, false).getBody();
  }

  public String createKubeResource(String deploymentUuid, String yaml) throws Exception {
    logger.info("In createKubeResource... deploymentUuid: " + deploymentUuid + "  yaml: " + yaml);
    String url = runtimeProfile.getSs_base_url() + "/deployment/" + deploymentUuid + "/installation";

    LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

    FileSystemResource fileSystemResource = new FileSystemResource(yaml);
    if (!fileSystemResource.exists()) {
      logger.error(yaml + " does NOT exist.");
      throw new FileNotFoundException(yaml + " does NOT exist.");
    }
    map.add("yamls", fileSystemResource);

    HttpHeaders headers = newHttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
    long startTime = System.currentTimeMillis();
    String resp = sendRequestToSS(url, HttpMethod.POST, entity, false).getBody();
    long endTime = System.currentTimeMillis();
    long duration = (endTime - startTime);
    logger.info("SS response(takes " + duration + " ms) of create yaml " + yaml + ": " + resp);

    return resp;
  }

  public String createKubeResources(String deploymentUuid, ArrayList<String> yamls) throws Exception {
    logger.info("In createKubeResources... deploymentUuid: " + deploymentUuid + "  yamls: " + yamls);
    String url = runtimeProfile.getSs_base_url() + "/deployment/" + deploymentUuid + "/installation";

    LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    for (String yaml : yamls) {
      FileSystemResource fileSystemResource = new FileSystemResource(yaml);
      if (!fileSystemResource.exists()) {
        logger.error(yaml + " does NOT exist.");
        throw new FileNotFoundException(yaml + " does NOT exist.");
      }
      map.add("yamls", fileSystemResource);
    }

    HttpHeaders headers = newHttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);

    return sendRequestToSS(url, HttpMethod.POST, entity, false).getBody();
  }

  public <T> T unmarshallJson(String json, Class<T> classOfT) throws Exception {
    logger.debug("unmarshallJson JSON: " + json);
    Gson gson = new Gson();
    Object object = gson.fromJson(json, (Type) classOfT);

    return Primitives.wrap(classOfT).cast(object);
  }

  public KubeResourceStatus getPodStatusByName(String deploymentUuid, String podName) throws Exception {
    logger.info("In getPodStatusByName... podName: " + podName);

    String url = runtimeProfile.getSs_base_url() + "/deployment/" + deploymentUuid + "/k8s-instance/pods/" + podName;

    HttpHeaders headers = newHttpHeaders();

    String jsonBody = "{\"action\": \"status\"}";
    HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

    try {
      long startTime = System.currentTimeMillis();
      String resp = sendRequestToSS(url, HttpMethod.POST, entity, false).getBody();
      long endTime = System.currentTimeMillis();
      long duration = (endTime - startTime);
      logger.info("SS response(takes " + duration + " ms) of getting pod " + podName + " status: " + resp);

      BatchInfo batchInfo = unmarshallJson(resp, BatchInfo.class);
      KubeInstanceInfo.K8sResponseBody.Status status = batchInfo.getKubeInstanceInfoList()
        .get(0)
        .unmarshallResponseBody()
        .getStatus();
      String phase = status
        .getPhase();
      Optional<KubeInstanceInfo.K8sResponseBody.Status.Condition> readyOption = status.getConditions()
        .stream()
        .filter(
          condition -> condition.getType().equals("Ready")
        )
        .findFirst();
      Boolean podReady = null;
      if (phase.equalsIgnoreCase("Running")) {
        if (readyOption.isPresent() && readyOption.get().getStatus() != null) {
          podReady = Boolean.parseBoolean(readyOption.get().getStatus().toLowerCase());
          if (podReady) {
            return new KubeResourceStatus("Pod", podName, phase, true, "/itsma/kube_pods/" + podName);
          }
        }
        return new KubeResourceStatus("Pod", podName, "Failed", null, "/itsma/kube_pods/" + podName);
      }

      return new KubeResourceStatus("Pod", podName, phase, null, "/itsma/kube_pods/" + podName);
    } catch (HttpClientErrorException e) {
      if (e.getRawStatusCode() == 404) {
        logger.warn("Pod " + podName + " not found.");
        //throw new K8sResourceNotFoundException("Pod " + podName + " not found.");
        return new KubeResourceStatus("Pod", podName, "Pending", null, "/itsma/kube_pods/" + podName);
      }
      throw e;
    }
  }

  public String getAllPodStatusInService(String deploymentUuid, String serviceName) throws Exception {
    logger.info("In getAllPodStatusInService... serviceName: " + serviceName);

    String url = runtimeProfile.getSs_base_url() + "/deployment/" + deploymentUuid + "/k8s-instance/services/" + serviceName + "/pods";

    HttpHeaders headers = newHttpHeaders();

    String jsonBody = "{\"action\": \"status\"}";
    HttpEntity<String> entity = new HttpEntity<String>(jsonBody, headers);

    try {
      long startTime = System.currentTimeMillis();
      String resp = sendRequestToSS(url, HttpMethod.POST, entity, false).getBody();
      long endTime = System.currentTimeMillis();
      long duration = (endTime - startTime);
      logger.info("SS response(takes " + duration + " ms) of getting svc " + serviceName + " status: " + resp);
      return resp;
    } catch (HttpClientErrorException e) {
      if (e.getRawStatusCode() == 404) {
        logger.warn("Service " + serviceName + " not found.");
        throw new K8sResourceNotFoundException("Service " + serviceName + " not found.");
      }
      throw e;
    }
  }

  public String getResourceStatus(String deploymentUuid, String installationId) throws Exception {
    logger.info("In getResourceStatus... deploymentUuid: " + deploymentUuid + " installationId: " + installationId);
    String url = runtimeProfile.getSs_base_url() + "/deployment/" + deploymentUuid + "/installation/" + installationId;

    HttpHeaders headers = newHttpHeaders();
    HttpEntity entity = new HttpEntity(headers);

    long startTime = System.currentTimeMillis();
    String resp = sendRequestToSS(url, HttpMethod.GET, entity, false).getBody();
    long endTime = System.currentTimeMillis();
    long duration = (endTime - startTime);
    logger.info("SS response(takes " + duration + " ms) of getting installationid " + installationId + " status: " + resp);
    return resp;
  }

  public String getConfiguration(String deploymentUuid, String key) throws Exception {
    logger.info("In getConfigurations... deploymentUuid: " + deploymentUuid + " key: " + key);

    String url = runtimeProfile.getSs_base_url() + "/deployment/" + deploymentUuid + "/configuration";
    if (key != null && !key.isEmpty())
      url = url + "/" + key;

    HttpHeaders headers = newHttpHeaders();
    HttpEntity entity = new HttpEntity(headers);
    return sendRequestToSS(url, HttpMethod.GET, entity, false).getBody();
  }

  public String createConfiguration(String deploymentUuid, String section, Map<String, Object> properties) throws Exception {
    logger.info("In createConfiguration... deploymentUuid: " + deploymentUuid + " section: " + section + " properties: " + properties);

    String url = runtimeProfile.getSs_base_url() + "/deployment/" + deploymentUuid + "/configuration";

    HttpHeaders headers = newHttpHeaders();

    JSONObject applied = new JSONObject();
    applied.put("applied", properties);
    JSONArray configuration = new JSONArray();
    JSONObject conf = new JSONObject();
    conf.put("key", section);
    conf.put("value", applied);
    configuration.put(conf);

    String jsonBody = new JSONObject().put("configuration", configuration).toString();
    logger.debug("save configuration to etcd: " + jsonBody);

    HttpEntity<String> entity = new HttpEntity<String>(jsonBody, headers);
    return sendRequestToSS(url, HttpMethod.POST, entity, false).getBody();
  }

  public String getAppRoleId(String deploymentUuid, JSONArray appRoles, String namespace) throws Exception {
    logger.debug("In getAppRoleId... deploymentUuid: " + deploymentUuid + " appRole: " + appRoles);

    String url = runtimeProfile.getSs_base_url() + "/deployment/" + deploymentUuid + "/vault/approles";

    HttpHeaders headers = newHttpHeaders();

    JSONObject jsonBody = new JSONObject();
    jsonBody.put("appRoleList", appRoles);
    jsonBody.put("vaultNamespace", namespace);

    HttpEntity<String> entity = new HttpEntity<String>(jsonBody.toString(), headers);
    return sendRequestToSS(url, HttpMethod.POST, entity, false).getBody();
  }

  public void saveAppRoleSecret(String deploymentUuid, String namespace, JSONArray secrets) throws Exception {
    logger.debug("In saveAppRoleSecret... deploymentUuid: " + deploymentUuid + " secrets: " + secrets);

    String url = runtimeProfile.getSs_base_url() + "/deployment/" + deploymentUuid + "/vault/secrets";

    HttpHeaders headers = newHttpHeaders();
    JSONObject jsonBody = new JSONObject();
    jsonBody.put("secrets", secrets);
    jsonBody.put("vaultNamespace", namespace);

    HttpEntity<String> entity = new HttpEntity<String>(jsonBody.toString(), headers);
    sendRequestToSS(url, HttpMethod.POST, entity, false);
  }

  public Map<String, Object> updateExtendAppRoles(String deploymentUuid, String namespace, JSONArray appRoleList) throws Exception {
    logger.debug("In updateExtendAppRoles... deploymentUuid: " + deploymentUuid + " appRoleList: " + appRoleList);

    String url = runtimeProfile.getSs_base_url() + "/deployment/" + deploymentUuid + "/vault/extendapproles";

    HttpHeaders headers = newHttpHeaders();
    JSONObject jsonBody = new JSONObject();
    jsonBody.put("appRoleExtendList", appRoleList);
    jsonBody.put("vaultNamespace", namespace);

    logger.debug("updateExtendAppRoles: " + jsonBody.toString());
    HttpEntity<String> entity = new HttpEntity<String>(jsonBody.toString(), headers);
    ResponseEntity<String> response = sendRequestToSS(url, HttpMethod.POST, entity, false);
    JSONObject jsonObject = new JSONObject(response.getBody());
    return jsonObject.toMap();
  }

  public void updateDeploymentStatus(String deploymentUuid, String deploymentStatus) {
    logger.debug("Change the status of a deployment. deploymentUuid: " + deploymentUuid + ", deploymentStatus: " + deploymentStatus);

    String url = runtimeProfile.getSs_base_url() + "/deployment/" + deploymentUuid + "/?deploymentStatus=" + deploymentStatus;

    try {
      HttpHeaders headers = newHttpHeaders();

      HttpEntity<String> entity = new HttpEntity<String>(headers);
      sendRequestToSS(url, HttpMethod.PUT, entity, false);
    } catch (Exception ex) {
      logger.error("Fail to change the status of a deployment to " + deploymentStatus);
    }
  }

  public HttpHeaders newHttpHeaders() throws Exception {
    Map<String, String> security = getSecurity(false);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add(ItsmaInstallerConstants.X_AUTH_TOKEN, security.get(ItsmaInstallerConstants.X_AUTH_TOKEN));
    headers.add(ItsmaInstallerConstants.X_CSRF_TOKEN, security.get(ItsmaInstallerConstants.X_CSRF_TOKEN));
    headers.add(ItsmaInstallerConstants.COOKIE, security.get(ItsmaInstallerConstants.JSESSIONID));

    return headers;
  }

  public Map<String, Object> createPV(String deploymentUuid, String volumeBody) {
    JSONObject jsonObject = null;
    try {
      logger.debug("In createPV... volumeBody: " + volumeBody);

      String url = runtimeProfile.getSs_base_url() + "/deployment/" + deploymentUuid + "/volumes";
      HttpHeaders headers = newHttpHeaders();
      HttpEntity<String> entity = new HttpEntity<String>(volumeBody, headers);
      ResponseEntity<String> response = sendRequestToSS(url, HttpMethod.POST, entity, false);
      jsonObject = new JSONObject(response.getBody());
    } catch (Exception ex) {
      logger.error("Exception happened when create deployment persistent volume, " + ex);
    }
    return (Map<String, Object>) jsonObject.toMap();
  }

  public Map<String, Object> getPVStatus(String deploymentUuid, String volumeName) {
    JSONObject jsonObject = null;
    try {
      logger.debug("In getPVStatus... volumeName: " + volumeName);

      String url = runtimeProfile.getSs_base_url() + "/deployment/" + deploymentUuid + "/volumes/" + volumeName + "/status";
      HttpHeaders headers = newHttpHeaders();
      HttpEntity<String> entity = new HttpEntity<String>(headers);
      ResponseEntity<String> response = sendRequestToSS(url, HttpMethod.GET, entity, true);
      jsonObject = new JSONObject(response.getBody());
      logger.debug("Get the pv status: " + jsonObject);
    } catch (Exception ex) {
      logger.error("Exception happened when get pv status, " + ex);
    }
    return (Map<String, Object>) jsonObject.toMap();
  }

  public List<SuiteDeploymentInfo> getSuiteDeploymentInfo(String deploymentStatus) throws Exception {
    try{
      String url;
      if (ItsmaUtil.isNullOrEmpty(deploymentStatus)) {
        url = runtimeProfile.getSs_base_url() + "/deployment";
      } else {
        url = runtimeProfile.getSs_base_url() + "/deployment?deploymentStatus=" + deploymentStatus;
      }
      HttpHeaders headers = newHttpHeaders();
      HttpEntity<String> entity = new HttpEntity<String>(headers);
      ResponseEntity<String> response = sendRequestToSS(url, HttpMethod.GET, entity, true);
      Gson gson = new Gson();
      List<SuiteDeploymentInfo> suiteDeploymentInfos = gson.fromJson(response.getBody(), new TypeToken<List < SuiteDeploymentInfo >>(){}.getType());
      logger.debug("===Response of getSuiteDeploymentInfo: " + suiteDeploymentInfos);
      return suiteDeploymentInfos;
    }catch (Exception ex){
      logger.error("===getSuiteDeploymentInfo: Failed. Exception: " + ex.getMessage());
      throw ex;
    }
  }

  public void registerAutoPassUrl() throws Exception {
    String url = runtimeProfile.getSs_base_url() + "/license-manager/" + runtimeProfile.getDeploymentUuid() + "/instanceUrl";

    HttpHeaders headers = newHttpHeaders();
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("ip", runtimeProfile.getIngress_fqdn());
    jsonObject.put("path", "autopass");
    jsonObject.put("port", "443");
    jsonObject.put("protocol", "https");

    HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);
    try {
      ResponseEntity<String> responseEntity = sendRequestToSS(url, HttpMethod.POST, entity, true);
      if (responseEntity.getStatusCode() != HttpStatus.CREATED) {
        logger.warn(String.format("===RegisterAutoPassUrl with(url=%s, header=%s, body=%s): Failed. HttpResponseCode: %s, HttpResponseBody: %s",
          url, headers, jsonObject.toString(), responseEntity.getStatusCode(), responseEntity.getBody()));
      } else {
        logger.info("===RegisterAutoPassUrl: Success.");
      }
    } catch (Exception e) {
      logger.warn(String.format("===RegisterAutoPassUrl with(url=%s, header=%s, body=%s): Failed. Exception happened. ExceptionMsg: %s",
        url, headers, jsonObject.toString(), e.getMessage()));
    }
  }

  public ResponseEntity<String> sendRequestToSS(String url, HttpMethod httpMethod, HttpEntity entity, boolean quiet) throws Exception {
    if (!quiet) {
      logger.info(String.format("===SendRequestToSS with (url=%s httpMethod=%s headers=%s body=%s)...",
        url, httpMethod, entity.getHeaders(), entity.getBody()));
    }

    try {
      // send request and parse result
      ResponseEntity<String> response = new RestTemplate().exchange(url, httpMethod, entity, String.class);
      if (response.getStatusCode() != HttpStatus.CREATED && response.getStatusCode() != HttpStatus.OK) {
        String message = String.format("===SendRequestToSS with (url=%s): Failed. HttpResponseCode: %s, HttpResponseBody: %s",
          url, response.getStatusCode(), response.getBody());
        logger.error(message);

        throw new Exception(message);
      }
      if (!quiet) {
        logger.info(String.format("===SendRequestToSS with (url=%s): Success. HttpResponseCode: %s, HttpResponseBody: %s",
          url, response.getStatusCode(), response.getBody()));
      }
      return response;
    } catch (HttpClientErrorException | HttpServerErrorException e) {
      if (e.getRawStatusCode() == 403) {
        // It might be caused by token expired, refresh token and try again
        logger.warn(String.format("===SendRequestToSS with (url=%s): Failed. HttpResponseCode: 403. Refresh token and try again...", url));
        Map<String, String> security = getSecurity(true);
        MultiValueMap headers = new LinkedMultiValueMap<String, String>();
        headers.putAll(entity.getHeaders());
        updateSecurityHeader(headers, security);

        HttpEntity refreshedEntity = new HttpEntity(entity.getBody(), headers);
        try {
          logger.info(String.format("===RetrySendRequestToSS with (url=%s, httpMethod=%s, headers=%s body=%s)...",
            url, httpMethod, refreshedEntity.getHeaders(), refreshedEntity.getBody()));
          ResponseEntity<String> response = new RestTemplate().exchange(url, httpMethod, refreshedEntity, String.class);
          if (response.getStatusCode() != HttpStatus.CREATED && response.getStatusCode() != HttpStatus.OK) {
            String message = String.format("===RetrySendRequestToSS with (url=%s): Failed. HttpResponseCode: %s, HttpResponseBody: %s",
              url, response.getStatusCode(), response.getBody());
            logger.error(message);
            throw new Exception(message);
          }
          if (!quiet) {
            logger.info(String.format("===RetrySendRequestToSS with (url=%s): Success. HttpResponseCode: %s, HttpResponseBody: %s",
              url, response.getStatusCode(), response.getBody()));
          }
          return response;
        } catch (HttpClientErrorException | HttpServerErrorException ee) {
          String message = String.format("===RetrySendRequestToSS with (url=%s): Failed. ExceptionRowStatusCode: %s, ExceptionResponseBody: %s",
            url, ee.getRawStatusCode(), ee.getResponseBodyAsString());
          logger.error(message);
          throw new Exception(message);
        }
      } else {
        String message = String.format("===SendRequestToSS with (url=%s): Failed. ExceptionRowStatusCode: %s, ExceptionResponseBody: %s",
          url, e.getRawStatusCode(), e.getResponseBodyAsString());
        logger.error(message);
        throw new Exception(message);
      }
    } catch (Exception ex) {
      String message = String.format("===SendRequestToSS with (url=%s): Failed. Exception Message:: %s",
        url, ex.getMessage());
      logger.error(message);
      throw ex;
    }
  }

  public void updateSecurityHeader(MultiValueMap header, Map<String, String> security) {
    if (header.containsKey(ItsmaInstallerConstants.X_AUTH_TOKEN)) {
      header.remove(ItsmaInstallerConstants.X_AUTH_TOKEN);
    }
    header.add(ItsmaInstallerConstants.X_AUTH_TOKEN, security.get(ItsmaInstallerConstants.X_AUTH_TOKEN));

    if (header.containsKey(ItsmaInstallerConstants.X_CSRF_TOKEN)) {
      header.remove(ItsmaInstallerConstants.X_CSRF_TOKEN);
    }
    header.add(ItsmaInstallerConstants.X_CSRF_TOKEN, security.get(ItsmaInstallerConstants.X_CSRF_TOKEN));

    if (header.containsKey(ItsmaInstallerConstants.COOKIE)) {
      header.remove(ItsmaInstallerConstants.COOKIE);
    }
    header.add(ItsmaInstallerConstants.COOKIE, security.get(ItsmaInstallerConstants.JSESSIONID));
  }
}