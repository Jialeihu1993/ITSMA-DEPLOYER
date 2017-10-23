package com.hpe.itsma.itsmaInstaller.constants;

/**
 * Define all constants used by the whole project
 *
 * @author danny.tian
 * @since 11/23/2016
 */
public class ItsmaInstallerConstants {

    // general
    public static final long STATUS_CHECK_INTERVAL = 3000; // millisecond

    // k8s
    public static final String NODE_LABEL_KEY = "node_label_key";
    public static final String NODE_LABEL_VALUE = "node_label_value";

    // http headers
    public static final String COOKIE = "Cookie";
    public static final String X_AUTH_TOKEN = "X-AUTH-TOKEN";
    public static final String X_CSRF_TOKEN = "X-CSRF-TOKEN";
    public static final String JSESSIONID = "jsessionId";

    // itsma suite configuration file
    public static final String ITSMA_PRODUCTS = "itsmaProducts.json";
    public static final String RETAINEDCONFIGMAP = "retainedSuiteConfigMap.json";
    public static final String CONFIGURATION_PROPERTIES = "configurationProperties.json";
    public static final String DELETED_INTERNAL_DBS = "deletedInternalDBs.json";

    // itsma-suite
    public static final String DOMAIN_NAME = "domain_name";
    public static final String SUITE_NAMESPACE = "SUITE_NAMESPACE";
    public static final String MASTERNODE_TIME_ZONE = "MASTERNODE_TIME_ZONE";
    public static final String SUITE_INSTALLER_HOST = "SUITE_INSTALLER_HOST";
    public static final String SUITE_INSTALLER_PORT = "SUITE_INSTALLER_PORT";
    public static final String INGRESS_FQDN = "INGRESS_FQDN";
    public static final String ITSMA_DEPLOYMENT_UUID = "DEPLOYMENT_UUID";
    public static final String IDM_ADMIN_KEY = "IDM_ADMIN_KEY";
    public static final String SUITE_LABEL_KEY = "SUITE_LABEL_KEY";
    public static final String SUITE_LABEL_VALUE = "SUITE_LABEL_VALUE";
    public static final String INGRESS_HOST_CRT = "ingress_host_crt";
    public static final String INGRESS_HOST_KEY = "ingress_host_key";
    public static final String DEPLOYMENTUUID = "deploymentUuid";
    public static final String FULLSERVICELIST = "fullServiceList";
    public static final String INSTALLEDSERVICELIST = "installedServiceList";
    public static final String SUITE_VERSION = "itom_suite_version";

    // light weight single sign on
    public static final String LWSSO_DOMAIN = "lwsso_domain";
    public static final String LWSSO_USER = "lwsso_user";
    public static final String LWSSO_INITSTRING = "lwsso_initstring";

    // database
    public static final String INTERNAL = "internal";
    public static final String PRODUCT_NAME = "product_name";
    public static final String DB_ENGINE = "db_engine";
    public static final String DB_SERVER = "db_server";
    public static final String DB_PORT = "db_port";
    public static final String DB_INST = "db_inst";
    public static final String DB_LOGIN = "db_login";
    public static final String DB_PASSWORD = "db_password";
    public static final String DBA_USERNAME = "dba_username";
    public static final String DBA_PASSWORD = "dba_password";

    // ldap
    public static final String LDAP_SERVER_IP = "ldap_server_ip";

    // security
    public static final String ITSMA_ADMIN_PASSWORD = "ITSMA_ADMIN_PASSWORD";

    //Suite Mode
    public static final String ITOM_SUITE_MODE = "itom_suite_mode";
    public static final String ITOM_H_MODE = "H_MODE";
    public static final String ITOM_X_MODE = "X_MODE";

    //Install Type
    public static final String ITOM_SUITE_INSTALL_TYPE = "itom_suite_install_type";
    public static final String NEW_INSTALL = "new_install";
    public static final String INSTALL_FROM_BACKUP = "install_from_backup";
    public static final String UPDATE_INSTALL = "update";

    //ITOM Products
    public static final String ITOM_AUTH = "itom-auth";
    public static final String ITOM_IDM = "itom-idm";
    public static final String ITOM_INGRESS = "itom-ingress";
    public static final String ITOM_AUTOPASS = "itom-autopass";
    public static final String ITOM_ITSMA_CONFIG = "itom-itsma-config";
    public static final String ITOM_OPENLDAP = "itom-openldap";

    public static final String ITOM_SM = "itom-sm";
    public static final String ITOM_CHAT = "itom-chat";
    public static final String ITOM_SMARTANALYTICS = "itom-smartanalytics";
    public static final String ITOM_SERVICE_PORTAL = "itom-service-portal";
    public static final String ITOM_XSERVICES = "itom-xservices";
    public static final String ITOM_XSERVICES_INFRA = "itom-xservices-infra";
    public static final String ITOM_XRUNTIME = "itom-xruntime";
    public static final String ITOM_XRUNTIME_INFRA = "itom-xruntime-infra";
    public static final String ITOM_LANDING_PAGE = "itom-landing-page";
    public static final String ITOM_CMDB = "itom-cmdb";
    public static final String ITOM_AUTOMATION = "itom-automation";

    public static final String RUNNING = "running";

    public static final String RESTORING = "RESTORING";
    public static final String FAILED = "FAILED";
    public static final String SUCCESS = "SUCCESS";

    // OS environment variables
    public static final String SUITE_UPDATE_NEW_VERSION = "UPDATE_VERSION";
}