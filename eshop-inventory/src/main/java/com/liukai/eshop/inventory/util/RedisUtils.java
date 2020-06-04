package com.liukai.eshop.inventory.util;

/**
 * Redis 工具类
 */
public class RedisUtils {

  /**
   * 生成 redis value 类型的键
   *
   * @param prefixKey 前缀键
   * @param key       业务 id
   * @return redis 键
   */
  public static String generatorValueKey(String prefixKey, Object key) {
    return prefixKey + key;
  }

}
