package com.example.demo2.conf;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import java.time.Duration;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.integration.redis.util.RedisLockRegistry;

/**
 * redis数据源配置
 *
 * @author simon
 * @date 2021/12/25
 */
@Configuration
//@ComponentScan(basePackages = {"com.jravity.jrlive.utils.redis"})
//@ComponentScan(basePackages = {"com.example.demo2.redis"})
public class RedisConfig {

    @Value("${spring.jr-live.timeout:10000}")
    long jrLiveTimeOut;

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


   /* *//**
     * 原欢句直播redis Factory
     * @param hostName
     * @param port
     * @param index
     * @return
     *//*
    @Bean(name = "RedisConnectionFactoryJrLive")
    public LettuceConnectionFactory connectionFactoryJrLive(
            @Value("${spring.jr-live.host}") String hostName,
            @Value("${spring.jr-live.port}") int port,
            @Value("${spring.jr-live.password}") String password,
            @Value("${spring.jr-live.database}") int index) {

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(index);
        redisStandaloneConfiguration.setHostName(hostName);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(jrLiveTimeOut))
                .poolConfig(genericObjectPoolConfig())
                .build();
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
        lettuceConnectionFactory.setShareNativeConnection(false);
        return lettuceConnectionFactory;
    }*/

    @Configuration
    class LettuceCustomizer implements LettuceClientConfigurationBuilderCustomizer {

        @Override
        public void customize(LettuceClientConfiguration.LettuceClientConfigurationBuilder clientConfigurationBuilder) {
            clientConfigurationBuilder.clientOptions(ClientOptions.builder().publishOnScheduler(true).build());
        }
    }


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

        ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                //开启自适应刷新
                //.enableAdaptiveRefreshTrigger(ClusterTopologyRefreshOptions.RefreshTrigger.MOVED_REDIRECT, ClusterTopologyRefreshOptions.RefreshTrigger.PERSISTENT_RECONNECTS)
                //开启所有自适应刷新，MOVED，ASK，PERSISTENT都会触发
                .enableAllAdaptiveRefreshTriggers()
                // 自适应刷新超时时间(默认30秒)
                .adaptiveRefreshTriggersTimeout(Duration.ofSeconds(25)) //默认关闭开启后时间为30秒
                // 开周期刷新
                .enablePeriodicRefresh(Duration.ofSeconds(20))  // 默认关闭开启后时间为60秒 ClusterTopologyRefreshOptions.DEFAULT_REFRESH_PERIOD 60  .enablePeriodicRefresh(Duration.ofSeconds(2)) = .enablePeriodicRefresh().refreshPeriod(Duration.ofSeconds(2))
                .build();


        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(defaultTimeOut))
                .poolConfig(genericObjectPoolConfig())
                .clientOptions(ClusterClientOptions.builder().topologyRefreshOptions(topologyRefreshOptions).publishOnScheduler(true).build())
                .build();
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
        lettuceConnectionFactory.setShareNativeConnection(true);
        //lettuceConnectionFactory.setValidateConnection(true);
        return lettuceConnectionFactory;
    }

    @Bean
    public RedisLockRegistry redisLockRegistry(@Qualifier("RedisConnectionFactory") LettuceConnectionFactory factory){
        return new RedisLockRegistry(factory, "registry-key", 30000L);
    }

    /**
     * 原欢句直播redis
     * @param factory
     * @return
     *//*
    @Bean(name = "redisTemplateJrLive")
    public RedisTemplate<String, Object> redisTemplateJrLive(@Qualifier("RedisConnectionFactoryJrLive")
                                                                         LettuceConnectionFactory factory){
//        factory.setShareNativeConnection(false);
//        factory.setValidateConnection(true);

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
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

    @Bean
    @Primary
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(@Qualifier("RedisConnectionFactory")
                                                                                   LettuceConnectionFactory factory) {
//        factory.setShareNativeConnection(false);
//        factory.setValidateConnection(true);

        RedisSerializer stringRedisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        RedisSerializationContext<String , Object> serializationContext = RedisSerializationContext.newSerializationContext()
                .key(stringRedisSerializer)
                //.value(jackson2JsonRedisSerializer)
                .value(stringRedisSerializer)
                .hashKey(stringRedisSerializer)
                //.hashValue(jackson2JsonRedisSerializer)
                .hashValue(stringRedisSerializer)
                .build();
        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }

    @Bean
    public ReactiveKeyCommands keyCommands(final ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        return reactiveRedisConnectionFactory.getReactiveConnection().keyCommands();
    }

    @Bean
    public ReactiveStringCommands stringCommands(final ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        return reactiveRedisConnectionFactory.getReactiveConnection().stringCommands();
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
}
