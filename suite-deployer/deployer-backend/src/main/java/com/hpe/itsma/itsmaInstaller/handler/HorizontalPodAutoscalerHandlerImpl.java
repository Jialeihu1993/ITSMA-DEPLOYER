package com.hpe.itsma.itsmaInstaller.handler;

import com.hpe.itsma.itsmaInstaller.constants.KubeResType;
import com.hpe.itsma.itsmaInstaller.service.K8sRestClient;
import io.fabric8.kubernetes.api.model.DoneableHorizontalPodAutoscaler;
import io.fabric8.kubernetes.api.model.HorizontalPodAutoscaler;
import io.fabric8.kubernetes.api.model.HorizontalPodAutoscalerList;
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
public class HorizontalPodAutoscalerHandlerImpl implements KubernetesResourceHandler {
  @Autowired
  private K8sRestClient k8sRestClient;

  @Override
  public boolean validate(KubernetesResource resource) {
    return resource instanceof HorizontalPodAutoscaler;
  }

  @Override
  public boolean validate(KubeResType type) {
    return type.equals(KubeResType.HORIZONTAL_POD_AUTOSCALER);
  }

  @Override
  public void createResourceByYaml(String namespace, KubernetesResource resource) {
    KubernetesClient client = k8sRestClient.newKubeClient();
    HorizontalPodAutoscaler hpa = (HorizontalPodAutoscaler) resource;
    logger.info("Creating HorizontalPodAutoscaler in namespace " + namespace);
    NonNamespaceOperation<HorizontalPodAutoscaler, HorizontalPodAutoscalerList, DoneableHorizontalPodAutoscaler, Resource<HorizontalPodAutoscaler, DoneableHorizontalPodAutoscaler>> hpas = client.autoscaling().horizontalPodAutoscalers().inNamespace(namespace);
    HorizontalPodAutoscaler result = hpas.create(hpa);
    logger.info("Created HorizontalPodAutoscaler " + result.getMetadata().getName());
  }

  @Override
  public KubernetesResource getResourceByName(String namespace, String name) {
    KubernetesClient client = k8sRestClient.newKubeClient();
    return client.autoscaling().horizontalPodAutoscalers().inNamespace(namespace).withName(name).get();
  }
}
