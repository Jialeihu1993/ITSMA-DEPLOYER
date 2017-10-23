package com.hpe.itsma.itsmaInstaller.handler;

import com.hpe.itsma.itsmaInstaller.constants.KubeResType;
import com.hpe.itsma.itsmaInstaller.service.K8sRestClient;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.fabric8.kubernetes.api.model.DoneableConfigMap;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zhongtao on 9/30/2017.
 */
@Component
public class ConfigmapHandlerImpl implements KubernetesResourceHandler {
  @Autowired
  private K8sRestClient k8sRestClient;

  @Override
  public boolean validate(KubernetesResource resource) {
    return resource instanceof ConfigMap;
  }

  @Override
  public boolean validate(KubeResType type) {
    return type.equals(KubeResType.CONFIGMAP);
  }

  @Override
  public void createResourceByYaml(String namespace, KubernetesResource resource) {
    KubernetesClient client = k8sRestClient.newKubeClient();
    ConfigMap configMap = (ConfigMap) resource;
    logger.info("Creating ConfigMap in namespace " + namespace);
    NonNamespaceOperation<ConfigMap, ConfigMapList, DoneableConfigMap, Resource<ConfigMap, DoneableConfigMap>> configMaps = client.configMaps().inNamespace(namespace);
    ConfigMap result = configMaps.create(configMap);
    logger.info("Created ConfigMap " + result.getMetadata().getName());
  }

  @Override
  public KubernetesResource getResourceByName(String namespace, String name) {
    KubernetesClient client = k8sRestClient.newKubeClient();
    return client.configMaps().inNamespace(namespace).withName(name).get();
  }
}
