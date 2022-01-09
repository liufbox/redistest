package com.example.demo1.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisTestService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    public String getTestValue(String key){

        return stringRedisTemplate.opsForValue().get(key);
    }
}
