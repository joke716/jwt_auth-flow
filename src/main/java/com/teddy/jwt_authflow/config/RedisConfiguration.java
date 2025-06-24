package com.teddy.jwt_authflow.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfiguration {
    @Bean
    public RedisConnectionFactory redisConnectionFactory(final RedisProperties redisProperties) {
        final var standaloneConfiguration = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        standaloneConfiguration.setPassword(redisProperties.getPassword());
        return new LettuceConnectionFactory(standaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(final RedisConnectionFactory redisConnectionFactory) {
        final var redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setDefaultSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return redisTemplate;
    }

    @Bean
    @Primary
    public CacheManager listCacheManager(final RedisConnectionFactory redisConnectionFactory) {
        return build(redisConnectionFactory, new Jackson2JsonRedisSerializer<>(Object.class));
    }

    @Bean
    public CacheManager objectCacheManager(final RedisConnectionFactory redisConnectionFactory) {
        return build(redisConnectionFactory, RedisSerializer.json());
    }

    private CacheManager build(final RedisConnectionFactory redisConnectionFactory, final RedisSerializer<?> redisSerializer) {
        final var serializer = SerializationPair.fromSerializer(redisSerializer);
        return RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(
                        RedisCacheConfiguration.defaultCacheConfig()
                                .disableCachingNullValues()
                                .serializeValuesWith(serializer))
                .build();
    }
}
