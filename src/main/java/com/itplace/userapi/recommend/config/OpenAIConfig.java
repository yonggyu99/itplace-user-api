package com.itplace.userapi.recommend.config;


import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OpenAIProperties.class)
@RequiredArgsConstructor
public class OpenAIConfig {
    private final OpenAIProperties props;

    @Bean
    public OpenAIClient openAIClient() {
        String host = props.getApi().getUrl();
        if (!host.endsWith("/")) {
            host += "/";
        }
        return OpenAIOkHttpClient.builder()
                .apiKey(props.getApi().getKey())
                .organization("itplace")
                .baseUrl(host)
                .build();
    }
}
