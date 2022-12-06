package com.atguigu.gmall.feign.ware;

import com.atguigu.gmall.feign.ware.fallback.WareFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(value = "ware-managcl", url = "http://localhost:9001/",
        // 容错兜底
        fallback = WareFeignClientFallback.class)
public interface WareFeignClient {


    /**
     * 查询是否有库存
     * http://localhost:9001/hasStock?skuId=43&num=2
     *
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("/hasStock")
    public String hasStock(@RequestParam("skuId") Long skuId,
                           @RequestParam("num") Integer num);

}
