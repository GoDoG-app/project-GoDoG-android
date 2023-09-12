package com.blue.walking.model;

import java.io.Serializable;
import java.util.Date;

public class SearchUserItems implements Serializable {

//    "id": 7,
//            "email": "bbb@naver.com",
//            "password": "$pbkdf2-sha256$29000$836v9R6DEGKMcc7Zu9c6Rw$I3BVLSTJI8jelWb2VgD99ew0JNldi5JI3SM6iJL4UW4",
//            "nickname": "hora",
//            "gender": 1,
//            "birth": "1989-10-02",
//            "proImgUrl": "https://project4-walking-app.s3.amazonaws.com/2023-09-06T08_16_47_807696_7.jpg",
//            "oneliner": "반갑습니다~ 같이 산책해요!",
//            "auth": 0,
//            "createdAt": "2023-08-21T06:53:55",
//            "updatedAt": "2023-09-07T01:46:16",
//            "loginType": 0,
//            "kakaoId": null

    public int id;
    public String email;
    public String password;
    public String nickname;
    public int gender;
    public Date birth;
    public String proImgUrl;
    public String oneliner;
    public int auth;
    public String createdAt;
    public String updatedAt;
    public int loginType;
    public String kakaoId;

    public SearchUserItems() {
    }
}
