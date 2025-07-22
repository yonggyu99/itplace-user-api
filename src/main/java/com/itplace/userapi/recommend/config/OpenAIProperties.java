package com.itplace.userapi.recommend.config;

import com.itplace.userapi.recommend.dto.Api;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.ai.openai")
public class OpenAIProperties {
    private String model;
    private Api api;

}
