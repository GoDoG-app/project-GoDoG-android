package com.blue.walking.model;

import java.io.Serializable;

public class Promise implements Serializable {

    public int id;
    public int userId;
    public int friendId;
    public String meetingPlace;
    public String meetingDay;
    public String meetingTime;
    public String userproImgUrl;
    public String friendproImgUrl;

    public Promise() {
    }

}
