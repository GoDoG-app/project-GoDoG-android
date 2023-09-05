package com.blue.walking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.PetApi;
import com.blue.walking.api.UserApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.PetList;
import com.blue.walking.model.ResultRes;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SettingActivity extends AppCompatActivity {

    ImageView imgBack;  // 뒤로가기
    TextView txtEmail;  // 유저 이메일
    TextView txtLogout;  // 로그아웃

    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        imgBack = findViewById(R.id.imgBack);
        txtEmail = findViewById(R.id.txtEmail);
        txtLogout = findViewById(R.id.txtLogout);


        // 로그아웃을 클릭했을 때
        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /** 로그아웃 API (미완) */
                Retrofit retrofit = NetworkClient.getRetrofitClient(SettingActivity.this);
                UserApi api = retrofit.create(UserApi.class);

                Log.i("pet","로그아웃 API 실행");

                // 유저 토큰
                SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                token = sp.getString(Config.ACCESS_TOKEN, "");

                Call<ResultRes> call = api.logout("Bearer " + token);
                call.enqueue(new Callback<ResultRes>() {
                    @Override
                    public void onResponse(Call<ResultRes> call, Response<ResultRes> response) {
                        if (response.isSuccessful()){
                            Log.i("pet","로그아웃 실행 성공");

                            SharedPreferences preferences = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.remove(Config.ACCESS_TOKEN);
                            editor.apply();

                            Intent intent;
                            intent = new Intent(SettingActivity.this, MainActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                            finish();

                        } else {
                            Log.i("pet","로그아웃 실행 실패");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultRes> call, Throwable t) {
                        Log.i("pet","로그아웃 실행 실패");
                    }
                });
            }
        });


        // 뒤로가기
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}