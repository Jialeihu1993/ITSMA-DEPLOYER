package com.hpe.itsma.itsmaInstaller.service;

import com.google.gson.Gson;
import com.hpe.itsma.itsmaInstaller.bean.IdmSigningKey;
import com.hpe.itsma.itsmaInstaller.bean.ItsmaProduct;
import com.hpe.itsma.itsmaInstaller.bean.SSAppRoleResponseBody;
import com.hpe.itsma.itsmaInstaller.constants.ItsmaInstallerConstants;
import com.hpe.itsma.itsmaInstaller.exception.VaultSecretValueEmptyException;
import com.hpe.itsma.itsmaInstaller.util.ItsmaUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * The class to do actions about Vault.
 *
 * @author zhongtao
 * @since 8/1/2017.
 */
@Service
public class VaultService {
  private static Log logger = LogFactory.getLog(VaultService.class);

  @Autowired
  private ProductsService productsService;

  @Autowired
  private SSRestClient ssRestClient;

  /**
   * Update the secret value of the specified secret key of the specified service.
   *
   * if no service is specified, traverse all services
   *
   * @param productName
   * @param secretKey
   * @param secretValue
   */
  public void updateVaultSecretValue(String productName, String secretKey, String secretValue) throws Exception {
    if (secretValue == null || secretValue.isEmpty()) {
      throw new VaultSecretValueEmptyException("The vault does not allow empty secret value.");
    }

    if (productName != null && !productName.isEmpty()) {
      ItsmaProduct product = productsService.getItsmaProduct(productName);
      if (product != null) {
        logger.info("Update the vault secret: " + secretKey + " of service: " + productName);
        ItsmaProduct.Vault vault = product.getVault();
        if (vault != null) {
          List<ItsmaProduct.Vault.Secret> secrets = vault.getSecrets();
          if (secrets != null) {
            Optional<ItsmaProduct.Vault.Secret> optionalSecret = secrets.stream().filter(
              secret -> secret.getSecretKey().equalsIgnoreCase(secretKey)
            ).findFirst();

            if (optionalSecret.isPresent()) {
              optionalSecret.get().setSecretValue(secretValue);
            }
          }
        }
      }
    } else {
      productsService.getItsmaProducts().stream().forEach(
        itsmaProduct -> {
          try {
            updateVaultSecretValue(itsmaProduct.getName(), secretKey, secretValue);
          } catch (Exception e) {
            logger.warn("Should never be here...");
          }
        }
      );
    }
  }

