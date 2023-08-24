package com.blue.walking.api;

import android.content.Context;

import com.blue.walking.config.Config;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {

    public static Retrofit retrofit;

    public static Retrofit getRetrofitClient(Context context){
        if (retrofit == null){
            // 네트워크 통신한 로그를 확인할때 필요한 코드(라이브러리에 추가해둠)
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // body로 쓰면 데이터 통신 모두 확인 가능
            //네트워크 연결시키는 코드
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1,TimeUnit.MINUTES)
                    .addInterceptor(loggingInterceptor)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(Config.HOST)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
