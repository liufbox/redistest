package com.example.demo5.service;

import com.example.demo5.base.bean.Animal;

public interface AnimalService {

    Animal getAnimal(Integer id);

    Animal setAnimal(Animal animal);

    void deleteAnimal(Integer id);

    Animal updateAnimal(Animal animal);
}
