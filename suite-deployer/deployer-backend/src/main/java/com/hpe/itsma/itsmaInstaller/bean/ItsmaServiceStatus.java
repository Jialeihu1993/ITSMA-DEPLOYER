package com.hpe.itsma.itsmaInstaller.bean;

/**
 * Created by tianlib on 5/26/2017.
 */
public class ItsmaServiceStatus {
  private String name;
  private String status;

  public ItsmaServiceStatus() {
  }

  public ItsmaServiceStatus(String name, String status) {
    this.name = name;
    this.status = status;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
