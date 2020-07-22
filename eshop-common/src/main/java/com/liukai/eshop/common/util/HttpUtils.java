package com.liukai.eshop.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class HttpUtils {

  public static void main(String[] args) {
    String url = "https://www.baidu.com/s";
    Map<String, String> params = new HashMap<>();
    params.put("ie", "utf-8");
    params.put("wd", "java");
    params.put("f", "8");

    get(url, params);
  }

  public static String get(String url) {
    return get(url, null);
  }

  public static String get(String url, Map<String, String> params) {
    String requestParams = buildGetParams(params);

    // 将参数拼接到 url 后面
    String requestUrl = url + requestParams;
    CloseableHttpClient httpClient = HttpClients.createDefault();
    try {
      HttpGet httpGet = new HttpGet(requestUrl);
      httpGet.addHeader("Accept-Charset", StandardCharsets.UTF_8.name());

      CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
      String response = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
      log.info("[HttpUtils get success]\t[url: {}, response: {}]", requestUrl, response);
      return response;
    } catch (Exception e) {
      log.error(String.format("[HttpUtils get fail]\t[url: %s]", requestUrl), e);
      throw new RuntimeException(e);
    } finally {
      HttpClientUtils.closeQuietly(httpClient);
      log.info("[HttpUtils get finish]\t[url: {}, params: {}]", url, params);
    }
  }

  private static String buildGetParams(Map<String, String> params) {
    if (MapUtils.isEmpty(params)) {
      return StringUtils.EMPTY;
    } else {
      return params.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
                   .collect(Collectors.joining("&", "?", ""));
    }
  }
}
