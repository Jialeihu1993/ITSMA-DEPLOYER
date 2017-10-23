package com.hpe.itsma.itsmaInstaller.bean;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by tianlib on 8/28/2017.
 */
@Component
public class DeployerContext {

  private String deploymentUuid;
  private DeployerStatus deployerStatus = new DeployerStatus();
  private Map<String, Object> suiteProperties = new HashMap<>();
  private List<ItsmaService> itsmaServices = new CopyOnWriteArrayList<>();
  private static final Object itsmaServiceStatusLock = new Object();
  private static final Object installStatusResultLock = new Object();
  private static final Object deployerStatusLock = new Object();

  public DeployerContext() {
    deployerStatus.setPhase(new DeployerStatus().new Phase(DeployerStatus.PhaseType.IDLE, ""));
  }

  public String getDeploymentUuid() {
    return deploymentUuid;
  }

  public void setDeploymentUuid(String deploymentUuid) {
    this.deploymentUuid = deploymentUuid;
  }

  public DeployerStatus getDeployerStatus() {
    return deployerStatus;
  }

  public void setDeployerStatus(DeployerStatus deployerStatus) {
    this.deployerStatus = deployerStatus;
  }

  public Map<String, Object> getSuiteProperties() {
    return suiteProperties;
  }

  public void setSuiteProperties(Map<String, Object> suiteProperties) {
    suiteProperties = suiteProperties;
  }

  public List<ItsmaService> getItsmaServices() {
    return itsmaServices;
  }

  public void setItsmaServices(List<ItsmaService> itsmaServices) {
    itsmaServices = itsmaServices;
  }

  public static Object getItsmaServiceStatusLock() {
    return itsmaServiceStatusLock;
  }

  public static Object getInstallStatusResultLock() {
    return installStatusResultLock;
  }

  public static Object getdeployerStatusLock() {
    return deployerStatusLock;
  }

  public void updateDeployerStatus(DeployerStatus.PhaseType name, String detail) {
    synchronized (deployerStatusLock) {
      deployerStatus.getPhase().setName(name);
      deployerStatus.getPhase().setDetail(detail);
    }
  }

  public void updateDeployerStatus(List<ItsmaServiceStatus> itsmaServiceStatuses) {
    synchronized (deployerStatusLock) {
      deployerStatus.setItsmaServiceStatuses(itsmaServiceStatuses);
    }
  }
}
