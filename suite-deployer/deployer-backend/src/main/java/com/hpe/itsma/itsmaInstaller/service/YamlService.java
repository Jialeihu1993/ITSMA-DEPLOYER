package com.hpe.itsma.itsmaInstaller.service;

import com.hpe.itsma.itsmaInstaller.bean.ItsmaServiceParameters;
import com.hpe.itsma.itsmaInstaller.bean.RuntimeProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.representer.Representer;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by zhongtao on 7/27/2017.
 */
@Component
public class YamlService {
  @Autowired
  private RuntimeProfile runtimeProfile;

  public Map<String, Object> convertYamlToMap(InputStream in) {
    PropertyUtils propertyUtils = new PropertyUtils();
    propertyUtils.setSkipMissingProperties(true);
    Representer representer = new Representer();
    representer.setPropertyUtils(propertyUtils);
    Yaml yaml = new Yaml(representer);
    Map<String, Object> result = yaml.loadAs(in, Map.class);
    return result;
  }

  public ItsmaServiceParameters convertYamlToItsmaServiceParameters(InputStream in) {
    PropertyUtils propertyUtils = new PropertyUtils();
    propertyUtils.setSkipMissingProperties(true);
    Representer representer = new Representer();
    representer.setPropertyUtils(propertyUtils);
    Yaml yaml = new Yaml(representer);
    ItsmaServiceParameters itsmaServiceParameters = yaml.loadAs(in, ItsmaServiceParameters.class);
    return itsmaServiceParameters;
  }
}