  public Map<String, String> saveSecret(String deploymentuuid, Map<String, Object> properties) throws Exception {
    Map<String, String> appRoleAndIds = new HashMap<>();
    String installType = properties.get(ItsmaInstallerConstants.ITOM_SUITE_INSTALL_TYPE).toString();
    String suiteMode=ItsmaUtil.getProperty(properties,ItsmaInstallerConstants.ITOM_SUITE_MODE).toString();
    String namespace = (String) properties.get(ItsmaInstallerConstants.SUITE_NAMESPACE);
    updateVaultSecretValue(
            null,
            "itom_itsma_sysadmin_password_secret_key",
            (String) properties.get("sysadmin_password")
    );

    Object value = properties.get("sm_internal_db");
    String smEngine = properties.get("sm_db_engine").toString();
    if (value != null && !(Boolean) value) {
      if (smEngine.equalsIgnoreCase("Postgres")&&
              !(installType.equalsIgnoreCase(ItsmaInstallerConstants.INSTALL_FROM_BACKUP)&&suiteMode.equalsIgnoreCase(ItsmaInstallerConstants.ITOM_H_MODE))) {
        updateVaultSecretValue(
                "itom-sm",
                "itom_itsma_dba_password_secret_key",
                (String) properties.get("sm_dba_password")
        );
      }
      updateVaultSecretValue(
              "itom-sm",
              "itom_itsma_db_password_secret_key",
              (String) properties.get("sm_db_password")
      );
      updateVaultSecretValue(
              "itom-chat",
              "itom_itsma_db_password_secret_key",
              (String) properties.get("sm_db_password")
      );
      updateVaultSecretValue(
              "itom-chat",
              "sm_database_password_secret_key",
              (String) properties.get("sm_db_password")
      );
    }

    value = properties.get("ucmdb_internal_db");
    String ucmdbEngine = properties.get("ucmdb_db_engine").toString();
    if (value != null && !(Boolean) value) {
      if(ucmdbEngine.equalsIgnoreCase("Postgres")&&
              !(installType.equalsIgnoreCase(ItsmaInstallerConstants.INSTALL_FROM_BACKUP)&&suiteMode.equalsIgnoreCase(ItsmaInstallerConstants.ITOM_H_MODE))){
        updateVaultSecretValue(
                "itom-cmdb",
                "itom_itsma_dba_password_secret_key",
                (String) properties.get("ucmdb_dba_password")
        );
      }
      updateVaultSecretValue(
              "itom-cmdb",
              "itom_itsma_db_password_secret_key",
              (String) properties.get("ucmdb_db_password")
      );
    }

    value = properties.get("sp_internal_db");
    if (value != null && !(Boolean) value) {
      if(!(installType.equalsIgnoreCase(ItsmaInstallerConstants.INSTALL_FROM_BACKUP)&&suiteMode.equalsIgnoreCase(ItsmaInstallerConstants.ITOM_H_MODE))){
        updateVaultSecretValue(
                "itom-service-portal",
                "itom_itsma_dba_password_secret_key",
                (String) properties.get("sp_dba_password")
        );
      }else {
        updateVaultSecretValue(
                "itom-service-portal",
                "itom_itsma_db_password_secret_key",
                (String) properties.get("sp_db_password")
        );
      }
    }

    value = properties.get("xservices_internal_db");
    if (value != null && !(Boolean) value) {
      if(!(installType.equalsIgnoreCase(ItsmaInstallerConstants.INSTALL_FROM_BACKUP)&&suiteMode.equalsIgnoreCase(ItsmaInstallerConstants.ITOM_H_MODE))){
        updateVaultSecretValue(
                "itom-xservices",
                "itom_itsma_dba_password_secret_key",
                (String) properties.get("xservices_dba_password")
        );
        updateVaultSecretValue(
                "itom-xservices-infra",
                "itom_itsma_dba_password_secret_key",
                (String) properties.get("xservices_dba_password")
        );
      }else{
        updateVaultSecretValue(
                "itom-xservices",
                "itom_itsma_db_password_secret_key",
                (String) properties.get("xservices_db_password")
        );
        updateVaultSecretValue(
                "itom-xservices-infra",
                "itom_itsma_db_password_secret_key",
                (String) properties.get("xservices_db_password")
        );
      }
    }

    value = properties.get("xruntime_internal_db");
    if (value != null && !(Boolean) value) {
      if(!(installType.equalsIgnoreCase(ItsmaInstallerConstants.INSTALL_FROM_BACKUP)&&suiteMode.equalsIgnoreCase(ItsmaInstallerConstants.ITOM_H_MODE))){
        updateVaultSecretValue(
                "itom-xruntime",
                "itom_itsma_dba_password_secret_key",
                (String) properties.get("xruntime_dba_password")
        );
        updateVaultSecretValue(
                "itom-xruntime-infra",
                "itom_itsma_dba_password_secret_key",
                (String) properties.get("xruntime_dba_password")
        );
      }else {
        updateVaultSecretValue(
                "itom-xruntime",
                "itom_itsma_db_password_secret_key",
                (String) properties.get("xruntime_db_password")
        );
        updateVaultSecretValue(
                "itom-xruntime-infra",
                "itom_itsma_db_password_secret_key",
                (String) properties.get("xruntime_db_password")
        );
      }
    }

    value = properties.get("bo_internal_db");
    if (value != null && !(Boolean) value) {
      if(!(installType.equalsIgnoreCase(ItsmaInstallerConstants.INSTALL_FROM_BACKUP)&&suiteMode.equalsIgnoreCase(ItsmaInstallerConstants.ITOM_H_MODE))){
        updateVaultSecretValue(
                "itom-bo",
                "itom_itsma_dba_password_secret_key",
                (String) properties.get("bo_dba_password")
        );
      }else{
        updateVaultSecretValue(
                "itom-bo",
                "itom_itsma_db_password_secret_key",
                (String) properties.get("bo_db_password")
        );
      }

    }

    value = properties.get("smarta_internal_db");
    if(value != null && !(Boolean) value){
      if(!(installType.equalsIgnoreCase(ItsmaInstallerConstants.INSTALL_FROM_BACKUP)&&suiteMode.equalsIgnoreCase(ItsmaInstallerConstants.ITOM_H_MODE))){
        updateVaultSecretValue(
                "itom-smartanalytics",
                "itom_itsma_dba_password_secret_key",
                (String) properties.get("smarta_dba_password")
        );
      }else{
        updateVaultSecretValue(
                "itom-smartanalytics",
                "itom_itsma_db_password_secret_key",
                (String) properties.get("smarta_db_password")
        );
      }
    }

    value = properties.get("idm_internal_db");
    if (value != null && !(Boolean) value) {
      if(!(installType.equalsIgnoreCase(ItsmaInstallerConstants.INSTALL_FROM_BACKUP)&&suiteMode.equalsIgnoreCase(ItsmaInstallerConstants.ITOM_H_MODE))){
        updateVaultSecretValue(
                "itom-idm",
                "itom_itsma_dba_password_secret_key",
                (String) properties.get("idm_dba_password")
        );
      }
      updateVaultSecretValue(
              "itom-idm",
              "itom_itsma_db_password_secret_key",
              (String) properties.get("idm_db_password")
      );
    }

    //save ldap_bind_user_password to vault
    Object externalLdap = properties.get("external_ldap");
    if (externalLdap != null && (Boolean) externalLdap) {
      value = properties.get("ldap_bind_user_password");
      if (value != null) {
        updateVaultSecretValue(null, "itom_itsma_openldap_root_secret_key", (String) value);
        updateVaultSecretValue(null, "itom_itsma_openldap_root_pwd_secret_key", (String) value);
      }
    }


    IdmSigningKey idmSigningKey = ssRestClient.getIdmSigningKey();
    updateVaultSecretValue("itom-autopass", "core_idm_signing_key", idmSigningKey.getValue());

    JSONArray appRoles = new JSONArray();
    for (ItsmaProduct product : productsService.getItsmaProducts()) {
      if (product.getVault() != null && product.getVault().isActive()) {
        String appRole = product.getName().toLowerCase();
        appRoles.put(new JSONObject().put("appRole", appRole));
      }
    }

    JSONArray secrets = new JSONArray();
    String response = ssRestClient.getAppRoleId(deploymentuuid, appRoles, namespace);
    Gson gson = new Gson();
    SSAppRoleResponseBody ssAppRoleResponseBody = gson.fromJson(response, SSAppRoleResponseBody.class);
    for (SSAppRoleResponseBody.AppRole appRole : ssAppRoleResponseBody.getAppRoleAndIdList()) {
      String appRoleName = appRole.getAppRole().toLowerCase();
      String appRoleId = appRole.getAppRoleId();
      logger.debug("SS return: appRole = " + appRoleName + " appRoleId = " + appRoleId);
      appRoleAndIds.put(appRoleName + "_vault_approle", appRoleName);
      appRoleAndIds.put(appRoleName + "_vault_approle_id", appRoleId);

      for (ItsmaProduct.Vault.Secret secret : productsService.getItsmaProduct(appRoleName).getVault().getSecrets()) {
        JSONObject secretJsonObject = new JSONObject();
        secretJsonObject.put("appRole", appRoleName);
        secretJsonObject.put("roleId", appRoleId);
        secretJsonObject.put("secretKey", secret.getSecretKey());
        secretJsonObject.put("secretValue", secret.getSecretValue());

        secrets.put(secretJsonObject);
      }
    }
    ssRestClient.saveAppRoleSecret(deploymentuuid, namespace, secrets);
    return appRoleAndIds;
  }

