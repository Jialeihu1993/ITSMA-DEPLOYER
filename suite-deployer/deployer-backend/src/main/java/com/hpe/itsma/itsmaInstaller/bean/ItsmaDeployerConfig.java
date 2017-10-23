package com.hpe.itsma.itsmaInstaller.bean;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by tianlib on 6/28/2017.
 */
@Component
@ConfigurationProperties("itsma")
public class ItsmaDeployerConfig {

  private Boolean devEnabled;
  private String master;
  private String userName;
  private String password;
  private String caCertFile;
  private String clientCertFile;
  private String clientKeyFile;

  private Integer logLevel;
  private Integer timeout;

  public Config buildConfig() {
    Config config;
    if (devEnabled) {
      config = new ConfigBuilder().withMasterUrl(master)
          .withTrustCerts(true)
          .withUsername(userName)
          .withPassword(password)
          .build();
      config.setCaCertFile(caCertFile);
      config.setClientCertFile(clientCertFile);
      config.setClientKeyFile(clientKeyFile);
      config.setConnectionTimeout(timeout);
      config.setRequestTimeout(timeout);
    } else {
      config = new ConfigBuilder().build();
    }
    return config;
  }

  public Boolean isDevEnabled() {
    return devEnabled;
  }

  public void setDevEnabled(Boolean devEnabled) {
    this.devEnabled = devEnabled;
  }

  public String getMaster() {
    return master;
  }

  public void setMaster(String master) {
    this.master = master;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getCaCertFile() {
    return caCertFile;
  }

  public void setCaCertFile(String caCertFile) {
    this.caCertFile = caCertFile;
  }

  public String getClientCertFile() {
    return clientCertFile;
  }

  public void setClientCertFile(String clientCertFile) {
    this.clientCertFile = clientCertFile;
  }

  public String getClientKeyFile() {
    return clientKeyFile;
  }

  public void setClientKeyFile(String clientKeyFile) {
    this.clientKeyFile = clientKeyFile;
  }

  public Integer getLogLevel() {
    return logLevel;
  }

  public void setLogLevel(Integer logLevel) {
    this.logLevel = logLevel;
  }

  public Integer getTimeout() {
    return timeout;
  }

  public void setTimeout(Integer timeout) {
    this.timeout = timeout;
  }
}