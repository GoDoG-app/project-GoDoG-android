package com.blue.walking.model;

public class ChatRoom {
    // 1:1채팅방
    public int id;
    public String userNickname;
    public String userImgUrl;
    public String Message;
    public String CreatedAt;

    public ChatRoom() {
    }


    public ChatRoom(int id, String userNickname, String userImgUrl) {
        this.id = id;
        this.userNickname = userNickname;
        this.userImgUrl = userImgUrl;
    }

    public ChatRoom(int id, String userNickname, String userImgUrl, String Message, String CreatedAt) {
        this.id = id;
        this.userNickname = userNickname;
        this.userImgUrl = userImgUrl;
        this.Message = Message;
        this.CreatedAt = CreatedAt;
    }
}
