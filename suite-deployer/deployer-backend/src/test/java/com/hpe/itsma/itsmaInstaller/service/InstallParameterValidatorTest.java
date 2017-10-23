package com.hpe.itsma.itsmaInstaller.service;

import com.hpe.itsma.itsmaInstaller.constants.ItsmaInstallerConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhongtao on 9/6/2017.
 */
public class InstallParameterValidatorTest {
  private InstallParameterValidator installParameterValidator;

  @Before
  public void setUp() throws Exception {
    installParameterValidator = new InstallParameterValidator();
  }

  /**
   * Test method for {@link InstallParameterValidator#validate(java.util.Map)}
   * <p>
   * Condition:
   * <ol>
   * <li>All required parameters are input.</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li>Return true</li>
   * </ol>
   */
  @Test
  public void testValidateHappyPath() {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("itom_suite_size", "demo");
    parameters.put("itom_suite_install_type", "new_install");
    parameters.put("itom_suite_mode", "H_MODE");
    parameters.put("sysadmin_password", "Admin_1234");
    parameters.put("domain_name", "shc-dev-suite-tao-zhong-1.hpeswlab.net");

    Assert.assertTrue(installParameterValidator.validate(parameters));
  }

  /**
   * Test method for {@link InstallParameterValidator#validate(java.util.Map)}
   * <p>
   * Condition:
   * <ol>
   * <li>"itom_suite_size" is missing.</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li>Throw IllegalArgumentException</li>
   * </ol>
   */
  @Test(expected = IllegalArgumentException.class)
  public void testValidateMissItom_suite_size() {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("itom_suite_install_type", "new_install");
    parameters.put("itom_suite_mode", "H_MODE");
    parameters.put("sysadmin_password", "Admin_1234");
    parameters.put("domain_name", "shc-dev-suite-tao-zhong-1.hpeswlab.net");

    installParameterValidator.validate(parameters);
  }

  /**
   * Test method for {@link InstallParameterValidator#validate(java.util.Map)}
   * <p>
   * Condition:
   * <ol>
   * <li>The value of "itom_suite_size" is empty.</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li>Throw IllegalArgumentException</li>
   * </ol>
   */
  @Test(expected = IllegalArgumentException.class)
  public void testValidateItom_suite_sizeIsEmpty() {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("itom_suite_size", "");
    parameters.put("itom_suite_install_type", "new_install");
    parameters.put("itom_suite_mode", "H_MODE");
    parameters.put("sysadmin_password", "Admin_1234");
    parameters.put("domain_name", "shc-dev-suite-tao-zhong-1.hpeswlab.net");

    installParameterValidator.validate(parameters);
  }

  /**
   * Test method for {@link InstallParameterValidator#validate(java.util.Map)}
   * <p>
   * Condition:
   * <ol>
   * <li>All required parameters are input and "itom_suite_install_type"  is "install_from_backup"</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li>Return true</li>
   * </ol>
   */
  @Test
  public void testValidateInstall_from_backup() {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("itom_suite_size", "demo");
    parameters.put("itom_suite_install_type", "install_from_backup");
    parameters.put("itom_suite_backup_package_dir", "1503973040342");
    parameters.put("itom_suite_backup_package_name", "ITSMA_v2017.07_1503973040342.zip");
    parameters.put("itom_suite_mode", "H_MODE");
    parameters.put("sysadmin_password", "Admin_1234");
    parameters.put("domain_name", "shc-dev-suite-tao-zhong-1.hpeswlab.net");

    Assert.assertTrue(installParameterValidator.validate(parameters));
  }

  /**
   * Test method for {@link InstallParameterValidator#validate(java.util.Map)}
   * <p>
   * Condition:
   * <ol>
   * <li>The value of "itom_suite_install_type"  is "install_from_backup", but "itom_suite_backup_package_dir" is empty.</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li>Throw IllegalArgumentException</li>
   * </ol>
   */
  @Test(expected = IllegalArgumentException.class)
  public void testValidateInstall_from_backupMissParameter() {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("itom_suite_size", "demo");
    parameters.put("itom_suite_install_type", "install_from_backup");
    parameters.put("itom_suite_backup_package_dir", "");
    parameters.put("itom_suite_backup_package_name", "ITSMA_v2017.07_1503973040342.zip");
    parameters.put("itom_suite_mode", "H_MODE");
    parameters.put("sysadmin_password", "Admin_1234");
    parameters.put("domain_name", "shc-dev-suite-tao-zhong-1.hpeswlab.net");

    installParameterValidator.validate(parameters);
  }

  /**
   * Test method for {@link InstallParameterValidator#validateDBParameters(java.util.Map)}
   * <p>
   * Condition:
   * <ol>
   * <li>Parameter 'database' is empty.</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li>Return true</li>
   * </ol>
   */
  @Test
  public void testValidateDBParameters() {
    Map<String, Object> sm = new HashMap<>();
    sm.put("product_name", "sm");
    sm.put("internal", false);
    sm.put("db_engine", "sqlserver");
    sm.put("db_server", "16.155.194.156");
    sm.put("db_port", "1433");
    sm.put("db_inst", "");
    sm.put("db_login", "sa");
    sm.put("db_password", "1Qaz2wsx3edc");

    Map<String, Object> ucmdb = new HashMap<>();
    ucmdb.put("product_name", "ucmdb");
    ucmdb.put("internal", false);
    ucmdb.put("db_engine", "oracle");
    ucmdb.put("db_server", "SGDLITVM0405.hpeswlab.net");
    ucmdb.put("db_port", "1521");
    ucmdb.put("db_inst", "lx_DB_GN");
    ucmdb.put("db_login", "lx_cmdb_db1");
    ucmdb.put("db_password", "cmdb_pwd");

    List<Map<String, Object>> databases = new ArrayList<>();
    databases.add(sm);
    databases.add(ucmdb);

    Map<String, Object> parameters = new HashMap<>();
    parameters.put("database", databases);
    Assert.assertTrue(installParameterValidator.validateDBParameters(parameters));
  }

