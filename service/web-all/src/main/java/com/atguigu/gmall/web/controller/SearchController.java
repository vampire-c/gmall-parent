package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.search.vo.SearchParamVo;
import com.atguigu.gmall.search.vo.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {

    @Autowired
    private SearchFeignClient searchFeignClient;

    @GetMapping("/list.html")
    public String searchListPage(SearchParamVo searchParamVo, Model model) {
        SearchResponseVo searchResponseVo = searchFeignClient.search(searchParamVo).getData();
        model.addAttribute("searchParam", searchResponseVo.getSearchParam());
        model.addAttribute("trademarkParam", searchResponseVo.getTrademarkParam());
        model.addAttribute("propsParamList", searchResponseVo.getPropsParamList());
        model.addAttribute("trademarkList", searchResponseVo.getTrademarkList());
        model.addAttribute("attrsList", searchResponseVo.getAttrsList());
        model.addAttribute("orderMap", searchResponseVo.getOrderMap());
        model.addAttribute("goodsList", searchResponseVo.getGoodsList());
        model.addAttribute("pageNo", searchResponseVo.getPageNo());
        model.addAttribute("totalPages", searchResponseVo.getTotalPages());
        model.addAttribute("urlParam", searchResponseVo.getUrlParam());
        return "list/index";
    }
}
