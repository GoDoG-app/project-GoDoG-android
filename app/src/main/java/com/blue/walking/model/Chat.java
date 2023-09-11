package com.blue.walking.model;


import java.io.Serializable;

public class Chat implements Serializable{
    // 채팅목록
    public int id;
    public String userNickname;
    public String userImgUrl;
    public String lastMessage;
    public String lastCreatedAt;
    public String chatRoomId;

    public Chat() {
    }

    public Chat(String chatRoomId, String lastMessage, String lastCreatedAt) {

        this.chatRoomId = chatRoomId;
        this.lastMessage = lastMessage;
        this.lastCreatedAt = lastCreatedAt;
    }

    public Chat(int id, String userNickname, String userImgUrl) {
        this.id = id;
        this.userNickname = userNickname;
        this.userImgUrl = userImgUrl;
    }

    public Chat(int id, String userNickname, String userImgUrl, String lastMessage, String lastCreatedAt) {
        this.id = id;
        this.userNickname = userNickname;
        this.userImgUrl = userImgUrl;
        this.lastMessage = lastMessage;
        this.lastCreatedAt = lastCreatedAt;
    }





}
