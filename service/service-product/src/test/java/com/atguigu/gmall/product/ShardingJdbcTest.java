package com.atguigu.gmall.product;

import com.atguigu.gmall.product.biz.CategoryBizService;
import com.atguigu.gmall.product.entity.SpuImage;
import com.atguigu.gmall.product.mapper.SpuImageMapper;
import com.atguigu.gmall.web.CategoryVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@SpringBootTest
public class ShardingJdbcTest {

    @Autowired
    SpuImageMapper spuImageMapper;

    @Test
    public void testSaveSpuImage() {

        SpuImage spuImage = new SpuImage();
        spuImage.setSpuId(0L);
        spuImage.setImgName("bbb");
        spuImage.setImgUrl("bbb");
        spuImageMapper.insert(spuImage);
        System.out.println("插入完成.....");
    }

    @Test
    public void testQuerySpuImage() {
        System.out.println(spuImageMapper.selectById(289L));
        System.out.println(spuImageMapper.selectById(289L));
        System.out.println(spuImageMapper.selectById(289L));
        System.out.println(spuImageMapper.selectById(289L));
        System.out.println(spuImageMapper.selectById(289L));
    }


    @Transactional // 接下来一次查询将主动路由给主库, 不负载均衡给从库
    @Test
    public void testQuerySpuImage2() {
        SpuImage spuImage = new SpuImage();
        spuImage.setSpuId(1L);
        spuImage.setImgName("ccc");
        String s = new Date().toString();
        spuImage.setImgUrl("ccc" + s);
        spuImageMapper.insert(spuImage);

        // HintManager.getInstance().setWriteRouteOnly();

        System.out.println(spuImageMapper.selectById(spuImage.getId()));
    }

    @Autowired
    CategoryBizService categoryBizService;

    @Test
    public void test() {
        List<CategoryVo> tree = categoryBizService.getCategoryTree();
        System.out.println(tree);
    }


}
