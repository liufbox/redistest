package com.example.demo1.controller;

import com.example.demo1.service.RedisTestService;
import java.security.Key;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private RedisTestService redisTestService;

    @RequestMapping ("/demoTest1")
    public String demoTest1(){
        return "ok";
    }

    /**
     * 从redis返回固定值
      * @param key
     * @return
     */
    @RequestMapping ("/demoTest2")
    public String demoTest2(@RequestParam(name = "key") String key){
        return redisTestService.getTestValue(key);
    }
}
