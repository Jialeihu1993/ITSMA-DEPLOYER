package com.hpe.itsma.itsmaInstaller.bean;

/**
 * Created by tianlib on 12/14/2016.
 */
public class SSRestResponseBody {
  private String installationId;
  private BatchInfo batchInfo;

  public String getInstallationId() {
    return installationId;
  }

  public void setInstallationId(String installationId) {
    this.installationId = installationId;
  }

  public BatchInfo getBatchInfo() {
    return batchInfo;
  }

  public void setBatchInfo(BatchInfo batchInfo) {
    this.batchInfo = batchInfo;
  }

  @Override
  public String toString() {
    return "installationId: " + installationId + "\r\n" + batchInfo;
  }
}
