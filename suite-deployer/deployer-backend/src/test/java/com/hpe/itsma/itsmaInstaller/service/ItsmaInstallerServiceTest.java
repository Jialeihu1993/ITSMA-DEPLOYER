package com.hpe.itsma.itsmaInstaller.service;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class ItsmaInstallerServiceTest {
//    @MockBean
//    private SSRestClient ssRestClient;
//
//    @MockBean
//    private RuntimeProfile runtimeProfile;
//
//    @Autowired
//    DeployerContext deployerContext;
//
//    @Autowired
//    private ItsmaInstallerService itsmaInstallerService;
//
//    @Rule
//    public TemporaryFolder tmpDir = new TemporaryFolder();


//    @Before
//    public void setup() {
//    }

//    @Test
//    public void getPodStatusByName_Phase_Running() {
//        try {
//            given(runtimeProfile.getDeploymentUuid()).willReturn("");
//            InputStream is = ItsmaInstallerServiceTest.class.getClassLoader().
//                getResourceAsStream("com.hpe.itsma.itsmaInstaller.service/pod_status_phase_running.json");
//            String pod_status_response = IOUtils.toString(is);
//
//            given(ssRestClient.getPodStatusByName("", "sm-idol-nfsfiles")).willReturn(pod_status_response);
//            KubeResourceStatus kubeResourceStatus = itsmaInstallerService.getPodStatusByName("sm-idol-nfsfiles");
//            assertThat(kubeResourceStatus.getName()).isEqualTo("sm-idol-nfsfiles");
//            assertThat(kubeResourceStatus.getKind()).isEqualTo("Pod");
//            assertThat(kubeResourceStatus.getStatus()).isEqualTo("Running");
//        } catch (Exception e) {
//            fail("Expected: No exception, Actual: exception happens.");
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void getPodStatusByName_Phase_Succeeded() {
//        try {
//            given(runtimeProfile.getDeploymentUuid()).willReturn("");
//            InputStream is = ItsmaInstallerServiceTest.class.getClassLoader().
//                getResourceAsStream("com.hpe.itsma.itsmaInstaller.service/pod_status_phase_succeeded.json");
//            String pod_status_response = IOUtils.toString(is);
//
//            given(ssRestClient.getPodStatusByName("", "sm-rte-pre")).willReturn(pod_status_response);
//            KubeResourceStatus kubeResourceStatus = itsmaInstallerService.getPodStatusByName("sm-rte-pre");
//            assertThat(kubeResourceStatus.getName()).isEqualTo("sm-rte-pre");
//            assertThat(kubeResourceStatus.getKind()).isEqualTo("Pod");
//            assertThat(kubeResourceStatus.getStatus()).isEqualTo("Succeeded");
//        } catch (Exception e) {
//            fail("Expected: No exception, Actual: exception happens.");
//            e.printStackTrace();
//        }
//    }

//    @Test
//    public void getServiceStatusByName_Phase_Failed1_Ready_False() {
//        try {
//            given(runtimeProfile.getDeploymentUuid()).willReturn("");
//            InputStream is = ItsmaInstallerServiceTest.class.getClassLoader().
//                getResourceAsStream("com.hpe.itsma.itsmaInstaller.service/service_status_phase_pending.json");
//            String svc_status_response = IOUtils.toString(is);
//            given(ssRestClient.getAllPodStatusInService("", "auth-svc")).willReturn(svc_status_response);
//
//            KubeResourceStatus kubeResourceStatus = itsmaInstallerService.getServiceStatusByName("auth-svc");
//            assertThat(kubeResourceStatus.getKind()).isEqualTo("Service");
//            assertThat(kubeResourceStatus.getStatus()).isEqualTo("Failed");
//            assertThat(kubeResourceStatus.isReady()).isFalse();
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("Expected: No exception, Actual: exception happens.");
//        }
//    }
//
//    @Test
//    public void getServiceStatusByName_Phase_Failed2_Ready_False() {
//        try {
//            given(runtimeProfile.getDeploymentUuid()).willReturn("");
//            InputStream is = ItsmaInstallerServiceTest.class.getClassLoader().
//                getResourceAsStream("com.hpe.itsma.itsmaInstaller.service/service_status_phase_failed.json");
//            String svc_status_response = IOUtils.toString(is);
//            given(ssRestClient.getAllPodStatusInService("", "auth-svc")).willReturn(svc_status_response);
//
//            KubeResourceStatus kubeResourceStatus = itsmaInstallerService.getServiceStatusByName("auth-svc");
//            assertThat(kubeResourceStatus.getKind()).isEqualTo("Service");
//            assertThat(kubeResourceStatus.getStatus()).isEqualTo("Failed");
//            assertThat(kubeResourceStatus.isReady()).isFalse();
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("Expected: No exception, Actual: exception happens.");
//        }
//    }
//
//    @Test
//    public void getServiceStatusByName_Phase_Pending1_Ready_False() {
//        try {
//            given(runtimeProfile.getDeploymentUuid()).willReturn("");
//            InputStream is = ItsmaInstallerServiceTest.class.getClassLoader().
//                getResourceAsStream("com.hpe.itsma.itsmaInstaller.service/service_status_empty.json");
//            String svc_status_reponse = IOUtils.toString(is);
//            given(ssRestClient.getAllPodStatusInService("", "auth-svc")).willReturn(svc_status_reponse);
//
//            KubeResourceStatus kubeResourceStatus = itsmaInstallerService.getServiceStatusByName("auth-svc");
//            assertThat(kubeResourceStatus.getKind()).isEqualTo("Service");
//            assertThat(kubeResourceStatus.getStatus()).isEqualTo("Pending");
//            assertThat(kubeResourceStatus.isReady()).isFalse();
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("Expected: No exception, Actual: exception happens.");
//        }
//    }
//
//    @Test
//    public void getServiceStatusByName_Phase_Pending2_Ready_False() {
//        try {
//            given(runtimeProfile.getDeploymentUuid()).willReturn("");
//            given(ssRestClient.getAllPodStatusInService("", "auth-svc")).willThrow(new K8sResourceNotFoundException());
//
//            KubeResourceStatus kubeResourceStatus = itsmaInstallerService.getServiceStatusByName("auth-svc");
//            assertThat(kubeResourceStatus.getKind()).isEqualTo("Service");
//            assertThat(kubeResourceStatus.getStatus()).isEqualTo("Pending");
//            assertThat(kubeResourceStatus.isReady()).isFalse();
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("Expected: No exception, Actual: exception happens.");
//        }
//    }
}
