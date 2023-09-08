package com.blue.walking.model;

import com.skt.tmap.TMapPoint;

import java.io.Serializable;

public class Park implements Serializable {

    // 공원 이름
    String name ;
    // 공원 주소
    String address ;
    // 공원 좌표
    double latitude;
    double longitude;

    public Park(){

    }

    public Park(String name, String address, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
