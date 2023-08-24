package com.blue.walking.api;

import com.blue.walking.model.User;
import com.blue.walking.model.UserRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApi {

    @POST("/user/register")
    Call<UserRes> register(@Body User user);

    @POST("/user/login")
    Call<UserRes> login(@Body User user);

}
