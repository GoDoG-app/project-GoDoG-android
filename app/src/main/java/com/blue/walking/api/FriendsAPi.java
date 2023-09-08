package com.blue.walking.api;

import com.blue.walking.model.Friends;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface FriendsAPi {

    @GET("/followlist")
    Call<Friends> getFriends(@Header("Authorization") String token);

}
