package com.blue.walking.model;

public class User {

    public String email;
    public String password;
    public String nickname;
    public int gender;
    public String birth;
    public String address;
    public double lat;
    public double lng;


    public User(){

    }

    public User(String email, String password, String nickname, int gender, String birth, String address, double lat, double lng) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.gender = gender;
        this.birth = birth;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }


}
