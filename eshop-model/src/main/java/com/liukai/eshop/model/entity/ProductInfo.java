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

  public static ProductInfo getDefaultInstance(Long productId) {
    ProductInfo productInfo;
    productInfo = new ProductInfo();
    productInfo.setId(productId);
    productInfo.setName("iphone7手机");
    productInfo.setPrice(5599L);
    productInfo.setPictureList("a.jpg,b.jpg");
    productInfo.setSpecification("iphone7的规格");
    productInfo.setService("iphone7的售后服务");
    productInfo.setColor("红色,白色,黑色");
    productInfo.setSize("5.5");
    productInfo.setShopId(1L);
    return productInfo;
  }

  @Override
  public String toString() {
    return "ProductInfo{" + "name='" + name + '\'' + ", price=" + price + ", pictureList='"
      + pictureList + '\'' + ", specification='" + specification + '\'' + ", service='" + service
      + '\'' + ", color='" + color + '\'' + ", size='" + size + '\'' + ", shopId=" + shopId
      + ", id=" + id + ", createTime=" + createTime + ", updateTime=" + updateTime + '}';
  }
}
