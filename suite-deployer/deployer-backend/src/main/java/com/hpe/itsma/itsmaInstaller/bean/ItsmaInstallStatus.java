package com.hpe.itsma.itsmaInstaller.bean;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by tianlib on 12/23/2016.
 */
public class ItsmaInstallStatus {
  private String phase = "";
  private List<KubeResourceStatus> kubeResourceStatuses = new CopyOnWriteArrayList<KubeResourceStatus>();

  public List<KubeResourceStatus> getKubeResourceStatuses() {
    return kubeResourceStatuses;
  }

  public void setKubeResourceStatuses(List<KubeResourceStatus> kubeResourceStatuses) {
    this.kubeResourceStatuses = kubeResourceStatuses;
  }

  public String getPhase() {
    return phase;
  }

  public void setPhase(String phase) {
    this.phase = phase;
  }

  @Override
  public String toString() {
    return "ItsmaInstallStatus = [phase = " + phase + ", kubeResourceStatuses = " + kubeResourceStatuses + "]";
  }
}
