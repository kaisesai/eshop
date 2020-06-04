package com.liukai.eshop.inventory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liukai.eshop.inventory.entity.ProductInventory;
import com.liukai.eshop.inventory.mapper.ProductInventoryMapper;
import com.liukai.eshop.inventory.service.ProductInventoryService;
import org.springframework.stereotype.Service;

/**
 * 商品库存 service 实现类
 *
 * @author liukai
 */
@Service("productInventoryService")
public class ProductInventoryServiceImpl
  extends ServiceImpl<ProductInventoryMapper, ProductInventory> implements ProductInventoryService {

}
