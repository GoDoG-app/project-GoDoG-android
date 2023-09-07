package com.blue.walking.api;

import com.blue.walking.model.RandomFriendRes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface RandomFriendAPI {

    @GET("/randomfriend/list")
    Call<RandomFriendRes> getRandomFriend(@Query("offset")int offset,
                                          @Query("limit")int limit,
                                          @Header("Authorization") String token);
}
