package com.hpe.itsma.itsmaInstaller.service;

import com.hpe.itsma.itsmaInstaller.bean.ItsmaDeployerConfig;
import com.hpe.itsma.itsmaInstaller.bean.KubeResourceStatus;
import com.hpe.itsma.itsmaInstaller.constants.KubeResType;
import com.hpe.itsma.itsmaInstaller.exception.K8sResTypeUnsupportedException;
import com.hpe.itsma.itsmaInstaller.handler.*;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.utils.Serialization;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tianlib on 6/28/2017.
 */
@Component
public class K8sRestClient {
  Log logger = LogFactory.getLog(K8sRestClient.class);

  @Autowired
  ItsmaDeployerConfig itsmaDeployerConfig;

  @Autowired
  private ConfigmapHandlerImpl configmapHandler;

  @Autowired
  private SecretHandlerImpl secretHandler;

  @Autowired
  private PersistentVolumeClaimHandlerImpl persistentVolumeClaimHandler;

  @Autowired
  private ServiceHandlerImpl serviceHandler;

  @Autowired
  private PodHandlerImpl podHandler;

  @Autowired
  private DeploymentHandlerImpl deploymentHandler;

  @Autowired
  private DaemonSetHandlerImpl daemonSetHandler;

  @Autowired
  private IngressHandlerImpl ingressHandler;

  @Autowired
  private HorizontalPodAutoscalerHandlerImpl horizontalPodAutoscalerHandler;

  private KubernetesClient kubernetesClient = null;

  public KubeResType getKubeResType(String kind) {
    KubeResType type = KubeResType.valueOf(kind.toUpperCase());
    return type;
  }

  public KubernetesClient newKubeClient() {
    if (this.kubernetesClient != null) {
      return this.kubernetesClient;
    }

    return new DefaultKubernetesClient(itsmaDeployerConfig.buildConfig());
  }

  public KubeResourceStatus getPodStatusByName(String namespace, String podName) {
    try {
      KubernetesClient client = newKubeClient();
      PodResource<Pod, DoneablePod> podResource = client.pods().inNamespace(namespace).withName(podName);
      if (podResource == null) {
        logger.error(String.format("===GetPodStatus with (namespace=%s, podname=%s): Success. PodResource: %s not found. " +
          "Return 'Pending' status.", namespace, podName, podName));
        return new KubeResourceStatus("Pod", podName, "Pending", false, "/itsma/kube_pods/" + podName);
      }

      Pod pod = podResource.get();
      if (pod == null) {
        logger.error(String.format("===GetPodStatus with (namespace=%s, podname=%s): Success. Pod: %s not found. " +
          "Return 'Pending' status.", namespace, podName, podName));
        return new KubeResourceStatus("Pod", podName, "Pending", false, "/itsma/kube_pods/" + podName);
      }

      PodStatus podStatus = pod.getStatus();
      if (podStatus == null
        || podStatus.getPhase() == null
        || podStatus.getPhase().isEmpty()
        || podStatus.getContainerStatuses().isEmpty()) {
        logger.error(String.format("===GetPodStatus with (namespace=%s, podname=%s): Success. Pod: %s exists, but can NOT get its status. " +
          "Return 'Failed' status.", namespace, podName, podName));
        return new KubeResourceStatus("Pod", podName, "Failed", false, "/itsma/kube_pods/" + podName);
      }

      if (podStatus.getPhase().equalsIgnoreCase("Pending")) {
        logger.warn(String.format("===GetPodStatus with (namespace=%s, podname=%s): Success. Pod: %s is pending. Return 'Pending' status. " +
          "PodStatus: %s.", namespace, podName, podName, pod.toString()));
        return new KubeResourceStatus("Pod", podName, "Pending", false, "/itsma/kube_pods/" + podName);
      }

      if (podStatus.getPhase().equalsIgnoreCase("Running")) {
        if (podStatus.getContainerStatuses().stream().allMatch(
          containerStatus -> containerStatus.getReady()
        )) {
          logger.info(String.format("===GetPodStatus with (namespace=%s, podname=%s): Success. Pod: %s is running. " +
            "All containers in the pod are ready. Return 'Running' status and 'Ready' is true.", namespace, podName, podName));
          return new KubeResourceStatus("Pod", podName, "Running", true, "/itsma/kube_pods/" + podName);
        }

        logger.warn(String.format("===GetPodStatus with (namespace=%s, podname=%s): Success. Pod: %s is running. " +
          "But some containers in the pod are NOT ready. Return 'Failed' status. PodStatus: %s.", namespace, podName, podName, pod.toString()));
        return new KubeResourceStatus("Pod", podName, "Failed", false, "/itsma/kube_pods/" + podName);
      }

      if (podStatus.getPhase().equalsIgnoreCase("Succeeded")) {
        logger.info(String.format("===GetPodStatus with (namespace=%s, podname=%s): Success. Pod: %s is Succeeded. " +
          "Return 'Succeeded' status.", namespace, podName, podName));
        return new KubeResourceStatus("Pod", podName, "Succeeded", false, "/itsma/kube_pods/" + podName);
      }

      logger.error(String.format("===GetPodStatus with (namespace=%s, podname=%s): Success. Pod: %s is failed. " +
        "Return 'Failed' status. PodStatus: %s.", namespace, podName, podName, pod.toString()));
      return new KubeResourceStatus("Pod", podName, "Failed", false, "/itsma/kube_pods/" + podName);
    } catch (KubernetesClientException e) {
      e.printStackTrace();
      logger.error(String.format("===GetPodStatus with (namespace=%s, podname=%s): Failed. Found exception: %s. " +
        "Return 'Failed' status.", namespace, podName, e.getMessage()));
      return new KubeResourceStatus("Pod", podName, "Failed", false, "/itsma/kube_pods/" + podName);
    }
  }

