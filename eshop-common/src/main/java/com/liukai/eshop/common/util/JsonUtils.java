package com.liukai.eshop.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Jackson 工具类
 */
@Slf4j
public class JsonUtils {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  static {
    //设置转换时的配置，具体视情况配置，如果通过此处配置，使用全局。也可以通过在bean的属性上添加注解配置对应的bean
    // 统一返回数据的输出风格
    OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    // 时区
    OBJECT_MAPPER.setTimeZone(TimeZone.getTimeZone("GMT+8"));

    //序列化的时候序列对象的所有属性  
    OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.ALWAYS);
    //属性为null的转换
    // objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    //反序列化的时候如果多了其他属性,不抛出异常  
    OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    //如果是空对象的时候,不抛异常  
    OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    //取消时间的转化格式,默认是时间戳,可以取消,同时需要设置要表现的时间格式  
    OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

  }

  public static String writeValueAsString(Object value) {
    try {
      return OBJECT_MAPPER.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      log.error("write value as string fail, value:{}, exception:{}", value, e.getMessage());
      return "";
    }
  }

  public static <T> T readValue(String content, Class<T> valueType) {
    try {
      if (StringUtils.isEmpty(content)) {
        return null;
      }
      return OBJECT_MAPPER.readValue(content, valueType);
    } catch (JsonProcessingException e) {
      log.error("read value fail, content:{}, valueType:{}, exception:{}", content, valueType,
                e.getMessage());
      return null;
    }
  }

  public static <T> T readValue(String content, TypeReference<T> valueTypeRef) {
    try {
      if (StringUtils.isEmpty(content)) {
        return null;
      }
      return OBJECT_MAPPER.readValue(content, valueTypeRef);
    } catch (JsonProcessingException e) {
      log.error("read value fail, content:{}, valueTypeRef:{}, exception:{}", content, valueTypeRef,
                e.getMessage());
      return null;
    }
  }

}
