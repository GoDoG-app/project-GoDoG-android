package com.blue.walking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class UserUpdateActivity extends AppCompatActivity {

    /** 화면뷰 */
    ImageView imgBack;  // 뒤로가기
    ImageView imgUser;  // 유저 프로필 사진
    TextView txtImg;   // 프로필 사진 선택
    EditText editNickName;  // 유저 닉네임
    Button btnBirth;  // 유저 생일
    TextView txtPlace;  // 유저 지역
    Button btnFemale;  // 유저 성별 여성
    Button btnMale;    // 유저 성별 남성
    EditText editComment;  // 한 줄 소개
    Button btnUpdate;  // 수정 완료 버튼


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update);

        imgUser = findViewById(R.id.imgUser);
        imgUser.setClipToOutline(true);  // 둥근 테두리 적용

    }
}