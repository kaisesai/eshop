package com.liukai.eshop.inventory.config;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

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

}
