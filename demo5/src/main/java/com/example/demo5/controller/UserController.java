package com.example.demo5.controller;



import com.example.demo5.base.bean.Animal;
import com.example.demo5.service.AnimalService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author l
 * @date Created in 2020/10/23 15:46
 */

@RequestMapping("/user")
@Slf4j
@RestController
public class UserController {

    private AnimalService animalService;


    //@Autowired
    public UserController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @RequestMapping("/queryAnimal")
    public String queryAnimal(@RequestParam(value = "ID") Integer ID) {
        Animal animal = animalService.getAnimal(ID);
        log.info("animal " + animal.toString());
        return animal.getName1()+animal.getName2();
    }
}

