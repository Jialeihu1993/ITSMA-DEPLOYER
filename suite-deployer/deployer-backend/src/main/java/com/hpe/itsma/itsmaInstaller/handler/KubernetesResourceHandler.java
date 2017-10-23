package com.hpe.itsma.itsmaInstaller.handler;

import com.hpe.itsma.itsmaInstaller.constants.KubeResType;
import com.hpe.itsma.itsmaInstaller.service.K8sRestClient;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by zhongtao on 9/29/2017.
 */
public interface KubernetesResourceHandler {
  Log logger = LogFactory.getLog(KubernetesResourceHandler.class);

  boolean validate(KubernetesResource resource);

  boolean validate(KubeResType type);

  void createResourceByYaml(String namespace, KubernetesResource resource);

  KubernetesResource getResourceByName(String namespace, String name);
}
