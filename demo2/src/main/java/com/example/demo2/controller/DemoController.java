package com.example.demo2.controller;

import com.example.demo2.service.RedisTestService;
import java.security.Key;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/demo")
@Slf4j
public class DemoController {

    private final RedisTestService redisTestService;

    public DemoController(RedisTestService redisTestService){
        this.redisTestService = redisTestService;
    }

    @RequestMapping ("/demoTest1")
    public Mono<String> demoTest1() {

        return Mono.just("ok2");
    }

    /**
     * 普通消费方式
     * @param key
     * @return
     */
    @RequestMapping ("/demoTest2")
    public Mono<String> demoTest2(@RequestParam(name = "key") String key) {

        return redisTestService.getTestValueV0(key);
    }

    /**
     * publishOn指定消费Scheduler
     * @param key
     * @return
     */
    @RequestMapping ("/demoTest3")
    public Mono<String> demoTest3(@RequestParam(name = "key") String key) {
        //log.info("key:{}", key);
        return redisTestService.getTestValueV1(key);
    }


    @RequestMapping ("/demoTest4")
    public Mono<String> demoTest4(@RequestParam(name = "key") String key) {
        //log.info("key:{}", key);
        return redisTestService.getTestValueV2(key);
    }



}
