package com.atguigu.gmall.product.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.entity.BaseAttrInfo;
import com.atguigu.gmall.product.entity.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "平台属性接口")
@RequestMapping("/admin/product")
@RestController
public class BaseAttrController {

    @Autowired
    private BaseAttrInfoService baseAttrInfoService;

    @Autowired
    private BaseAttrValueService baseAttrValueService;

    /**
     * 根据分类id获取平台属性列表
     *
     * @param c1id
     * @param c2id
     * @param c3id
     * @return
     */
    @ApiOperation("根据分类id获取属性值列表")
    @GetMapping("/attrInfoList/{c1id}/{c2id}/{c3id}")
    public Result getAttrInfoList(@PathVariable Long c1id, @PathVariable Long c2id, @PathVariable Long c3id) {
        List<BaseAttrInfo> baseAttrInfoList = this.baseAttrInfoService.getAttrInfoList(c1id, c2id, c3id);
        return Result.ok(baseAttrInfoList);
    }

    /**
     * 保存/修改 平台属性
     *
     * @param baseAttrInfo
     * @return
     */
    @ApiOperation("保存/修改 平台属性")
    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo) {
        if (StringUtils.isEmpty(baseAttrInfo.getId())) {
            // id为空, 添加保存
            this.baseAttrInfoService.saveAttrInfo(baseAttrInfo);
        } else {
            // id不为空, 修改
            this.baseAttrInfoService.updateAttrInfo(baseAttrInfo);

        }
        return Result.ok();
    }

    /**
     * 根据平台属性ID获取平台属性
     *
     * @param attrId
     * @return
     */
    @ApiOperation("根据平台属性ID获取平台属性")
    @GetMapping("/getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable Long attrId) {
        List<BaseAttrValue> attrValueList = this.baseAttrValueService.getAttrValueList(attrId);
        return Result.ok(attrValueList);
    }


}
