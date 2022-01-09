package com.example.demo4.controller;


import com.example.demo4.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


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
    public String demoTest1() {

        return "ok";
    }

    /**
     * 从redis获取固定值
     * @return
     */
    @RequestMapping ("/demoTest2")
    public String demoTest2(@RequestParam(name = "key") String key) {

        return redisService.get(key);
    }





}
