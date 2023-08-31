package com.blue.walking.api;

import com.blue.walking.model.PostList;
import com.blue.walking.model.ResultRes;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PostApi {

    // 커뮤니티 게시물 작성 API
    @Multipart
    @POST("/posting")
    Call<ResultRes> upPost(@Header("Authorization") String token,
                           @Part MultipartBody.Part photo,
                           @Part ("content") RequestBody content,
                           @Part ("category") RequestBody category
                           );
                            // 폼 데이터 보내는 @Part

    // 커뮤니티 게시물 전체 가져오기 API
    @GET("/posting/list")
    Call<PostList> getPostList(@Query("offset")int offset,
                               @Query("limit")int limit,
                               @Header("Authorization") String token);

    // 커뮤니티 게시물 좋아요 API
    @POST("/post/{postId}/like")
    Call<ResultRes> setPostLike(@Path("postId") int postId,
                                @Header("Authorization") String token);

    // 커뮤니티 게시물 좋아요 해제 API
    @DELETE("/post/{postId}/like")
    Call<ResultRes> deletePostLike(@Path("postId") int postId,
                                @Header("Authorization") String token);

}
