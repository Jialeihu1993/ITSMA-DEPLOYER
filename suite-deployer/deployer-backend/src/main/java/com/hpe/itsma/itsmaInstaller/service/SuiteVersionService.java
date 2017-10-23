package com.hpe.itsma.itsmaInstaller.service;

import com.hpe.itsma.itsmaInstaller.bean.ItsmaProduct;
import com.hpe.itsma.itsmaInstaller.bean.RuntimeProfile;
import com.hpe.itsma.itsmaInstaller.bean.SuiteVersion;
import com.hpe.itsma.itsmaInstaller.constants.ItsmaInstallerConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by tianlib on 10/10/2017.
 */
@Component
public class SuiteVersionService {
  private static Log logger = LogFactory.getLog(SuiteVersionService.class);

  @Autowired
  RuntimeProfile runtimeProfile;

  @Autowired
  K8sRestClient k8sRestClient;

  @Autowired
  ProductsService productsService;

  public SuiteVersion currentVersion() throws Exception {
    return suiteVersion(currentSimpleVersion());
  }

  public SuiteVersion updateVersion() throws Exception {
    return suiteVersion(updateSimpleVersion());
  }

  public String currentSimpleVersion() throws Exception {
    return k8sRestClient.queryConfigMap(
        runtimeProfile.getNamespace(),
        "itsma-common-configmap",
        ItsmaInstallerConstants.SUITE_VERSION
    );
  }

  public String updateSimpleVersion() throws Exception {
    return runtimeProfile.getSuite_update_new_version();
  }

  /**
   * getSuiteVersion get the new version of suite will be updated to
   *
   * suite version including the version, suite mode(H, X?) and the service list
   *
   * @return
   * @throws Exception
   */
  public SuiteVersion suiteVersion(String version) throws Exception {
    if(version.isEmpty()) {
      String warnMsg = "Failed to get suite version.";
      logger.warn(warnMsg);
      throw new Exception(warnMsg);
    }

    SuiteVersion suiteVersion = new SuiteVersion();
    suiteVersion.setSuiteVersion(version);

    String mode = k8sRestClient.queryConfigMap(
        runtimeProfile.getNamespace(),
        "itsma-common-configmap",
        ItsmaInstallerConstants.ITOM_SUITE_MODE);

    if(mode == null || mode.isEmpty()) {
      String warnMsg = "Failed to get suite mode.";
      logger.warn(warnMsg);
      throw new Exception(warnMsg);
    }
    // translate mode, because using "H_MODE" in configmap, while using "hmode" in itsmaProducts.json. damn it!
    if(mode.equalsIgnoreCase(ItsmaInstallerConstants.ITOM_H_MODE)) {
      mode = "hmode";
    } else if(mode.equalsIgnoreCase(ItsmaInstallerConstants.ITOM_X_MODE)) {
      mode = "xmode";
    }

    suiteVersion.setSuiteMode(mode);
    List<ItsmaProduct> products = productsService.getProductsFromConfigByMode(
        runtimeProfile.getBuiltin_config_path() + "/" + version + "/" + ItsmaInstallerConstants.ITSMA_PRODUCTS,
        mode);

    for(ItsmaProduct product : products) {
      SuiteVersion.Service service = suiteVersion.new Service();
      service.setName(product.getName());
      service.setVersion(product.getVersion());
      suiteVersion.getServices().add(service);
    }

    return suiteVersion;
  }
}
