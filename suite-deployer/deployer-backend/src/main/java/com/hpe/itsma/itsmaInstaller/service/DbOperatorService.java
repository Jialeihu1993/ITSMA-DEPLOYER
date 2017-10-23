package com.hpe.itsma.itsmaInstaller.service;

import com.hpe.itsma.dbservice.DbUtils;
import com.hpe.itsma.itsmaInstaller.bean.ItsmaProduct;
import com.hpe.itsma.itsmaInstaller.constants.ItsmaInstallerConstants;
import com.hpe.itsma.itsmaInstaller.util.ItsmaUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * Created by huapei.
 */
@Service
public class DbOperatorService {
    private static Log logger = LogFactory.getLog(DbOperatorService.class);

    @Autowired
    private ProductsService productsService;

    public void createInstanceAndResetPassword(Map<String, Object> properties) throws Exception {
        String installType = properties.get(ItsmaInstallerConstants.ITOM_SUITE_INSTALL_TYPE).toString();
        String suiteMode = ItsmaUtil.getProperty(properties, ItsmaInstallerConstants.ITOM_SUITE_MODE).toString();

        ItsmaProduct sm = productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_SM);
        ItsmaProduct ucmdb = productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_CMDB);
        ItsmaProduct idm = productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_IDM);
        if (installType.equalsIgnoreCase(ItsmaInstallerConstants.NEW_INSTALL)) {
            if (sm != null && sm.isActive()) {
                createDBinstance(properties, "sm", "servicemanagement", "servicemanagement", "servicemanagement");
            }
            if (ucmdb != null && ucmdb.isActive()) {
                createDBinstance(properties, "ucmdb", "ucmdb", "ucmdb", "ucmdb");
            }
            if (idm != null && idm.isActive()) {
                createDBinstance(properties, "idm", "idm", "idm", "idm");
            }
        } else if (installType.equalsIgnoreCase(ItsmaInstallerConstants.INSTALL_FROM_BACKUP) && suiteMode.equalsIgnoreCase(ItsmaInstallerConstants.ITOM_X_MODE)
                && ucmdb != null && ucmdb.isActive()) {
            boolean internal = (Boolean) properties.get("ucmdb_" + ItsmaInstallerConstants.INTERNAL + "_db");
            String dbEngine = (String) properties.get("ucmdb_" + ItsmaInstallerConstants.DB_ENGINE);
            if (!internal && dbEngine.equalsIgnoreCase("postgres")) {
                Connection ucmdbConn = getServiceExternalPGConnection(properties, "ucmdb");
                DbUtils.resetPGUserPassword(ucmdbConn, "ucmdb", "ucmdb");
            }
        }
    }

    public void createDBinstance(Map<String, Object> properties, String productName, String username, String password, String dbname) throws Exception {
        String dbaUsername = (String) properties.get(productName + "_" + ItsmaInstallerConstants.DBA_USERNAME);
        boolean internal = (Boolean) properties.get(productName + "_" + ItsmaInstallerConstants.INTERNAL + "_db");
        String dbEngine = (String) properties.get(productName + "_" + ItsmaInstallerConstants.DB_ENGINE);

        if (!internal && dbEngine.equalsIgnoreCase("postgres")) {
            Connection connection = getServiceExternalPGConnection(properties, productName);
            createDBandUser(connection, username, password, dbname, dbaUsername);
        }
    }

    public void createDBandUser(Connection conn, String username, String password, String dbname, String dbauserName) throws SQLException {
        logger.info("Start create role <" + username + "> and db instance <" + dbname + "> .");
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
//		String sql2= " create user "+dbName+" with REPLICATION superuser password '"+dbName+"'";
            String createRoleSql = " create user " + username + " with CREATEDB CREATEROLE LOGIN password '" + password + "'";
            String grantSql = "GRANT " + username + " TO " + dbauserName;
            stmt.executeUpdate(createRoleSql);
            stmt.executeUpdate(grantSql);
            logger.info("create dbuser " + username + " success!");

            String dbinstanceCreateSql = "CREATE DATABASE " + dbname + " with owner " + username;
            stmt.executeUpdate(dbinstanceCreateSql);
            logger.info("create database " + dbname + " success!");
        } catch (SQLException e) {
            logger.error(e);
            throw new SQLException("create role " + username + " and database: +" + dbname + " failed");
        } finally {
            stmt.close();
            conn.close();
        }
    }

    public Connection getServiceExternalPGConnection(Map<String, Object> properties, String productName) throws Exception {
        Connection conn;

        String dbServer = (String) properties.get(productName + "_" + ItsmaInstallerConstants.DB_SERVER);
        String dbPort = (String) properties.get(productName + "_" + ItsmaInstallerConstants.DB_PORT);
        String dbaUsername = (String) properties.get(productName + "_" + ItsmaInstallerConstants.DBA_USERNAME);
        String dbaPassword = (String) properties.get(productName + "_" + ItsmaInstallerConstants.DBA_PASSWORD);
        String dbUri = "jdbc:postgresql://" + dbServer + ":" + dbPort + "/postgres";

        conn = DbUtils.getPGConnection(dbUri, dbaUsername, dbaPassword);

        return conn;
    }

}
