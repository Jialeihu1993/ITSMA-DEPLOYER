package com.hpe.itsma.itsmaInstaller.controller;

import com.hpe.itsma.itsmaInstaller.bean.KubeResourceStatus;
import com.hpe.itsma.itsmaInstaller.service.ItsmaInstallerService;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.util.HashMap;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by tianlib on 7/17/2017.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(ItsmaInstallerController.class)
@ActiveProfiles("test")
public class ItsmaInstallerControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  @Qualifier("itsmaInstallerService")
  private ItsmaInstallerService itsmaInstallerService;

  @Test
  public void testInstall() throws Exception {
    ItsmaInstallerService service = Mockito.spy(ItsmaInstallerService.class);
    Mockito.doNothing().when(service).install(new HashMap<>());

    InputStream is = ItsmaInstallerControllerTest.class.getClassLoader().getResourceAsStream(
      "com.hpe.itsma.itsmaInstaller.controller/install.json"
    );
    final String properties = IOUtils.toString(is);
    this.mvc.perform(post("/itsma/install").contentType(MediaType.APPLICATION_JSON_VALUE).content(properties))
      .andExpect(status().isCreated());
  }

  @Test
  public void testInstall_Admin_Password_IsEmpty() throws Exception {
    InputStream is = ItsmaInstallerControllerTest.class.getClassLoader().getResourceAsStream(
      "com.hpe.itsma.itsmaInstaller.controller/install-sysadmin-password-is-empty.json"
    );
    final String properties = IOUtils.toString(is);
    this.mvc.perform(post("/itsma/install").contentType(MediaType.APPLICATION_JSON_VALUE).content(properties))
      .andExpect(status().is4xxClientError());
  }

  @Test
  public void testInstall_Domain_Name_IsEmpty() throws Exception {
    InputStream is = ItsmaInstallerControllerTest.class.getClassLoader().getResourceAsStream(
      "com.hpe.itsma.itsmaInstaller.controller/install-domain-name-is-empty.json"
    );
    final String properties = IOUtils.toString(is);
    this.mvc.perform(post("/itsma/install").contentType(MediaType.APPLICATION_JSON_VALUE).content(properties))
      .andExpect(status().is4xxClientError());
  }

  @Test
  public void testInstall_itom_suite_size_Missing() throws Exception {
    InputStream is = ItsmaInstallerControllerTest.class.getClassLoader().getResourceAsStream(
      "com.hpe.itsma.itsmaInstaller.controller/install-itom_suite_size-missing.json"
    );
    final String properties = IOUtils.toString(is);
    this.mvc.perform(post("/itsma/install").contentType(MediaType.APPLICATION_JSON_VALUE).content(properties))
      .andExpect(status().is4xxClientError());
  }

  @Test
  public void testInstall_database() throws Exception {
    InputStream is = ItsmaInstallerControllerTest.class.getClassLoader().getResourceAsStream(
      "com.hpe.itsma.itsmaInstaller.controller/install-database.json"
    );
    final String properties = IOUtils.toString(is);
    this.mvc.perform(post("/itsma/install").contentType(MediaType.APPLICATION_JSON_VALUE).content(properties))
      .andExpect(status().isCreated());
  }

  @Test
  public void testInstall_database_db_engine_IsEmpty() throws Exception {
    InputStream is = ItsmaInstallerControllerTest.class.getClassLoader().getResourceAsStream(
      "com.hpe.itsma.itsmaInstaller.controller/install-database-db-engine-is-empty.json"
    );
    final String properties = IOUtils.toString(is);
    this.mvc.perform(post("/itsma/install").contentType(MediaType.APPLICATION_JSON_VALUE).content(properties))
      .andExpect(status().is4xxClientError());
  }

  @Test
  public void testInstall_database_internal_Missing() throws Exception {
    InputStream is = ItsmaInstallerControllerTest.class.getClassLoader().getResourceAsStream(
      "com.hpe.itsma.itsmaInstaller.controller/install-database-internal-missing.json"
    );
    final String properties = IOUtils.toString(is);
    this.mvc.perform(post("/itsma/install").contentType(MediaType.APPLICATION_JSON_VALUE).content(properties))
      .andExpect(status().is4xxClientError());
  }

  @Test
  public void testInstall_database_db_server_IsEmpty() throws Exception {
    InputStream is = ItsmaInstallerControllerTest.class.getClassLoader().getResourceAsStream(
      "com.hpe.itsma.itsmaInstaller.controller/install-database-db-server-is-empty.json"
    );
    final String properties = IOUtils.toString(is);
    this.mvc.perform(post("/itsma/install").contentType(MediaType.APPLICATION_JSON_VALUE).content(properties))
      .andExpect(status().is4xxClientError());
  }

  @Test
  public void testInstall_database_db_login_IsEmpty() throws Exception {
    InputStream is = ItsmaInstallerControllerTest.class.getClassLoader().getResourceAsStream(
      "com.hpe.itsma.itsmaInstaller.controller/install-database-db-login-is-empty.json"
    );
    final String properties = IOUtils.toString(is);
    this.mvc.perform(post("/itsma/install").contentType(MediaType.APPLICATION_JSON_VALUE).content(properties))
      .andExpect(status().is4xxClientError());
  }

  @Test
  public void testInstall_database_db_password_IsEmpty() throws Exception {
    InputStream is = ItsmaInstallerControllerTest.class.getClassLoader().getResourceAsStream(
      "com.hpe.itsma.itsmaInstaller.controller/install-database-db-password-is-empty.json"
    );
    final String properties = IOUtils.toString(is);
    this.mvc.perform(post("/itsma/install").contentType(MediaType.APPLICATION_JSON_VALUE).content(properties))
      .andExpect(status().is4xxClientError());
  }

  @Test
  public void testInstall_database_oracle() throws Exception {
    InputStream is = ItsmaInstallerControllerTest.class.getClassLoader().getResourceAsStream(
      "com.hpe.itsma.itsmaInstaller.controller/install-database-oracle.json"
    );
    final String properties = IOUtils.toString(is);
    this.mvc.perform(post("/itsma/install").contentType(MediaType.APPLICATION_JSON_VALUE).content(properties))
      .andExpect(status().isCreated());
  }

  @Test
  public void testInstall_database_oracle_db_inst_IsEmpty() throws Exception {
    InputStream is = ItsmaInstallerControllerTest.class.getClassLoader().getResourceAsStream(
      "com.hpe.itsma.itsmaInstaller.controller/install-database-oracle-db-inst-is-empty.json"
    );
    final String properties = IOUtils.toString(is);
    this.mvc.perform(post("/itsma/install").contentType(MediaType.APPLICATION_JSON_VALUE).content(properties))
      .andExpect(status().is4xxClientError());
  }

  @Test
  public void testGetInstallStatus() throws Exception {
    given(itsmaInstallerService.getInstallStatus()).willReturn(
      "{\"name\": \"itom-sm\", \"status\": \"DEPLOYED\"}");
    this.mvc.perform(get("/itsma/install").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk()).andExpect(content().string("{\"name\": \"itom-sm\", \"status\": \"DEPLOYED\"}"));
  }

