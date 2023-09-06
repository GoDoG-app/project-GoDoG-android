package com.blue.walking.model;

import com.skt.tmap.TMapPoint;

public class Park {

    // 공원 이름
    String name ;
    // 공원 주소
    String address ;
    // 공원 좌표
    TMapPoint lat_lng ;

    public Park(){
    }

    public Park(String name, String address, TMapPoint lat_lng) {
        this.name = name;
        this.address = address;
        this.lat_lng = lat_lng;
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

    public TMapPoint getLat_lng() {
        return lat_lng;
    }

    public void setLat_lng(TMapPoint lat_lng) {
        this.lat_lng = lat_lng;
    }
}
