package com.blue.walking.api;

import com.blue.walking.model.ResultRes;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PetApi {

    // 펫 등록 API
    @Multipart
    @POST("/pet/register")
    Call<ResultRes> petRegister(@Header("Authorization") String token,
                                @Part MultipartBody.Part photo,
                                @Part ("petName") RequestBody petName,
                                @Part ("petAge") RequestBody petAge,
                                @Part ("petGender") RequestBody petGender);

}
