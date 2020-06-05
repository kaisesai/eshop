package com.liukai.eshop.inventory.entity;

import lombok.*;

import java.io.Serializable;

/**
 * (ProductInventory)实体类
 *
 * @author liukai
 * @since 2020-06-03 21:37:40
 */
@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductInventory extends Common implements Serializable {

  /**
   * 默认的一个实例，id 为 -1
   */
  public static final ProductInventory DEFAULT_FAIL_INSTANCE;

  private static final long serialVersionUID = 108080540113010836L;

  static {
    DEFAULT_FAIL_INSTANCE = new ProductInventory();
    DEFAULT_FAIL_INSTANCE.setId(-1L);
  }

  /**
   * 主键
   */
  private Long id;

  /**
   * 商品ID
   */
  private Long productId;

  /**
   * 库存数量
   */
  private Long inventoryCnt;

}
