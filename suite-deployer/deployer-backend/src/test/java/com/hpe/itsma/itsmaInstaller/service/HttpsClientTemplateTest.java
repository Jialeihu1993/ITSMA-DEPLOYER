package com.hpe.itsma.itsmaInstaller.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

/**
 * Created by zhongtao on 7/26/2017.
 */
public class HttpsClientTemplateTest {

  private HttpsClientTemplate httpsClientTemplate;

  @Before
  public void setUp(){
    httpsClientTemplate = new HttpsClientTemplate();
  }

  /**
   * Test method for {@link HttpsClientTemplate#getRestTemplate()}
   *
   * Condition:
   * <ol>
   * <li>Happy path</li>
   * </ol>
   *
   * Expectation:
   * <ol>
   * <li>RestTemplate has 'httpClient'</li>
   * </ol>
   *
   */
  @Test
  public void testGetRestTemplate() throws NoSuchFieldException{
    RestTemplate restTemplate = new RestTemplate();
    restTemplate = httpsClientTemplate.getRestTemplate();
    Assert.assertEquals("org.springframework.http.client.HttpComponentsClientHttpRequestFactory",restTemplate.getRequestFactory().getClass().getName());
    Assert.assertNotNull(restTemplate.getRequestFactory().getClass().getDeclaredField("httpClient"));
  }
}