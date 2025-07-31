package com.itplace.userapi.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
@EnableRedisRepositories // Redis 레포지토리 기능 활성화
public class RedisConfig {
    // RedisProperties 객체 생성
    private final RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // 1) Standalone 설정에 호스트/포트, 비밀번호(AUTH) 추가
        RedisStandaloneConfiguration serverConfig =
                new RedisStandaloneConfiguration();
        serverConfig.setHostName(redisProperties.getHost());
        serverConfig.setPort(redisProperties.getPort());

        // 2) Lettuce 클라이언트에 SSL/TLS 활성화
        LettuceClientConfiguration clientConfig =
                LettuceClientConfiguration.builder()
                        .useSsl()
                        .build();

        return new LettuceConnectionFactory(serverConfig, clientConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        // RedisTemplate 인스턴스 생성
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // Redis 연결 팩토리 설정
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        // 키를 문자열로 직렬화하도록 설정
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 값을 JSON으로 직렬화하도록 설정
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 설정이 완료된 RedisTemplate 인스턴스를 반환
        return redisTemplate;
    }
}