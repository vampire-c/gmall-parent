package com.atguigu.gmall.search.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;


/*
shards: 分片
replicas: 副本数
 */
@Document(indexName = "person-data")
@Data
public class Person {
    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String userName;

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String address;

    @Field(type = FieldType.Date,
            format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date birthDay;

    @Field(type = FieldType.Long, index = false)
    private Integer age;

}
