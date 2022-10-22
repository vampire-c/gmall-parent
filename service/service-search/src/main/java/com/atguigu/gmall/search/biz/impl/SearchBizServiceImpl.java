package com.atguigu.gmall.search.biz.impl;

import com.atguigu.gmall.search.biz.SearchBizService;
import com.atguigu.gmall.search.entity.Goods;
import com.atguigu.gmall.search.entity.SearchAttr;
import com.atguigu.gmall.search.respository.GoodsRepository;
import com.atguigu.gmall.search.vo.SearchOrderMapVo;
import com.atguigu.gmall.search.vo.SearchParamVo;
import com.atguigu.gmall.search.vo.SearchRespAttr;
import com.atguigu.gmall.search.vo.SearchResponseVo;
import com.atguigu.gmall.search.vo.SearchTmVo;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchBizServiceImpl implements SearchBizService {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 商品上架保存到es
     *
     * @param goods
     */
    @Override
    public void onSaleGoods(Goods goods) {
        goodsRepository.save(goods);
    }

    /**
     * 商品下架,数据从es中删除
     *
     * @param skuId
     */
    @Override
    public void cancelSaleGoods(Long skuId) {
        goodsRepository.deleteById(skuId);
    }

    /**
     * 商品检索
     *
     * @param searchParamVo
     * @return
     */
    @Override
    public SearchResponseVo search(SearchParamVo searchParamVo) {
        // 根据前端发送的数据构建一个es检索条件
        Query query = buildQuery(searchParamVo);

        // es检索
        SearchHits<Goods> search = elasticsearchRestTemplate.search(query, Goods.class, IndexCoordinates.of("goods"));

        // 根据检索结果封装为前端可用的相应结果Vo
        SearchResponseVo searchResponseVo = buildSearchResponseVo(searchParamVo, search);

        return searchResponseVo;
    }

    /**
     * 更新热度分
     *
     * @param skuId
     * @param hotScore
     */
    @Override
    public void updateHotScore(Long skuId, Long hotScore) {
        Goods goods = goodsRepository.findById(skuId).get();
        goods.setHotScore(hotScore);
        goodsRepository.save(goods);
    }


    /**
     * 根据前端发送的数据构建一个es检索条件
     *
     * @param searchParamVo
     * @return
     */
    private Query buildQuery(SearchParamVo searchParamVo) {
        // bool
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // must
        List<QueryBuilder> must = boolQueryBuilder.must();

        // 按照分类查询
        if (!StringUtils.isEmpty(searchParamVo.getCategory1Id())) {
            must.add(QueryBuilders.termQuery("category1Id", searchParamVo.getCategory1Id()));
        }
        if (!StringUtils.isEmpty(searchParamVo.getCategory2Id())) {
            must.add(QueryBuilders.termQuery("category2Id", searchParamVo.getCategory2Id()));
        }
        if (!StringUtils.isEmpty(searchParamVo.getCategory3Id())) {
            must.add(QueryBuilders.termQuery("category3Id", searchParamVo.getCategory3Id()));
        }

        // 按照关键字模糊查询
        if (!StringUtils.isEmpty(searchParamVo.getKeyword())) {
            must.add(QueryBuilders.matchQuery("title", searchParamVo.getKeyword()));
        }

        // 按照品牌检索  3:华为
        if (!StringUtils.isEmpty(searchParamVo.getTrademark())) {
            long tmId = Long.parseLong(searchParamVo.getTrademark().split(":")[0]);
            must.add(QueryBuilders.termQuery("tmId", tmId));
        }

        // 按照平台属性检索  props=4:256G:机身储存&props=1:4500-11999:价格
        if (!StringUtils.isEmpty(searchParamVo.getProps()) && searchParamVo.getProps().length > 0) {
            // 遍历属性检索条件  4:256G:机身储存
            for (String prop : searchParamVo.getProps()) {
                //  {4, 256G, 机身储存}
                String[] attrs = prop.split(":");
                BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
                // attrId  4
                queryBuilder.must(QueryBuilders.termQuery("attrs.attrId", attrs[0]));
                // attrValue  256G
                queryBuilder.must(QueryBuilders.termQuery("attrs.attrValue", attrs[1]));

                // 每个平台属性都要构造一个嵌入式的查询
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", queryBuilder, ScoreMode.None);
                must.add(nestedQueryBuilder);
            }
        }
        NativeSearchQuery dsl = new NativeSearchQuery(boolQueryBuilder);

        // 分页信息 pageNo=1
        PageRequest pageRequest = PageRequest.of(searchParamVo.getPageNo() - 1, searchParamVo.getPageSize());
        dsl.setPageable(pageRequest);

        // 排序信息 order=2:asc
        if (!StringUtils.isEmpty(searchParamVo.getOrder())) {
            String[] order = searchParamVo.getOrder().split(":");
            // 排序条件
            Sort sort = null;
            switch (order[0]) {
                case "1": // 热度排序
                    sort = Sort.by("hotScore");
                    sort = "asc".equals(order[1]) ? sort.ascending() : sort.descending();
                    break;
                case "2": // 价格排序
                    sort = Sort.by("price");
                    sort = "asc".equals(order[1]) ? sort.ascending() : sort.descending();
                    break;
            }
            dsl.addSort(sort);
        }

        // 品牌聚合分析
        // 品牌id聚合
        TermsAggregationBuilder tmIdAgg = AggregationBuilders
                .terms("tmIdAgg")
                .field("tmId").size(200);
        // 子聚合 品牌名
        TermsAggregationBuilder tmNameAgg = AggregationBuilders
                .terms("tmNameAgg")
                .field("tmName").size(1);
        tmIdAgg.subAggregation(tmNameAgg);
        // 子聚合 品牌logo
        TermsAggregationBuilder tmLogoAgg = AggregationBuilders
                .terms("tmLogoAgg")
                .field("tmLogoUrl").size(1);
        tmIdAgg.subAggregation(tmLogoAgg);
        // 品牌聚合完成
        dsl.addAggregation(tmIdAgg);

        // 属性集合分析
        NestedAggregationBuilder attrAgg = AggregationBuilders
                .nested("attrAgg", "attrs");
        // 属性id聚合
        TermsAggregationBuilder attrIdAgg = AggregationBuilders
                .terms("attrIdAgg")
                .field("attrs.attrId").size(200);
        attrAgg.subAggregation(attrIdAgg);
        // 属性名聚合
        TermsAggregationBuilder attrNameAgg = AggregationBuilders
                .terms("attrNameAgg")
                .field("attrs.attrName").size(1);
        attrIdAgg.subAggregation(attrNameAgg);
        // 属性值聚合
        TermsAggregationBuilder attrValueAgg = AggregationBuilders
                .terms("attrValueAgg")
                .field("attrs.attrValue").size(200);
        attrIdAgg.subAggregation(attrValueAgg);
        // 属性聚合完成
        dsl.addAggregation(attrAgg);

        // 模糊匹配设置高亮
        if (!StringUtils.isEmpty(searchParamVo.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("title")
                    .preTags("<span style = 'color:red'>")
                    .postTags("</span>");
            HighlightQuery highlightQuery = new HighlightQuery(highlightBuilder);
            dsl.setHighlightQuery(highlightQuery);
        }

        return dsl;
    }


    /**
     * 根据检索结果封装为前端可用的相应结果Vo
     *
     * @param searchParamVo
     * @param search
     * @return
     */
    private SearchResponseVo buildSearchResponseVo(SearchParamVo searchParamVo, SearchHits<Goods> search) {
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        // 检索条件,返回
        searchResponseVo.setSearchParam(searchParamVo);
        // 如果选择了品牌,返回
        if (!StringUtils.isEmpty(searchParamVo.getTrademark())) {
            searchResponseVo.setTrademarkParam("品牌" + searchParamVo.getTrademark().split(":")[1]);
        }
        // 如果选择了属性,属性面包屑
        if (!StringUtils.isEmpty(searchParamVo.getProps()) && searchParamVo.getProps().length > 0) {
            List<SearchAttr> searchAttrList = Arrays.stream(searchParamVo.getProps()).map(s -> {
                SearchAttr searchAttr = new SearchAttr();
                String[] split = s.split(":");
                searchAttr.setAttrId(Long.parseLong(split[0]));
                searchAttr.setAttrValue(split[1]);
                searchAttr.setAttrName(split[2]);
                return searchAttr;
            }).collect(Collectors.toList());
            searchResponseVo.setPropsParamList(searchAttrList);
        }

        // 品牌列表
        ParsedLongTerms tmIdAgg = search.getAggregations().get("tmIdAgg");
        // for循环
        /*
        List<SearchTmVo> trademarkList = new ArrayList<>();
        for (Terms.Bucket tmIdAggBucket : tmIdAgg.getBuckets()) {
            SearchTmVo searchTmVo = new SearchTmVo();
            // 品牌id
            long tmId = tmIdAggBucket.getKeyAsNumber().longValue();
            searchTmVo.setTmId(tmId);
            // 品牌name
            ParsedStringTerms tmNameAgg = tmIdAggBucket.getAggregations().get("tmNameAgg");
            String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
            searchTmVo.setTmName(tmName);
            // 品牌logo
            ParsedStringTerms tmLogoAgg = tmIdAggBucket.getAggregations().get("tmLogoAgg");
            String tmLogo = tmLogoAgg.getBuckets().get(0).getKeyAsString();
            searchTmVo.setTmLogoUrl(tmLogo);
            // 添加到集合
            trademarkList.add(searchTmVo);
        }
        */
        // stream
        List<SearchTmVo> trademarkList = tmIdAgg.getBuckets().stream().map(tmIdAggBucket -> {
            SearchTmVo searchTmVo = new SearchTmVo();
            // 品牌id
            searchTmVo.setTmId(tmIdAggBucket.getKeyAsNumber().longValue());
            // 品牌name
            searchTmVo.setTmName((
                    (ParsedStringTerms) tmIdAggBucket.getAggregations().get("tmNameAgg"))
                    .getBuckets().get(0).getKeyAsString()
            );
            // 品牌logo
            searchTmVo.setTmLogoUrl((
                    (ParsedStringTerms) tmIdAggBucket.getAggregations().get("tmLogoAgg"))
                    .getBuckets().get(0).getKeyAsString()
            );
            return searchTmVo;
        }).collect(Collectors.toList());
        searchResponseVo.setTrademarkList(trademarkList);

        // 属性列表
        ParsedLongTerms attrIdAgg = ((ParsedNested) search.getAggregations().get("attrAgg"))
                .getAggregations().get("attrIdAgg");
        List<SearchRespAttr> attrList = attrIdAgg.getBuckets().stream().map(attrIdAggBucket -> {
            SearchRespAttr searchRespAttr = new SearchRespAttr();
            // 属性id
            searchRespAttr.setAttrId(attrIdAggBucket.getKeyAsNumber().longValue());
            // 属性name
            searchRespAttr.setAttrName((
                    (ParsedStringTerms) attrIdAggBucket.getAggregations().get("attrNameAgg"))
                    .getBuckets().get(0).getKeyAsString()
            );
            // 属性值
            searchRespAttr.setAttrValueList(
                    ((ParsedStringTerms) attrIdAggBucket.getAggregations().get("attrValueAgg"))
                            .getBuckets().stream().map(bucket -> bucket.getKeyAsString())
                            .collect(Collectors.toList())
            );
            return searchRespAttr;
        }).collect(Collectors.toList());
        searchResponseVo.setAttrsList(attrList);

        // 排序信息
        String order = searchParamVo.getOrder();
        SearchOrderMapVo searchOrderMapVo = new SearchOrderMapVo("1", "desc");
        if (!StringUtils.isEmpty(order) && order.contains(":")) {
            String[] orders = order.split(":");
            searchOrderMapVo.setType(orders[0]);
            searchOrderMapVo.setSort(orders[1]);
        }
        searchResponseVo.setOrderMap(searchOrderMapVo);

        // 商品列表
        List<Goods> goodsList = search.getSearchHits().stream()
                .map(goodsSearchHit -> {
                    Goods goods = goodsSearchHit.getContent();
                    if (!StringUtils.isEmpty(searchParamVo.getKeyword())) {
                        goods.setTitle(goodsSearchHit.getHighlightField("title").get(0));
                    }
                    return goods;
                }).collect(Collectors.toList());
        searchResponseVo.setGoodsList(goodsList);

        // 当前页码
        searchResponseVo.setPageNo(searchParamVo.getPageNo());

        // 总页码
        long totalHits = search.getTotalHits();
        Long totalPages = totalHits % searchParamVo.getPageSize() == 0 ? totalHits / searchParamVo.getPageSize() : totalHits / searchParamVo.getPageSize() + 1;
        searchResponseVo.setTotalPages(totalPages);

        // urlParam
        searchResponseVo.setUrlParam(makeUrlParam(searchParamVo));

        return searchResponseVo;
    }

    /**
     * 拼接请求参数,生成完整url地址
     *
     * @param searchParamVo
     * @return
     */
    private String makeUrlParam(SearchParamVo searchParamVo) {
        StringBuilder url = new StringBuilder("list.html?");
        // 分类
        if (!StringUtils.isEmpty(searchParamVo.getCategory1Id())) {
            url.append("&category1Id=" + searchParamVo.getCategory1Id());
        }
        if (!StringUtils.isEmpty(searchParamVo.getCategory2Id())) {
            url.append("&category2Id=" + searchParamVo.getCategory2Id());
        }
        if (!StringUtils.isEmpty(searchParamVo.getCategory3Id())) {
            url.append("&category3Id=" + searchParamVo.getCategory3Id());
        }
        // 关键字
        if (!StringUtils.isEmpty(searchParamVo.getKeyword())) {
            url.append("&keyword=" + searchParamVo.getKeyword());
        }
        // 品牌条件
        if (!StringUtils.isEmpty(searchParamVo.getTrademark())) {
            url.append("&trademark=" + searchParamVo.getTrademark());
        }
        // 属性条件
        if (!StringUtils.isEmpty(searchParamVo.getProps()) && searchParamVo.getProps().length > 0) {
            for (String prop : searchParamVo.getProps()) {
                url.append("&props=" + prop);
            }
        }
        // 排序条件
        // if (!StringUtils.isEmpty(searchParamVo.getOrder())) {
        //     url.append("&order=" + searchParamVo.getOrder());
        // }
        // url.append("&pageNo=" + searchParamVo.getPageNo());
        // url.append("&pageSize=" + searchParamVo.getPageSize());
        return url.toString();
    }


}
