package com.hpe.itsma.itsmaInstaller.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianlib on 10/10/2017.
 */
public class SuiteVersion {
  private String suiteVersion;
  private String suiteMode;

  public class Service {
    private String name;
    private String version;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getVersion() {
      return version;
    }

    public void setVersion(String version) {
      this.version = version;
    }

    @Override
    public String toString() {
      return "service=[name: " + name + " version: " + version + "]";
    }
  }

  private List<Service> services = new ArrayList<>();

  public String getSuiteVersion() {
    return suiteVersion;
  }

  public void setSuiteVersion(String suiteVersion) {
    this.suiteVersion = suiteVersion;
  }

  public String getSuiteMode() {
    return suiteMode;
  }

  public void setSuiteMode(String suiteMode) {
    this.suiteMode = suiteMode;
  }

  public List<Service> getServices() {
    return services;
  }

  public void setServices(List<Service> services) {
    this.services = services;
  }

  @Override
  public String toString() {
    return "SuiteVersion=[suiteVersion: " + suiteVersion + " suiteMode: " + suiteMode + " services: " + services + "]";
  }
}
