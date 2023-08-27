package com.blue.walking.api;

import com.blue.walking.model.User;
import com.blue.walking.model.UserKakaoToken;
import com.blue.walking.model.UserRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApi {

    @POST("/user/register")
    Call<UserRes> register(@Body User user);

    @POST("/user/login")
    Call<UserRes> login(@Body User user);

    // 카카오 로그인 인증 토큰
    @POST("/user/login/kakaoToken")
    Call<UserKakaoToken> sendToken(@Body UserKakaoToken userKakaoToken);

}
