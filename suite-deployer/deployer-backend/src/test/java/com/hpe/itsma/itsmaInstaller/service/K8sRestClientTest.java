package com.hpe.itsma.itsmaInstaller.service;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by tianlib on 7/27/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class K8sRestClientTest {
  @Rule
  public KubernetesServer server = new KubernetesServer(false);

  @Autowired
  public K8sRestClient k8sRestClient;

  private Field field = null;

  @Before
  public void setup() {
    try {
      field = k8sRestClient.getClass().getDeclaredField("kubernetesClient");
      field.setAccessible(true);
    } catch (Exception e) {
      fail("field kubernetesClient is missing");
    }
  }

  @Test
  public void testGetPodStatusByName() {
    class Case {
      String path;
      String phase;
      Boolean ready;
      String expected;

      public Case(String path, String phase, Boolean ready, String expected) {
        this.path = path;
        this.phase = phase;
        this.ready = ready;
        this.expected = expected;
      }
    }
    List<Case> cases = new ArrayList<>();
    cases.add(
        new Case(
            "/api/v1/namespaces/test/ErrorPath",
            "Running",
            true,
            "{\"kind\": \"Pod\",\"name\": \"test-pod\",\"status\": \"Pending\", \"ready\": false,\"self\": \"/itsma/kube_pods/test-pod\"}"
        )
    );
    cases.add(
        new Case(
            "/api/v1/namespaces/test/pods/test-pod",
            "Running",
            true,
            "{\"kind\": \"Pod\",\"name\": \"test-pod\",\"status\": \"Running\", \"ready\": true,\"self\": \"/itsma/kube_pods/test-pod\"}"
        )
    );
    cases.add(
        new Case(
            "/api/v1/namespaces/test/pods/test-pod",
            "Pending",
            true,
            "{\"kind\": \"Pod\",\"name\": \"test-pod\",\"status\": \"Pending\", \"ready\": false,\"self\": \"/itsma/kube_pods/test-pod\"}"
        )
    );
    cases.add(
        new Case(
            "/api/v1/namespaces/test/pods/test-pod",
            "Succeeded",
            true,
            "{\"kind\": \"Pod\",\"name\": \"test-pod\",\"status\": \"Succeeded\", \"ready\": false,\"self\": \"/itsma/kube_pods/test-pod\"}"
        )
    );
    cases.add(
        new Case(
            "/api/v1/namespaces/test/pods/test-pod",
            "Failed",
            true,
            "{\"kind\": \"Pod\",\"name\": \"test-pod\",\"status\": \"Failed\", \"ready\": false,\"self\": \"/itsma/kube_pods/test-pod\"}"
        )
    );

    cases.stream().forEach(
      aCase -> {
          PodBuilder pb = new PodBuilder();
          List<ContainerStatus> statuses = new ArrayList<>();
          statuses.add(new ContainerStatus("xxx", "xxx", "xxx", null, "xxx", aCase.ready, 0, null));
          PodStatus podStatus = new PodStatus();
          podStatus.setPhase(aCase.phase);
          podStatus.setContainerStatuses(statuses);
          pb.withStatus(podStatus);
          server.expect().withPath(aCase.path)
              .andReturn(200, pb.build()).once();

        try {
          field.set(k8sRestClient, server.getClient());
          assertThat(k8sRestClient.getPodStatusByName("test", "test-pod").toJson()).isEqualToIgnoringCase(aCase.expected);
        } catch (Exception e) {
          fail("case failed because of exception: " + e.getMessage());
        }
      }
    );

  }

//  @Test
//  public void testGetServiceStatusByName() {
//    class Case {
//      String path;
//      String phase;
//      Boolean ready;
//      String expected;
//
//      public Case(String path, String phase, Boolean ready, String expected) {
//        this.path = path;
//        this.phase = phase;
//        this.ready = ready;
//        this.expected = expected;
//      }
//    }
//    List<Case> cases = new ArrayList<>();
//    cases.add(
//        new Case(
//            "/api/v1/namespaces/test/ErrorPath",
//            "Running",
//            true,
//            "{\"kind\": \"Service\",\"name\": \"test-svc\",\"status\": \"Pending\", \"ready\": false,\"self\": \"/itsma/kube_services/test-svc\"}"
//        )
//    );
//    cases.add(
//        new Case(
//            "/api/v1/namespaces/test/services/test-svc",
//            "Running",
//            true,
//            "{\"kind\": \"Service\",\"name\": \"test-svc\",\"status\": \"Running\", \"ready\": true,\"self\": \"/itsma/kube_services/test-svc\"}"
//        )
//    );
//    cases.add(
//        new Case(
//            "/api/v1/namespaces/test/services/test-svc",
//            "Pending",
//            true,
//            "{\"kind\": \"Service\",\"name\": \"test-svc\",\"status\": \"Pending\", \"ready\": false,\"self\": \"/itsma/kube_services/test-svc\"}"
//        )
//    );
//    cases.add(
//        new Case(
//            "/api/v1/namespaces/test/services/test-svc",
//            "Succeeded",
//            true,
//            "{\"kind\": \"Service\",\"name\": \"test-svc\",\"status\": \"Succeeded\", \"ready\": false,\"self\": \"/itsma/kube_services/test-svc\"}"
//        )
//    );
//    cases.add(
//        new Case(
//            "/api/v1/namespaces/test/services/test-svc",
//            "Failed",
//            true,
//            "{\"kind\": \"Service\",\"name\": \"test-svc\",\"status\": \"Failed\", \"ready\": false,\"self\": \"/itsma/kube_services/test-svc\"}"
//        )
//    );
//
//    cases.stream().forEach(
//        aCase -> {
//          ServiceBuilder sb = new ServiceBuilder();
//          ServiceSpec ss = new ServiceSpec();
//          Map<String, String> selector = new HashMap<String, String>();
//          selector.put("test-svc", "test-svc");
//          ss.setSelector(selector);
//          sb.withSpec(ss);
//          server.expect().withPath("/api/v1/namespaces/test/services/test-svc").andReturn(200, sb.build()).once();
//
//          DeploymentBuilder db = new DeploymentBuilder();
//
//          PodBuilder pb = new PodBuilder();
//          List<ContainerStatus> statuses = new ArrayList<>();
//          statuses.add(new ContainerStatus("xxx", "xxx", "xxx", null, "xxx", aCase.ready, 0, null));
//          PodStatus podStatus = new PodStatus();
//          podStatus.setPhase(aCase.phase);
//          podStatus.setContainerStatuses(statuses);
//          pb.withStatus(podStatus).withSpec(new PodSpec());
//          server.expect().withPath(aCase.path)
//              .andReturn(200, pb.build()).once();
//
//          try {
//            field.set(k8sRestClient, server.getClient());
//            assertThat(k8sRestClient.getPodStatusByName("test", "test-pod").toJson()).isEqualToIgnoringCase(aCase.expected);
//          } catch (Exception e) {
//            fail("case failed because of exception: " + e.getMessage());
//          }
//        }
//    );
//  }

  @Test
  public void testListConfigMapByNamespace() {
    server.expect().withPath("/api/v1/namespaces/test/configmaps").andReturn(200, new ConfigMapListBuilder()
        .addNewItem().and().addNewItem().and().build()).once();

    try {
      field.set(k8sRestClient, server.getClient());
      assertThat(k8sRestClient.listConfigMapByNamespace("test")).isNotNull();
    } catch (Exception e) {
      fail("case failed because of exception: " + e.getMessage());
    }

  }

  @Test
  public void testCreateResourceByYaml() {
    server.expect().post().withPath("/api/v1/namespaces/itsma1/pods").andReturn(201, new PodBuilder()
        .withNewMetadata().withResourceVersion("12345").and().build()).once();

    try{
      field.set(k8sRestClient, server.getClient());
      InputStream is = K8sRestClientTest.class.getClassLoader()
          .getResourceAsStream("com.hpe.itsma.itsmaInstaller.service/itom-sm-deploy-controller.yaml");
      String yamlPath = K8sRestClientTest.class.getClassLoader().getResource("com.hpe.itsma.itsmaInstaller.service/itom-sm-deploy-controller.yaml").getPath();
      k8sRestClient.createResourceByYaml(yamlPath, "itsma1");
    } catch (Exception e) {
      fail("case failed because of exception: " + e.getMessage());
    }
  }

  @Test
  public void testDeleteResourceByYaml() {
    server.expect().delete().withPath("/api/v1/namespaces/itsma1/pods/itom-sm-deploy-controller-pod").andReturn(200, true).once();

    try{
      field.set(k8sRestClient, server.getClient());
      InputStream is = K8sRestClientTest.class.getClassLoader()
          .getResourceAsStream("com.hpe.itsma.itsmaInstaller.service/itom-sm-deploy-controller.yaml");
      assertThat(k8sRestClient.deleteResourceByYaml(is)).isTrue();
    } catch (Exception e) {
      fail("case failed because of exception: " + e.getMessage());
    }
  }
}
