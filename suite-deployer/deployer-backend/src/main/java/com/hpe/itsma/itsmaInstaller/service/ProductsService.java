package com.hpe.itsma.itsmaInstaller.service;

import com.google.gson.Gson;
import com.hpe.itsma.itsmaInstaller.bean.ItsmaProduct;
import com.hpe.itsma.itsmaInstaller.constants.ItsmaInstallerConstants;
import com.hpe.itsma.itsmaInstaller.util.ItsmaUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The class to do actions about Product.
 *
 * @author zhongtao
 * @since 8/1/2017.
 */
@Service
public class ProductsService {
  private static Log logger = LogFactory.getLog(ProductsService.class);

  private List<ItsmaProduct> itsmaProducts = new ArrayList<ItsmaProduct>();

  /**
   * Load json file 'itsmaProducts.json' as a List of ItsmaProduct.
   *
   * @param is
   * @throws IOException
   */
  public void loadProducts(InputStream is) throws IOException {
    Gson gson = new Gson();
    JSONObject jsonObject = new JSONObject(IOUtils.toString(is, "UTF-8"));
    JSONArray products = (JSONArray) jsonObject.get("itsmaServices");
    for (int i = 0; i < products.length(); i++) {
      ItsmaProduct product = gson.fromJson(products.getJSONObject(i).toString(), ItsmaProduct.class);
      itsmaProducts.add(product);
    }

    logger.debug("All products: " + itsmaProducts);
  }

  /**
   * Deactive product that will not be deployed
   * <p>
   * Infra product will always be deployed. Using com.hpe.itsma.itsmaInstaller.service.ProductsService#isInfraProduct(java.lang.String) to get the infra products.
   * All the products will be installed if no service in properties 'activated_services'
   * </p>
   *
   * @param properties
   */
  public void deactiveProducts(Map<String, Object> properties) {
    //if use external ldap, itom-openldap pod will not start. default start itom-openldap
    String externalLdap = "false";
    try {
      externalLdap = ItsmaUtil.getPropertyEx(properties, "external_ldap").toString();
    } catch (IllegalArgumentException e) {
      logger.info("Parameter 'external_ldap' does not exist! Using internal 'itom-openldap'");
    }
    properties.put("external_ldap", Boolean.valueOf(externalLdap));

    //oActiveServices is from frontend inputed
    List<String> activeServiceNames = new ArrayList<>();
    Object oActiveServices = properties.get("activated_services");
    if (oActiveServices != null) {
      List<Map<String, String>> activatedServices = (List<Map<String, String>>) oActiveServices;
      activatedServices.stream().forEach(service -> activeServiceNames.add(service.get("name")));
    }

    String itomSuiteMode = ItsmaUtil.getPropertyEx(properties, "itom_suite_mode").toString();

    if (itomSuiteMode.equalsIgnoreCase(ItsmaInstallerConstants.ITOM_H_MODE)) {
      this.activeHModeProducts(activeServiceNames, itsmaProducts, externalLdap);
    } else if (itomSuiteMode.equalsIgnoreCase(ItsmaInstallerConstants.ITOM_X_MODE)) {
      this.activeXModeProducts(activeServiceNames, itsmaProducts, externalLdap);
    } else {
      logger.error("Illegal itom_suite_mode: " + itomSuiteMode);
      throw new IllegalArgumentException("Illegal itom_suite_mode: " + itomSuiteMode);
    }

    List<String> deactivatedPP = this.getDeactivatedProducts();
    if (!ItsmaUtil.isNullOrEmpty(deactivatedPP)) {
      properties.put("deactivated_pp", (String[]) deactivatedPP.toArray(new String[deactivatedPP.size()]));
    } else {
      properties.put("deactivated_pp", new String[0]);
    }
  }

  public List<String> getInfraProducts() {
    List<String> infraProducts = new ArrayList<String>();
    for (ItsmaProduct product : itsmaProducts) {
      if (!ItsmaUtil.isNullOrEmpty(product.getMode())) {
        String[] mode = product.getMode().split(",");
        for (int i = 0; i < mode.length; i++) {
          if (mode[i].trim().equalsIgnoreCase("infra")) {
            infraProducts.add(product.getName());
          }
        }
      }
    }

    return infraProducts;
  }

