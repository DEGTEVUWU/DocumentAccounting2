package com.ivan_degtev.documentaccounting2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchConfiguration;

@Configuration
public class ELKConfig {

//    @Override
//    public ClientConfiguration clientConfiguration() {
//        return ClientConfiguration.builder()
//                .connectedTo("localhost:9200")
//                .build();
//    }
}
