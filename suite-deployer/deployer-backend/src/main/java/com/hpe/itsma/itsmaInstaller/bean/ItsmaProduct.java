package com.hpe.itsma.itsmaInstaller.bean;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianlib on 2/10/2017.
 */
@Component
public class ItsmaProduct {
  private String name;
  private boolean active;
  private String mode;
  private String version;
  private Vault vault;

  public ItsmaProduct() {}

  public ItsmaProduct(String name, String mode, String version) {
    this.name = name;
    this.mode = mode;
    this.version = version;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public String getMode() {
    return mode;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public Vault getVault() {
    return vault;
  }

  public void setVault(Vault vault) {
    this.vault = vault;
  }

  public class Vault {
    private boolean active;
    private String readwriteroles;
    private String readonlyroles;
    private List<Secret> secrets = new ArrayList<Secret>();

    public boolean isActive() {
      return active;
    }

    public void setActive(boolean active) {
      this.active = active;
    }

    public String getReadWriteRoles() {
      return readwriteroles;
    }

    public void setReadWriteRoles(String readwriteroles) {
      this.readwriteroles = readwriteroles;
    }

    public String getReadOnlyRoles() {
      return readonlyroles;
    }

    public void setReadOnlyRoles(String readonlyroles) {
      this.readonlyroles = readonlyroles;
    }

    public List<Secret> getSecrets() {
      return secrets;
    }

    public void setSecrets(List<Secret> secrets) {
      this.secrets = secrets;
    }

    public class Secret {
      private String secretKey;
      private String secretValue;

      public String getSecretKey() {
        return secretKey;
      }

      public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
      }

      public String getSecretValue() {
        return secretValue;
      }

      public void setSecretValue(String secretValue) {
        this.secretValue = secretValue;
      }

      @Override
      public String toString() {
        return "secret = [secretKey=" + secretKey + ", secretValue=" + secretValue + "]";
      }
    }

    @Override
    public String toString() {
      return "vault = [active=" + active + ", secrets=" + secrets + "]";
    }
  }

  @Override
  public String toString() {
    return "product = [name=" + name + ", active=" + active + ", vault=" + vault==null ? "null" : vault + "]";
  }
}
