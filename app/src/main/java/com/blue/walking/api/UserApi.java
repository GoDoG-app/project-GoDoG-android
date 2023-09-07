package com.blue.walking.api;

import com.blue.walking.model.KakaoUser;
import com.blue.walking.model.ResultRes;
import com.blue.walking.model.User;
import com.blue.walking.model.UserList;
import com.blue.walking.model.UserRes;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserApi {

    @POST("/user/register")
    Call<UserRes> register(@Body User user);

    @POST("/user/login")
    Call<UserRes> login(@Body User user);

    // 유저 정보 API
    @GET("/user/profile")
    Call<UserList> getUserInfo(@Header("Authorization") String token);

    // 로그아웃 API
    @DELETE("/user/logout")
    Call<ResultRes> logout(@Header("Authorization") String token);

    // 내 정보 수정 API
    @Multipart
    @PUT("/user/profile")
    Call<ResultRes> UserEdit(@Header("Authorization") String token,
                             @Part MultipartBody.Part photo,
                             @Part ("nickname") RequestBody nickname,
                             @Part ("oneliner") RequestBody oneliner,
                             @Part ("address") RequestBody address,
                             @Part ("lat") RequestBody lat,
                             @Part ("lng") RequestBody lng);

    // 카카오 로그인 API
    @POST("/user/kakaologin")
    Call<UserRes> kakaoLogin(@Body KakaoUser kakaoUser);

    // 특정 유저정보 API
    @GET("/user/{id}")
    Call<UserList> getFriendInfo(@Path("id") int id);

}
