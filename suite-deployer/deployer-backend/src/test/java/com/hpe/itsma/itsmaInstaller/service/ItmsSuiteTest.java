package com.hpe.itsma.itsmaInstaller.service;

import com.hpe.itsma.itsmaInstaller.bean.DeployerContext;
import com.hpe.itsma.itsmaInstaller.bean.ItsmaProduct;
import com.hpe.itsma.itsmaInstaller.bean.ItsmaService;
import com.hpe.itsma.itsmaInstaller.bean.RuntimeProfile;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.BDDMockito.given;

/**
 * Created by tianlib on 9/4/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ItmsSuiteTest {
    @MockBean
    private SSRestClient ssRestClient;

    @MockBean
    private RuntimeProfile runtimeProfile;

    @Autowired
    private DeployerContext deployerContext;

    @Autowired
    private ItsmaSuite itsmaSuite;

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    @Test
    public void deserializeItsmaService() {
        try {
            InputStream is = ItsmaInstallerServiceTest.class.getClassLoader().
                    getResourceAsStream("com.hpe.itsma.itsmaInstaller.service/manifest_auth.yaml");

            ItsmaService itsmaService = itsmaSuite.deserializeItsmaService(is);
            assertThat(itsmaService.getName()).isEqualToIgnoringCase("itom-auth");
            assertThat(itsmaService.getVersion()).isEqualToIgnoringCase("1.0.1");
            assertThat(itsmaService.getDeployDeps()).isNull();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Expected: No exception, Actual: exception happens.");
        }
    }

    @Test
    public void getServiceRegistryUrl() {
        try {
            given(runtimeProfile.getDefault_registry_url()).willReturn("shc-harbor-dev.hpeswlab.net/itsma");
            assertThat(itsmaSuite.getServiceRegistryUrl("itom-auth")).isEqualToIgnoringCase("shc-harbor-dev.hpeswlab.net/itsma");

            deployerContext.getSuiteProperties().put("default_registry_url", "shc-harbor-dev.hpeswlab.net/default");
            assertThat(itsmaSuite.getServiceRegistryUrl("itom-auth")).isEqualToIgnoringCase("shc-harbor-dev.hpeswlab.net/default");

            Map<String, String> svcSM = new HashMap<>();
            svcSM.put("name", "itom-sm");
            svcSM.put("registry_url", "shc-harbor-dev.hpeswlab.net/sm");

            Map<String, String> svcUcmdb = new HashMap<>();
            svcUcmdb.put("name", "itom-ucmdb");

            List<Map<String, String>> services = new ArrayList<>();
            services.add(svcSM);
            services.add(svcUcmdb);

            deployerContext.getSuiteProperties().put("activated_services", services);
            assertThat(itsmaSuite.getServiceRegistryUrl("itom-sm")).isEqualTo("shc-harbor-dev.hpeswlab.net/sm");
            assertThat(itsmaSuite.getServiceRegistryUrl("itom-ucmdb")).isEqualTo("shc-harbor-dev.hpeswlab.net/default");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Expected: No exception, Actual: exception happens.");
        }
    }

    @Test
    public void getServiceControllerImgTag() {
        try{
            ItsmaProduct productSM = new ItsmaProduct();
            productSM.setName("itom-sm");
            productSM.setVersion("1.0.1");

            assertThat(itsmaSuite.getServiceControllerImgTag(productSM)).isEqualTo("1.0.1");

            Map<String, String> svcSM = new HashMap<>();
            svcSM.put("name", "itom-sm");

            Map<String, String> svcUcmdb = new HashMap<>();
            svcUcmdb.put("name", "itom-ucmdb");
            svcUcmdb.put("controller_img_tag", "1.0.2");

            List<Map<String, String>> services = new ArrayList<>();
            services.add(svcSM);
            services.add(svcUcmdb);

            deployerContext.getSuiteProperties().put("activated_services", services);
            assertThat(itsmaSuite.getServiceControllerImgTag(productSM)).isEqualTo("1.0.1");

            ItsmaProduct productUcmdb = new ItsmaProduct();
            productUcmdb.setName("itom-ucmdb");
            assertThat(itsmaSuite.getServiceControllerImgTag(productUcmdb)).isEqualTo("1.0.2");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Expected: No exception, Actual: exception happens.");
        }
    }

    @Test
    public void createAllServiceDeployController() {
        try{
            ItsmaService itsmaService = new ItsmaService();
            itsmaService.setName("itom-sm");
            itsmaService.setRegistryUrl("shc-harbor-dev.hpeswlab.net/itsma");
            itsmaService.setControllerImgTag("1.0.1");

            deployerContext.getItsmaServices().add(itsmaService);
            deployerContext.getSuiteProperties().put("itom_suite_install_type", "new_install");

            given(runtimeProfile.getNamespace()).willReturn("itsma1");
            given(runtimeProfile.getTimezone()).willReturn("Asia/Shanghai");
            given(runtimeProfile.getSuite_install_backend_url()).willReturn("http://suite-conf-svc-itsma.core.svc.cluster.local:8081/itsma");
            given(runtimeProfile.getItsma_services_path()).willReturn(tmpDir.getRoot().getAbsolutePath());
            given(ssRestClient.createKubeResource(runtimeProfile.getDeploymentUuid(), "")).willReturn("");

            tmpDir.newFolder("controller_yamls");
            File tempYamlDir = tmpDir.newFile("controller_yamls/itom-sm-deploy-controller.yaml");

            itsmaSuite.createAllServiceDeployController("new_install");

            String expectedSmDeployControllerYaml = IOUtils.toString(
                    ItsmaInstallerServiceTest.class.getClassLoader().getResourceAsStream("com.hpe.itsma.itsmaInstaller.service/itom-sm-deploy-controller.yaml")
            );

            String actualSmDeployControllerYaml = IOUtils.toString(
                    new FileSystemResource(tempYamlDir.getAbsolutePath()).getInputStream()
            );
            assertThat(actualSmDeployControllerYaml).isEqualTo(expectedSmDeployControllerYaml);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Expected: No exception, Actual: exception happens.");
        }
    }
}