//  @Test
//  public void testUploadServiceManifest() throws Exception {
//    ItsmaInstallerService service = Mockito.spy(ItsmaInstallerService.class);
//    Mockito.doNothing().when(service).saveManifest("", "");
//
//    final String manifest = "name: itom-auth\n" +
//        "version: 1.0.1\n" +
//        "owner: auth\n" +
//        "deployDeps:\n" +
//        "apiSpec: auth-swagger.yaml\n";
//
//    this.mvc.perform(post("/itsma/itsma_services/itom-auth/manifest").contentType(MediaType.TEXT_PLAIN_VALUE).content(manifest))
//        .andExpect(status().isCreated());
//  }
//
//  @Test
//  public void testCreateKubeResourceWithPayload() throws Exception {
//    ItsmaInstallerService service = Mockito.spy(ItsmaInstallerService.class);
//    Mockito.doNothing().when(service).createKubeResourceByContent("", "", "");
//
//    InputStream is = ItsmaInstallerControllerTest.class.getClassLoader().
//        getResourceAsStream("com.hpe.itsma.itsmaInstaller.controller/itom-auth-rc.yaml");
//
//    final String yaml = IOUtils.toString(is);
//    this.mvc.perform(post("/itsma/itsma_services/itom-auth/kube_resources").header("resourcename", "itom-auth-rc").content(yaml))
//        .andExpect(status().isCreated());
//  }
//
//  @Test
//  public void testUpdateItsmaServiceStatus() throws Exception {
//    ItsmaInstallerService service = Mockito.spy(ItsmaInstallerService.class);
//    Mockito.doNothing().when(service).setItsmaServiceStatus("", new HashMap<>());
//
//    InputStream is = ItsmaInstallerControllerTest.class.getClassLoader().
//        getResourceAsStream("com.hpe.itsma.itsmaInstaller.controller/itom-sm-status.json");
//
//    final String json = IOUtils.toString(is);
//    this.mvc.perform(put("/itsma/itsma_services/itom-sm").contentType(MediaType.APPLICATION_JSON_VALUE).content(json))
//        .andExpect(status().isOk());
//  }
//
//  @Test
//  public void testGetItsmaServiceStatus() throws Exception {
//    given(itsmaInstallerService.getItsmaServiceStatus("")).willReturn(new ItsmaServiceStatus("itom-sm", "SUCCESS"));
//
//    InputStream is = ItsmaInstallerControllerTest.class.getClassLoader().
//        getResourceAsStream("com.hpe.itsma.itsmaInstaller.controller/itom-sm-status.json");
//
//    final String json = IOUtils.toString(is);
//    this.mvc.perform(get("/itsma/itsma_services/itom-sm").accept(MediaType.APPLICATION_JSON_VALUE))
//        .andExpect(status().isOk());
//  }

  @Test
  public void testGetPodStatusByName() throws Exception {
    given(itsmaInstallerService.getPodStatusByName("")).willReturn(
      new KubeResourceStatus("Pod", "sm-rte-pre", "Pending", false, "/itsma/kube_pods/sm-rte-pre"));

    this.mvc.perform(get("/itsma/kube_pods/sm-rte-pre").accept(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(status().isOk());
  }

  @Test
  public void testGetServiceStatusByName() throws Exception {
    given(itsmaInstallerService.getServiceStatusByName("")).willReturn(
      new KubeResourceStatus("Service", "idm-svc", "Running", true, "/itsma/kube_services/idm-svc"));

    this.mvc.perform(get("/itsma/kube_services/idm-svc").accept(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(status().isOk());
  }
}