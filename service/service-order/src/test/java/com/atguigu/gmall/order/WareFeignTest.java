package com.atguigu.gmall.order;

import com.atguigu.gmall.feign.ware.WareFeignClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WareFeignTest {
    @Autowired
    private WareFeignClient wareFeignClient;

    @Test
    public void testHasStock() {
        String hasStock0 = wareFeignClient.hasStock(42L, 9990);
        String hasStock1 = wareFeignClient.hasStock(42L, 9991);
        String hasStock2 = wareFeignClient.hasStock(42L, 9992);
        System.out.println(hasStock0);
        System.out.println(hasStock1);
        System.out.println(hasStock2);

    }
}
