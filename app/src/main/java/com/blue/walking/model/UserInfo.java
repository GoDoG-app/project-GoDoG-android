package com.blue.walking.model;

public class UserInfo {

//    {
//        "result": "success",
//            "info": [
//        {
//            "id": 6,
//                "userEmail": "aaa@naver.com",
//                "userNickname": "뷔",
//                "userGender": 1,
//                "userBirth": "1989-10-02",
//                "userImgUrl": "https://project4-walking-app.s3.amazonaws.com/2023-09-04T11_15_09_025304_6.jpg",
//                "userOneliner": "안녕하세요",
//                "userLoginType": 0,
//                "userKakaoId": null,
//                "userAddress": "인천광역시 서구 심곡동"
//        }
//    ]
//    }

    public String userEmail;
    public String userNickname;
    public int userGender;
    public String userBirth;
    public String userImgUrl;
    public String userOneliner;
    public int userLoginType;
    public String userKakaoId;
    public String userAddress;
    public double lat;
    public double lng;

    public UserInfo() {
    }

    public UserInfo(String userNickname, String userImgUrl, String userAddress) {
        this.userNickname = userNickname;
        this.userImgUrl = userImgUrl;
        this.userAddress = userAddress;
    }

    public UserInfo(String userEmail, String userNickname, int userGender,
                    String userBirth, String userImgUrl, String userOneliner,
                    int userLoginType, String userKakaoId, String userAddress) {
        this.userEmail = userEmail;
        this.userNickname = userNickname;
        this.userGender = userGender;
        this.userBirth = userBirth;
        this.userImgUrl = userImgUrl;
        this.userOneliner = userOneliner;
        this.userLoginType = userLoginType;
        this.userKakaoId = userKakaoId;
        this.userAddress = userAddress;
    }
}
