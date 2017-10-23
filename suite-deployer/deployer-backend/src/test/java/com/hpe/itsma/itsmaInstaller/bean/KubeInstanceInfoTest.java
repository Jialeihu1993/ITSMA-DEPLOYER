package com.hpe.itsma.itsmaInstaller.bean;

import com.hpe.itsma.itsmaInstaller.service.ItsmaInstallerService;
import com.hpe.itsma.itsmaInstaller.util.ItsmaUtil;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by gongy on 22/05/2017.
 */

public class KubeInstanceInfoTest {

  @Test
  public void deserializeJson() throws Exception {
    ItsmaInstallerService itsmaInstallerService = new ItsmaInstallerService();
    InputStream is = KubeInstanceInfoTest.class.getResourceAsStream("kubeInstanceInfoList1.json");
    String json = IOUtils.toString(is);
    BatchInfo batchInfo = ItsmaUtil.unmarshallJson(json, BatchInfo.class);
    KubeInstanceInfo.K8sResponseBody responseBody = batchInfo.getKubeInstanceInfoList().get(0)
            .unmarshallResponseBody();
    assertThat(responseBody)
            .isNotNull();
    assertThat(responseBody.getStatus().getConditions())
            .isNotNull()
            .hasSize(3);

    KubeInstanceInfo.K8sResponseBody.Status.Condition ready = responseBody.getStatus().getConditions().stream()
            .filter(
                    condition -> condition.getType().equals("Ready")
            )
            .findFirst()
            .get();
    assertThat(ready.getStatus()).isEqualToIgnoringCase("false");


  }
}