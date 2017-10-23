package com.hpe.itsma.itsmaInstaller.bean;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.InputStream;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;
/**
 * Created by gongy on 22/05/2017.
 */
public class KubeResourceStatusTest {
  @Test
  public void toJson1() throws Exception {
    KubeResourceStatus kubeResourceStatus=new KubeResourceStatus("kind1", "name1", "status1",true, "self1");
    String json = kubeResourceStatus.toJson();

    InputStream is = KubeInstanceInfoTest.class.getResourceAsStream("kubeResourceStatus1.json");
    String expectedJson = IOUtils.toString(is);
    JSONAssert.assertEquals(expectedJson, json, false);

  }

  @Test
  public void toJson2() throws Exception {
    KubeResourceStatus kubeResourceStatus=new KubeResourceStatus("kind2", "name2", "status2",false, "self2");
    String json = kubeResourceStatus.toJson();

    InputStream is = KubeInstanceInfoTest.class.getResourceAsStream("kubeResourceStatus2.json");
    String expectedJson = IOUtils.toString(is);
    JSONAssert.assertEquals(expectedJson, json, false);

  }

  @Test
  public void toJson3() throws Exception {
    KubeResourceStatus kubeResourceStatus=new KubeResourceStatus("kind3", "name3", "status3",null, "self3");
    String json = kubeResourceStatus.toJson();

    InputStream is = KubeInstanceInfoTest.class.getResourceAsStream("kubeResourceStatus3.json");
    String expectedJson = IOUtils.toString(is);
    JSONAssert.assertEquals(expectedJson, json, false);

  }

}