package com.blue.walking.api;

import com.blue.walking.model.Friends;
import com.blue.walking.model.ResultRes;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FriendsAPi {

    // 친구 목록 API
    @GET("/followlist")
    Call<Friends> getFriends(@Header("Authorization") String token);

    // 친구 맺기 API
    @POST("/follow/{followee_id}")
    Call<ResultRes> postFollowee(@Header("Authorization") String token,
                                 @Path("followee_id") int followee_id);

    // 친구 끊기 API
    @DELETE("/follow/{followee_id}")
    Call<ResultRes> deleteFollowee(@Header("Authorization") String token,
                                   @Path("followee_id") int followee_id);

}
