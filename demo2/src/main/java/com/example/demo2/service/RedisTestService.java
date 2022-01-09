package com.example.demo2.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
public class RedisTestService {
    private final ReactiveRedisTemplate reactiveRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    private Integer count = 0;

    Scheduler scheduler;

    public RedisTestService(ReactiveRedisTemplate reactiveRedisTemplate,
                            RedisTemplate<String, Object> redisTemplate){

        this.reactiveRedisTemplate =  reactiveRedisTemplate;
        this.redisTemplate = redisTemplate;

        this.scheduler =  Schedulers.elastic();
    }


    /*public Mono<String> getTestValue(String key){
        String realKey = key + (count++ % 10000);
        Mono<String> secretRedisMono = reactiveRedisTemplate.opsForValue().get(realKey);
        //secretRedisMono.block();



        return secretRedisMono;
    }*/

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

}
