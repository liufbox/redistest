package com.example.demo3.controller;

import com.example.demo3.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/demo")
@Slf4j
public class DemoController {

    private final RedisService redisService;

    public DemoController(RedisService redisService){
        this.redisService = redisService;
    }

    /**
     * 返回内存中值，可以做为优化的天花板
     * @return
     */
    @RequestMapping ("/demoTest1")
    public Mono<String> demoTest1() {

        return Mono.just("ok2");
    }

    /**
     * 从redis获取固定值
     * @return
     */
    @RequestMapping ("/demoTest2")
    public Mono<String> demoTest2(@RequestParam(name = "key") String key) {

        return Mono.just(redisService.get(key));
    }





}
