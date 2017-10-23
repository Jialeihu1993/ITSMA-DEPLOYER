package com.hpe.itsma.itsmaInstaller.service;

import com.hpe.itsma.itsmaInstaller.bean.ItsmaServiceParameters;
import com.hpe.itsma.itsmaInstaller.bean.RuntimeProfile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

/**
 * Created by zhongtao on 8/1/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class YamlServiceTest {
  @MockBean
  private RuntimeProfile runtimeProfile;

  @Autowired
  private YamlService yamlService;

  /**
   * Test method for {@link YamlService#convertYamlToItsmaServiceParameters(java.io.InputStream)}
   * <p>
   * Condition:
   * <ol>
   * <li>Happy path</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li>The yaml file is parsed correctly.</li>
   * </ol>
   */
  @Test
  public void testConvertYamlToItsmaServiceParameters() throws IOException {
    Map<String, String> profileParameter = new HashMap<String, String>();
    profileParameter.put("itom_auth_limits_cpu", "300m");
    profileParameter.put("itom_auth_limits_memory", "1000Mi");
    profileParameter.put("itom_auth_requests_cpu", "300m");
    profileParameter.put("itom_auth_requests_memory", "100Mi");

    InputStream is = YamlServiceTest.class.getClassLoader().getResourceAsStream("com.hpe.itsma.itsmaInstaller.service/manifest_auth.yaml");
    ItsmaServiceParameters itsmaServiceParameters = yamlService.convertYamlToItsmaServiceParameters(is);
    Assert.assertEquals(profileParameter, itsmaServiceParameters.getProfile_parameters().getDemo());
  }
}
