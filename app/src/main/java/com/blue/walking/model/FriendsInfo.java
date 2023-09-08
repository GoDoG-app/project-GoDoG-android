package com.blue.walking.model;

import java.io.Serializable;

public class FriendsInfo implements Serializable {

    public int id;
    public int followerId;
    public int followeeId;
    public String nickname;
    public String proImgUrl;
    public String oneliner;

    public FriendsInfo() {
    }

    public FriendsInfo(String nickname, String proImgUrl, String oneliner) {
        this.nickname = nickname;
        this.proImgUrl = proImgUrl;
        this.oneliner = oneliner;
    }
}
