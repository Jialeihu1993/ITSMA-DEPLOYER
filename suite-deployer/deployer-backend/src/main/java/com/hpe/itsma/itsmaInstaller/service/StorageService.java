package com.hpe.itsma.itsmaInstaller.service;

import com.hpe.itsma.itsmaInstaller.bean.RuntimeProfile;
import com.hpe.itsma.itsmaInstaller.constants.ItsmaInstallerConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by zhongtao on 8/16/2017.
 */
@Component
public class StorageService {
  private static Log logger = LogFactory.getLog(StorageService.class);

  @Autowired
  private SSRestClient ssRestClient;

  @Autowired
  private RuntimeProfile runtimeProfile;

  public String getConfiguration(String key) throws Exception {
    return ssRestClient.getConfiguration(runtimeProfile.getDeploymentUuid(), key);
  }

  public String createConfiguration(Map<String, Object> properties) throws Exception {
    return ssRestClient.createConfiguration(runtimeProfile.getDeploymentUuid(), (String) properties.get(ItsmaInstallerConstants.SUITE_NAMESPACE), properties);
  }
}
