package com.blue.walking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class PetUpdateActivity extends AppCompatActivity {

    ImageView imgBack;  // 뒤로가기 버튼
    ImageView imgPet;   // 강아지 사진
    TextView txtImg;    // 사진 선택
    EditText editPetName;   // 강아지 이름
    Button editPetAge;   // 강아지 생일
    Button btnMale;    // 강아지 성별 남자
    Button btnFemale;  // 강아지 성별 여자
    EditText editComment;   // 한 줄 소개
    Button btnUpdate;   // 수정완료

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_update);

        imgPet = findViewById(R.id.imgPet);
        imgPet.setClipToOutline(true);  // 둥근 테두리 적용

    }
}