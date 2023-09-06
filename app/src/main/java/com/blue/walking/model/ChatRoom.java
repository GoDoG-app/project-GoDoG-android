package com.blue.walking.model;

public class ChatRoom {

    public int id;
    public String userNickname;
    public String userImgUrl;
    public String lastMessage;
    public String LastCreatedAt;

    public ChatRoom() {
    }


    public ChatRoom(int id, String userNickname, String userImgUrl) {
        this.id = id;
        this.userNickname = userNickname;
        this.userImgUrl = userImgUrl;
    }

    public ChatRoom(int id, String userNickname, String userImgUrl, String lastMessage, String LastCreatedAt) {
        this.id = id;
        this.userNickname = userNickname;
        this.userImgUrl = userImgUrl;
        this.lastMessage = lastMessage;
        this.LastCreatedAt = LastCreatedAt;
    }
}
