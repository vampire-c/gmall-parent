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
     * ?????????????????????es
     *
     * @param goods
     */
    @Override
    public void onSaleGoods(Goods goods) {
        goodsRepository.save(goods);
    }

    /**
     * ????????????,?????????es?????????
     *
     * @param skuId
     */
    @Override
    public void cancelSaleGoods(Long skuId) {
        goodsRepository.deleteById(skuId);
    }

    /**
     * ????????????
     *
     * @param searchParamVo
     * @return
     */
    @Override
    public SearchResponseVo search(SearchParamVo searchParamVo) {
        // ???????????????????????????????????????es????????????
        Query query = buildQuery(searchParamVo);

        // es??????
        SearchHits<Goods> search = elasticsearchRestTemplate.search(query, Goods.class, IndexCoordinates.of("goods"));

        // ??????????????????????????????????????????????????????Vo
        SearchResponseVo searchResponseVo = buildSearchResponseVo(searchParamVo, search);

        return searchResponseVo;
    }

    /**
     * ???????????????
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
     * ???????????????????????????????????????es????????????
     *
     * @param searchParamVo
     * @return
     */
    private Query buildQuery(SearchParamVo searchParamVo) {
        // bool
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // must
        List<QueryBuilder> must = boolQueryBuilder.must();

        // ??????????????????
        if (!StringUtils.isEmpty(searchParamVo.getCategory1Id())) {
            must.add(QueryBuilders.termQuery("category1Id", searchParamVo.getCategory1Id()));
        }
        if (!StringUtils.isEmpty(searchParamVo.getCategory2Id())) {
            must.add(QueryBuilders.termQuery("category2Id", searchParamVo.getCategory2Id()));
        }
        if (!StringUtils.isEmpty(searchParamVo.getCategory3Id())) {
            must.add(QueryBuilders.termQuery("category3Id", searchParamVo.getCategory3Id()));
        }

        // ???????????????????????????
        if (!StringUtils.isEmpty(searchParamVo.getKeyword())) {
            must.add(QueryBuilders.matchQuery("title", searchParamVo.getKeyword()));
        }

        // ??????????????????  3:??????
        if (!StringUtils.isEmpty(searchParamVo.getTrademark())) {
            long tmId = Long.parseLong(searchParamVo.getTrademark().split(":")[0]);
            must.add(QueryBuilders.termQuery("tmId", tmId));
        }

        // ????????????????????????  props=4:256G:????????????&props=1:4500-11999:??????
        if (!StringUtils.isEmpty(searchParamVo.getProps()) && searchParamVo.getProps().length > 0) {
            // ????????????????????????  4:256G:????????????
            for (String prop : searchParamVo.getProps()) {
                //  {4, 256G, ????????????}
                String[] attrs = prop.split(":");
                BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
                // attrId  4
                queryBuilder.must(QueryBuilders.termQuery("attrs.attrId", attrs[0]));
                // attrValue  256G
                queryBuilder.must(QueryBuilders.termQuery("attrs.attrValue", attrs[1]));

                // ??????????????????????????????????????????????????????
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", queryBuilder, ScoreMode.None);
                must.add(nestedQueryBuilder);
            }
        }
        NativeSearchQuery dsl = new NativeSearchQuery(boolQueryBuilder);

        // ???????????? pageNo=1
        PageRequest pageRequest = PageRequest.of(searchParamVo.getPageNo() - 1, searchParamVo.getPageSize());
        dsl.setPageable(pageRequest);

        // ???????????? order=2:asc
        if (!StringUtils.isEmpty(searchParamVo.getOrder())) {
            String[] order = searchParamVo.getOrder().split(":");
            // ????????????
            Sort sort = null;
            switch (order[0]) {
                case "1": // ????????????
                    sort = Sort.by("hotScore");
                    sort = "asc".equals(order[1]) ? sort.ascending() : sort.descending();
                    break;
                case "2": // ????????????
                    sort = Sort.by("price");
                    sort = "asc".equals(order[1]) ? sort.ascending() : sort.descending();
                    break;
            }
            dsl.addSort(sort);
        }

        // ??????????????????
        // ??????id??????
        TermsAggregationBuilder tmIdAgg = AggregationBuilders
                .terms("tmIdAgg")
                .field("tmId").size(200);
        // ????????? ?????????
        TermsAggregationBuilder tmNameAgg = AggregationBuilders
                .terms("tmNameAgg")
                .field("tmName").size(1);
        tmIdAgg.subAggregation(tmNameAgg);
        // ????????? ??????logo
        TermsAggregationBuilder tmLogoAgg = AggregationBuilders
                .terms("tmLogoAgg")
                .field("tmLogoUrl").size(1);
        tmIdAgg.subAggregation(tmLogoAgg);
        // ??????????????????
        dsl.addAggregation(tmIdAgg);

        // ??????????????????
        NestedAggregationBuilder attrAgg = AggregationBuilders
                .nested("attrAgg", "attrs");
        // ??????id??????
        TermsAggregationBuilder attrIdAgg = AggregationBuilders
                .terms("attrIdAgg")
                .field("attrs.attrId").size(200);
        attrAgg.subAggregation(attrIdAgg);
        // ???????????????
        TermsAggregationBuilder attrNameAgg = AggregationBuilders
                .terms("attrNameAgg")
                .field("attrs.attrName").size(1);
        attrIdAgg.subAggregation(attrNameAgg);
        // ???????????????
        TermsAggregationBuilder attrValueAgg = AggregationBuilders
                .terms("attrValueAgg")
                .field("attrs.attrValue").size(200);
        attrIdAgg.subAggregation(attrValueAgg);
        // ??????????????????
        dsl.addAggregation(attrAgg);

        // ????????????????????????
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
     * ??????????????????????????????????????????????????????Vo
     *
     * @param searchParamVo
     * @param search
     * @return
     */
    private SearchResponseVo buildSearchResponseVo(SearchParamVo searchParamVo, SearchHits<Goods> search) {
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        // ????????????,??????
        searchResponseVo.setSearchParam(searchParamVo);
        // ?????????????????????,??????
        if (!StringUtils.isEmpty(searchParamVo.getTrademark())) {
            searchResponseVo.setTrademarkParam("??????" + searchParamVo.getTrademark().split(":")[1]);
        }
        // ?????????????????????,???????????????
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

        // ????????????
        ParsedLongTerms tmIdAgg = search.getAggregations().get("tmIdAgg");
        // for??????
        /*
        List<SearchTmVo> trademarkList = new ArrayList<>();
        for (Terms.Bucket tmIdAggBucket : tmIdAgg.getBuckets()) {
            SearchTmVo searchTmVo = new SearchTmVo();
            // ??????id
            long tmId = tmIdAggBucket.getKeyAsNumber().longValue();
            searchTmVo.setTmId(tmId);
            // ??????name
            ParsedStringTerms tmNameAgg = tmIdAggBucket.getAggregations().get("tmNameAgg");
            String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
            searchTmVo.setTmName(tmName);
            // ??????logo
            ParsedStringTerms tmLogoAgg = tmIdAggBucket.getAggregations().get("tmLogoAgg");
            String tmLogo = tmLogoAgg.getBuckets().get(0).getKeyAsString();
            searchTmVo.setTmLogoUrl(tmLogo);
            // ???????????????
            trademarkList.add(searchTmVo);
        }
        */
        // stream
        List<SearchTmVo> trademarkList = tmIdAgg.getBuckets().stream().map(tmIdAggBucket -> {
            SearchTmVo searchTmVo = new SearchTmVo();
            // ??????id
            searchTmVo.setTmId(tmIdAggBucket.getKeyAsNumber().longValue());
            // ??????name
            searchTmVo.setTmName((
                    (ParsedStringTerms) tmIdAggBucket.getAggregations().get("tmNameAgg"))
                    .getBuckets().get(0).getKeyAsString()
            );
            // ??????logo
            searchTmVo.setTmLogoUrl((
                    (ParsedStringTerms) tmIdAggBucket.getAggregations().get("tmLogoAgg"))
                    .getBuckets().get(0).getKeyAsString()
            );
            return searchTmVo;
        }).collect(Collectors.toList());
        searchResponseVo.setTrademarkList(trademarkList);

        // ????????????
        ParsedLongTerms attrIdAgg = ((ParsedNested) search.getAggregations().get("attrAgg"))
                .getAggregations().get("attrIdAgg");
        List<SearchRespAttr> attrList = attrIdAgg.getBuckets().stream().map(attrIdAggBucket -> {
            SearchRespAttr searchRespAttr = new SearchRespAttr();
            // ??????id
            searchRespAttr.setAttrId(attrIdAggBucket.getKeyAsNumber().longValue());
            // ??????name
            searchRespAttr.setAttrName((
                    (ParsedStringTerms) attrIdAggBucket.getAggregations().get("attrNameAgg"))
                    .getBuckets().get(0).getKeyAsString()
            );
            // ?????????
            searchRespAttr.setAttrValueList(
                    ((ParsedStringTerms) attrIdAggBucket.getAggregations().get("attrValueAgg"))
                            .getBuckets().stream().map(bucket -> bucket.getKeyAsString())
                            .collect(Collectors.toList())
            );
            return searchRespAttr;
        }).collect(Collectors.toList());
        searchResponseVo.setAttrsList(attrList);

        // ????????????
        String order = searchParamVo.getOrder();
        SearchOrderMapVo searchOrderMapVo = new SearchOrderMapVo("1", "desc");
        if (!StringUtils.isEmpty(order) && order.contains(":")) {
            String[] orders = order.split(":");
            searchOrderMapVo.setType(orders[0]);
            searchOrderMapVo.setSort(orders[1]);
        }
        searchResponseVo.setOrderMap(searchOrderMapVo);

        // ????????????
        List<Goods> goodsList = search.getSearchHits().stream()
                .map(goodsSearchHit -> {
                    Goods goods = goodsSearchHit.getContent();
                    if (!StringUtils.isEmpty(searchParamVo.getKeyword())) {
                        goods.setTitle(goodsSearchHit.getHighlightField("title").get(0));
                    }
                    return goods;
                }).collect(Collectors.toList());
        searchResponseVo.setGoodsList(goodsList);

        // ????????????
        searchResponseVo.setPageNo(searchParamVo.getPageNo());

        // ?????????
        long totalHits = search.getTotalHits();
        Long totalPages = totalHits % searchParamVo.getPageSize() == 0 ? totalHits / searchParamVo.getPageSize() : totalHits / searchParamVo.getPageSize() + 1;
        searchResponseVo.setTotalPages(totalPages);

        // urlParam
        searchResponseVo.setUrlParam(makeUrlParam(searchParamVo));

        return searchResponseVo;
    }

    /**
     * ??????????????????,????????????url??????
     *
     * @param searchParamVo
     * @return
     */
    private String makeUrlParam(SearchParamVo searchParamVo) {
        StringBuilder url = new StringBuilder("list.html?");
        // ??????
        if (!StringUtils.isEmpty(searchParamVo.getCategory1Id())) {
            url.append("&category1Id=" + searchParamVo.getCategory1Id());
        }
        if (!StringUtils.isEmpty(searchParamVo.getCategory2Id())) {
            url.append("&category2Id=" + searchParamVo.getCategory2Id());
        }
        if (!StringUtils.isEmpty(searchParamVo.getCategory3Id())) {
            url.append("&category3Id=" + searchParamVo.getCategory3Id());
        }
        // ?????????
        if (!StringUtils.isEmpty(searchParamVo.getKeyword())) {
            url.append("&keyword=" + searchParamVo.getKeyword());
        }
        // ????????????
        if (!StringUtils.isEmpty(searchParamVo.getTrademark())) {
            url.append("&trademark=" + searchParamVo.getTrademark());
        }
        // ????????????
        if (!StringUtils.isEmpty(searchParamVo.getProps()) && searchParamVo.getProps().length > 0) {
            for (String prop : searchParamVo.getProps()) {
                url.append("&props=" + prop);
            }
        }
        // ????????????
        // if (!StringUtils.isEmpty(searchParamVo.getOrder())) {
        //     url.append("&order=" + searchParamVo.getOrder());
        // }
        // url.append("&pageNo=" + searchParamVo.getPageNo());
        // url.append("&pageSize=" + searchParamVo.getPageSize());
        return url.toString();
    }


}