  public boolean isInfraProduct(String name) {
    List<String> infraProducts = this.getInfraProducts();
    if (infraProducts.contains(name)) {
      return true;
    }
    return false;
  }

  public List<String> getHModeProducts() {
    List<String> hmodeProducts = new ArrayList<String>();
    for (ItsmaProduct product : itsmaProducts) {
      if (!ItsmaUtil.isNullOrEmpty(product.getMode())) {
        String[] mode = product.getMode().split(",");
        for (int i = 0; i < mode.length; i++) {
          if (mode[i].trim().equalsIgnoreCase("hmode")) {
            hmodeProducts.add(product.getName());
          }
        }
      } else {
        hmodeProducts.add(product.getName());
      }
    }

    return hmodeProducts;
  }

  public boolean isHModeProduct(String name) {
    List<String> hmodeProducts = this.getHModeProducts();
    if (hmodeProducts.contains(name)) {
      return true;
    }
    return false;
  }

  public List<String> getXModeProducts() {
    List<String> xmodeProducts = new ArrayList<String>();
    for (ItsmaProduct product : itsmaProducts) {
      if (!ItsmaUtil.isNullOrEmpty(product.getMode())) {
        String[] mode = product.getMode().split(",");
        for (int i = 0; i < mode.length; i++) {
          if (mode[i].trim().equalsIgnoreCase("xmode")) {
            xmodeProducts.add(product.getName());
          }
        }
      } else {
        xmodeProducts.add(product.getName());
      }
    }

    return xmodeProducts;
  }

  public boolean isXModeProduct(String name) {
    List<String> xmodeProducts = this.getXModeProducts();
    if (xmodeProducts.contains(name)) {
      return true;
    }
    return false;
  }

  /**
   * getProductsFromConfigByMode return the product list of the given mode
   *
   * @param configFile the absolute path of itsmaProducts.json
   *
   * @param mode suite mode
   * @return
   */
  public List<ItsmaProduct> getProductsFromConfigByMode(String configFile, String mode) throws Exception {
    List<ItsmaProduct> itsmaProducts = new ArrayList<>();
    InputStream is = new FileSystemResource(configFile).getInputStream();
    Gson gson = new Gson();
    JSONObject jsonObject = new JSONObject(IOUtils.toString(is, "UTF-8"));
    JSONArray products = (JSONArray) jsonObject.get("itsmaServices");
    for (int i = 0; i < products.length(); i++) {
      ItsmaProduct product = gson.fromJson(products.getJSONObject(i).toString(), ItsmaProduct.class);
      if(product.getMode().equalsIgnoreCase(mode)) {
        itsmaProducts.add(product);
      }
    }

    return itsmaProducts;
  }

  public List<ItsmaProduct> getItsmaProducts() {
    return itsmaProducts;
  }

  /**
   * getItsmaProductsAsString return all products as a string separated by comma
   *
   * @return
   */
  public String getItsmaProductsAsString() {
    String products = "";

    for (Iterator<ItsmaProduct> it = itsmaProducts.iterator(); it.hasNext(); ) {
      products += it.next().getName();
      if (it.hasNext()) {
        products += ",";
      }
    }
    return products;
  }

  /**
   * getActivatedItsmaProductsAsString return all activated products as a string separated by comma
   *
   * @return
   */
  public String getActivatedItsmaProductsAsString() {
    String products = "";

    for (Iterator<ItsmaProduct> it = itsmaProducts.iterator(); it.hasNext(); ) {
      ItsmaProduct itsmaProduct = it.next();
      if (itsmaProduct.isActive()) {
        if (!products.isEmpty()) {
          products += ",";
        }
        products += itsmaProduct.getName();
      }
    }
    return products;
  }

