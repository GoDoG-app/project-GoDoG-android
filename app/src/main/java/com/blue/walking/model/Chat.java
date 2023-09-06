package com.blue.walking.model;


public class Chat {

//                "id": 24,
//                        "userEmail": "test12@naver.com",
//                        "userNickname": "테스터",
//                        "userGender": 0,
//                        "userBirth": "2023-08-24",
//                        "userImgUrl": null,
//                        "userOneliner": null,
//                        "userLoginType": 0,
//                        "userKakaoId": null,
//                        "userAddress": "인천 서구 심곡동"


    public int id;
    public String userNickname;
    public String userImgUrl;
    public String message;
    public String createdAt;

    public Chat() {
    }


    public Chat(int id, String userNickname, String userImgUrl) {
        this.id = id;
        this.userNickname = userNickname;
        this.userImgUrl = userImgUrl;
    }

    public Chat(int id, String userNickname, String userImgUrl, String message, String createdAt) {
        this.id = id;
        this.userNickname = userNickname;
        this.userImgUrl = userImgUrl;
        this.message = message;
        this.createdAt = createdAt;
    }




}
