package com.example.demo1.conf;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.support.ConnectionPoolSupport;
import java.time.Duration;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.integration.redis.util.RedisLockRegistry;

@Configuration
//@ComponentScan(basePackages = {"com.jravity.jrlive.utils.redis"})
public class RedisConfig {

    @Value("${spring.redis.timeout}")
    long defaultTimeOut;

    @Value("${spring.redis.common.lettuce.pool.max-idle}")
    int maxIdle;

    @Value("${spring.redis.common.lettuce.pool.min-idle}")
    int minIdle;

    @Value("${spring.redis.common.lettuce.pool.max-active}")
    int maxTotal;

    @Value("${spring.redis.common.lettuce.pool.max-wait}")
    long maxWait;


    /**
     * 龙直播redis Factory（默认）
     * @param hostName
     * @param port
     * @param password
     * @param index
     * @return
     */
    @Bean(name = "RedisConnectionFactory")
    @Primary
    public LettuceConnectionFactory connectionFactory(
            @Value("${spring.redis.host}") String hostName,
            @Value("${spring.redis.port}") int port,
            @Value("${spring.redis.password}") String password,
            @Value("${spring.redis.database}") int index) {

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(index);
        redisStandaloneConfiguration.setHostName(hostName);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setPassword(RedisPassword.of(password));


       /* ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                //开启自适应刷新
                //.enableAdaptiveRefreshTrigger(ClusterTopologyRefreshOptions.RefreshTrigger.MOVED_REDIRECT, ClusterTopologyRefreshOptions.RefreshTrigger.PERSISTENT_RECONNECTS)
                //开启所有自适应刷新，MOVED，ASK，PERSISTENT都会触发
                .enableAllAdaptiveRefreshTriggers()
                // 自适应刷新超时时间(默认30秒)
                .adaptiveRefreshTriggersTimeout(Duration.ofSeconds(25)) //默认关闭开启后时间为30秒
                // 开周期刷新
                .enablePeriodicRefresh(Duration.ofSeconds(20))  // 默认关闭开启后时间为60秒 ClusterTopologyRefreshOptions.DEFAULT_REFRESH_PERIOD 60  .enablePeriodicRefresh(Duration.ofSeconds(2)) = .enablePeriodicRefresh().refreshPeriod(Duration.ofSeconds(2))
                .build();*/


        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(defaultTimeOut))
                .poolConfig(genericObjectPoolConfig())
                //.clientOptions(ClusterClientOptions.builder().topologyRefreshOptions(topologyRefreshOptions).publishOnScheduler(true).build())
                .build();
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
        lettuceConnectionFactory.setShareNativeConnection(true);
        return lettuceConnectionFactory;
    }

    @Bean
    public RedisLockRegistry redisLockRegistry(@Qualifier("RedisConnectionFactory") LettuceConnectionFactory factory){
        return new RedisLockRegistry(factory, "registry-key", 30000L);
    }



    @Bean
    public GenericObjectPoolConfig genericObjectPoolConfig() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(maxIdle);
        genericObjectPoolConfig.setMinIdle(minIdle);
        genericObjectPoolConfig.setMaxTotal(maxTotal);
        genericObjectPoolConfig.setMaxWaitMillis(maxWait);
        genericObjectPoolConfig.setTimeBetweenEvictionRunsMillis(100);

        return genericObjectPoolConfig;
    }


    /**
     * 龙直播redis
     * @param factory
     * @return
     */
    @Bean(name = "redisTemplate")
    @Primary
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("RedisConnectionFactory")
                                                               LettuceConnectionFactory factory){
//        factory.setShareNativeConnection(false);
//        factory.setValidateConnection(true);

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);


        /* 使用Jackson2JsonRedisSerialize 替换默认序列化(默认采用的是JDK序列化)
         *  key采用StringRedisSerializer， value采用Jackson2JsonRedisSerializer
         *  */
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        redisTemplate.setKeySerializer(stringRedisSerializer);//key序列化
        redisTemplate.setValueSerializer(stringRedisSerializer);  //value序列化
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(stringRedisSerializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }



    /**
     * @Description: 防止redis入库序列化乱码的问题
     * @return     返回类型
     * @date 2018/4/12 10:54
     */
    /*@Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        *//* 使用Jackson2JsonRedisSerialize 替换默认序列化(默认采用的是JDK序列化)
         *  key采用StringRedisSerializer， value采用Jackson2JsonRedisSerializer
         *  *//*
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        redisTemplate.setKeySerializer(stringRedisSerializer);//key序列化
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);  //value序列化
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }*/

    @Bean
    public RedisKeyCommands keyCommands(final RedisConnectionFactory redisConnectionFactory) {
        return redisConnectionFactory.getConnection().keyCommands();
    }

    @Bean
    public RedisStringCommands stringCommands(final RedisConnectionFactory redisConnectionFactory) {
        return redisConnectionFactory.getConnection().stringCommands();
    }
}
