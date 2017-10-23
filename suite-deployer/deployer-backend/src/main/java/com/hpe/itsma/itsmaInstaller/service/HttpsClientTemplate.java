package com.hpe.itsma.itsmaInstaller.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by tianlib on 7/10/2017.
 */
public class HttpsClientTemplate {
  private static Log logger = LogFactory.getLog(HttpsClientTemplate.class);


  public RestTemplate getRestTemplate() {

    CloseableHttpClient httpClient = null;
    try {
      httpClient = HttpClients.custom().
          setHostnameVerifier(new AllowAllHostnameVerifier()).
          setSslcontext(new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            public boolean isTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
              return true;
            }
          }).build()).build();
    } catch (Exception e) {
      throw new RuntimeException("Can't build SSL ingored httpclient");
    }
    HttpComponentsClientHttpRequestFactory factory = new
        HttpComponentsClientHttpRequestFactory(httpClient);

    // set timeout
    // factory.setConnectTimeout(1);

    // create restTemplate
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setRequestFactory(factory);

    return restTemplate;
  }
}
