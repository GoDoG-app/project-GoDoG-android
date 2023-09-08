package com.blue.walking.model;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Date;

public class Firebase implements Serializable {

    public String userNickname;
    public String userImgUrl;
    public String userAddress;
    public String commentContent;
    public Date createdAt; // 댓글 작성 시간
    public Date updatedAt; // 수정 시간

    public boolean isLiked;
    public int likeCount;

    public String documentId;

    public String getDocumentId() {
        return documentId; // documentId는 Firebase 객체의 고유 문서 ID입니다.
    }


    public Firebase() {
    }

    // 사용자 정보를 가져오는 생성자
    public Firebase(String userNickname, String userImgUrl, String userAddress) {
        this.userNickname = userNickname;
        this.userImgUrl = userImgUrl;
        this.userAddress = userAddress;
    }

    // 댓글 생성자
    public Firebase(String userNickname, String userImgUrl, String userAddress, String commentContent, Date createdAt, Date updatedAt, String documentId) {
        this.userNickname = userNickname;
        this.userImgUrl = userImgUrl;
        this.userAddress = userAddress;
        this.commentContent = commentContent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.documentId = documentId;
    }

    public Firebase(boolean isLiked, int likeCount) {
        this.isLiked = isLiked;
        this.likeCount = likeCount;
    }

    public Firebase(String documentId) {
        this.documentId = documentId;
    }
}
