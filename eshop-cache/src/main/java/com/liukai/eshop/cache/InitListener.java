package com.liukai.eshop.cache;

import com.liukai.eshop.cache.task.CachePrewarmTask;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 容器启动监听器
 */
@Component
public class InitListener implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    ServletContext servletContext = sce.getServletContext();
    // 获取到容器
    WebApplicationContext webApplicationContext = WebApplicationContextUtils
        .getWebApplicationContext(servletContext);

    // 设置容器类
    SpringContextUtil.setWebApplicationContext(webApplicationContext);

    // 启动预处理热点数据线程
    new Thread(new CachePrewarmTask(), "cachePrewarmTaskThread").start();
  }

}
