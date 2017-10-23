package com.hpe.itsma.itsmaInstaller.handler;

import com.hpe.itsma.itsmaInstaller.constants.KubeResType;
import com.hpe.itsma.itsmaInstaller.service.K8sRestClient;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.extensions.DaemonSet;
import io.fabric8.kubernetes.api.model.extensions.DaemonSetList;
import io.fabric8.kubernetes.api.model.extensions.DoneableDaemonSet;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zhongtao on 9/30/2017.
 */
@Component
public class DaemonSetHandlerImpl implements KubernetesResourceHandler {
  @Autowired
  private K8sRestClient k8sRestClient;

  @Override
  public boolean validate(KubernetesResource resource) {
    return resource instanceof DaemonSet;
  }

  @Override
  public boolean validate(KubeResType type) {
    return type.equals(KubeResType.DAEMONSET);
  }

  @Override
  public void createResourceByYaml(String namespace, KubernetesResource resource) {
    KubernetesClient client = k8sRestClient.newKubeClient();
    DaemonSet daemonSet = (DaemonSet) resource;
    logger.info("Creating DaemonSet in namespace " + namespace);
    NonNamespaceOperation<DaemonSet, DaemonSetList, DoneableDaemonSet, Resource<DaemonSet, DoneableDaemonSet>> daemonSets = client.extensions().daemonSets().inNamespace(namespace);
    DaemonSet result = daemonSets.create(daemonSet);
    logger.info("Created DaemonSet " + result.getMetadata().getName());
  }

  @Override
  public KubernetesResource getResourceByName(String namespace, String name) {
    KubernetesClient client = k8sRestClient.newKubeClient();
    return client.extensions().daemonSets().inNamespace(namespace).withName(name).get();
  }
}
