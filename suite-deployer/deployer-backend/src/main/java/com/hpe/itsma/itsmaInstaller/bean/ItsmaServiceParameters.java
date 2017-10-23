package com.hpe.itsma.itsmaInstaller.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by zhongtao on 7/3/2017.
 */
public class ItsmaServiceParameters {
  private static Log logger = LogFactory.getLog(ItsmaServiceParameters.class);

  private ItsmaProfileParameters profile_parameters;

  public ItsmaProfileParameters getProfile_parameters() {
    return profile_parameters;
  }

  public void setProfile_parameters(ItsmaProfileParameters profile_parameters) {
    this.profile_parameters = profile_parameters;
  }
}
