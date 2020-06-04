package com.liukai.eshop.inventory.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * (ProductInventory)实体类
 *
 * @author liukai
 * @since 2020-06-03 21:37:40
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class ProductInventory extends Common implements Serializable {

  private static final long serialVersionUID = 108080540113010836L;

  /**
   * 主键
   */
  private Long id;

  /**
   * 商品ID
   */
  private Long productId;

}
