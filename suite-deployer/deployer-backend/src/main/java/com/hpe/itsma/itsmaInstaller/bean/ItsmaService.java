package com.hpe.itsma.itsmaInstaller.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianlib on 3/1/2017.
 */
public class ItsmaService {
  private static Log logger = LogFactory.getLog(ItsmaService.class);

  private String name;
  private String version;
  private String owner;
  private List<ItsmaService> deployDeps = new ArrayList<ItsmaService>();
  private String deployEntry;
  private String deployEntities;
  private String deployContent;
  private String apiSpec;
  private String registryUrl;
  private String controllerImgTag;
  private STATUS status;

  public enum STATUS {
    DEPLOYED,
    WAIT,
    READY,
    FAILED,
    SUCCESS,
    RUNNING,
    TERMINATING,
    TERMINATED
  }

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

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public List<ItsmaService> getDeployDeps() {
    return deployDeps;
  }

  public void setDeployDeps(List<ItsmaService> deployDeps) {
    this.deployDeps = deployDeps;
  }

  public String getDeployEntry() {
    return deployEntry;
  }

  public void setDeployEntry(String deployEntry) {
    this.deployEntry = deployEntry;
  }

  public String getDeployEntities() {
    return deployEntities;
  }

  public void setDeployEntities(String deployEntities) {
    this.deployEntities = deployEntities;
  }

  public String getDeployContent() {
    return deployContent;
  }

  public void setDeployContent(String deployContent) {
    this.deployContent = deployContent;
  }

  public String getApiSpec() {
    return apiSpec;
  }

  public void setApiSpec(String apiSpec) {
    this.apiSpec = apiSpec;
  }

  public String getRegistryUrl() {
    return registryUrl;
  }

  public void setRegistryUrl(String registryUrl) {
    this.registryUrl = registryUrl;
  }

  public String getControllerImgTag() {
    return controllerImgTag;
  }

  public void setControllerImgTag(String controllerImgTag) {
    this.controllerImgTag = controllerImgTag;
  }

  public STATUS getStatus() {
    return status;
  }

  public void setStatus(STATUS status) {
    this.status = status;
  }

  public ItsmaService copy(final ItsmaService itsmaService) {
    this.name = itsmaService.getName();
    this.owner = itsmaService.getOwner();
    this.deployDeps = itsmaService.getDeployDeps();
    this.apiSpec = itsmaService.getApiSpec();

    return this;
  }

  @Override
  public String toString() {
    return "ItsmaService = [name=" + name
        + " version=" + version
        + " owner=" + owner
        + " deployDeps=" + deployDeps
        + " deployEntities=" + deployEntities
        + " deployContent=" + deployContent
        + " apiSpec=" + apiSpec;
  }
}