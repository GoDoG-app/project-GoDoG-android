package com.blue.walking.api;

import com.blue.walking.model.ResultRes;
import com.blue.walking.model.User;
import com.blue.walking.model.UserKakaoToken;
import com.blue.walking.model.UserList;
import com.blue.walking.model.UserRes;

import javax.xml.transform.Result;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserApi {

    @POST("/user/register")
    Call<UserRes> register(@Body User user);

    @POST("/user/login")
    Call<UserRes> login(@Body User user);

    // 카카오 로그인 인증 토큰
    @POST("/user/kakao/oauth")
    Call<UserKakaoToken> sendToken(@Body UserKakaoToken userKakaoToken);

    // 유저 정보 API
    @GET("/user/profile")
    Call<UserList> getUserInfo(@Header("Authorization") String token);

    // 로그아웃 API
    @DELETE("/user/logout")
    Call<ResultRes> logout(@Header("Authorization") String token);

}
