package com.blue.walking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class PetRegisterActivity extends AppCompatActivity {

    ImageView imgBack;  // 뒤로가기 버튼

    ImageView imgPet;  // 강아지 이미지
    TextView txtImg;   // 강아지 사진 선택
    EditText editPetName;  // 강아지 이름
    Button editPetAge;  // 강아지 생일 선택
    Button btnPetMale;  // 강아지 성별 남성
    Button btnPetFemale;  // 강아지 성별 여성
    EditText editComment;  // 한 줄 소개
    Switch switch1;  // 대표 설정

    Button btnRegister;  // 추가 완료


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_register);

        imgPet = findViewById(R.id.imgPet);
        imgPet.setClipToOutline(true);  // 둥근 테두리 적용


        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(PetRegisterActivity.this, UserFragment.class);
                startActivity(intent); // 음.. 내 정보로 돌아가지 않고 홈으로 돌아감. 수정필요
            }
        });

    }
}