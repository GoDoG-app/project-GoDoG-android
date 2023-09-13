package com.blue.walking.api;

import com.blue.walking.model.ResultRes;
import com.blue.walking.model.WalkingList;
import com.blue.walking.model.WalkingRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface WalkingApi {

    @POST("/walkinglist")
    Call<ResultRes> getWalkingList(@Header("Authorization") String token,
                                   @Body WalkingList walkList);

    @GET("/walkinglist/my")
    Call<WalkingRes> getMyWalkingList(@Header("Authorization") String token);

}
