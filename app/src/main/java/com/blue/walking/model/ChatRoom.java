package com.blue.walking.model;

public class ChatRoom {
    // 1:1채팅방
    public int senderId;
    public int receiverId;
    public String userNickname;
    public String userImgUrl;
    public String Message;
    public String CreatedAt;

    public ChatRoom() {
    }

    public ChatRoom(int senderId, int receiverId, String userNickname, String userImgUrl, String message, String createdAt) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.userNickname = userNickname;
        this.userImgUrl = userImgUrl;
        Message = message;
        CreatedAt = createdAt;
    }
}