  /**
   * Return the status of the specified k8s service by name
   * <p>
   * we are talking about k8s service that an abstraction of a set of pods, suppose those pods doing
   * same job, so the service will be in ready status when at least one of those pods are in running status and
   * the ready property is true.
   *
   * @param svcName
   * @return
   * @throws Exception
   */
  public KubeResourceStatus getServiceStatusByName(String namespace, String svcName) {
    try {
      KubernetesClient client = newKubeClient();
      Service service = client.services().inNamespace(namespace).withName(svcName).get();
      if (service != null && service.getSpec() != null) {
        List<Pod> pods = client.pods().inNamespace(namespace).withLabels(service.getSpec().getSelector()).list().getItems();
        // return 'Pending' if no pod present
        if (pods.isEmpty()) {
          logger.info(String.format("===GetServiceStatus with (namespace=%s, servicename=%s): Success. Service: %s exists, but it's empty." +
            "Return 'Pending' status.", namespace, svcName, svcName));
          return new KubeResourceStatus("Service", svcName, "Pending", false, "/itsma/kube_services/" + svcName);
        }

        // return 'Running' if any pod is ready
        if (pods.stream().anyMatch(
          pod -> {
            logger.debug(String.format("===The status of pod: [%s]: %s", pod.getMetadata().getName(), pod.toString()));
            KubeResourceStatus kubeResourceStatus = getPodStatusByName(namespace, pod.getMetadata().getName());
            if (kubeResourceStatus.getStatus().equalsIgnoreCase("Running") && kubeResourceStatus.getReady()) {
              return true;
            }
            return false;
          })) {
          logger.info(String.format("===GetServiceStatus with (namespace=%s, servicename=%s): Success. Service: %s is ready. " +
            "Return 'Running' status and 'Ready' is true.", namespace, svcName, svcName));
          return new KubeResourceStatus("Service", svcName, "Running", true, "/itsma/kube_services/" + svcName);
        }

        // return 'Failed' when pos exists but not ready
        logger.warn(String.format("===GetServiceStatus with (namespace=%s, servicename=%s): Success. No pod is ready. " +
          "Return 'Failed' status", namespace, svcName, svcName));
        return new KubeResourceStatus("Service", svcName, "Failed", false, "/itsma/kube_services/" + svcName);
      } else { // Not found service
        logger.error(String.format("===GetServiceStatus with (namespace=%s, servicename=%s): Success. Service: %s not found. " +
          "Return 'Pending' status", namespace, svcName, svcName));
        return new KubeResourceStatus("Service", svcName, "Pending", false, "/itsma/kube_services/" + svcName);
      }
    } catch (KubernetesClientException e) {
      e.printStackTrace();
      logger.error(String.format("===GetServiceStatus with (namespace=%s, servicename=%s): Failed. Found Exception: %s. " +
        "Return 'Failed' status", namespace, svcName, e.getMessage()));
      return new KubeResourceStatus("Service", svcName, "Failed", false, "/itsma/kube_services/" + svcName);
    }
  }

