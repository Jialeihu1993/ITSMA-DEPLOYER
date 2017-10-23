package com.hpe.itsma.itsmaInstaller.bean;

/**
 * Created by tianlib on 12/23/2016.
 */
public class KubeResourceStatus {

  private String kind;
  private String name;
  private String status;
  private String self;
  private Boolean ready;

  public KubeResourceStatus(String kind, String name, String status,Boolean ready, String self) {
    this.kind = kind;
    this.name = name;
    this.status = status;
    this.self = self;
    this.ready = ready;
  }

  public String getKind() {
    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
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

  public String getSelf() {
    return self;
  }

  public void setSelf(String self) {
    this.self = self;
  }

  public Boolean getReady() {
    return ready;
  }

  public void setReady(Boolean ready) {
    this.ready = ready;
  }

  public String toJson() {
    return "{\"kind\": \"" + kind + "\",\"name\": \"" + name + "\",\"status\": \"" + status + "\", \"ready\": " + ready + ",\"self\": \"" + self + "\"}";
  }
}
