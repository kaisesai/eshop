package com.liukai.eshop.common.web.config;

import com.liukai.eshop.common.util.JsonUtils;
import com.liukai.eshop.common.web.model.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 通用响应处理器
 *
 * @author liukai
 */
@RestControllerAdvice
public class ResponseHandler implements ResponseBodyAdvice<Object> {

  //当返回值为true时功能才生效
  @Override
  public boolean supports(MethodParameter returnType,
                          Class<? extends HttpMessageConverter<?>> converterType) {
    // if(converterType.isAssignableFrom(MappingJackson2HttpMessageConverter.class)){
    //   return true;
    // }else{
    //   return false;
    // }
    return true;
  }

  @Override
  public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                MediaType selectedContentType,
                                Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                ServerHttpRequest request, ServerHttpResponse response) {

    // 处理 string 字符串
    if (body instanceof String) {
      return JsonUtils.writeValueAsString(Result.succ(body));
    }
    return Result.succ(body);
  }

}
