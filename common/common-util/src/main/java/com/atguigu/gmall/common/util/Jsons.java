package com.atguigu.gmall.common.util;


import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Jsons {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String toString(Object o) {
        String str = null;
        try {
            str = objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error("{} 转String字符串异常: {}", o, e);
        }
        return str;
    }


    public static <T> T toObject(String str, TypeReference<T> typeReference) {
        if (RedisConst.TEMP_DATA.equals(str)) {
            return null;
        }
        T t = null;
        try {
            t = objectMapper.readValue(str, typeReference);
        } catch (JsonProcessingException e) {
            log.error("{} 转对象异常: {}", str, e);
        }
        return t;
    }

    public static <T> T toObject(String str, Class<T> clazz) {
        if (RedisConst.TEMP_DATA.equals(str)) {
            throw new GmallException(ResultCodeEnum.DATA_NOT_EXIST);
        }
        T t = null;
        try {
            t = objectMapper.readValue(str, clazz);
        } catch (JsonProcessingException e) {
            log.error("{} 转对象异常: {}", str, e);
        }
        return t;
    }
}
