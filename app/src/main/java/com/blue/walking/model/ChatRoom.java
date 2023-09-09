package com.blue.walking.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.io.Serializable;
import java.util.Date;

public class ChatRoom implements Serializable {
    // 1:1채팅방
    public int id;
    public String userNickname;
    public String userImgUrl;
    public String message;
    public Object createdAt;

    public ChatRoom() {
    }

    public ChatRoom(int id, String userNickname, String userImgUrl, String message, Object createdAt) {
        this.id = id;
        this.userNickname = userNickname;
        this.userImgUrl = userImgUrl;
        this.message = message;
        this.createdAt = createdAt;
    }

}
