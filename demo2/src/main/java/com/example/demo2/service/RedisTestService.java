package com.example.demo2.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service

public class RedisTestService {
    private final ReactiveRedisTemplate reactiveRedisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    private final RedisTemplate<String, Object> redisTemplate;

    private Integer count = 0;

    Scheduler scheduler;

    public RedisTestService(ReactiveRedisTemplate reactiveRedisTemplate,
                            StringRedisTemplate stringRedisTemplate,
                            RedisTemplate<String, Object> redisTemplate){

        this.reactiveRedisTemplate =  reactiveRedisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisTemplate = redisTemplate;

        this.scheduler =  Schedulers.elastic();
    }

    /**
     * 普通消费
     * @param key
     * @return
     */
    public Mono<String> getTestValueV0(String key){
        Mono<String> secretRedisMono = reactiveRedisTemplate.opsForValue().get(key);
        //secretRedisMono.block();

        return secretRedisMono;
    }


    /**
     * publishOn指定消费Scheduler
     * @param key
     * @return
     */
    public Mono<String> getTestValueV1(String key){
        Mono<String> secretRedisMono = reactiveRedisTemplate.opsForValue().get(key);
        return secretRedisMono.publishOn(Schedulers.elastic());
    }


    public Mono<String> getTestValueV2(String key){
        Mono<String> secretRedisMono = reactiveRedisTemplate.opsForValue().get(key);
        return secretRedisMono.publishOn(scheduler);
    }

    public Mono<String> getTestValueV3(String key){
        String secretRedisMono = stringRedisTemplate.opsForValue().get(key);
        return Mono.just(secretRedisMono);
    }

}
