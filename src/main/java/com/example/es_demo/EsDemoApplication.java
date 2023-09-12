package com.example.es_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.config.EnableElasticsearchAuditing;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
//@EnableElasticsearchRepositories(basePackages = "com.example.es_demo.repository")
//@EnableElasticsearchAuditing
public class EsDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(EsDemoApplication.class, args);
	}

}
