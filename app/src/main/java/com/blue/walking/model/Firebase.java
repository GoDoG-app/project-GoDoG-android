package com.blue.walking.model;

import com.google.firebase.Timestamp;

public class Firebase {

    public String userNickname;
    public String userImgUrl;
    public String userAddress;
    public String commentContent;
    public Timestamp createdAt; // 댓글 작성 시간
    public Timestamp updatedAt; // 수정 시간
    public int postId;

    public Firebase() {
    }

    // 사용자 정보를 가져오는 생성자
    public Firebase(String userNickname, String userImgUrl, String userAddress) {
        this.userNickname = userNickname;
        this.userImgUrl = userImgUrl;
        this.userAddress = userAddress;
    }

    // 댓글 생성자
    public Firebase(String userNickname, String userImgUrl, String userAddress, String commentContent, Timestamp createdAt, Timestamp updatedAt) {
        this.userNickname = userNickname;
        this.userImgUrl = userImgUrl;
        this.userAddress = userAddress;
        this.commentContent = commentContent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