  /**
   * saveSecret save secrets for the given appRole and appRoleId
   *
   * @param namespace
   * @param deploymentuuid
   * @param appRole
   * @param appRoleId
   * @param secrets
   * @throws Exception
   */
  public void saveSecret(String namespace,
                         String deploymentuuid,
                         String appRole,
                         String appRoleId,
                         List<ItsmaProduct.Vault.Secret> secrets) throws Exception {
    JSONArray secretArray = new JSONArray();
    for (ItsmaProduct.Vault.Secret secret : secrets) {
      JSONObject secretJsonObject = new JSONObject();
      secretJsonObject.put("appRole", appRole);
      secretJsonObject.put("roleId", appRoleId);
      secretJsonObject.put("secretKey", secret.getSecretKey());
      secretJsonObject.put("secretValue", secret.getSecretValue());

      secretArray.put(secretJsonObject);
    }
    ssRestClient.saveAppRoleSecret(deploymentuuid, namespace, secretArray);
  }

  public void updateVaultExtendApproles(String deploymentuuid, Map<String, Object> properties) throws Exception {
    String namespace = (String) properties.get(ItsmaInstallerConstants.SUITE_NAMESPACE);

    List<String> appRoleNameList = new ArrayList<String>();
    for (ItsmaProduct product : productsService.getItsmaProducts()) {
      if (product.getVault() != null && product.getVault().isActive()) {
        String appRole = product.getName().toLowerCase();
        appRoleNameList.add(appRole);
      }
    }

    JSONArray appRoleExtendList = new JSONArray();
    for (String appRoleName : appRoleNameList) {
      ItsmaProduct.Vault vault = productsService.getItsmaProduct(appRoleName).getVault();
      if (!ItsmaUtil.isNullOrEmpty(vault) && (!ItsmaUtil.isNullOrEmpty(vault.getReadWriteRoles()) || !ItsmaUtil.isNullOrEmpty(vault.getReadOnlyRoles()))) {
        JSONObject appRoleJsonObject = new JSONObject();
        appRoleJsonObject.put("appRole", appRoleName);
        if (!ItsmaUtil.isNullOrEmpty(vault.getReadWriteRoles())) {
          //check whether the readwriteroles is valid/active
          String readwriterolesArr[] = vault.getReadWriteRoles().split(",");
          StringBuffer readwriteroles = new StringBuffer();
          for (int i = 0; i < readwriterolesArr.length; i++) {
            if (appRoleNameList.contains(readwriterolesArr[i])) {
              if (ItsmaUtil.isNullOrEmpty(readwriteroles)) {
                readwriteroles.append(readwriterolesArr[i]);
              } else {
                readwriteroles.append(", ").append(readwriterolesArr[i]);
              }
            } else {
              logger.warn("appRole " + readwriterolesArr[i] + " doesn't exist for readwriteroles. Ignore this appRole");
            }
          }
          appRoleJsonObject.put("readWriteRoles", readwriteroles.toString());
        }
        if (!ItsmaUtil.isNullOrEmpty(vault.getReadOnlyRoles())) {
          //check whether the readonlyroles is valid/active
          String readonlyrolesArr[] = vault.getReadOnlyRoles().split(",");
          StringBuffer readonlyroles = new StringBuffer();
          for (int i = 0; i < readonlyrolesArr.length; i++) {
            if (appRoleNameList.contains(readonlyrolesArr[i])) {
              if (ItsmaUtil.isNullOrEmpty(readonlyroles)) {
                readonlyroles.append(readonlyrolesArr[i]);
              } else {
                readonlyroles.append(", ").append(readonlyrolesArr[i]);
              }
            } else {
              logger.warn("appRole " + readonlyrolesArr[i] + " doesn't exist for readonlyroles. Ignore this appRole");
            }
          }
          appRoleJsonObject.put("readOnlyRoles", readonlyroles.toString());
        }
        appRoleExtendList.put(appRoleJsonObject);
      }
    }
    Map<String, Object> response = ssRestClient.updateExtendAppRoles(deploymentuuid, namespace, appRoleExtendList);
    List list = (List<Map<String, String>>) response.get("appRoleAndIdExtendList");
    for(int i = 0; i < list.size(); i++){
      Map<String, String> map = (Map<String, String>) list.get(i);
      String appRoleStatus = map.get("appRoleStatus");
      if(!appRoleStatus.equalsIgnoreCase("SUCCESS")){
        logger.error("Fail to update extend app roles.");
        throw new Exception("Fail to update extend app roles.");
      }
    }
  }
}
