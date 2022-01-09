package com.example.demo4.conf;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {

    @Bean("jedis.config")
    public JedisPoolConfig jedisPoolConfig(
            @Value("${spring.redis.common.jedis.pool.min-idle}") int minIdle,
            @Value("${spring.redis.common.jedis.pool.max-idle}") int maxIdle,
            @Value("${spring.redis.common.jedis.pool.max-wait}") int maxWaitMillis,
            @Value("${spring.redis.common.jedis.pool.block-when-exhausted:false}") boolean blockWhenExhausted,
            @Value("${spring.redis.common.jedis.pool.max-total:3000}") int maxTotal) {

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMinIdle(minIdle);
        config.setMaxIdle(maxIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        config.setMaxTotal(maxTotal);
        // 连接耗尽时是否阻塞, false 报异常,ture阻塞直到超时, 默认true
        config.setBlockWhenExhausted(blockWhenExhausted);
        // 是否启用pool的jmx管理功能, 默认true
        config.setJmxEnabled(true);
        return config;
    }

    @Bean
    public JedisPool jedisPool(//
                               @Qualifier("jedis.config") JedisPoolConfig config, //
                               @Value("${spring.redis.host}") String host, //
                               @Value("${spring.redis.port}") int port,
                               @Value("${spring.redis.password}") String password,
                               @Value("${spring.redis.database}") int database,
                               @Value("${spring.redis.timeout}") int timeout
    ) {
        return new JedisPool(config, host, port, timeout, StringUtils.isEmpty(password)? null : password , database);
    }
}