  public List<ConfigMap> listConfigMapByNamespace(String namespace) {
    try {
      List<ConfigMap> configMaps = new ArrayList<>();
      KubernetesClient client = newKubeClient();
      configMaps = client.configMaps().inNamespace(namespace).list().getItems();
      logger.info("Totally " + configMaps.size() + " ConfigMap is found.");
      return configMaps;
    } catch (KubernetesClientException e) {
      logger.error(String.format("===listConfigMapByNamespace with (namespace=%s): Failed. Found Exception: %s.", namespace, e.getMessage()));
      return null;
    }
  }

  /**
   * updateOrNewDataOfConfigMap adds data or update the exising data in ConfigMap
   *
   * @param namespace     namespace that the configmap belongs to
   * @param configMapName the name of the configmap
   * @param data          the data needs be added/updated
   * @throws Exception
   */
  public void updateOrNewDataOfConfigMap(String namespace, String configMapName, Map<String, String> data) throws Exception {
    logger.info("updateOrNewDataOfConfigMap in namespace [" + namespace + "], configMapName [" + configMapName + "]" + ", data [" + data.toString() + "].");
    try {
      KubernetesClient client = newKubeClient();
      client.configMaps().inNamespace(namespace).withName(configMapName).edit().addToData(data).done();
    } catch (KubernetesClientException e) {
      logger.error(String.format("===updateConfigMap with (namespace=%s, name=%s, data=%s): Failed. Found Exception: %s.", namespace, configMapName, data, e.getMessage()));
      throw e;
    }
  }

  /**
   * queryConfigMap get the value of the specified key in the given ConfigMap
   *
   * @param namespace namespace that the configmap belongs to
   * @param name      the name of the configmap
   * @param key       the key to query
   * @return value if found, null if not found
   * @throws Exception
   */
  public String queryConfigMap(String namespace, String name, String key) throws Exception {
    try {
      KubernetesClient client = newKubeClient();
      return client.configMaps().inNamespace(namespace).withName(name).get().getData().get(key);
    } catch (KubernetesClientException e) {
      logger.error(String.format("===queryConfigMap with (namespace=%s, name=%s, key=%s): Failed. Found Exception: %s.",
        namespace, name, key, e.getMessage()));
      throw e;
    } catch (NullPointerException npe) {
      logger.warn(String.format("===queryConfigMap with (namespace=%s, name=%s, key=%s): nothing found.",
        namespace, name, key));
      return null;
    }
  }

  /**
   * Create k8s resource by yaml. The method can create k8s kind
   * <p>
   * <li>ConfigMap</li>
   * <li>Secret</li>
   * <li>PersistentVolumeClaim</li>
   * <li>Service</li>
   * <li>Pod</li>
   * <li>Deployment</li>
   * <li>DaemonSet</li>
   * <li>Ingress</li>
   * <li>HorizontalPodAutoscaler</li>
   * </p>
   * <p>If you want to create <li>PersistentVolume</li>, please use this method {@link K8sRestClient#createResourceByYaml(java.lang.String)}</p>
   *
   * @param fileName
   * @param namespace
   * @throws FileNotFoundException
   */
  public void createResourceByYaml(String fileName, String namespace) throws FileNotFoundException {
    KubernetesClient client = newKubeClient();
    List<HasMetadata> resources = client.load(new FileInputStream(fileName)).get();
    HasMetadata resource = resources.get(0);

    KubernetesResourceHandler[] kubernetesResourceHandlers = {
      configmapHandler,
      secretHandler,
      persistentVolumeClaimHandler,
      serviceHandler,
      podHandler,
      deploymentHandler,
      daemonSetHandler,
      ingressHandler,
      horizontalPodAutoscalerHandler
    };
    for (KubernetesResourceHandler handler : kubernetesResourceHandlers) {
      if (handler.validate(resource)) {
        handler.createResourceByYaml(namespace, resource);
        return;
      }
    }

    logger.error("Loaded resource is not a ConfigMap/Secret/PersistentVolumeClaim/Service/Pod/Deployment/DaemonSet/Ingress/HorizontalPodAutoscaler! " + resource);
    throw new IllegalArgumentException();
  }

