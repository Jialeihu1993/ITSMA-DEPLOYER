package com.hpe.itsma.itsmaInstaller.handler;

import com.hpe.itsma.itsmaInstaller.constants.KubeResType;
import com.hpe.itsma.itsmaInstaller.service.K8sRestClient;
import io.fabric8.kubernetes.api.model.DoneablePod;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.PodResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zhongtao on 9/30/2017.
 */
@Component
public class PodHandlerImpl implements KubernetesResourceHandler {
  @Autowired
  private K8sRestClient k8sRestClient;

  @Override
  public boolean validate(KubernetesResource resource) {
    return resource instanceof Pod;
  }

  @Override
  public boolean validate(KubeResType type) {
    return type.equals(KubeResType.POD);
  }

  @Override
  public void createResourceByYaml(String namespace, KubernetesResource resource) {
    KubernetesClient client = k8sRestClient.newKubeClient();
    Pod pod = (Pod) resource;
    logger.info("Creating Pod in namespace " + namespace);
    NonNamespaceOperation<Pod, PodList, DoneablePod, PodResource<Pod, DoneablePod>> pods = client.pods().inNamespace(namespace);
    Pod result = pods.create(pod);
    logger.info("Created Pod " + result.getMetadata().getName());
  }

  @Override
  public KubernetesResource getResourceByName(String namespace, String name) {
    KubernetesClient client = k8sRestClient.newKubeClient();
    return client.pods().inNamespace(namespace).withName(name).get();
  }
}
