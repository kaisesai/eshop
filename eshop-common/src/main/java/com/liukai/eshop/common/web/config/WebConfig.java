package com.liukai.eshop.common.web.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

/**
 * web相关的定制化配置
 *
 * @author liukai
 */
@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

  // @Override
  // public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
  //       /*
  //        先把JackSon的消息转换器删除.
  //        备注: (1)源码分析可知，返回json的过程为:
  //                   Controller调用结束后返回一个数据对象，for循环遍历conventers，找到支持application/json的HttpMessageConverter，然后将返回的数据序列化成json。
  //                   具体参考org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor的writeWithMessageConverters方法
  //              (2)由于是list结构，我们添加的fastjson在最后。因此必须要将jackson的转换器删除，不然会先匹配上jackson，导致没使用fastjson
  //       */
  //   for (int i = converters.size() - 1; i >= 0; i--) {
  //     if (converters.get(i) instanceof MappingJackson2HttpMessageConverter) {
  //       converters.remove(i);
  //     }
  //   }
  //   FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
  //   //自定义fastjson配置
  //   FastJsonConfig config = new FastJsonConfig();
  //   config.setSerializerFeatures(SerializerFeature.WriteMapNullValue,
  //                                // 是否输出值为null的字段,默认为false,我们将它打开
  //                                SerializerFeature.WriteNullListAsEmpty,
  //                                // 将Collection类型字段的字段空值输出为[]
  //                                SerializerFeature.WriteNullStringAsEmpty,   // 将字符串类型字段的空值输出为空字符串
  //                                SerializerFeature.WriteNullNumberAsZero,    // 将数值类型字段的空值输出为0
  //                                SerializerFeature.WriteDateUseDateFormat,
  //                                SerializerFeature.DisableCircularReferenceDetect    // 禁用循环引用
  //                               );
  //   fastJsonHttpMessageConverter.setFastJsonConfig(config);
  //   // 添加支持的MediaTypes;不添加时默认为*/*,也就是默认支持全部
  //   // 但是MappingJackson2HttpMessageConverter里面支持的MediaTypes为application/json
  //   // 参考它的做法, fastjson也只添加application/json的MediaType
  //   List<MediaType> fastMediaTypes = new ArrayList<>();
  //   fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
  //   fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
  //   converters.add(fastJsonHttpMessageConverter);
  // }

  // @Override
  // public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
  //   //1、定义一个convert转换消息的对象
  //   FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
  //   //2、添加fastjson的配置信息
  //   FastJsonConfig fastJsonConfig = new FastJsonConfig();
  //   fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat,
  //                                        // 是否输出值为null的字段,默认为false,我们将它打开
  //                                        SerializerFeature.WriteMapNullValue,
  //                                        // 将Collection类型字段的字段空值输出为[]
  //                                        SerializerFeature.WriteNullListAsEmpty,
  //                                        // 将字符串类型字段的空值输出为空字符串
  //                                        SerializerFeature.WriteNullStringAsEmpty,
  //                                        // 将数值类型字段的空值输出为0
  //                                        SerializerFeature.WriteNullNumberAsZero,
  //                                        SerializerFeature.WriteDateUseDateFormat,
  //                                        // 禁用循环引用
  //                                        SerializerFeature.DisableCircularReferenceDetect
  //                                        // 驼峰转下划线
  //                                        //  SerializerFeature.PropertyNamingStrategy
  //                                       );
  //
  //   SerializeConfig serializeConfig = new SerializeConfig();
  //   serializeConfig.setPropertyNamingStrategy(PropertyNamingStrategy.SnakeCase);
  //
  //   fastJsonConfig.setSerializeConfig(serializeConfig);
  //
  //   //3、在convert中添加配置信息
  //   fastConverter.setFastJsonConfig(fastJsonConfig);
  //   //4、将convert添加到converters中
  //   converters.add(fastConverter);
  //   //5、追加默认转换器
  //   super.addDefaultHttpMessageConverters(converters);
  // }

  /**
   * 统一输出风格
   * See {@link PropertyNamingStrategy.SnakeCaseStrategy} for details.
   *
   * @param converters
   */
  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    for (int i = 0; i < converters.size(); i++) {
      if (converters.get(i) instanceof MappingJackson2HttpMessageConverter) {

        //设置转换时的配置，具体视情况配置，如果通过此处配置，使用全局。也可以通过在bean的属性上添加注解配置对应的bean
        ObjectMapper objectMapper = new ObjectMapper();
        // 统一返回数据的输出风格
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        // 时区
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        //序列化的时候序列对象的所有属性  
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        //属性为null的转换
        // objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        //反序列化的时候如果多了其他属性,不抛出异常  
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        //如果是空对象的时候,不抛异常  
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        //取消时间的转化格式,默认是时间戳,可以取消,同时需要设置要表现的时间格式  
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        converters.set(i, converter);
        break;
      }
    }
  }

}
