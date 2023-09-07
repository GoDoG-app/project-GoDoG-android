package com.blue.walking.model;

public class RandomFriend {

//                "id": 7,
//                        "lat": 37.5492548,
//                        "lng": 126.6773376,
//                        "proImgUrl": "https://project4-walking-app.s3.amazonaws.com/2023-09-06T08_16_47_807696_7.jpg",
//                        "nickname": "hora",
//                        "distance": 1.4850563893424542

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
