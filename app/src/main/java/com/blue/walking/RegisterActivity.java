package com.blue.walking;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.NetworkStatus;
import com.blue.walking.api.UserApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.User;
import com.blue.walking.model.UserRes;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {

    /** 화면뷰 */
    ImageView imgBack;  // 뒤로가기 버튼

    EditText editEmail;  // 이메일 입력
    EditText editPassword;   // 비밀번호 입력
    EditText editPassword2;  // 비밀번호 확인
    EditText editNickname;   // 닉네임 입력
    TextView txtPlace;  // 지역 설정
    Button btnBirth;    // 생일 입력
    Button btnMale;     // 성별 남성
    Button btnFemale;   // 성별 여성

    Button btnRegister; // 회원가입

    String token;
    String date = null;
    int gender = 1;
    double latitude;
    double longitude;
    String address;

    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editPassword2 = findViewById(R.id.editPassword2);
        editNickname = findViewById(R.id.editNickname);
        txtPlace = findViewById(R.id.txtPlace);
        btnBirth = findViewById(R.id.btnBirth);
        btnMale = findViewById(R.id.btnMale);
        btnFemale = findViewById(R.id.btnFemale);
        btnRegister = findViewById(R.id.btnRegister);
        imgBack = findViewById(R.id.imgBack);

        imgBack.setOnClickListener(new View.OnClickListener() {
            // 뒤로가기
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("주소설정페이지", "주소입력창 클릭");
                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if(status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {

                    Log.i("주소설정페이지", "주소입력창 클릭");
                    Intent intent = new Intent(getApplicationContext(), RegionActivity.class);
                    // 화면전환 애니메이션 없애기
                    overridePendingTransition(0, 0);
                    // 주소결과
                    startActivityForResult(intent, SEARCH_ADDRESS_ACTIVITY);

                }else {
                    Toast.makeText(getApplicationContext(),
                            "인터넷 연결을 확인해주세요.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

        // 생일가져오기
        btnBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 오늘 날짜로 셋팅하기 위해 변수로 저장
                Calendar current = Calendar.getInstance();
                int y = current.get(Calendar.YEAR);
                int m = current.get(Calendar.MONTH);
                int d = current.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // 월은 0이 1월이기때문에 +1해서 사용해준다(0부터 11까지 있다)
                        // 월은 +1해서 저장
                        int m = month + 1;

                        // 선택한 월이 한자리수일때
                        String strM;
                        if (m < 10) {
                            strM = "0"+m;
                        } else {
                            strM = ""+m;
                        }

                        // 선택한 일이 한자리수일때
                        String strD;
                        if (dayOfMonth < 10) {
                            strD = "0"+dayOfMonth;
                        } else {
                            strD = ""+dayOfMonth;
                        }

                        date = year + "-" + strM + "-" + strD;
                        btnBirth.setText(date);
                        Log.i("DATE",date);
                    }
                }, y, m, d); // 날짜 셋팅한 변수 입력
                // 화면에 보여주기
                datePickerDialog.show();
            }
        });

        btnMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnMale.setBackgroundColor(Color.parseColor("#262D33"));
                btnMale.setTextColor(Color.parseColor("#FFFFFF"));
                btnFemale.setBackgroundColor(Color.parseColor("#FFFFFF"));
                btnFemale.setTextColor(Color.parseColor("#262D33"));
            }
        });

        btnFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnFemale.setBackgroundColor(Color.parseColor("#262D33"));
                btnFemale.setTextColor(Color.parseColor("#FFFFFF"));
                btnMale.setBackgroundColor(Color.parseColor("#FFFFFF"));
                btnMale.setTextColor(Color.parseColor("#262D33"));
                gender = 0;
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = editEmail.getText().toString().trim();
                Pattern pattern = Patterns.EMAIL_ADDRESS;
                if (pattern.matcher(email).matches() == false){
                    Snackbar.make(btnRegister,
                            "이메일 형식을 확인하세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                String password1 = editPassword.getText().toString().trim();
                if (password1.length() < 6 || password1.length() > 12){
                    Snackbar.make(btnRegister,
                            "비밀번호 길이를 확인하세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                String password2 = editPassword2.getText().toString().trim();
                if (!password2.equals(password1)){
                    Snackbar.make(btnRegister,
                            "비밀번호가 일치하지않습니다.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                String nickname = editNickname.getText().toString().trim();
                if (nickname.length() < 2 || nickname.length() > 10){
                    Snackbar.make(btnRegister,
                            "닉네임은 2글자 이상, 10글자 이하입니다.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                // 지역과 생일 성별 조건은 어떻게 if문을 넣을것인가?



                Retrofit retrofit = NetworkClient.getRetrofitClient(RegisterActivity.this);
                UserApi api =retrofit.create(UserApi.class);

                Log.i("DATE",date);

                if (date != null && !date.isEmpty()) {
                    User user = new User(email, password1, nickname, gender, date, address, latitude, longitude);


                    Call<UserRes> call = api.register(user);

                    showProgress();

                    call.enqueue(new Callback<UserRes>() {
                        @Override
                        public void onResponse(Call<UserRes> call, Response<UserRes> response) {

                            dismissProgress();
                            if (response.isSuccessful()) {
                                SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();

                                UserRes res = response.body();
                                editor.putString(Config.ACCESS_TOKEN, res.access_token);
                                editor.apply();

                                Log.i("DATE",date);

                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);

                                finish();
                            } else if (response.code() == 400) {
                                Snackbar.make(btnRegister,
                                        "이미 회원입니다. 로그인하세요.",
                                        Snackbar.LENGTH_SHORT).show();
                                return;
                            } else if (response.code() == 401) {

                            } else if (response.code() == 404) {

                            } else if (response.code() == 500) {

                            } else {

                            }

                        }

                        @Override
                        public void onFailure(Call<UserRes> call, Throwable t) {

                        }
                    });
                }else {
                    Snackbar.make(btnRegister, "생일을 선택하세요.", Snackbar.LENGTH_SHORT).show();
                }


            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.i("test", "onActivityResult");

        switch (requestCode) {
            case SEARCH_ADDRESS_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    // 주소 데이터 추출
                    String sido = intent.getStringExtra("sido");
                    String sigungu = intent.getStringExtra("sigungu");
                    String hname = intent.getStringExtra("hname");
                    String fullAddr = intent.getStringExtra("fullAddr");

                    if (sido != null && sigungu != null && hname != null && fullAddr != null) {
                        // 주소 데이터 로그찍기
                        Log.i("test", "시/도:" + sido);
                        Log.i("test", "시/군/구:" + sigungu);
                        Log.i("test", "행정동 이름:" + hname);
                        Log.i("test", "전체 주소:" + fullAddr);

                        // 시군구동 합쳐서 보여주기
                        address = sido+" "+sigungu+" "+hname;
                        txtPlace.setText(address);

                        // 위도 경도 구하기
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocationName(fullAddr, 1);
                            if (!addresses.isEmpty()) {
                                latitude = addresses.get(0).getLatitude();
                                longitude = addresses.get(0).getLongitude();

                                // 위도 경도 로그 표시
                                Log.i("test", "위도: " + latitude + ", 경도: " + longitude);

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
                break;
        }
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