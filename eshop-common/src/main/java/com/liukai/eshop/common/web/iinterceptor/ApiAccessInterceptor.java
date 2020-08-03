package com.liukai.eshop.common.web.iinterceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * API 接口请求访问拦截器
 * <p>
 * 用于打印 API 接口的访问日志，包含请求路径、请求参数、请求头等等信息
 */
@Slf4j
// @Component
public class ApiAccessInterceptor extends HandlerInterceptorAdapter implements InitializingBean {

  static final String KEY_API_ACCESS_START = "api.access.start";

  public ApiAccessInterceptor() {
  }

  public void init() {
    log.info(getClass().getSimpleName() + " init");
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
    throws Exception {
    // 请求执行之前，设置请求开始时间 key-value 信息
    request.setAttribute(KEY_API_ACCESS_START, System.currentTimeMillis());
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                              Object handler, Exception ex) throws Exception {
    // 构建请求参数、请求头、请求体、异常等信息，写入请求日志
    // LogItem logItem = LogParser.parse(request, ex);
    // logItem.PushBack("authUid", String.valueOf(AuthResolver.getAuthorizedUid()));
    // log(request, ex, logItem);
  }

  // private void log(HttpServletRequest request, Exception ex, LogItem logItem) {
  // StringBuilder logBf = new StringBuilder();
  // boolean ifFirst = true;
  // for (LogContent ct : logItem.GetLogContents()) {
  //   if (ifFirst) {
  //     ifFirst = false;
  //   } else {
  //     logBf.append(", ").append(ct.GetKey()).append("=").append(ct.GetValue());
  //   }
  // }
  // 日志包含token
  // AuthInfo authInfo = AuthResolver.getAuthInfo();
  // if (authInfo != null && authInfo.isAuthenticated() && authInfo.getSession() != null) {
  //   logBf.append(", token=").append(authInfo.getSession().getAuthKey());
  // }
  // logBf.append(", newClient=").append(ProtoJsonUtil.toJson(RequestContext.get().getClient()));
  // logBf.append(", header=").append(parseHeaders(request));
  // logger.info(logBf.toString());
  // if (ex != null) {
  //   String msg = String
  //     .format("ERROR -> ip=%s, uri=%s", WebUtil.getIp(request), WebUtil.getMappingUri(request));
  //   logger.error(msg, ex);
  // }
  // }

  private Map<String, String> parseHeaders(HttpServletRequest request) {
    Map<String, String> map = new HashMap<>();
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String key = headerNames.nextElement();
      String value = request.getHeader(key);
      map.put(key, value);
    }
    return map;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                         ModelAndView modelAndView) throws Exception {
    // do nothing
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    init();
  }

}
