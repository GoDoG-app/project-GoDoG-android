package com.blue.walking.api;

import com.blue.walking.model.PromiseList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface PromiseApi {

    // 약속 목록
    @GET("/promiselist")
    Call<PromiseList> promiselist(@Header("Authorization") String token);

}
