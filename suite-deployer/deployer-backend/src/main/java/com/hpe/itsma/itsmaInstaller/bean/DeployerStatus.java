package com.hpe.itsma.itsmaInstaller.bean;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by tianlib on 9/8/2017.
 */
public class DeployerStatus {
  public enum PhaseType {
    IDLE,
    RUNNING,
    INSTALLING,
    UPDATING,
    SHUTTINGDOWN,
    RESTORING
  }

  public class Phase {
    private PhaseType name;
    private String detail;

    public Phase() {}

    public Phase(PhaseType name, String detail) {
      this.name = name;
      this.detail = detail;
    }

    public PhaseType getName() {
      return name;
    }

    public void setName(PhaseType name) {
      this.name = name;
    }

    public String getDetail() {
      return detail;
    }

    public void setDetail(String detail) {
      this.detail = detail;
    }
  }

  private Phase phase;
  private List<ItsmaServiceStatus> itsmaServiceStatuses;

  public Phase getPhase() {
    return phase;
  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public List<ItsmaServiceStatus> getItsmaServiceStatuses() {
    return itsmaServiceStatuses;
  }

  public void setItsmaServiceStatuses(List<ItsmaServiceStatus> itsmaServiceStatuses) {
    this.itsmaServiceStatuses = itsmaServiceStatuses;
  }

  public String toJson() {
    return new Gson().toJson(this);
  }
}
