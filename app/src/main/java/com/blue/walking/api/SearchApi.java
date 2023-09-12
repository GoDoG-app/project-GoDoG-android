package com.blue.walking.api;

import com.blue.walking.model.Friends;
import com.blue.walking.model.SearchUser;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface SearchApi {


//    @GET("/followlist")
//    Call<Friends> getFriends(@Header("Authorization") String token);
    // 유저 검색 API
    @GET("/search")
    Call<SearchUser> getSearchUser(@Query("name")String name);

}
