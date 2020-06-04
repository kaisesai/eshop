package com.liukai.eshop.inventory.controller;

import com.liukai.eshop.inventory.entity.ProductInventory;
import com.liukai.eshop.inventory.request.ProductInventoryCacheRefreshRequest;
import com.liukai.eshop.inventory.request.ProductInventoryDBUpdateRequest;
import com.liukai.eshop.inventory.service.ProductInventoryService2;
import com.liukai.eshop.inventory.service.RequestAsyncProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * (ProductInventory)表控制层
 *
 * @author liukai
 * @since 2020-06-03 21:37:55
 */
@Slf4j
@RestController
@RequestMapping("productInventory")
public class ProductInventoryController {

  /**
   * 服务对象
   */
  @Autowired
  private ProductInventoryService2 productInventoryService;

  @Autowired
  private RequestAsyncProcessService requestAsyncProcessService;

  /**
   * 更新商品库存信息
   *
   * @param productInventory
   * @return
   */
  @PostMapping("/updateProductInventory")
  public boolean updateProductInventory(@RequestBody ProductInventory productInventory) {

    // 更新数据库请求
    ProductInventoryDBUpdateRequest request = new ProductInventoryDBUpdateRequest(productInventory,
                                                                                  productInventoryService);

    // 执行请求
    requestAsyncProcessService.process(request);

    return true;
  }

  /**
   * 查询商品库存信息
   *
   * @param productId
   * @return
   */
  @GetMapping("/getProductInventory")
  public ProductInventory getProductInventory(@RequestParam(value = "product_id") Long productId) {

    try {
      // 刷新缓存操作
      ProductInventoryCacheRefreshRequest request = new ProductInventoryCacheRefreshRequest(
        productId, productInventoryService);

      // 执行请求
      requestAsyncProcessService.process(request);
      ProductInventory productInventory;

      // 等待
      long startTime = System.currentTimeMillis();
      long waitTime = 0L;

      while (waitTime <= 200) {
        // 等待超过 200ms 直接退出
        // 尝试查询缓存
        productInventory = productInventoryService.getCache(productId);

        if (productInventory != null) {
          // 查到了直接返回结果
          return productInventory;
        } else {
          // 没有查到，等待一段时间
          Thread.sleep(20);
        }
        waitTime = System.currentTimeMillis() - startTime;
      }

      // 直接查询数据库
      productInventory = productInventoryService.getByProductId(productId);
      if (productInventory != null) {
        return productInventory;
      }

    } catch (InterruptedException e) {
      log.warn("InterruptedException", e);
    }

    // 没有查找到则返回 id 为 -1 的值
    ProductInventory productInventory = new ProductInventory();
    productInventory.setId(-1L);
    productInventory.setProductId(productId);
    return productInventory;
  }

  /**
   * 通过主键查询单条数据
   *
   * @param id 主键
   * @return 单条数据
   */
  @GetMapping("selectOne")
  public ProductInventory selectOne(@RequestParam Long id) {
    return this.productInventoryService.getById(id);
  }

}
