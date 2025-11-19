package com.ridehub.user.config;

import ai.z.openapi.ZaiClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(ZaiChatbotProperties.class)
public class ZaiChatbotConfiguration {

    @Bean
    public ZaiClient zaiClient(ZaiChatbotProperties properties) {
        if (StringUtils.hasText(properties.getApiKey())) {
            return ZaiClient.builder()
                    .apiKey(properties.getApiKey())
                    .baseUrl(properties.getBaseUrl())
                    .build();
        } else {
            return ZaiClient.builder()
                    .baseUrl(properties.getBaseUrl())
                    .build();
        }
    }
}