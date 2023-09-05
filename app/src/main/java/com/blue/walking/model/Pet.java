package com.blue.walking.model;

import java.io.File;
import java.io.Serializable;

public class Pet implements Serializable {

    public int id;
    public int userId;
    public String petName;
    public String petAge;
    public String petGender;
    public String petProUrl;
    public String oneliner;
    public String createdAt;
    public String updatedAt;

    public Pet(){}

    public Pet(int id, int userId, String petName,
               String petAge, String petGender, String petProUrl,
               String oneliner, String createdAt, String updatedAt) {
        this.id = id;
        this.userId = userId;
        this.petName = petName;
        this.petAge = petAge;
        this.petGender = petGender;
        this.petProUrl = petProUrl;
        this.oneliner = oneliner;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
