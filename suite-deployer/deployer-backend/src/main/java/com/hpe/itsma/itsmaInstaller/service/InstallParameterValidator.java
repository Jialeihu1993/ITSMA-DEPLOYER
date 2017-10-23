package com.hpe.itsma.itsmaInstaller.service;

import com.hpe.itsma.itsmaInstaller.constants.ItsmaInstallerConstants;
import com.hpe.itsma.itsmaInstaller.util.ItsmaUtil;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by tianlib on 3/25/2017.
 */
public class InstallParameterValidator {
  private final String[] cannotNullorEmptyKeys = new String[]{
    "itom_suite_size",
    "itom_suite_install_type",
    "itom_suite_mode",
    "sysadmin_password",
    "domain_name",
  };


  public boolean validateDBParameters(Map<String, Object> parameters) {
    if (!ItsmaUtil.isNullOrEmpty(ItsmaUtil.getProperty(parameters, "database"))) {
      List<Map<String, Object>> databases = (List<Map<String, Object>>) ItsmaUtil.getProperty(parameters, "database");
      for (Map<String, Object> dbParaOfProduct : databases) {
        checkEmpty(dbParaOfProduct, ItsmaInstallerConstants.DB_ENGINE);
        checkNull(dbParaOfProduct, ItsmaInstallerConstants.INTERNAL);
        checkEmpty(dbParaOfProduct, ItsmaInstallerConstants.DB_SERVER);
        // Only oracle db need test DB_INST
        if (dbParaOfProduct.get(ItsmaInstallerConstants.DB_ENGINE).toString().equalsIgnoreCase("oracle")) {
          checkEmpty(dbParaOfProduct, ItsmaInstallerConstants.DB_INST);
        }
        checkEmpty(dbParaOfProduct, ItsmaInstallerConstants.DB_LOGIN);
        checkEmpty(dbParaOfProduct, ItsmaInstallerConstants.DB_PASSWORD);
      }
    }
    return true;
  }

  public void checkNull(Map<String, Object> properties, String key) {
    Object value = properties.get(key);
    if (value == null) {
      throw new IllegalArgumentException("Parameter <" + key + "> can NOT be null.");
    }
  }

  public void checkEmpty(Map<String, Object> properties, String key) {
    this.checkNull(properties, key);

    String value = (String) properties.get(key);
    if (value.isEmpty()) {
      throw new IllegalArgumentException("Parameter <" + key + "> can NOT be empty.");
    }
  }

  public boolean validateInstallType(Map<String, Object> parameters) {
    String installType = parameters.get("itom_suite_install_type").toString();
    if (installType.equalsIgnoreCase("install_from_backup")) {
      this.checkEmpty(parameters, "itom_suite_backup_package_dir");
      this.checkEmpty(parameters, "itom_suite_backup_package_name");
    }
    return true;
  }

  /**
   * (1) At least one upper case letter
   * (2) At least one lower case letter
   * (3) At least one special character from the following: ,\:/. _?&%=+-[]()|
   * (4) At least one digit
   *
   * @param parameters
   * @return
   */
  public boolean validateAdminpassword(Map<String, Object> parameters) {
    String sysadmin_password = parameters.get("sysadmin_password").toString();
    String sysadminPattern = "(?=(.*[a-z])+)(?=(.*[A-Z])+)(?=(.*[0-9])+)(?=(.*[,\\\\:/._?&%=+\\-\\[\\]()])+)[0-9a-zA-Z,\\\\:/._?&%=+-\\[\\]()]{10,16}";
    boolean isMatch = Pattern.matches(sysadminPattern, sysadmin_password);

    if (!isMatch) {
      throw new IllegalArgumentException("Ensure that password is 10 to 16 characters long  and contains a mix of upper and lower case characters, one numeric and one special character. Special characters only allow \\\",\\:/._?&%=+-[]()\\\"");
    }

    if(parameters.get("itom_suite_mode").toString().equalsIgnoreCase(ItsmaInstallerConstants.ITOM_X_MODE)){
      this.checkEmpty(parameters, "boadmin_password");
      String boadmin_password = parameters.get("boadmin_password").toString();
      String boadminPattern = "(?=(.*[a-z])+)(?=(.*[A-Z])+)(?=(.*[0-9])+)(?=(.*[,\\\\:/._?&%=+\\-\\[\\]()])+)[0-9a-zA-Z,\\\\:/._?&%=+-\\[\\]()]{10,16}";
      isMatch = Pattern.matches(boadminPattern, boadmin_password);

      if (!isMatch) {
        throw new IllegalArgumentException("Ensure that password is 10 to 16 characters long  and contains a mix of upper and lower case characters, one numeric and one special character. Special characters only allow \\\",\\:/._?&%=+-[]()\\\"");
      }
    }

    return true;
  }

  public boolean validate(Map<String, Object> parameters) {
    for (String key : cannotNullorEmptyKeys) {
      this.checkEmpty(parameters, key);
    }

    this.validateInstallType(parameters);

    this.validateAdminpassword(parameters);

    this.validateDBParameters(parameters);

    return true;
  }
}
