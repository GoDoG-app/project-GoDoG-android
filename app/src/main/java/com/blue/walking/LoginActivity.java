package com.blue.walking;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.UserApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.User;
import com.blue.walking.model.UserKakaoToken;
import com.blue.walking.model.UserRes;
import com.google.android.material.snackbar.Snackbar;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.common.util.Utility;
import com.kakao.sdk.user.UserApiClient;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    EditText editEmail;
    EditText editPassword;
    Button btnLogin;
    TextView txtRegister;
    ImageView imgKakao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);

        txtRegister.setOnClickListener(new View.OnClickListener() {
            // 회원가입 텍스트 클릭 -> 회원가입 엑티비티로 이동
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editEmail.getText().toString().trim();
                Pattern pattern = Patterns.EMAIL_ADDRESS;
                if (pattern.matcher(email).matches() == false){
                    Snackbar.make(btnLogin,
                            "이메일 형식을 바르게 입력하세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                String password = editPassword.getText().toString().trim();
                if (password.length() < 6 || password.length() >12){
                    Snackbar.make(btnLogin,
                            "비밀번호 길이가 잘못되었습니다.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                showProgress();

                Retrofit retrofit = NetworkClient.getRetrofitClient(LoginActivity.this);
                UserApi api = retrofit.create(UserApi.class);

                User user = new User(email, password);

                Call<UserRes> call = api.login(user);

                call.enqueue(new Callback<UserRes>() {
                    @Override
                    public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                        dismissProgress();
                        if (response.isSuccessful()){
                            UserRes res = response.body();

                            SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(Config.ACCESS_TOKEN, res.access_token);
                            editor.apply();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        } else if(response.code() == 400) {
                            Snackbar.make(btnLogin,
                                    "이메일이나 비밀번호가 맞지않습니다.",
                                    Snackbar.LENGTH_SHORT).show();
                            return;
                        } else {
                            // 400에러났을때(서버문제)
                            Snackbar.make(btnLogin,
                                    "서버에 문제가 있습니다.",
                                    Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    @Override
                    public void onFailure(Call<UserRes> call, Throwable t) {
                        dismissProgress();
                    }
                });
            }
        });

        // 카카오 로그인
        imgKakao = findViewById(R.id.imgKakao);

        // 키해시 가져오기
        String keyHash = Utility.INSTANCE.getKeyHash(LoginActivity.this);
        Log.i("test", "키해시:"+keyHash);

        // sdk 초기화
        KakaoSdk.init(LoginActivity.this, "80e37f349a16fcd565298d9e9591c338");

        imgKakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this, (oAuthToken, error) -> {
                    if (error != null) {
                        // 로그인 실패
                        Log.e("aaa", "로그인 실패", error);
                    } else if (oAuthToken != null) {
                        // 로그인 성공
                        Log.i("aaa", "로그인 성공(토큰): " + oAuthToken.getAccessToken());

                        // 토큰을 받아오면 토큰을 서버로 전달
                        Retrofit retrofit = NetworkClient.getRetrofitClient(LoginActivity.this);

                        UserApi api = retrofit.create(UserApi.class);

                        UserKakaoToken userKakaoToken = new UserKakaoToken(oAuthToken.getIdToken());

                        Call<UserKakaoToken> call = api.sendToken(userKakaoToken);

                        call.enqueue(new Callback<UserKakaoToken>() {
                            @Override
                            public void onResponse(Call<UserKakaoToken> call, Response<UserKakaoToken> response) {
                                if (response.isSuccessful()) {

                                    Log.i("test","토큰 전송 성공: "+userKakaoToken);

                                } else {
                                    Log.e("test","토큰 전송 실패: "+response.code());
                                }

                            }

                            @Override
                            public void onFailure(Call<UserKakaoToken> call, Throwable t) {

                                Log.e(TAG,"서버 연결 오류:",t);

                            }
                        });



                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        // 프로필 수정창으로 이동
                        startActivity(intent);
                    }
                    return null;
                });

            }
        });


    }
    Dialog dialog;

    void showProgress(){
        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(new ProgressBar(this));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    // 다이얼로그를 사라지게 하는 함수
    void dismissProgress(){
        dialog.dismiss();
    }
}