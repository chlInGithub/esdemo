package com.example.es_demo.config;

import java.util.Arrays;

import com.example.es_demo.config.conversion.MapToPerson;
import com.example.es_demo.config.conversion.PersonToMap;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;

//@Configuration
public class ESConfig extends AbstractElasticsearchConfiguration {

    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration build = ClientConfiguration.builder().connectedTo("localhost:9200").build();
        RestHighLevelClient rest = RestClients.create(build).rest();
        return rest;
    }

    /**
     *添加自定义的域对象转换器
     */
    @Override
    public ElasticsearchCustomConversions elasticsearchCustomConversions() {
        ElasticsearchCustomConversions conversions
                = new ElasticsearchCustomConversions(Arrays.asList(new MapToPerson(), new PersonToMap()));
        return conversions;
    }
}
