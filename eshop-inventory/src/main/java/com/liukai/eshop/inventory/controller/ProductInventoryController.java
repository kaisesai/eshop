package com.liukai.eshop.inventory.controller;

import com.liukai.eshop.inventory.entity.ProductInventory;
import com.liukai.eshop.inventory.service.ProductInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * (ProductInventory)表控制层
 *
 * @author liukai
 * @since 2020-06-03 21:37:55
 */
@RestController
@RequestMapping("productInventory")
public class ProductInventoryController {

  /**
   * 服务对象
   */
  @Autowired
  private ProductInventoryService productInventoryService;

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
