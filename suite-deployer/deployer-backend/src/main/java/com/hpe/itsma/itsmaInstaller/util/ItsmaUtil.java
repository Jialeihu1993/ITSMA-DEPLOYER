package com.hpe.itsma.itsmaInstaller.util;

import com.google.gson.Gson;
import com.google.gson.internal.Primitives;
import com.hpe.itsma.itsmaInstaller.constants.ItsmaInstallerConstants;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

/**
 * Created by zhongtao on 7/5/2017.
 */
public class ItsmaUtil {
  private static Log logger = LogFactory.getLog(ItsmaUtil.class);

  private ItsmaUtil() {
  }

  public static boolean isNullOrEmpty(Object obj) {
    if (obj == null)
      return true;

    if (obj instanceof CharSequence)
      return ((CharSequence) obj).length() == 0;

    if (obj instanceof Collection)
      return ((Collection) obj).isEmpty();

    if (obj instanceof Map)
      return ((Map) obj).isEmpty();

    if (obj instanceof Object[]) {
      Object[] object = (Object[]) obj;
      if (object.length == 0) {
        return true;
      }
      boolean empty = true;
      for (int i = 0; i < object.length; i++) {
        if (!isNullOrEmpty(object[i])) {
          empty = false;
          break;
        }
      }
      return empty;
    }
    return false;
  }

  public static <T> T unmarshallJson(String json, Class<T> classOfT) {
    logger.debug("unmarshallJson JSON: " + json);
    Gson gson = new Gson();
    Object object = gson.fromJson(json, (Type) classOfT);

    return Primitives.wrap(classOfT).cast(object);
  }

  public static void putPropIfNotExist(Map<String, Object> properties, String key, Object defValue) {
    if (!properties.containsKey(key)) {
      properties.put(key, defValue);
    }
  }

  /**
   * Get property value from Map<String, Object> properties according to the property key.
   * If the property key is not exist, An IllegalArgumentException will be threw.
   * If you do not want an Exception, please use <code>ItsmaUtil#getProperty(java.util.Map, java.lang.String)</code>
   *
   * @param properties
   * @param key
   * @return
   * @throws IllegalArgumentException
   */
  public static Object getPropertyEx(Map<String, Object> properties, String key) throws IllegalArgumentException {
    Object value = properties.get(key);
    if (value == null) {
      throw new IllegalArgumentException("Parameter <" + key + "> can NOT be null.");
    }
    return value;
  }

  /**
   * Get property value from Map<String, Object> properties according to the property key.
   * If the property key is not exist, return ""
   *
   * @param properties
   * @param key
   * @return
   */
  public static Object getProperty(Map<String, Object> properties, String key) {
    Object value = properties.get(key);
    if (ItsmaUtil.isNullOrEmpty(value)) {
      return "";
    }
    return value;
  }

  public static String inputStream2String(InputStream is) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(is));
    StringBuffer buffer = new StringBuffer();
    String line = "";
    while ((line = in.readLine()) != null) {
      buffer.append(line);
    }
    return buffer.toString();
  }

  /**
   * Read json file in resources and transform to map
   *
   * @param filename
   * @return Map<String,Object>
   * @throws Exception
   */
  public static Map<String,Object> readResourceJson2Map(String filename) throws Exception{
    Map<String,Object> properties;
    InputStream is = Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream(filename);
    JSONObject propertiesMap = new JSONObject(IOUtils.toString(is, "UTF-8"));
    properties = propertiesMap.toMap();
    return properties;
  }
}
