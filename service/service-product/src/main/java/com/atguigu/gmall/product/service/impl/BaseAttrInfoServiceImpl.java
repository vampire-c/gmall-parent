package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.entity.BaseAttrInfo;
import com.atguigu.gmall.product.entity.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Anonymous
 * @description 针对表【base_attr_info(属性表)】的数据库操作Service实现
 * @createDate 2022-09-26 19:16:22
 */
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo>
        implements BaseAttrInfoService {

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueService baseAttrValueService;


    /**
     * 根据分类id获取属性值列表
     *
     * @param c1id
     * @param c2id
     * @param c3id
     * @return
     */
    @Override
    public List<BaseAttrInfo> getAttrInfoList(Long c1id, Long c2id, Long c3id) {

        List<BaseAttrInfo> baseAttrInfoList = this.baseAttrInfoMapper.getAttrInfoList(c1id, c2id, c3id);

        return baseAttrInfoList;
    }

    /**
     * 保存平台属性
     *
     * @param baseAttrInfo
     */
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        // 保存平台属性
        this.baseAttrInfoMapper.insert(baseAttrInfo);

        // 保存属性值表数据
        Long id = baseAttrInfo.getId();
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue baseAttrValue : attrValueList) {
            baseAttrValue.setAttrId(id);
        }
        this.baseAttrValueService.saveBatch(attrValueList);
    }

    /**
     * 修改平台属性
     *
     * @param baseAttrInfo
     */
    @Override
    public void updateAttrInfo(BaseAttrInfo baseAttrInfo) {
        // 修改平台属性
        this.baseAttrInfoMapper.updateById(baseAttrInfo);

        // 修改属性值表数据
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();

        // 前端修改后的id集合
        List<Long> ids = attrValueList.stream()
                .filter(item -> !StringUtils.isEmpty(item.getId()))
                .map(item -> item.getId())
                .collect(Collectors.toList());

        if (ids.size() > 0) {
            // 删除不在修改后id集合中的属性
            QueryWrapper<BaseAttrValue> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("attr_id", baseAttrInfo.getId());
            queryWrapper.notIn("id", ids);
            this.baseAttrValueService.remove(queryWrapper);
        } else {
            // 全删
            QueryWrapper<BaseAttrValue> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("attr_id", baseAttrInfo.getId());
            this.baseAttrValueService.remove(queryWrapper);
        }

        // 遍历修改后的属性集合, 有id修改, 没id新增
        attrValueList.stream().forEach(item -> {
                    if (StringUtils.isEmpty(item.getId())) {
                        // 新增
                        item.setAttrId(baseAttrInfo.getId());
                        baseAttrValueService.save(item);
                    } else {
                        // 修改
                        baseAttrValueService.updateById(item);
                    }
                }
        );
    }


    // public static void main(String[] args) {
    //     List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
    //     List<Integer> collect = list.stream()
    //             .map(item -> item + 2)
    //             .filter(item -> item % 2 == 0)
    //             .collect(Collectors.toList());
    //
    //     System.out.println(collect);
    // }
}




