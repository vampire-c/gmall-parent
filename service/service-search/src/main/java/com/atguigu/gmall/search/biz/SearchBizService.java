package com.atguigu.gmall.search.biz;

import com.atguigu.gmall.search.entity.Goods;
import com.atguigu.gmall.search.vo.SearchParamVo;
import com.atguigu.gmall.search.vo.SearchResponseVo;

public interface SearchBizService {
    /**
     * 商品上架,数据保存到es
     *
     * @param goods
     */
    void onSaleGoods(Goods goods);

    /**
     * 商品下架,数据从es中删除
     *
     * @param skuId
     */
    void cancelSaleGoods(Long skuId);

    /**
     * 商品检索
     * @param searchParamVo
     * @return
     */
    SearchResponseVo search(SearchParamVo searchParamVo);

    /**
     * 更新呢热度分
     * @param skuId
     * @param hotScore
     */
    void updateHotScore(Long skuId, Long hotScore);
}