  /**
   * Test method for {@link InstallParameterValidator#validateDBParameters(java.util.Map)}
   * <p>
   * Condition:
   * <ol>
   * <li>Parameter 'database' is empty.</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li>Return true</li>
   * </ol>
   */
  @Test
  public void testValidateDBParametersIsNull() {
    Map<String, Object> parameters = new HashMap<>();
    Assert.assertTrue(installParameterValidator.validateDBParameters(parameters));
  }

  /**
   * Test method for {@link InstallParameterValidator#validateDBParameters(java.util.Map)}
   * <p>
   * Condition:
   * <ol>
   * <li>Parameter 'database' is empty.</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li>Throw IllegalArgumentException</li>
   * </ol>
   */
  @Test(expected = IllegalArgumentException.class)
  public void testValidateDBParametersMissOracleDb_inst() {
    Map<String, Object> sm = new HashMap<>();
    sm.put("product_name", "sm");
    sm.put("internal", false);
    sm.put("db_engine", "sqlserver");
    sm.put("db_server", "16.155.194.156");
    sm.put("db_port", "1433");
    sm.put("db_inst", "");
    sm.put("db_login", "sa");
    sm.put("db_password", "1Qaz2wsx3edc");

    Map<String, Object> ucmdb = new HashMap<>();
    ucmdb.put("product_name", "ucmdb");
    ucmdb.put("internal", false);
    ucmdb.put("db_engine", "oracle");
    ucmdb.put("db_server", "SGDLITVM0405.hpeswlab.net");
    ucmdb.put("db_port", "1521");
    ucmdb.put("db_login", "lx_cmdb_db1");
    ucmdb.put("db_password", "cmdb_pwd");

    List<Map<String, Object>> databases = new ArrayList<>();
    databases.add(sm);
    databases.add(ucmdb);

    Map<String, Object> parameters = new HashMap<>();
    parameters.put("database", databases);
    installParameterValidator.validateDBParameters(parameters);
  }

  /**
   * Test method for {@link InstallParameterValidator#validateAdminpassword(java.util.Map)}
   * <p>
   * Condition:
   * <ol>
   * <li>Parameter 'sysadmin_password' is valid.</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li>Return true</li>
   * </ol>
   */
  @Test
  public void testValidateAdminpassword() {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("itom_suite_mode", ItsmaInstallerConstants.ITOM_H_MODE);
    parameters.put("sysadmin_password", "Admin_1234");
    Assert.assertTrue(installParameterValidator.validateAdminpassword(parameters));
  }

  /**
   * Test method for {@link InstallParameterValidator#validateAdminpassword(java.util.Map)}
   * <p>
   * Condition:
   * <ol>
   * <li>Parameter 'sysadmin_password' & 'boadmin_password' is valid.</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li>Return true</li>
   * </ol>
   */
  @Test
  public void testValidateAdminpasswordBoadminPassword() {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("itom_suite_mode", ItsmaInstallerConstants.ITOM_X_MODE);
    parameters.put("sysadmin_password", "Admin_1234");
    parameters.put("boadmin_password", "Admin_1234");
    Assert.assertTrue(installParameterValidator.validateAdminpassword(parameters));
  }

  /**
   * Test method for {@link InstallParameterValidator#validateAdminpassword(java.util.Map)}
   * <p>
   * Condition:
   * <ol>
   * <li>Parameter 'sysadmin_password' is valid & 'boadmin_password' is null.</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li>Return true</li>
   * </ol>
   */
  @Test(expected = IllegalArgumentException.class)
  public void testValidateAdminpasswordBoadminPasswordIsNull() {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("itom_suite_mode", ItsmaInstallerConstants.ITOM_X_MODE);
    parameters.put("sysadmin_password", "Admin_1234");
    Assert.assertTrue(installParameterValidator.validateAdminpassword(parameters));
  }

  /**
   * Test method for {@link InstallParameterValidator#validateAdminpassword(java.util.Map)}
   * <p>
   * Condition:
   * <ol>
   * <li>Parameter 'sysadmin_password' is valid & 'boadmin_password' is Empty.</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li>Return true</li>
   * </ol>
   */
  @Test(expected = IllegalArgumentException.class)
  public void testValidateAdminpasswordBoadminPasswordIsEmpty() {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("itom_suite_mode", ItsmaInstallerConstants.ITOM_X_MODE);
    parameters.put("sysadmin_password", "Admin_1234");
    parameters.put("boadmin_password", "");
    Assert.assertTrue(installParameterValidator.validateAdminpassword(parameters));
  }

  /**
   * Test method for {@link InstallParameterValidator#validateAdminpassword(java.util.Map)}
   * <p>
   * Condition:
   * <ol>
   * <li>Parameter 'sysadmin_password' is not valid.</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li>Throw IllegalArgumentException</li>
   * </ol>
   */
  @Test(expected = IllegalArgumentException.class)
  public void testValidateAdminpasswordNotValidPassword() {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("sysadmin_password", "1234");
    installParameterValidator.validateAdminpassword(parameters);
  }
}
