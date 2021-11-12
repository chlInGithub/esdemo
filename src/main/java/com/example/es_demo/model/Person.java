package com.example.es_demo.model;

import java.time.Instant;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 使用@Document，表示持久化到es中的doc。默认，启动时，自动创建索引。
 */
@Data
@Document(indexName = "es_person")
public class Person {

    @Id
    @Field(name = "id")
    String id;

    /**
     * 通过Field配置索引分析器和搜索分析器
     */
    @Field(name = "name", type = FieldType.Text, analyzer = "", searchAnalyzer = "")
    String name;

    @Field(name = "age", type = FieldType.Integer)
    Integer age;

    @CreatedBy
    String createdBy;

    @LastModifiedBy
    String lastModifiedBy;

    @CreatedDate
    Instant createdDate;

    @LastModifiedDate
    Instant lastModifiedDate;
}
