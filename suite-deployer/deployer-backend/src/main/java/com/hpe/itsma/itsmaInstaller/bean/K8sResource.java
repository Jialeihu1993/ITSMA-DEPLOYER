package com.hpe.itsma.itsmaInstaller.bean;

import java.util.Map;

/**
 * Created by tianlib on 1/3/2017.
 */
public class K8sResource {

  private String apiVersion;
  private String kind;
  private Map<String, Object> metadata;
  private Map<String, Object> data;

  public Map<String, Object> getSpec() {
    return spec;
  }

  public void setSpec(Map<String, Object> spec) {
    this.spec = spec;
  }

  private Map<String, Object> spec;

  public String getKind() {
    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public Map<String, Object> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
  }

  public Map<String, Object> getData() {
    return data;
  }

  public void setData(Map<String, Object> data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return "K8sResource = [apiVersion: " + apiVersion + ", kind: " + kind +
        ", name: " + metadata.get("name") + ", namespace: " + metadata.get("namespace") + "]";
  }
}
