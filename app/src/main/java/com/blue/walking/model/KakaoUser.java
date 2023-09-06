package com.blue.walking.model;

public class KakaoUser {

    public String email;
    public String nickname;
    public int loginType;
    public String kakaoId;

    public KakaoUser() {

    }

    public KakaoUser(String email, String nickname, int loginType, String kakaoId) {
        this.email = email;
        this.nickname = nickname;
        this.loginType = loginType;
        this.kakaoId = kakaoId;
    }
}
