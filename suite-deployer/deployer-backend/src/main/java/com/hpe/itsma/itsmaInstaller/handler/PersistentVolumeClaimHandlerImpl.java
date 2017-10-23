package com.hpe.itsma.itsmaInstaller.handler;

import com.hpe.itsma.itsmaInstaller.constants.KubeResType;
import com.hpe.itsma.itsmaInstaller.service.K8sRestClient;
import io.fabric8.kubernetes.api.model.DoneablePersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zhongtao on 9/30/2017.
 */
@Component
public class PersistentVolumeClaimHandlerImpl implements KubernetesResourceHandler {

  @Autowired
  private K8sRestClient k8sRestClient;

  @Override
  public boolean validate(KubernetesResource resource) {
    return resource instanceof PersistentVolumeClaim;
  }

  @Override
  public boolean validate(KubeResType type) {
    return type.equals(KubeResType.PVC);
  }

  @Override
  public void createResourceByYaml(String namespace, KubernetesResource resource) {
    KubernetesClient client = k8sRestClient.newKubeClient();
    PersistentVolumeClaim persistentVolumeClaim = (PersistentVolumeClaim) resource;
    logger.info("Creating PersistentVolumeClaim in namespace " + namespace);
    NonNamespaceOperation<PersistentVolumeClaim, PersistentVolumeClaimList, DoneablePersistentVolumeClaim, Resource<PersistentVolumeClaim, DoneablePersistentVolumeClaim>> persistentVolumeClaims = client.persistentVolumeClaims().inNamespace(namespace);
    PersistentVolumeClaim result = persistentVolumeClaims.create(persistentVolumeClaim);
    logger.info("Created PersistentVolumeClaim " + result.getMetadata().getName());
  }

  @Override
  public KubernetesResource getResourceByName(String namespace, String name) {
    KubernetesClient client = k8sRestClient.newKubeClient();
    return client.persistentVolumeClaims().inNamespace(namespace).withName(name).get();
  }
}
