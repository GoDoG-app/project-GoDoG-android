package com.blue.walking.api;

import com.blue.walking.model.Pet;
import com.blue.walking.model.PetList;
import com.blue.walking.model.ResultRes;
import com.blue.walking.model.User;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface PetApi {

    // 펫 등록 API
    @Multipart
    @POST("/pet/register")
    Call<ResultRes> petRegister(@Header("Authorization") String token,
                                @Part MultipartBody.Part photo,
                                @Part ("petName") RequestBody petName,
                                @Part ("petAge") RequestBody petAge,
                                @Part ("petGender") RequestBody petGender,
                                @Part ("petOneliner") RequestBody petOneliner);

    // 내 펫 정보 API
    @GET("/pet/info")
    Call<PetList> petInfo(@Header("Authorization") String token);

    // 펫 정보변경 API
    @Multipart
    @PUT("/pet/edit")
    Call<ResultRes> petEdit(@Header("Authorization") String token,
                            @Part MultipartBody.Part photo,
                            @Part ("petName") RequestBody petName,
                            @Part ("petAge") RequestBody petAge,
                            @Part ("petGender") RequestBody petGender,
                            @Part ("petOneliner") RequestBody petOneliner);

}
