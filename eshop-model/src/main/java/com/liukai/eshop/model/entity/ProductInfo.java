package com.liukai.eshop.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 商品信息
 */
@EqualsAndHashCode(callSuper = false)
@Data
@Accessors(chain = true)
public class ProductInfo extends Common {

  private String name;

  private Long price;

  private String pictureList;

  private String specification;

  private String service;

  private String color;

  private String size;

  private Long shopId;

}
