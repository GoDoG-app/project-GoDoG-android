package com.blue.walking.api;

import com.blue.walking.model.Post;
import com.blue.walking.model.PostList;
import com.blue.walking.model.ResultRes;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface PostApi {

    // 커뮤니티 게시물 작성 API
    @POST("/posting")
    Call<ResultRes> upPost(@Header("Authorization") String token,
                           @Part MultipartBody.Part photo,
                           @Body Post post);
                            // 폼 데이터 보내는 @Part

    // 커뮤니티 게시물 전체보기 API
    @GET("/posting/list")
    Call<PostList> getPostList(@Query("offset")int offset,
                               @Query("limit")int limit,
                               @Header("Authorization") String token);
}
