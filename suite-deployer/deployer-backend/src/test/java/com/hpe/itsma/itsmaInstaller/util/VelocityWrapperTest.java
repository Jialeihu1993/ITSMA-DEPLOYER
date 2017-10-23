package com.hpe.itsma.itsmaInstaller.util;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tianlib on 11/2/2016.
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class VelocityWrapperTest {

  @Rule
  public TemporaryFolder tmpDir = new TemporaryFolder();

  /**
   * Test method for {@link VelocityWrapper#transformFiles()}
   */
  @Test
  public void testTransformFiles() throws Exception {
    Map<String, Object> properties = new HashMap<>();
    properties.put("namespace", "itsma1");

    String templatePath = VelocityWrapperTest.class.getClassLoader().getResource("com.hpe.itsma.itsmaInstaller.util/yaml_templates").getPath();
    templatePath = URLDecoder.decode(templatePath, "UTF8");
    tmpDir.newFolder("yamls");
    String outputPath = tmpDir.getRoot().getPath() + "/yamls";
    outputPath = URLDecoder.decode(outputPath, "UTF8");
    VelocityWrapper vw = new VelocityWrapper(templatePath, outputPath, properties);
    vw.init();
    vw.transformFiles();
    File namespaceFile = new File(outputPath + "/namespace.yaml");
    Assert.assertEquals("apiVersion: v1kind: Namespacemetadata:  name: itsma1  labels:    name: itsma1    itsmaService: deployer",ItsmaUtil.inputStream2String(new FileInputStream(namespaceFile)));
  }

  /**
   * Test method for {@link VelocityWrapper#transformFile(java.lang.String, java.lang.String, java.util.Map)}
   */
  @Test
  public void testTransformFile() throws Exception {
    Map<String, Object> properties = new HashMap<>();
    properties.put("namespace", "itsma1");

    String templatePath = VelocityWrapperTest.class.getClassLoader().getResource("com.hpe.itsma.itsmaInstaller.util/yaml_templates").getPath();
    templatePath = URLDecoder.decode(templatePath, "UTF8");
    tmpDir.newFolder("yamls");
    String outputPath = tmpDir.getRoot().getPath() + "/yamls";
    outputPath = URLDecoder.decode(outputPath, "UTF8");
    VelocityWrapper velocityWrapper = new VelocityWrapper();
    velocityWrapper.transformFile(templatePath + "/namespace.yaml", outputPath + "/namespace.yaml", properties);

    File namespaceFile = new File(outputPath + "/namespace.yaml");
    Assert.assertEquals("apiVersion: v1kind: Namespacemetadata:  name: itsma1  labels:    name: itsma1    itsmaService: deployer",ItsmaUtil.inputStream2String(new FileInputStream(namespaceFile)));
  }
}

