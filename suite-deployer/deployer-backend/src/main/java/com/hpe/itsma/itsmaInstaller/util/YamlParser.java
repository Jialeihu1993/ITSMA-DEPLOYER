package com.hpe.itsma.itsmaInstaller.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.hpe.itsma.itsmaInstaller.bean.K8sResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.FileSystemResource;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianlib on 1/4/2017.
 */
public class YamlParser {
  private static Log logger = LogFactory.getLog(YamlParser.class);

  private static final String YAML_RES_COMMENT = "#";
  private static final String YAML_RES_DELIMITER = "---";

  private String name;

  public YamlParser(String name) {
    this.name = name;
  }

  public List<K8sResource> parse() throws Exception {
    Constructor constructor = new Constructor(K8sResource.class);//Car.class is root
    TypeDescription carDescription = new TypeDescription(K8sResource.class);
    constructor.addTypeDescription(carDescription);
    Yaml yaml = new Yaml(constructor);

    InputStream input = new FileSystemResource(name).getInputStream();

    List<K8sResource> k8sResources = new ArrayList<K8sResource>();
    for (String resource : split(input)) {
      k8sResources.add(yaml.loadAs(resource, K8sResource.class));
    }
    return k8sResources;
  }

  //split yaml by delemeter "---"
  public static List<String> split(InputStream is) throws Exception {
    List<String> yamlContentList = new ArrayList<String>();
    BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    try {
      String aLine;
      StringBuffer sb = new StringBuffer();
      while ((aLine = in.readLine()) != null) {
        if (!aLine.startsWith(YAML_RES_DELIMITER)) {
          if (!aLine.startsWith(YAML_RES_COMMENT) && !aLine.isEmpty()) {
            sb.append(aLine).append(System.lineSeparator());
          }
        } else {
          if (sb.toString().length() > 0) {
            yamlContentList.add(sb.toString());
          }
          sb = new StringBuffer();
        }
      }
      if (sb.toString().length() > 0) {
        yamlContentList.add(sb.toString());
      }
    } catch (Exception e) {
      throw e;
    } finally {
      in.close();
    }
    return yamlContentList;
  }

  public static List parseYamlFileToList(String yamlFile) throws IOException {
    List list = null;
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    list = mapper.readValue(new File(yamlFile), List.class);
    return list;
  }
}
