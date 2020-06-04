package com.liukai.eshop.inventory.request;

import com.liukai.eshop.inventory.entity.ProductInventory;
import com.liukai.eshop.inventory.service.ProductInventoryService2;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 商品库存缓存刷新请求
 */
@Slf4j
public class ProductInventoryCacheRefreshRequest implements Request {

  /**
   * 商品 id
   */
  private final Long productId;

  private final ProductInventoryService2 productInventoryService;

  public ProductInventoryCacheRefreshRequest(Long productId,
                                             ProductInventoryService2 productInventoryService) {
    this.productId = productId;
    this.productInventoryService = productInventoryService;
  }

  @Override
  public void process() {
    // 1. 读取数据库数据
    ProductInventory productInventory = productInventoryService.getByProductId(productId);

    log.info("写请求：模拟写耗时操作，休眠 10 秒钟");
    try {
      TimeUnit.SECONDS.sleep(10);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // 2. 设置缓存
    productInventoryService.setCache(productInventory);
  }

  @Override
  public Long getProductId() {
    return productId;
  }

  @Override
  public boolean isForceRefresh() {
    return false;
  }

}
