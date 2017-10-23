package com.hpe.itsma.itsmaInstaller.bean;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by tianlib on 12/14/2016.
 */
public class BatchInfo {

  private List<KubeInstanceInfo> kubeInstanceInfoList = new LinkedList<KubeInstanceInfo>();

  private boolean foundInvalidate = false;

  private int invalidateCount = 0;

  private List<String> invalidateContentList = new LinkedList<String>();

  public void increaseInvalidateCount(){
    invalidateCount++;
  }

  public List<KubeInstanceInfo> getKubeInstanceInfoList() {
    return kubeInstanceInfoList;
  }

  public void setKubeInstanceInfoList(List<KubeInstanceInfo> kubeInstanceInfoList) {
    this.kubeInstanceInfoList = kubeInstanceInfoList;
  }

  public boolean isFoundInvalidate() {
    return foundInvalidate;
  }

  public void setFoundInvalidate(boolean foundInvalidate) {
    this.foundInvalidate = foundInvalidate;
  }

  public int getInvalidateCount() {
    return invalidateCount;
  }

  public void setInvalidateCount(int invalidateCount) {
    this.invalidateCount = invalidateCount;
  }

  public List<String> getInvalidateContentList() {
    return invalidateContentList;
  }

  public void setInvalidateContentList(List<String> invalidateContentList) {
    this.invalidateContentList = invalidateContentList;
  }

  @Override
  public String toString() {
    return "BatchInfo [kubeInstanceInfoList=" + kubeInstanceInfoList
        + ", foundInvalidate=" + foundInvalidate + ", invalidateCount="
        + invalidateCount + ", invalidateContentList="
        + invalidateContentList + "]";
  }

}
