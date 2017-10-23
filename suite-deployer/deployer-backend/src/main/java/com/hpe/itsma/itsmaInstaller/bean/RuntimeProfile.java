package com.hpe.itsma.itsmaInstaller.bean;

import com.hpe.itsma.itsmaInstaller.constants.ItsmaInstallerConstants;
import com.hpe.itsma.itsmaInstaller.service.SSRestClient;
import com.hpe.itsma.itsmaInstaller.util.BashCommand;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tianlib on 2/20/2017.
 */
@Component
public class RuntimeProfile {
  private static Log logger = LogFactory.getLog(RuntimeProfile.class);

  @Value("${standlone}")
  private boolean standlone;

  @Value("${namespace}")
  private String namespace;

  @Value("${timezone}")
  private String timezone;

  @Value("${nfs_server}")
  private String nfs_server;

  @Value("${global_nfs_expose}")
  private String global_nfs_expose;

  @Value("${db_nfs_expose}")
  private String db_nfs_expose;

  @Value("${smartanalytics_nfs_expose}")
  private String smartanalytics_nfs_expose;

  @Value("${itsma_admin_key}")
  private String itsma_admin_key;

  @Value("${itsma_admin_pwd}")
  private String itsma_admin_pwd;

  @Value("${suite_label_key}")
  private String suite_label_key;

  @Value("${suite_label_value}")
  private String suite_label_value;

  @Value("${suite_install_backend_url}")
  private String suite_install_backend_url;

  @Value("${suite_update_backend_url}")
  private String suite_update_backend_url;

  @Value("${suite_installer_host}")
  private String suite_installer_host;

  @Value("${suite_installer_port}")
  private String suite_installer_port;

  @Value("${backup_svc_url}")
  private String backup_svc_url;

  @Value("${ingress_fqdn}")
  private String ingress_fqdn;

  @Value("${ingress_host_crt}")
  private String ingress_host_crt;

  @Value("${ingress_host_key}")
  private String ingress_host_key;

  @Value("${builtin_config_path}")
  private String builtin_config_path;

  @Value("${itsma_services_path}")
  private String itsma_services_path;

  @Value("${default_registry_url}")
  private String default_registry_url;

  @Value("${suite_update_new_version}")
  private String suite_update_new_version;

  private String deploymentUuid = null;

  @Autowired
  private DeployerContext deployerContext;

  public boolean isStandlone() {
    return standlone;
  }

  public String getNamespace() {
    if (isStandlone()) {
      return namespace;
    } else {
      return getSysEnv(ItsmaInstallerConstants.SUITE_NAMESPACE);
    }
  }

  public String getTimezone() {
    if (isStandlone()) {
      return timezone;
    } else {
      return getSysEnv(ItsmaInstallerConstants.MASTERNODE_TIME_ZONE);
    }
  }

  public String getNfs_server() {
    return nfs_server;
  }

  public String getGlobal_nfs_expose() {
    return global_nfs_expose;
  }

  public String getDb_nfs_expose() {
    return db_nfs_expose;
  }

  public String getSmartanalytics_nfs_expose() {
    return smartanalytics_nfs_expose;
  }

  public String getItsma_admin_key() {
    if (isStandlone()) {
      return itsma_admin_key;
    } else {
      return getSysEnv(ItsmaInstallerConstants.IDM_ADMIN_KEY);
    }
  }

  public String getItsma_admin_pwd() {
    if (isStandlone()) {
      return itsma_admin_pwd;
    } else {
      try {
        // the password of itsma_admin has been stored in namespace 'core' in vault
        // if currently in namespace 'core', using: get_secret idm_itsma_admin_password
        // if currently in other namespace, using: get_secret idm_itsma_admin_password baseinfra core
        // when deployer doing update, it's in namespace 'itsmaX', else it's in namespace 'core'
        String password = "";
        String suiteInstallType = "";
        Map<String, Object> properties = deployerContext.getSuiteProperties();
        if(properties != null) {
          suiteInstallType = (String) properties.get("itom_suite_install_type");
        }
        if(suiteInstallType != null && suiteInstallType.equalsIgnoreCase("update")) {
          password = new BashCommand("get_secret " + getItsma_admin_key() + " baseinfra core").exec();
        } else {
          password = new BashCommand("get_secret " + getItsma_admin_key()).exec();
        }

        logger.debug("get_secret: " + password);
        return password.substring("PASS=".length());
      } catch (Exception e) {
        logger.error("Failed to get_secret!");
        e.printStackTrace();
        throw new RuntimeException("Failed to get_secret!");
      }
    }
  }

  public String getSuite_label_key() {
    if (isStandlone()) {
      return suite_label_key;
    } else {
      return getSysEnv(ItsmaInstallerConstants.SUITE_LABEL_KEY);
    }
  }

  public String getSuite_label_value() {
    if (isStandlone()) {
      return suite_label_value;
    } else {
      return getSysEnv(ItsmaInstallerConstants.SUITE_LABEL_VALUE);
    }
  }

