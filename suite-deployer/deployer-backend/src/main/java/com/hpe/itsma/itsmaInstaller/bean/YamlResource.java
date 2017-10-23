package com.hpe.itsma.itsmaInstaller.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianlib on 12/30/2016.
 */
public class YamlResource {
  private String name;
  private String type;
  private String product;
  private List<DependsOnItem> dependsOn = new ArrayList<DependsOnItem>();
  private boolean needReadyInCurrentPhase;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getProduct() {
    return product;
  }

  public void setProduct(String product) {
    this.product = product;
  }

  public List<DependsOnItem> getDependsOn() {
    return dependsOn;
  }

  public void setDependsOn(List<DependsOnItem> dependsOn) {
    this.dependsOn = dependsOn;
  }

  public boolean isNeedReadyInCurrentPhase() {
    return needReadyInCurrentPhase;
  }

  public void setNeedReadyInCurrentPhase(boolean needReadyInCurrentPhase) {
    this.needReadyInCurrentPhase = needReadyInCurrentPhase;
  }

  public class DependsOnItem {
    private String kind;
    private String name;
    private boolean ready = false;
    private String expectStatus;

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

    public boolean isReady() {
      return ready;
    }

    public void setReady(boolean ready) {
      this.ready = ready;
    }

    public String getExpectStatus() {
      return expectStatus;
    }

    public void setExpectStatus(String expectStatus) {
      this.expectStatus = expectStatus;
    }

    @Override
    public String toString() {
      return "DependsOnItem = [kind = " + kind + ", name = " + name +
          ", ready = " + ready + ", expectStatus = " + expectStatus + "]";
    }
  }

  @Override
  public String toString() {
    return "YamlResource = [name = " + name + ", type = " + type +
        ", product = " + product + ", dependsOn = " + dependsOn +
        ", needReadyInCurrentPhase = " + needReadyInCurrentPhase + "]";
  }
}
