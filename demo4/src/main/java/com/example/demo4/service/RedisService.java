package com.example.demo4.service;

public interface RedisService {
    String get(String key);

    boolean set(String key, String val);
}