  public String getSuite_install_backend_url() {
    return suite_install_backend_url;
  }

  public String getSuite_update_backend_url() {
    return suite_update_backend_url;
  }

  public String getSuite_installer_host() {
    if (isStandlone()) {
      return suite_installer_host;
    } else {
      return getSysEnv(ItsmaInstallerConstants.SUITE_INSTALLER_HOST);
    }
  }

  public String getSuite_installer_port() {
    if (isStandlone()) {
      return suite_installer_port;
    } else {
      return getSysEnv(ItsmaInstallerConstants.SUITE_INSTALLER_PORT);
    }
  }

  public String getBackup_svc_url() {
    if (isStandlone()) {
      return backup_svc_url;
    } else {
      backup_svc_url = "http://" + "itom-itsma-backup-svc." + this.getNamespace() + ".svc.cluster.local:8081";
      return backup_svc_url;
    }
  }

  public String getIngress_fqdn() {
    if (isStandlone()) {
      return ingress_fqdn;
    } else {
      return getSysEnv(ItsmaInstallerConstants.INGRESS_FQDN);
    }
  }

  public String getIngress_host_crt() {
    if (isStandlone()) {
      return ingress_host_crt;
    } else {
      try {
        return new BashCommand("base64 -w 0 /var/run/secrets/boostport.com/server.crt").exec();
      } catch (Exception e) {
        logger.error("Failed to get /var/run/secrets/boostport.com/server.crt");
        throw new RuntimeException("Failed to get /var/run/secrets/boostport.com/server.crt");
      }
    }
  }

  public String getIngress_host_key() {
    if (isStandlone()) {
      return ingress_host_key;
    } else {
      try {
        return new BashCommand("base64 -w 0 /var/run/secrets/boostport.com/server.key").exec();
      } catch (Exception e) {
        logger.error("Failed to get /var/run/secrets/boostport.com/server.key");
        throw new RuntimeException("Failed to get /var/run/secrets/boostport.com/server.key");
      }
    }
  }

  public String getDeploymentUuid() {
    if (deploymentUuid != null)
      return deploymentUuid;

    if (isStandlone()) {
      SSRestClient ss = new SSRestClient(this);
      try {
        deploymentUuid = ss.createDeployment();
        ss.createFeature(deploymentUuid);
        ss.suiteConfig(deploymentUuid, getNamespace());
        logger.debug("Got deploymentUuid: " + deploymentUuid);
        return deploymentUuid;
      } catch (Exception e) {
        logger.error("Failed to get deploymentuuid.");
        throw new RuntimeException("Failed to get deploymentuuid.");
      }
    } else {
      deploymentUuid = getSysEnv(ItsmaInstallerConstants.ITSMA_DEPLOYMENT_UUID);
      return deploymentUuid;
    }
  }

  public void setDeploymentUuid(String deploymentUuid) {
    this.deploymentUuid = deploymentUuid;
  }

  public String getSs_base_url() {
    return "http://" + getSuite_installer_host() + ":" + getSuite_installer_port() + "/urest/v1.1";
  }

  public String getBuiltin_config_path() {
    return builtin_config_path;
  }

  public String getItsma_services_path() {
    return itsma_services_path;
  }

  public String getDefault_registry_url() {
    return default_registry_url;
  }

  public String getSuite_update_new_version() {
    if(isStandlone()) {
      return suite_update_new_version;
    }

    return getSysEnv(ItsmaInstallerConstants.SUITE_UPDATE_NEW_VERSION);
  }

  public String getSysEnv(String key) {
    String val = System.getenv(key);
    if (val == null || val.isEmpty()) {
      logger.error("Failed to get system env " + key);
      throw new RuntimeException("Failed to get system env: " + key);
    }
    return val;
  }

  public Map<String, String> toMap() {
    Map<String, String> properties = new HashMap<String, String>();
    properties.put(ItsmaInstallerConstants.SUITE_NAMESPACE, getNamespace());
    properties.put("time_zone", getTimezone());
    properties.put(ItsmaInstallerConstants.IDM_ADMIN_KEY, getItsma_admin_key());
    properties.put(ItsmaInstallerConstants.SUITE_LABEL_KEY, getSuite_label_key());
    properties.put(ItsmaInstallerConstants.SUITE_LABEL_VALUE, getSuite_label_value());
    properties.put(ItsmaInstallerConstants.SUITE_INSTALLER_HOST, getSuite_installer_host());
    properties.put(ItsmaInstallerConstants.SUITE_INSTALLER_PORT, getSuite_installer_port());
    properties.put(ItsmaInstallerConstants.INGRESS_HOST_CRT, getIngress_host_crt());
    properties.put(ItsmaInstallerConstants.INGRESS_HOST_KEY, getIngress_host_key());
    properties.put(ItsmaInstallerConstants.ITSMA_DEPLOYMENT_UUID, getDeploymentUuid());
    properties.put(ItsmaInstallerConstants.ITSMA_ADMIN_PASSWORD, getItsma_admin_pwd());

    return properties;
  }
}
