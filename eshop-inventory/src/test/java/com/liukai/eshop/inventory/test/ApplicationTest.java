package com.liukai.eshop.inventory.test;

import com.liukai.eshop.inventory.InventoryApplication;
import org.jasypt.encryption.StringEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = InventoryApplication.class)
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
  
  @Test
  public void testDec() {
    String url
      = "xnRSJ7Zx4n4rqFx4yZGnOz738Cl63YwpqWuIAtgbYlapj6GbbjuSKHVEVcGNqgac/bmIBwI4Uycg7NQbAb0IcJT6WRXCz4uT7/Z4KnVl4WAVv+t1wFZzCYAVMeUnWv4UAByixYAIdGLBrqaUe6rWV04QQ4Q0oi8rvlIWt/pdCoNw46iB/5F/bDk4EOCXEN/j";
    url = encryptor.decrypt(url);
    System.out.println("url = " + url);
    
    String username = "6IBWzB+q+qamnZey0dQRS1StZ3qIKV+1/f8k9m4dSeSv3woXRTMlAnEGxFIaR5zz";
    username = encryptor.decrypt(username);
    System.out.println("username = " + username);
    
    String password = "Oe20yK7y64jgEn9Ms+vXTW+rZCuk6DK1SuVJfHcMXGqm5z1n5VqI3A0+jhFLruV5";
    password = encryptor.decrypt(password);
    System.out.println("username = " + password);
  }
  
}