  /**
   * Create k8s resource by yaml. The method can create k8s kind <li>PersistentVolume</li>
   * If you want to create
   * <p>
   * <li>ConfigMap</li>
   * <li>Secret</li>
   * <li>PersistentVolumeClaim</li>
   * <li>Service</li>
   * <li>Pod</li>
   * <li>Deployment</li>
   * <li>DaemonSet</li>
   * <li>Ingress</li>
   * <li>HorizontalPodAutoscaler</li>
   * </p>,
   * please use this method {@link K8sRestClient#createResourceByYaml(java.lang.String, java.lang.String)}
   *
   * @param fileName
   * @throws FileNotFoundException
   */
  public void createResourceByYaml(String fileName) throws FileNotFoundException {
    KubernetesClient client = newKubeClient();
    List<HasMetadata> resources = client.load(new FileInputStream(fileName)).get();
    HasMetadata resource = resources.get(0);
    if (resource instanceof PersistentVolume) {
      PersistentVolume persistentVolume = (PersistentVolume) resource;
      logger.info("Creating PersistentVolume");
      NonNamespaceOperation<PersistentVolume, PersistentVolumeList, DoneablePersistentVolume, Resource<PersistentVolume, DoneablePersistentVolume>> persistentVolumes = client.persistentVolumes();
      PersistentVolume result = persistentVolumes.create(persistentVolume);
      logger.info("Created PersistentVolume " + result.getMetadata().getName());
    } else {
      logger.error("Loaded resource is not a PersistentVolume! " + resource);
      throw new IllegalArgumentException();
    }
  }

  /**
   * Get k8s resource by name. The method can get k8s kind
   * <p>
   * <li>ConfigMap</li>
   * <li>Secret</li>
   * <li>PersistentVolumeClaim</li>
   * <li>Service</li>
   * <li>Pod</li>
   * <li>Deployment</li>
   * <li>DaemonSet</li>
   * <li>Ingress</li>
   * <li>HorizontalPodAutoscaler</li>
   * </p>
   * <p>If you want to get <li>PersistentVolume</li>, please use this method {@link K8sRestClient#getResourceByName(java.lang.String, com.hpe.itsma.itsmaInstaller.constants.KubeResType)}</p>
   *
   * @param namespace
   * @param name
   * @param type
   * @return
   * @throws K8sResTypeUnsupportedException
   */
  public KubernetesResource getResourceByName(String namespace, String name, KubeResType type) throws K8sResTypeUnsupportedException {
    KubernetesResourceHandler[] kubernetesResourceHandlers = {
      configmapHandler,
      secretHandler,
      persistentVolumeClaimHandler,
      serviceHandler,
      podHandler,
      deploymentHandler,
      daemonSetHandler,
      ingressHandler,
      horizontalPodAutoscalerHandler
    };
    for (KubernetesResourceHandler handler : kubernetesResourceHandlers) {
      if (handler.validate(type)) {
        return handler.getResourceByName(namespace, name);
      }
    }
    logger.error("Current KubeResType is [" + type + "]. Not supported.");
    throw new K8sResTypeUnsupportedException("Unsupported kubernetes resource type");
  }

