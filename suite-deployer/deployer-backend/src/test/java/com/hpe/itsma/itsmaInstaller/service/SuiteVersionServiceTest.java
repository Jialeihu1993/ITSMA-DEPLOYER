package com.hpe.itsma.itsmaInstaller.service;

import com.hpe.itsma.itsmaInstaller.bean.ItsmaProduct;
import com.hpe.itsma.itsmaInstaller.bean.RuntimeProfile;
import com.hpe.itsma.itsmaInstaller.constants.ItsmaInstallerConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.BDDMockito.given;

/**
 * Created by tianlib on 10/10/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class SuiteVersionServiceTest {

  @MockBean
  RuntimeProfile runtimeProfile;
  @MockBean
  K8sRestClient k8sRestClient;
  @MockBean
  ProductsService productsService;

  @Autowired
  SuiteVersionService suiteVersionService;

  @Test
  public void testSuiteVersion() throws Exception {
    try{
      suiteVersionService.suiteVersion("");
      fail("Expected: exception, Actual: no exception");
    } catch (Exception e) {

    }

    given(k8sRestClient.queryConfigMap(
        runtimeProfile.getNamespace(),
        "itsma-common-configmap",
        ItsmaInstallerConstants.ITOM_SUITE_MODE)).willReturn("H_MODE");

    String version = "2018.02";
    List<ItsmaProduct> itsmaProducts = new ArrayList<>();
    itsmaProducts.add(new ItsmaProduct("itom-sm", "hmode", "123"));
    itsmaProducts.add(new ItsmaProduct("itom-cmdb", "hmode", "123"));
    given(productsService.getProductsFromConfigByMode(
        runtimeProfile.getBuiltin_config_path() + "/" + version + "/" + ItsmaInstallerConstants.ITSMA_PRODUCTS,
        "hmode")).willReturn(itsmaProducts);

    assertThat(suiteVersionService.suiteVersion(version).toString()).isEqualTo(
        "SuiteVersion=[suiteVersion: 2018.02 suiteMode: hmode services: [service=[name: itom-sm version: 123], service=[name: itom-cmdb version: 123]]]"
    );
  }
}