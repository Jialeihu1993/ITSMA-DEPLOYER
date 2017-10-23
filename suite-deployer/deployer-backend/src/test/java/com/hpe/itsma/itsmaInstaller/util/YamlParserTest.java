package com.hpe.itsma.itsmaInstaller.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

/**
 * Created by zhongtao on 9/12/2017.
 */
public class YamlParserTest {
  /**
   * Test method for {@link YamlParser#parseYamlFileToList(java.lang.String)}
   */
  @Test
  public void testParseYamlFileToList() throws Exception {
    String configmapYamlPath = YamlParserTest.class.getClassLoader().getResource("com.hpe.itsma.itsmaInstaller.util/backup-configmap.yaml.txt").getPath();
    configmapYamlPath = URLDecoder.decode(configmapYamlPath, "UTF8");
    List<Map<String, Object>> configMapList = (List<Map<String, Object>>) YamlParser.parseYamlFileToList(configmapYamlPath);
    Assert.assertEquals(19, configMapList.size());
    for (Map<String, Object> map : configMapList) {
      Assert.assertEquals("ConfigMap", map.get("kind"));
    }
  }
}
