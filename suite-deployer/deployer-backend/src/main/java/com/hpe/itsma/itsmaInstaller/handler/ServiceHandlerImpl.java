package com.hpe.itsma.itsmaInstaller.handler;

import com.hpe.itsma.itsmaInstaller.constants.KubeResType;
import com.hpe.itsma.itsmaInstaller.service.K8sRestClient;
import io.fabric8.kubernetes.api.model.DoneableService;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zhongtao on 9/30/2017.
 */
@Component
public class ServiceHandlerImpl implements KubernetesResourceHandler {
  @Autowired
  private K8sRestClient k8sRestClient;

  @Override
  public boolean validate(KubernetesResource resource) {
    return resource instanceof Service;
  }

  @Override
  public boolean validate(KubeResType type) {
    return type.equals(KubeResType.SERVICE);
  }

  @Override
  public void createResourceByYaml(String namespace, KubernetesResource resource) {
    KubernetesClient client = k8sRestClient.newKubeClient();
    Service service = (Service) resource;
    logger.info("Creating Service in namespace " + namespace);
    NonNamespaceOperation<Service, ServiceList, DoneableService, Resource<Service, DoneableService>> services = client.services().inNamespace(namespace);
    Service result = services.create(service);
    logger.info("Created Service " + result.getMetadata().getName());
  }

  @Override
  public KubernetesResource getResourceByName(String namespace, String name) {
    KubernetesClient client = k8sRestClient.newKubeClient();
    return client.services().inNamespace(namespace).withName(name).get();
  }
}
