package com.atguigu.gmall.common.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Jsons {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String toString(Object o) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error("对象:{}, json转换异常: {}", o, e);
        }
        return json;
    }
}
