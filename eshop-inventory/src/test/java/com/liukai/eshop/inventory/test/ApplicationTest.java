package com.liukai.eshop.inventory.test;

import com.liukai.eshop.inventory.EshopInventoryApplication;
import org.jasypt.encryption.StringEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EshopInventoryApplication.class)
public class ApplicationTest {

  @Autowired
  StringEncryptor encryptor;

  /**
   * 生成加密之后的数据库信息
   */
  @Test
  public void getPass() {

    // String url = encryptor.encrypt("你的数据库链接");
    // String name = encryptor.encrypt("你的数据库名");
    // String password = encryptor.encrypt("你的数据库密码");
    // System.out.println(url + "----------------");
    // System.out.println(name + "----------------");
    // Assert.assertTrue(name.length() > 0);
    // Assert.assertTrue(password.length() > 0);
  }

}