  /**
   * Return activated_services like below format.
   * [{
   * "name": "itom-sm"
   * }, {
   * "name": "itom-cmdb"
   * }]
   *
   * @return
   */
  public JSONArray getActivatedServices() {
    JSONArray activated_services = new JSONArray();

    for (Iterator<ItsmaProduct> it = itsmaProducts.iterator(); it.hasNext(); ) {
      ItsmaProduct itsmaProduct = it.next();
      if (itsmaProduct.isActive()) {
        JSONObject product = new JSONObject();
        product.put("name", itsmaProduct.getName());
        activated_services.put(product);
      }
    }
    return activated_services;
  }

  public ItsmaProduct getItsmaProduct(String name) {
    for (ItsmaProduct product : itsmaProducts) {
      if (product.getName().equalsIgnoreCase(name)) {
        return product;
      }
    }
    return null;
  }

  public List<String> getDeactivatedProducts() {
    ArrayList<String> deactivatedPP = new ArrayList<String>();
    for (ItsmaProduct product : itsmaProducts) {
      if (!product.isActive()) {
        deactivatedPP.add(product.getName());
      }
    }
    return deactivatedPP;
  }

  public boolean isValidProductName(String productName) {
    for (ItsmaProduct product : itsmaProducts) {
      if (product.getName().equals(productName)) {
        return true;
      }
    }
    return false;
  }

  private void activeHModeProducts(List<String> activeServiceNames, List<ItsmaProduct> itsmaProducts, String externalLdap) {
    //if activeServiceNames is empty, all the products of infrastructure and H-Mode will be installed.
    logger.debug("Active Services: " + activeServiceNames);
    for (ItsmaProduct product : itsmaProducts) {
      boolean bActiveServiceNames;
      if (activeServiceNames.isEmpty()) {
        bActiveServiceNames = true;
      } else {
        bActiveServiceNames = activeServiceNames.contains(product.getName());
      }

      if (this.isInfraProduct(product.getName())) {
        if (product.getName().equalsIgnoreCase(ItsmaInstallerConstants.ITOM_OPENLDAP) && externalLdap.equalsIgnoreCase("true")) {
          logger.debug("Infrastructure Product <" + product.getName() + "> has been disabled.");
          product.setActive(false);
        } else if (product.isActive()) {
          // Active the product if the 'active' field is true in itsmaProducts.json
          product.setActive(true);
        } else {
          logger.debug("Infrastructure Product <" + product.getName() + "> has been disabled.");
          product.setActive(false);
        }
      } else if (this.isHModeProduct(product.getName()) && product.isActive() && bActiveServiceNames) {
        // There is a prerequisite that the product will be actived if the 'active' field is true in itsmaProducts.json
        product.setActive(true);
      } else {
        logger.debug("H-Mode Product <" + product.getName() + "> has been disabled.");
        product.setActive(false);
      }
    }
  }

  private void activeXModeProducts(List<String> activeServiceNames, List<ItsmaProduct> itsmaProducts, String externalLdap) {
    //if activeServiceNames is empty, all the products of infrastructure and X-Mode will be installed.
    logger.debug("Active Services: " + activeServiceNames);
    for (ItsmaProduct product : itsmaProducts) {
      boolean bActiveServiceNames;
      if (activeServiceNames.isEmpty()) {
        bActiveServiceNames = true;
      } else {
        bActiveServiceNames = activeServiceNames.contains(product.getName());
      }

      if (this.isInfraProduct(product.getName())) {
        if (product.getName().equalsIgnoreCase(ItsmaInstallerConstants.ITOM_OPENLDAP) && externalLdap.equalsIgnoreCase("true")) {
          logger.debug("Infrastructure Product <" + product.getName() + "> has been disabled.");
          product.setActive(false);
        } else if (product.isActive()) {
          // Active the product if the 'active' field is true in itsmaProducts.json
          product.setActive(true);
        } else {
          logger.debug("Infrastructure Product <" + product.getName() + "> has been disabled.");
          product.setActive(false);
        }
      } else if (this.isXModeProduct(product.getName()) && product.isActive() && bActiveServiceNames) {
        // There is a prerequisite that the product will be actived if the 'active' field is true in itsmaProducts.json
        product.setActive(true);
      } else {
        logger.debug("X-Mode Product <" + product.getName() + "> has been disabled.");
        product.setActive(false);
      }
    }
  }
}
