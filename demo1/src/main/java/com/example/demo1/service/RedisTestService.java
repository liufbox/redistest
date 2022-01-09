package com.example.demo1.service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.util.JSONPObject;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.RedisClientInfo;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class RedisTestService {

    private final StringRedisTemplate stringRedisTemplate;



    private final RedisTemplate<String, Object> redisTemplate;

    public String getTestValue(String key){

        //log.info("##:{}", JSONObject.toJSONString(redisTemplate.getClientList()));

        return  redisTemplate.opsForValue().get(key).toString();
    }
}
