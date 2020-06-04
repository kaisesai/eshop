package com.liukai.eshop.inventory;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author liukai
 */
@EnableTransactionManagement
@SpringBootApplication
@MapperScan("com.liukai.eshop.inventory.mapper")
public class EshopInventoryApplication {

  public static void main(String[] args) {
    SpringApplication.run(EshopInventoryApplication.class, args);
  }

  @Bean
  public PaginationInterceptor paginationInterceptor() {
    PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
    // 设置请求的页面大于最大页后操作， true调回到首页，false 继续请求  默认false
    // paginationInterceptor.setOverflow(false);
    // 设置最大单页限制数量，默认 500 条，-1 不受限制
    // paginationInterceptor.setLimit(500);
    // 开启 count 的 join 优化,只针对部分 left join
    paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize(true));
    return paginationInterceptor;
  }

  // @Bean
  // public JedisCluster jedisCluster() {
  //   // 这里使用 redis-trib.rb check 192.168.99.170:7001 找到 3 个 master 节点，添加进来
  //   Set<HostAndPort> jedisClusterNodes = new HashSet<>();
  //   jedisClusterNodes.add(new HostAndPort("39.96.95.51", 7001));
  //   jedisClusterNodes.add(new HostAndPort("39.96.95.51", 7002));
  //   jedisClusterNodes.add(new HostAndPort("39.96.95.51", 7003));
  //   jedisClusterNodes.add(new HostAndPort("39.96.95.51", 7004));
  //   jedisClusterNodes.add(new HostAndPort("39.96.95.51", 7005));
  //   jedisClusterNodes.add(new HostAndPort("39.96.95.51", 7006));
  //   jedisClusterNodes.add(new HostAndPort("39.96.95.51", 7007));
  //   jedisClusterNodes.add(new HostAndPort("39.96.95.51", 7008));
  //   JedisCluster jedisCluster = new JedisCluster(jedisClusterNodes);
  //   return jedisCluster;
  // }

}