  /**
   * Get k8s resource by yaml. The method can get k8s kind <li>PersistentVolume</li>
   * If you want to get
   * <p>
   * <li>ConfigMap</li>
   * <li>Secret</li>
   * <li>PersistentVolumeClaim</li>
   * <li>Service</li>
   * <li>Pod</li>
   * <li>Deployment</li>
   * <li>DaemonSet</li>
   * <li>Ingress</li>
   * <li>HorizontalPodAutoscaler</li>
   * </p>,
   * please use this method {@link K8sRestClient#getResourceByName(java.lang.String, java.lang.String, com.hpe.itsma.itsmaInstaller.constants.KubeResType)}
   *
   * @param name
   * @param type
   * @return
   * @throws K8sResTypeUnsupportedException
   */
  public KubernetesResource getResourceByName(String name, KubeResType type) throws K8sResTypeUnsupportedException {
    KubernetesResource resource = null;
    KubernetesClient client = newKubeClient();
    if (type.equals(KubeResType.PV)) {
      resource = client.persistentVolumes().withName(name).get();
    } else {
      logger.error("Current KubeResType is [" + type + "]. Not supported. Please use com.hpe.itsma.itsmaInstaller.service.K8sRestClient.getResourceByName(String namespace, String name, KubeResType type) and try again.");
      throw new K8sResTypeUnsupportedException("Unsupported kubernetes resource type");
    }
    return resource;
  }

  public boolean restartPodsByDeployment(String namespace, Deployment deployment) {
    boolean succeed = true;
    String deploymentName = null;
    try {
      if (deployment == null) {
        succeed = false;
      } else {
        KubernetesClient client = newKubeClient();
        deploymentName = deployment.getMetadata().getName();
        logger.info("Updating deployment [" + deploymentName + "] to restart pod");
        int replicas = deployment.getSpec().getReplicas();
        if (replicas == 0) {
          replicas = 1;
        }
        client.extensions().deployments().inNamespace(namespace).withName(deploymentName).edit().editSpec().withReplicas(0).endSpec().done();
        client.extensions().deployments().inNamespace(namespace).withName(deploymentName).edit().editSpec().withReplicas(replicas).endSpec().done();
        logger.info("Restart pods of deployment [ " + deploymentName + " ] successfully. ");
      }
    } catch (KubernetesClientException e) {
      succeed = false;
      logger.error("KubernetesClientException on restarting pods of deployment [" + deploymentName + "]: " + e.toString());
    }
    return succeed;
  }

  public Boolean deleteResourceByYaml(InputStream is) {
    try {
      KubernetesClient client = newKubeClient();
      return client.load(is).delete();
    } catch (KubernetesClientException e) {
      try {
        logger.error(String.format("===DeleteResource with (yaml=%s): Failed. Found Exception: %s.", IOUtils.toString(is), e.getMessage()));
      } catch (IOException ee) {
        logger.error(String.format("===DeleteResource: Failed. Found Exception: %s.", e.getMessage()));
      }
      return false;
    }
  }

  /**
   * loadConfigMapFromYaml return a list of ConfigMap by parsing a ConfigMap yaml
   *
   * @param is
   * @return a list of ConfigMap
   */
  public List<ConfigMap> loadConfigMapFromYaml(InputStream is) {
    List<ConfigMap> configMaps = new ArrayList<>();
    try {
      KubernetesClient client = newKubeClient();
      List<HasMetadata> resources = client.load(is).get();
      if (resources.isEmpty()) {
        logger.info("===LoadConfigMapFromYaml. No resources loaded.");
        return configMaps;
      }
      resources.stream().forEach(
        resource -> {
          if (resource instanceof ConfigMap) {
            configMaps.add((ConfigMap) resource);
          }
        }
      );
      return configMaps;
    } catch (KubernetesClientException e) {
      logger.warn("===LoadConfigMapFromYaml: Failed. Exception: " + e.getMessage());
      e.printStackTrace();
    }

    return configMaps;
  }

  public String dumpResourceAsYaml(String namespace) {
    try {
      KubernetesClient client = newKubeClient();
      ServiceList services = client.services().inNamespace(namespace).list();
      return Serialization.asYaml(services);
    } catch (KubernetesClientException e) {
      logger.error(String.format("===DumpResourceAsYaml: Failed. Found Exception: %s.", e.getMessage()));
      return "";
    }
  }

