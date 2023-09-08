package com.blue.walking.model;

import java.io.Serializable;

public class RandomFriend implements Serializable {


    public int id;
    public double lat;
    public double lng;
    public String proImgUrl;
    public String nickname;
    public double distance;

    public RandomFriend() {
    }

    public RandomFriend(int id, double lat, double lng, String proImgUrl, String nickname, int distance) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.proImgUrl = proImgUrl;
        this.nickname = nickname;
        this.distance = distance;
    }

}
