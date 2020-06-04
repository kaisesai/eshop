package com.liukai.eshop.inventory.config;

import com.liukai.eshop.inventory.model.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 通用响应处理器
 *
 * @author liukai
 */
@ControllerAdvice(basePackages = "com.liukai.eshop.inventory.controller")
public class ResponseHandler implements ResponseBodyAdvice<Object> {

  //当返回值为true时功能才生效
  @Override
  public boolean supports(MethodParameter methodParameter, Class aClass) {
    return true;
  }

  @Override
  public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                MediaType selectedContentType,
                                Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                ServerHttpRequest request, ServerHttpResponse response) {

    // if (o instanceof String) {
    //   // return JsonUtil.toJsonString(Result.succ(o));
    //   return JSON.toJSONString(Result.succ(o));
    // }
    return Result.succ(body);

  }

}
