package com.blue.walking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    EditText editEmail;  // 이메일 입력
    EditText editPassword;   // 비밀번호 입력
    EditText editPassword2;  // 비밀번호 확인
    EditText editNickname;   // 닉네임 입력
    TextView txtPlace;  // 지역 설정
    Switch switch1;     // 나이 비공개 여부
    Button btnBrith;    // 생일 입력
    Button btnMale;     // 성별 남성
    Button btnFemale;   // 성별 여성
    Button btnRegister; // 회원가입

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

    }
}