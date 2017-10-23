package com.hpe.itsma.itsmaInstaller.util;

import com.hpe.itsma.itsmaInstaller.bean.SSRestResponseBody;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhongtao on 7/26/2017.
 */
public class ItsmaUtilTest {

  /**
   * Test method for {@link ItsmaUtil#isNullOrEmpty(java.lang.Object)}
   * <p>
   * Condition:
   * <ol>
   * <li>Object is null</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li>Return true</li>
   * </ol>
   */
  @Test
  public void testIsNullOrEmptyNull() {
    Assert.assertTrue(ItsmaUtil.isNullOrEmpty(null));
  }

  /**
   * Test method for {@link ItsmaUtil#isNullOrEmpty(java.lang.Object)}
   * <p>
   * Condition:
   * <ol>
   * <li>String is empty</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li>Return true</li>
   * </ol>
   */
  @Test
  public void testIsNullOrEmptyEmptyString() {
    Assert.assertTrue(ItsmaUtil.isNullOrEmpty(""));
  }

  /**
   * Test method for {@link ItsmaUtil#isNullOrEmpty(java.lang.Object)}
   * <p>
   * Condition:
   * <ol>
   * <li>Map is empty</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li>Return true</li>
   * </ol>
   */
  @Test
  public void testIsNullOrEmptyEmptyMap() {
    Assert.assertTrue(ItsmaUtil.isNullOrEmpty(new HashMap<>()));
  }

  /**
   * Test method for {@link ItsmaUtil#isNullOrEmpty(java.lang.Object)}
   * <p>
   * Condition:
   * <ol>
   * <li>Array length is 0</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li>Return true</li>
   * </ol>
   */
  @Test
  public void testIsNullOrEmptyZeroLengthArray() {
    Assert.assertTrue(ItsmaUtil.isNullOrEmpty(new Object[]{}));
  }

  /**
   * Test method for {@link ItsmaUtil#isNullOrEmpty(java.lang.Object)}
   * <p>
   * Condition:
   * <ol>
   * <li>Array is not null, but all the value is empty</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li>Return true</li>
   * </ol>
   */
  @Test
  public void testIsNullOrEmptyArray() {
    Assert.assertTrue(ItsmaUtil.isNullOrEmpty(new Object[]{"", ""}));
  }

  /**
   * Test method for {@link ItsmaUtil#isNullOrEmpty(java.lang.Object)}
   * <p>
   * Condition:
   * <ol>
   * <li>Array is not empty</li>
   * </ol>
   * <p>
   * Expectation:
   * <ol>
   * <li>Return false</li>
   * </ol>
   */
  @Test
  public void testIsNullOrNotEmptyArray() {
    Assert.assertFalse(ItsmaUtil.isNullOrEmpty(new Object[]{"", 1}));
  }

  /**
   * Test method for {@link ItsmaUtil#unmarshallJson(java.lang.String, java.lang.Class)}
   */
  @Test
  public void testUnmarshallJson() throws Exception {
    InputStream is = ItsmaUtilTest.class.getClassLoader().getResourceAsStream("com.hpe.itsma.itsmaInstaller.util/response-ss-community.json");
    String responseBody = ItsmaUtil.inputStream2String(is);
    SSRestResponseBody ssRestResponseBody = ItsmaUtil.unmarshallJson(responseBody, SSRestResponseBody.class);
    Assert.assertEquals("106", ssRestResponseBody.getInstallationId());
    Assert.assertEquals("smarta-ss-community-svc", ssRestResponseBody.getBatchInfo().getKubeInstanceInfoList().get(0).getInstanceName());
  }

  /**
   * Test method for {@link ItsmaUtil#putPropIfNotExist(java.util.Map, java.lang.String, java.lang.Object)}
   */
  @Test
  public void testPutPropIfNotExist(){
    Map<String, Object> properties = new HashMap<>();
    properties.put("key1", "value1");
    ItsmaUtil.putPropIfNotExist(properties,"key2", "value2");
    ItsmaUtil.putPropIfNotExist(properties,"key1", "value2");
    Assert.assertEquals("value1", properties.get("key1"));
    Assert.assertEquals("value2", properties.get("key2"));
  }

  /**
   * Test method for {@link ItsmaUtil#getPropertyEx(java.util.Map, java.lang.String)}
   */
  @Test(expected = IllegalArgumentException.class)
  public void testGetPropertyEx() {
    Map<String, Object> properties = new HashMap<>();
    ItsmaUtil.getPropertyEx(properties,"key1");
  }

  /**
   * Test method for {@link ItsmaUtil#getProperty(java.util.Map, java.lang.String)}
   */
  @Test
  public void testGetProperty() throws Exception {
    Map<String, Object> properties = new HashMap<>();
    Assert.assertEquals("", ItsmaUtil.getProperty(properties,"key1"));
  }

  /**
   * Test method for {@link ItsmaUtil#inputStream2String(java.io.InputStream)}
   */
  @Test
  public void testInputStream2String() throws Exception {
    InputStream is = ItsmaUtilTest.class.getClassLoader().getResourceAsStream("com.hpe.itsma.itsmaInstaller.util/test.json");
    String json = ItsmaUtil.inputStream2String(is);
    Assert.assertEquals("{  \"name\": \"itom-sm\",  \"status\": \"SUCCESS\"}", json);
  }
}