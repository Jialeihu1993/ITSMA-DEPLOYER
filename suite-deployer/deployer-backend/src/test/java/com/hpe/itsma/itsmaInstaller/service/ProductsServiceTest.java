package com.hpe.itsma.itsmaInstaller.service;

import com.hpe.itsma.itsmaInstaller.bean.RuntimeProfileTest;
import com.hpe.itsma.itsmaInstaller.constants.ItsmaInstallerConstants;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Created by zhongtao on 8/1/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ProductsServiceTest {
  @Autowired
  private ProductsService productsService;

  private final int productSize = 17;

  @Before
  public void setUp() {
    // clean up
    productsService.getItsmaProducts().clear();
  }

  /**
   * Test method for {@link com.hpe.itsma.itsmaInstaller.service.ProductsService#loadProducts}
   * <p>
   * Condition:
   * <ol>
   * <li>Happy path</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li>The number of ItsmaProduct is 18.</li>
   * <li>The product is active or not pending on the field 'active' in itsmaProducts.json</li>
   * </ol>
   */
  @Test
  public void testLoadProducts() throws IOException {
    InputStream is = ProductsServiceTest.class.getClassLoader().getResourceAsStream("com.hpe.itsma.itsmaInstaller.service/itsmaProducts.json");
    productsService.loadProducts(is);
    Assert.assertEquals(productSize, productsService.getItsmaProducts().size());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_AUTH).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_IDM).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_INGRESS).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_AUTOPASS).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_ITSMA_CONFIG).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_OPENLDAP).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_SM).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_CHAT).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_SMARTANALYTICS).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_SERVICE_PORTAL).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XSERVICES).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XSERVICES_INFRA).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XRUNTIME).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XRUNTIME_INFRA).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_LANDING_PAGE).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_CMDB).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_AUTOMATION).isActive());
  }

  /**
   * Test method for {@link com.hpe.itsma.itsmaInstaller.service.ProductsService#deactiveProducts}
   * <p>
   * Condition:
   * <ol>
   * <li>H-Mode & 'activated_services' is empty in the payload </li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li> All the products of infrastructure and H-Mode will be installed.</li>
   * </ol>
   */
  @Test
  public void testDeactiveProductsHModeFullInstallation() throws IOException {
    InputStream is = ProductsServiceTest.class.getClassLoader().getResourceAsStream("com.hpe.itsma.itsmaInstaller.service/itsmaProducts.json");
    Map<String, Object> properties = new HashMap<>();
    properties.put("itom_suite_mode", "H_MODE");

    productsService.loadProducts(is);
    productsService.deactiveProducts(properties);

    Assert.assertEquals(productSize, productsService.getItsmaProducts().size());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_AUTH).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_IDM).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_INGRESS).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_AUTOPASS).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_ITSMA_CONFIG).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_OPENLDAP).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_SM).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_CHAT).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_SMARTANALYTICS).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_SERVICE_PORTAL).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XSERVICES).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XSERVICES_INFRA).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XRUNTIME).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XRUNTIME_INFRA).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_LANDING_PAGE).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_CMDB).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_AUTOMATION).isActive());
  }

  /**
   * Test method for {@link com.hpe.itsma.itsmaInstaller.service.ProductsService#deactiveProducts}
   * <p>
   * Condition:
   * <ol>
   * <li>H-Mode & only itom-sm in the payload </li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li> All the products of infrastructure and only itom-sm will be installed.</li>
   * </ol>
   */
  @Test
  public void testDeactiveProductsHModeSM() throws IOException {
    InputStream is = ProductsServiceTest.class.getClassLoader().getResourceAsStream("com.hpe.itsma.itsmaInstaller.service/itsmaProducts.json");
    List<Map<String, String>> services = new ArrayList<>();
    Map<String, String> sm = new HashMap<>();
    sm.put("name", "itom-sm");

    services.add(sm);

    Map<String, Object> properties = new HashMap<>();
    properties.put("itom_suite_mode", "H_MODE");
    properties.put("activated_services", services);

    productsService.loadProducts(is);
    productsService.deactiveProducts(properties);

    Assert.assertEquals(productSize, productsService.getItsmaProducts().size());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_AUTH).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_IDM).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_INGRESS).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_AUTOPASS).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_ITSMA_CONFIG).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_OPENLDAP).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_SM).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_CHAT).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_SMARTANALYTICS).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_SERVICE_PORTAL).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XSERVICES).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XSERVICES_INFRA).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XRUNTIME).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XRUNTIME_INFRA).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_LANDING_PAGE).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_CMDB).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_AUTOMATION).isActive());
  }

  /**
   * Test method for {@link com.hpe.itsma.itsmaInstaller.service.ProductsService#deactiveProducts}
   * <p>
   * Condition:
   * <ol>
   * <li>H-Mode & only itom-chat in the payload, but itom-chat's active field is false.</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li> Only the products of infrastructure will be installed. itom-chat will not be installed.</li>
   * </ol>
   */
  @Test
  public void testDeactiveProductsHModeChat() throws IOException {
    InputStream is = ProductsServiceTest.class.getClassLoader().getResourceAsStream("com.hpe.itsma.itsmaInstaller.service/itsmaProducts.json");
    List<Map<String, String>> services = new ArrayList<>();
    Map<String, String> chat = new HashMap<>();
    chat.put("name", "itom-chat");

    services.add(chat);

    Map<String, Object> properties = new HashMap<>();
    properties.put("itom_suite_mode", "H_MODE");
    properties.put("activated_services", services);

    productsService.loadProducts(is);
    productsService.deactiveProducts(properties);

    Assert.assertEquals(productSize, productsService.getItsmaProducts().size());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_AUTH).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_IDM).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_INGRESS).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_AUTOPASS).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_ITSMA_CONFIG).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_OPENLDAP).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_SM).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_CHAT).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_SMARTANALYTICS).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_SERVICE_PORTAL).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XSERVICES).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XSERVICES_INFRA).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XRUNTIME).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XRUNTIME_INFRA).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_LANDING_PAGE).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_CMDB).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_AUTOMATION).isActive());
  }

  /**
   * Test method for {@link com.hpe.itsma.itsmaInstaller.service.ProductsService#deactiveProducts}
   * <p>
   * Condition:
   * <ol>
   * <li>H-Mode & 'activated_services' is empty & Using external LDAP</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li> All the products of infrastructure except itom-openldap and H-Mode will be installed.</li>
   * </ol>
   */
  @Test
  public void testDeactiveProductsHModeExternalLDAP() throws IOException {
    InputStream is = ProductsServiceTest.class.getClassLoader().getResourceAsStream("com.hpe.itsma.itsmaInstaller.service/itsmaProducts.json");
    Map<String, Object> properties = new HashMap<>();
    properties.put("itom_suite_mode", "H_MODE");
    properties.put("external_ldap", true);

    productsService.loadProducts(is);
    productsService.deactiveProducts(properties);

    Assert.assertEquals(productSize, productsService.getItsmaProducts().size());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_AUTH).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_IDM).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_INGRESS).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_AUTOPASS).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_ITSMA_CONFIG).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_OPENLDAP).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_SM).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_CHAT).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_SMARTANALYTICS).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_SERVICE_PORTAL).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XSERVICES).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XSERVICES_INFRA).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XRUNTIME).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XRUNTIME_INFRA).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_LANDING_PAGE).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_CMDB).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_AUTOMATION).isActive());
  }

  /**
   * Test method for {@link com.hpe.itsma.itsmaInstaller.service.ProductsService#deactiveProducts}
   * <p>
   * Condition:
   * <ol>
   * <li>X-Mode & 'activated_services' is empty in the payload </li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li> All the products of infrastructure and X-Mode will be installed.</li>
   * </ol>
   */
  @Test
  public void testDeactiveProductsXModeFullInstallation() throws IOException {
    InputStream is = ProductsServiceTest.class.getClassLoader().getResourceAsStream("com.hpe.itsma.itsmaInstaller.service/itsmaProducts.json");
    Map<String, Object> properties = new HashMap<>();
    properties.put("itom_suite_mode", "X_MODE");

    productsService.loadProducts(is);
    productsService.deactiveProducts(properties);

    Assert.assertEquals(productSize, productsService.getItsmaProducts().size());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_AUTH).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_IDM).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_INGRESS).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_AUTOPASS).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_ITSMA_CONFIG).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_OPENLDAP).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_SM).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_CHAT).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_SMARTANALYTICS).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_SERVICE_PORTAL).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XSERVICES).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XSERVICES_INFRA).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XRUNTIME).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XRUNTIME_INFRA).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_LANDING_PAGE).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_CMDB).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_AUTOMATION).isActive());
  }

  /**
   * Test method for {@link com.hpe.itsma.itsmaInstaller.service.ProductsService#deactiveProducts}
   * <p>
   * Condition:
   * <ol>
   * <li>itom-xruntime & itom-xruntime-infra are in the payload</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li>Only itom-xruntime & itom-xruntime-infra & some infrastructure products will be installed.</li>
   * <li>CMDB will not be installed.</li>
   * </ol>
   */
  @Test
  public void testDeactiveProductsXModeCMDBNotInstalled() throws IOException {
    List<Map<String, String>> services = new ArrayList<>();
    Map<String, String> xruntime = new HashMap<>();
    xruntime.put("name", "itom-xruntime");

    Map<String, String> xruntimeInfra = new HashMap<>();
    xruntimeInfra.put("name", "itom-xruntime-infra");

    services.add(xruntime);
    services.add(xruntimeInfra);

    Map<String, Object> properties = new HashMap<>();
    properties.put("itom_suite_mode", "X_MODE");
    properties.put("activated_services", services);

    InputStream is = ProductsServiceTest.class.getClassLoader().getResourceAsStream("com.hpe.itsma.itsmaInstaller.service/itsmaProducts.json");
    productsService.loadProducts(is);
    productsService.deactiveProducts(properties);

    Assert.assertEquals(productSize, productsService.getItsmaProducts().size());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_AUTH).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_IDM).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_INGRESS).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_AUTOPASS).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_ITSMA_CONFIG).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_OPENLDAP).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_SM).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_CHAT).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_SMARTANALYTICS).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_SERVICE_PORTAL).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XSERVICES).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XSERVICES_INFRA).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XRUNTIME).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XRUNTIME_INFRA).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_LANDING_PAGE).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_CMDB).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_AUTOMATION).isActive());
  }

  /**
   * Test method for {@link com.hpe.itsma.itsmaInstaller.service.ProductsService#deactiveProducts}
   * <p>
   * Condition:
   * <ol>
   * <li>X-Mode & 'activated_services' is empty & Using external LDAP</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li> All the products of infrastructure except itom-openldap and X-Mode will be installed.</li>
   * </ol>
   */
  @Test
  public void testDeactiveProductsXModeExternalLDAP() throws IOException {
    InputStream is = ProductsServiceTest.class.getClassLoader().getResourceAsStream("com.hpe.itsma.itsmaInstaller.service/itsmaProducts.json");
    Map<String, Object> properties = new HashMap<>();
    properties.put("itom_suite_mode", "X_MODE");
    properties.put("external_ldap", true);

    productsService.loadProducts(is);
    productsService.deactiveProducts(properties);

    Assert.assertEquals(productSize, productsService.getItsmaProducts().size());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_AUTH).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_IDM).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_INGRESS).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_AUTOPASS).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_ITSMA_CONFIG).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_OPENLDAP).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_SM).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_CHAT).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_SMARTANALYTICS).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_SERVICE_PORTAL).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XSERVICES).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XSERVICES_INFRA).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XRUNTIME).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_XRUNTIME_INFRA).isActive());
    Assert.assertEquals(false, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_LANDING_PAGE).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_CMDB).isActive());
    Assert.assertEquals(true, productsService.getItsmaProduct(ItsmaInstallerConstants.ITOM_AUTOMATION).isActive());
  }

  /**
   * Test method for {@link com.hpe.itsma.itsmaInstaller.service.ProductsService#deactiveProducts}
   * <p>
   * Condition:
   * <ol>
   * <li>invalid itom_suite_mode inputed</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li> All the products of infrastructure except itom-openldap and X-Mode will be installed.</li>
   * </ol>
   */
  @Test(expected = IllegalArgumentException.class)
  public void testDeactiveProductsInvalidMode() throws IOException {
    InputStream is = ProductsServiceTest.class.getClassLoader().getResourceAsStream("com.hpe.itsma.itsmaInstaller.service/itsmaProducts.json");
    Map<String, Object> properties = new HashMap<>();
    properties.put("itom_suite_mode", "Invalid_MODE");

    productsService.loadProducts(is);
    productsService.deactiveProducts(properties);
  }
}
