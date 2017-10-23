package com.hpe.itsma.itsmaInstaller.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhongtao on 7/3/2017.
 */
public class ItsmaProfileParameters {
  private static Log logger = LogFactory.getLog(ItsmaProfileParameters.class);

  private Map<String, String> defaultVal = new HashMap<String, String>();
  private Map<String, String> demo = new HashMap<String, String>();
  private Map<String, String> xsmall = new HashMap<String, String>();
  private Map<String, String> extrasmall = new HashMap<String, String>();
  private Map<String, String> small = new HashMap<String, String>();
  private Map<String, String> medium = new HashMap<String, String>();
  private Map<String, String> large = new HashMap<String, String>();

  public Map<String, String> getDefaultVal() {
    return defaultVal;
  }

  public void setDefaultVal(Map<String, String> defaultVal) {
    this.defaultVal = defaultVal;
  }

  public Map<String, String> getDemo() {
    return demo;
  }

  public void setDemo(Map<String, String> demo) {
    this.demo = demo;
  }

  public Map<String, String> getXsmall() {
    return xsmall;
  }

  public void setXsmall(Map<String, String> xsmall) {
    this.xsmall = xsmall;
  }

  public Map<String, String> getExtrasmall() {
    return extrasmall;
  }

  public void setExtrasmall(Map<String, String> extrasmall) {
    this.extrasmall = extrasmall;
  }

  public Map<String, String> getSmall() {
    return small;
  }

  public void setSmall(Map<String, String> small) {
    this.small = small;
  }

  public Map<String, String> getMedium() {
    return medium;
  }

  public void setMedium(Map<String, String> medium) {
    this.medium = medium;
  }

  public Map<String, String> getLarge() {
    return large;
  }

  public void setLarge(Map<String, String> large) {
    this.large = large;
  }
}
