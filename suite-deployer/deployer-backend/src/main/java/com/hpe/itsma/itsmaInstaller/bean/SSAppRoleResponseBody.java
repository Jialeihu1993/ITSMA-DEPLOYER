package com.hpe.itsma.itsmaInstaller.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianlib on 12/26/2016.
 */
public class SSAppRoleResponseBody {

  private List<AppRole> appRoleAndIdList = new ArrayList<AppRole>();
  private String vaultNamespace;

  public List<AppRole> getAppRoleAndIdList() {
    return appRoleAndIdList;
  }

  public void setAppRoleAndIdList(List<AppRole> appRoleAndIdList) {
    this.appRoleAndIdList = appRoleAndIdList;
  }

  public String getVaultNamespace() {
    return vaultNamespace;
  }

  public void setVaultNamespace(String vaultNamespace) {
    this.vaultNamespace = vaultNamespace;
  }

  public class AppRole {
    public String getAppRole() {
      return appRole;
    }

    public void setAppRole(String appRole) {
      this.appRole = appRole;
    }

    public String getAppRoleId() {
      return appRoleId;
    }

    public void setAppRoleId(String appRoleId) {
      this.appRoleId = appRoleId;
    }

    private String appRole;
    private String appRoleId;
  }
}
