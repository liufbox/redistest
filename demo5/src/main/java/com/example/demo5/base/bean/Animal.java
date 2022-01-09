package com.example.demo5.base.bean;

import lombok.Data;

@Data
public class Animal {

    Integer id;

    String name1;

    String name2;

    public Animal(){

    }

    public Animal(Integer id, String name1, String name2){
        this.id = id;
        this.name1 = name1;
        this.name2 = name2;
    }
}
