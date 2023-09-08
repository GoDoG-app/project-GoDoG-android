package com.blue.walking.model;


public class Chat {
    // 채팅목록
    public int id;
    public String userNickname;
    public String userImgUrl;
    public String lastMessage;
    public String lastCreatedAt;

    public Chat() {
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
