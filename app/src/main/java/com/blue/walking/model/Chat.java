package com.blue.walking.model;


public class Chat {
    // 채팅목록
    public int id;
    public String userNickname;
    public String userImgUrl;
    public String lastMessage;
    public Object lastCreatedAt;
    public String chatRoomId;

    public Chat() {
    }

    public Chat(String chatRoomId, String lastMessage, Object lastCreatedAt) {

        this.chatRoomId = chatRoomId;
        this.lastMessage = lastMessage;
        this.lastCreatedAt = lastCreatedAt;
    }

    public Chat(int id, String userNickname, String userImgUrl) {
        this.id = id;
        this.userNickname = userNickname;
        this.userImgUrl = userImgUrl;
    }

    public Chat(int id, String userNickname, String userImgUrl, String lastMessage, Object lastCreatedAt) {
        this.id = id;
        this.userNickname = userNickname;
        this.userImgUrl = userImgUrl;
        this.lastMessage = lastMessage;
        this.lastCreatedAt = lastCreatedAt;
    }

    public Chat(String chatRoomId, String userNickname, String userImgUrl, String lastMessage, Object lastCreatedAt) {
        this.chatRoomId = chatRoomId;
        this.userNickname = userNickname;
        this.userImgUrl = userImgUrl;
        this.lastMessage = lastMessage;
        this.lastCreatedAt = lastCreatedAt;
    }




}
