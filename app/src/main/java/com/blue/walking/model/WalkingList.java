package com.blue.walking.model;

public class WalkingList {

    public int id;
    public int petId;
    public double time;
    public double distance;
    public String createdAt;

    public WalkingList(){

    }

    public WalkingList(int id, double time, double distance) {
        this.id = id;
        this.time = time;
        this.distance = distance;
    }

    public WalkingList(int id, double time, double distance, String createdAt) {
        this.id = id;
        this.time = time;
        this.distance = distance;
        this.createdAt = createdAt;
    }
}
