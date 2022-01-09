package com.example.demo5.service.impl;



import com.example.demo5.base.bean.Animal;
import com.example.demo5.service.AnimalService;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author l
 * @date Created in 2020/11/4 17:33
 */
@Service
@CacheConfig(cacheNames = "my-redis-cache1", cacheManager = "redisCacheManager")
public class AnimalServiceImpl implements AnimalService {


    @Override
    @Cacheable(key = "#id",sync = true)
    public Animal getAnimal(Integer id) {
        System.out.println("操作数据库，返回Animal");
        return new Animal(id, "cat", "fish");
    }


    /**
     * 使用@CachePut注解的方法，一定要有返回值，该注解声明的方法缓存的是方法的返回结果。
     * it always causes the
     * method to be invoked and its result to be stored in the associated cache
     **/
    @Override
    @CachePut(key = "#animal.getId()")
    public Animal setAnimal(Animal animal) {
        System.out.println("存入数据库");
        return animal;
    }

    @Override
    @CacheEvict(key = "#id")
    public void deleteAnimal(Integer id) {
        System.out.println("删除数据库中animal");
    }

    @Override
    @CachePut(key = "#animal.getId()")
    public Animal updateAnimal(Animal animal) {
        System.out.println("修改animal,并存入数据库");
        return animal;
    }


}

