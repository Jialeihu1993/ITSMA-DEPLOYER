package com.hpe.itsma.itsmaInstaller.bean;

import org.springframework.stereotype.Component;

/**
 * Created by tianlib on 9/1/2017.
 */
@Component
public class SuiteDeploymentInfo {
  private String deploymentStatus;
  private String deploymentUuid;
  private String firstInstallDate;
  private String namespace;
  private String nfsIp;
  private String nfsOutputPath;
  private int reconfigPort;
  private String reconfigUrl;
  private String suite;
  private String version;

  public String getDeploymentStatus() {
    return deploymentStatus;
  }

  public void setDeploymentStatus(String deploymentStatus) {
    this.deploymentStatus = deploymentStatus;
  }

  public String getDeploymentUuid() {
    return deploymentUuid;
  }

  public void setDeploymentUuid(String deploymentUuid) {
    this.deploymentUuid = deploymentUuid;
  }

  public String getFirstInstallDate() {
    return firstInstallDate;
  }

  public void setFirstInstallDate(String firstInstallDate) {
    this.firstInstallDate = firstInstallDate;
  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public String getNfsIp() {
    return nfsIp;
  }

  public void setNfsIp(String nfsIp) {
    this.nfsIp = nfsIp;
  }

  public String getNfsOutputPath() {
    return nfsOutputPath;
  }

  public void setNfsOutputPath(String nfsOutputPath) {
    this.nfsOutputPath = nfsOutputPath;
  }

  public int getReconfigPort() {
    return reconfigPort;
  }

  public void setReconfigPort(int reconfigPort) {
    this.reconfigPort = reconfigPort;
  }

  public String getReconfigUrl() {
    return reconfigUrl;
  }

  public void setReconfigUrl(String reconfigUrl) {
    this.reconfigUrl = reconfigUrl;
  }

  public String getSuite() {
    return suite;
  }

  public void setSuite(String suite) {
    this.suite = suite;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  @Override
  public String toString() {
    return "SuiteDeploymentInfo{" +
      "deploymentStatus='" + deploymentStatus + '\'' +
      ", deploymentUuid='" + deploymentUuid + '\'' +
      ", firstInstallDate='" + firstInstallDate + '\'' +
      ", namespace='" + namespace + '\'' +
      ", nfsIp='" + nfsIp + '\'' +
      ", nfsOutputPath='" + nfsOutputPath + '\'' +
      ", reconfigPort=" + reconfigPort +
      ", reconfigUrl='" + reconfigUrl + '\'' +
      ", suite='" + suite + '\'' +
      ", version='" + version + '\'' +
      '}';
  }
}