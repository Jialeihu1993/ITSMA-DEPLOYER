package com.hpe.itsma.itsmaInstaller.bean;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by tianlib on 12/14/2016.
 */
public class KubeInstanceInfo {
  private String type;
  private String apiVersion;
  private String instanceName;
  private String namespace;
  private String yamlContent;

  private String responseBody;
  private K8sResponseBody k8sResponseBody;
  private int statusCode;
  /**
   * @param type
   * @param apiVersion
   * @param instanceName
   * @param namespace
   * @param yamlContent
   */
  public KubeInstanceInfo(String type,
                          String apiVersion,
                          String instanceName,
                          String namespace,
                          String yamlContent,
                          String responseBody) {
    this.type = type;
    this.apiVersion = apiVersion;
    this.instanceName = instanceName;
    this.namespace = namespace;
    this.yamlContent = yamlContent;
    this.responseBody = responseBody;
  }

  public String getType() {
    return type;
  }

  public String getYamlContent() {
    return yamlContent;
  }

  public void setYamlContent(String yamlContent) {
    this.yamlContent = yamlContent;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public String getInstanceName() {
    return instanceName;
  }

  public void setInstanceName(String instanceName) {
    this.instanceName = instanceName;
  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public String getResponseBody() {
    return responseBody;
  }

  public void setResponseBody(String responseBody) {
    this.responseBody = responseBody;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public K8sResponseBody unmarshallResponseBody() {
    Gson gson = new Gson();
    return gson.fromJson(responseBody, K8sResponseBody.class);
  }

  public class K8sResponseBody {
    public class Metadata {
      private String name;
      private String namespace;
      private String selfLink;
      private String uid;
      private String resourceVersion;
      private String creationTimestamp;

      public String getCreationTimestamp() {
        return creationTimestamp;
      }

      public void setCreationTimestamp(String creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
      }

      public String getName() {
        return name;
      }

      public void setName(String name) {
        this.name = name;
      }

      public String getNamespace() {
        return namespace;
      }

      public void setNamespace(String namespace) {
        this.namespace = namespace;
      }

      public String getSelfLink() {
        return selfLink;
      }

      public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
      }

      public String getUid() {
        return uid;
      }

      public void setUid(String uid) {
        this.uid = uid;
      }

      public String getResourceVersion() {
        return resourceVersion;
      }

      public void setResourceVersion(String resourceVersion) {
        this.resourceVersion = resourceVersion;
      }
    }

    class Spec {
    }

    public class Status {
      public String getPhase() {
        return phase;
      }

      public void setPhase(String phase) {
        this.phase = phase;
      }

      private String phase;

      public List<Condition> getConditions() {
        return conditions;
      }

      public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
      }

      private List<Condition> conditions;
      @Override
      public String toString() {
        return "\r\nStatus: [phase=" + phase + "，conditions=" + conditions + "]";
      }

      public class Condition {
        private String type;
        private String status;
        private String lastProbeTime;
        private String lastTransitionTime;
        private String reason;
        private String message;

        public String getType() {
          return type;
        }

        public void setType(String type) {
          this.type = type;
        }

        public String getStatus() {
          return status;
        }

        public void setStatus(String status) {
          this.status = status;
        }

        public String getLastProbeTime() {
          return lastProbeTime;
        }

        public void setLastProbeTime(String lastProbeTime) {
          this.lastProbeTime = lastProbeTime;
        }

        public String getLastTransitionTime() {
          return lastTransitionTime;
        }

        public void setLastTransitionTime(String lastTransitionTime) {
          this.lastTransitionTime = lastTransitionTime;
        }

        public String getReason() {
          return reason;
        }

        public void setReason(String reason) {
          this.reason = reason;
        }

        public String getMessage() {
          return message;
        }

        public void setMessage(String message) {
          this.message = message;
        }

        @Override
        public String toString() {
          return "\r\nStatus: [" +
                  "type=" + type +
                  "，status=" + status +
                  "，lastProbeTime=" + lastProbeTime +
                  "，lastTransitionTime=" + lastTransitionTime +
                  "，reason=" + reason +
                  "，message=" + message +
                  "]";
        }
      }

    }

    private String kind;
    private String apiVersion;
    private Metadata metadata;
    private Spec spec;
    private Status status;

    public Metadata getMetadata() {
      return metadata;
    }

    public void setMetadata(Metadata metadata) {
      this.metadata = metadata;
    }

    public Spec getSpec() {
      return spec;
    }

    public void setSpec(Spec spec) {
      this.spec = spec;
    }

    public Status getStatus() {
      return status;
    }

    public void setStatus(Status status) {
      this.status = status;
    }

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


    @Override
    public String toString() {
      return "\r\nK8sResponseBody [kind=" + kind + ", apiVersion=" + apiVersion
          + ", metadata=" + metadata + ", spec=" + spec + ", status=" + status + "]";
    }
  }


  @Override
  public String toString() {
    return "KubeInstanceInfo [type=" + type + ", apiVersion=" + apiVersion
        + ", instanceName=" + instanceName + ", namespace=" + namespace
        + ", yamlContent=" + yamlContent + ", responseBody="
        + responseBody + ", statusCode=" + statusCode + "]";
  }

}