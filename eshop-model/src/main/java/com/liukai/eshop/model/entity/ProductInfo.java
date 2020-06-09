package com.liukai.eshop.model.entity;

import lombok.*;

/**
 * 商品信息
 */
@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductInfo extends Common {

  private Long id;

  private String name;

  private String desc;

}
