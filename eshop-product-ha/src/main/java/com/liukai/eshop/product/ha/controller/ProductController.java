package com.liukai.eshop.product.ha.controller;

import com.liukai.eshop.model.entity.ProductInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping(value = "/product/ha")
@RestController
public class ProductController {

  /**
   * 批量返回商品信息的接口
   *
   * @param productIdsStr 商品 id 用英文逗号分隔
   */
  @RequestMapping("/getProducts")
  public List<ProductInfo> getProduct(@RequestParam(value = "productIdsStr") String productIdsStr) {
    String[] productIds = productIdsStr.split(",");
    return Arrays.stream(productIds).map(productId -> {

      long productIdLong = 0;
      try {
        productIdLong = Long.parseLong(productId);
      } catch (NumberFormatException ignored) {
      }
      return ProductInfo.getDefaultInstance(productIdLong);
    }).collect(Collectors.toList());
  }

}