  public void deleteResource(String namespace, String label, KubeResType type) throws Exception {
    try {
      KubernetesClient client = newKubeClient();
      switch (type) {
        case CONFIGMAP:
          client.configMaps().inNamespace(namespace).withLabel(label).delete();
          break;
        case SECRET:
          client.secrets().inNamespace(namespace).withLabel(label).delete();
          break;
        case PV:
          client.persistentVolumes().withLabel(label).delete();
          break;
        case PVC:
          client.persistentVolumeClaims().inNamespace(namespace).withLabel(label).delete();
          break;
        case SERVICE:
          client.services().inNamespace(namespace).withLabel(label).delete();
          break;
        case POD:
          client.pods().inNamespace(namespace).withLabel(label).delete();
        case DEPLOYMENT:
          client.extensions().deployments().inNamespace(namespace).withLabel(label).delete();
          break;
        case DAEMONSET:
          client.extensions().daemonSets().inNamespace(namespace).withLabel(label).delete();
          break;
        case INGRESS:
          client.extensions().ingresses().inNamespace(namespace).withLabel(label).delete();
          break;
        default:
          throw new K8sResTypeUnsupportedException("Unsupported kubernetes resource type");
      }
    } catch (KubernetesClientException e) {
      logger.error(String.format("===DeleteResource: Failed. Found Exception: %s.", e.getMessage()));
      throw e;
    }
  }

  public void deleteResourceByName(String namespace, String name, KubeResType type) throws K8sResTypeUnsupportedException {
    try {
      KubernetesClient client = newKubeClient();
      switch (type) {
        case CONFIGMAP:
          client.configMaps().inNamespace(namespace).withName(name).delete();
          break;
        case SECRET:
          client.secrets().inNamespace(namespace).withName(name).delete();
          break;
        case PV:
          client.persistentVolumes().withName(name).delete();
          break;
        case PVC:
          client.persistentVolumeClaims().inNamespace(namespace).withName(name).delete();
          break;
        case SERVICE:
          client.services().inNamespace(namespace).withName(name).delete();
          break;
        case POD:
          client.pods().inNamespace(namespace).withName(name).delete();
        case DEPLOYMENT:
          client.extensions().deployments().inNamespace(namespace).withName(name).delete();
          break;
        case DAEMONSET:
          client.extensions().daemonSets().inNamespace(namespace).withName(name).delete();
          break;
        case INGRESS:
          client.extensions().ingresses().inNamespace(namespace).withName(name).delete();
          break;
        default:
          throw new K8sResTypeUnsupportedException("Unsupported kubernetes resource type");
      }
    } catch (KubernetesClientException e) {
      logger.error(String.format("===DeleteResource: Failed. Found Exception: %s.", e.getMessage()));
      throw e;
    }
  }

  /**
   * isServiceRunning check if the given Itsma service is running or not
   *
   * @param namespace   namespace that the configmap belongs to
   * @param serviceName the name of Itsma service
   * @return
   * @throws Exception
   */
  public boolean isServiceRunning(String namespace, String serviceName) throws Exception {
    try {
      KubernetesClient client = newKubeClient();
      return !client.pods().inNamespace(namespace).withLabel("itsmaService=" + serviceName).list().getItems().isEmpty() ||
        !client.services().inNamespace(namespace).withLabel("itsmaService=" + serviceName).list().getItems().isEmpty() ||
        !client.extensions().ingresses().inNamespace(namespace).withLabel("itsmaService=" + serviceName).list().getItems().isEmpty();
    } catch (KubernetesClientException e) {
      logger.error(String.format("===isServiceRunning: Failed. Found Exception: %s.", e.getMessage()));
      throw e;
    }
  }

  public Boolean restartService(String serviceName) {
    try {
      KubernetesClient client = newKubeClient();
      Service service = client.services().withName(serviceName).get();
      client.resource(service).delete();

      client.resource(service).createOrReplace();
      return true;
    } catch (KubernetesClientException e) {
      return false;
    }
  }
}


