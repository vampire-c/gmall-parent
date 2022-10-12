package com.atguigu.gmall.product.biz.impl;

import com.atguigu.gmall.cache.annotation.MallCache;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.product.biz.CategoryBizService;
import com.atguigu.gmall.product.entity.CategoryViewEntity;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.web.CategoryVo;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CategoryBizServiceImpl implements CategoryBizService {

    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;


    private Map<String, Object> cache = new ConcurrentHashMap<>();

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 数据库插叙所有分类, 封装程一个嵌套的树形结构
     *
     * @return
     */
    @MallCache(cacheKey = RedisConst.CATEGORYS_CACHE)
    @Override
    public List<CategoryVo> getCategoryTree() {
        List<CategoryVo> tree = baseCategory1Mapper.getCategoryTree();
        return tree;
    }


    public List<CategoryVo> getCategoryTree1() {
        // 首先查缓存
        String categorys = redisTemplate.opsForValue().get("categorys");
        // 如果缓存中不存在
        if (StringUtils.isEmpty(categorys)) {
            // 回源查找
            List<CategoryVo> tree = baseCategory1Mapper.getCategoryTree();
            // 将回源查找的数据放入缓存
            redisTemplate.opsForValue().set("categorys", Jsons.toString(tree));
            return tree;
        }
        // 缓存中存在, 返回, 将String字符串转封装为返回值类型
        List<CategoryVo> tree = Jsons.toObject(categorys, new TypeReference<List<CategoryVo>>() {
        });
        return tree;
    }


    // 本地缓存
    // public List<CategoryVo> getCategoryTree() {
    //     Object categorys = cache.get("categorys");
    //     if (StringUtils.isEmpty(categorys)) {
    //         List<CategoryVo> tree = baseCategory1Mapper.getCategoryTree();
    //         cache.put("categorys",tree);
    //         return tree;
    //     }
    //     return (List<CategoryVo>) categorys;
    // }


    /**
     * 根据skuInfo的三级分类id查询完整三层路径信息
     *
     * @param c3Id
     * @return
     */
    @Override
    public CategoryViewEntity getCategoryView(Long c3Id) {
        return baseCategory1Mapper.getCategoryView(c3Id);
    }

}
