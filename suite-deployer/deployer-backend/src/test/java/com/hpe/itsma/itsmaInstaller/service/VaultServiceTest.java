package com.hpe.itsma.itsmaInstaller.service;

import com.hpe.itsma.itsmaInstaller.bean.ItsmaProduct;
import com.hpe.itsma.itsmaInstaller.bean.RuntimeProfileTest;
import com.hpe.itsma.itsmaInstaller.exception.VaultSecretValueEmptyException;
import com.hpe.itsma.itsmaInstaller.util.ItsmaUtil;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Created by zhongtao on 8/1/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class VaultServiceTest {

  @Autowired
  private ProductsService productsService;

  @Autowired
  private VaultService vaultService;

  @After
  public void tearDown() {
    // clean up
    if (!ItsmaUtil.isNullOrEmpty(productsService.getItsmaProducts())) {
      productsService.getItsmaProducts().clear();
    }
  }

  @Test
  public void test_updateVaultSecretValue_EmptySecretValue() {
    try {
      vaultService.updateVaultSecretValue(null, "xxxx", "");
      fail("Expected: VaultSecretValueEmptyException, Actual: No exception");
    } catch (Exception e) {
      if (!(e instanceof VaultSecretValueEmptyException)) {
        fail("Expected: VaultSecretValueEmptyException, Actual: ", e);
      }
    }
  }

  @Test
  public void test_updateVaultSecretValue_SpecifiedProduct() {
    InputStream is = VaultServiceTest.class.getClassLoader().getResourceAsStream("com.hpe.itsma.itsmaInstaller.service/itsmaProducts.json");
    try {
      productsService.loadProducts(is);
    } catch (Exception e) {
      fail("failed to load itsma products");
    }

    try {
      vaultService.updateVaultSecretValue("itom-sm", "itom_itsma_db_password_secret_key", "1234567890");
      assertThat(productsService.getItsmaProduct("itom-sm").getVault().getSecrets().stream().filter(
        secret -> secret.getSecretKey().equalsIgnoreCase("itom_itsma_db_password_secret_key")
      ).findFirst().get().getSecretValue()).isEqualToIgnoringCase("1234567890");
    } catch (Exception e) {
      fail("Expected: no exception, Actual: ", e);
    }
  }

  @Test
  public void test_updateVaultSecretValue_AllProducts() {
    InputStream is = VaultServiceTest.class.getClassLoader().getResourceAsStream("com.hpe.itsma.itsmaInstaller.service/itsmaProducts.json");
    try {
      productsService.loadProducts(is);
    } catch (Exception e) {
      fail("failed to load itsma products");
    }

    try {
      vaultService.updateVaultSecretValue(null, "itom_itsma_sysadmin_password_secret_key", "1234567890");
      productsService.getItsmaProducts().stream().forEach(
        itsmaProduct -> {
          Optional<ItsmaProduct.Vault.Secret> s;
          if (itsmaProduct.getVault() != null) {
            s = itsmaProduct.getVault().getSecrets().stream().filter(
              secret -> secret.getSecretKey().equalsIgnoreCase("itom_itsma_sysadmin_password_secret_key")
            ).findFirst();
            if (s.isPresent()) {
              assertThat(s.get().getSecretValue()).isEqualToIgnoringCase("1234567890");
            }
          }
        }
      );
    } catch (Exception e) {
      fail("Expected: no exception, Actual: ", e);
    }
  }
}
