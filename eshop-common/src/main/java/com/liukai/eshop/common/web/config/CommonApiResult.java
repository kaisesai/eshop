package com.liukai.eshop.common.web.config;

import java.lang.annotation.*;

/**
 * 通用 api 接口注释，使用注解标识的接口方法或者接口所在的类，会被标记为使用返回值使用同一的响应参数返回给客户端。
 * <p>
 * 同一返回类型为参见：{@link com.liukai.eshop.common.web.model.Result} 类
 */
@Documented
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommonApiResult {

}
