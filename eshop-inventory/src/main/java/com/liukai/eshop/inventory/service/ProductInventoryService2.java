package com.liukai.eshop.inventory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liukai.eshop.inventory.entity.ProductInventory;

/**
 * 商品库存 service
 *
 * @author liukai
 */
public interface ProductInventoryService2 extends IService<ProductInventory> {

  /**
   * 查询库存缓存
   *
   * @param productId 商品库存 id
   */
  ProductInventory getCache(Long productId);

  ProductInventory getByProductId(Long productId);


  /**
   * 删除库存缓存
   *
   * @param productId 商品库存 id
   */
  void removeCache(Long productId);

  /**
   * 设置库存缓存
   *
   * @param productInventory 商品库存
   */
  void setCache(ProductInventory productInventory);




}
