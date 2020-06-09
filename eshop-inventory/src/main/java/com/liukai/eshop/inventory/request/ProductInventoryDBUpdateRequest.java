package com.liukai.eshop.inventory.request;

import com.liukai.eshop.inventory.service.ProductInventoryService2;
import com.liukai.eshop.model.entity.ProductInventory;
import lombok.extern.slf4j.Slf4j;

/**
 * 商品库存数据更新请求
 */
@Slf4j
public class ProductInventoryDBUpdateRequest implements Request {

  private ProductInventory productInventory;

  private ProductInventoryService2 productInventoryService;

  public ProductInventoryDBUpdateRequest(ProductInventory productInventory,
                                         ProductInventoryService2 productInventoryService) {
    this.productInventory = productInventory;
    this.productInventoryService = productInventoryService;
  }

  @Override
  public void process() {
    // 1. 删除缓存
    productInventoryService.removeCache(productInventory.getProductId());
    log.info("删除缓存数据，product_id:{}", productInventory.getProductId());

    // log.debug("更新数据库操作：写请求：模拟写耗时操作，休眠 10 秒钟");
    // try {
    //   TimeUnit.SECONDS.sleep(10);
    // } catch (InterruptedException e) {
    //   e.printStackTrace();
    // }

    // 2. 更新缓存
    productInventoryService.updateById(productInventory);
    log.debug("更新数据库，product_id:{}，productInventory:{}", productInventory.getProductId(),
              productInventory);
  }

  @Override
  public Long getProductId() {
    return this.productInventory.getProductId();
  }

  @Override
  public boolean isForceRefresh() {
    return false;
  }

}
