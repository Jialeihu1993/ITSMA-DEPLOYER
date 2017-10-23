package com.hpe.itsma.itsmaInstaller.bean;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by tianlib on 2/21/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class RuntimeProfileTest {

  @Autowired
  private RuntimeProfile runtimeProfile;

  @Test
  public void test_properties() {
    assertThat(runtimeProfile.isStandlone()).isTrue();
    assertThat(runtimeProfile.getTimezone()).isEqualTo("Asia/Shanghai");
    assertThat(runtimeProfile.getSuite_label_key()).isEqualTo("Worker");
    assertThat(runtimeProfile.getSuite_label_value()).isEqualTo("label");

    assertThat(runtimeProfile.getItsma_admin_key()).isEqualTo("idm_itsma_admin_password");

    assertThat(runtimeProfile.getSuite_install_backend_url()).isEqualTo("http://suite-conf-svc-itsma.core.svc.cluster.local:8081");

    assertThat(runtimeProfile.getBuiltin_config_path()).isEqualTo("/app/itsma-deployer/config");
    assertThat(runtimeProfile.getItsma_services_path()).isEqualTo("/yamls_output");

    assertThat(runtimeProfile.getDefault_registry_url()).isEqualTo("shc-harbor-dev.hpeswlab.net/itsma");
  }
}
