package com.telecom.plans.config;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import java.time.Duration;

/**
 * The cache PROVIDER is chosen per environment by spring.cache.type
 * (caffeine in dev/qa, redis in staging/prod). Redis-only tuning lives here
 * and is activated only when Redis is the provider.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
    RedisCacheManagerBuilderCustomizer redisCacheCustomizer() {
        return builder -> builder
                .withCacheConfiguration("plans",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(5))
                                .prefixCacheNameWith("telecom-plans:"))
                .withCacheConfiguration("planByCode",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(30))
                                .prefixCacheNameWith("telecom-plans:"));
    }
}
