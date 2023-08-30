package com.blue.walking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FriendActivity extends AppCompatActivity {

    /** 화면뷰 */
    Button btnChat;   // 채팅 보내기
    Button btnFollower;  // 친구 등록

    ImageView imgBack;  // 뒤로가기
    TextView userNickname;  // 유저 닉네임
    TextView userInfo;   // 유저 성별과 나이
    TextView userPlace;  // 유저 지역
    TextView userComment;  // 유저 한 줄 소개
    ImageView imgUser;  // 유저 프로필 사진

    TextView userTemp;  // 온도 숫자 표시
    ProgressBar progressBar;  // 온도 표시
    TextView userScore1;  // 내가 받은 후기1
    TextView userScore2;  // 내가 받은 후기2

    ImageView imgPet;  // 동물 프로필 사진
    TextView petNickname;  // 동물 이름
    TextView petInfo;  // 동물 성별과 나이
    TextView petComment;  // 동물 한 줄 소개

    Button btnWalkList;    // 산책 기록
    Button btnCommuList;   // 게시글(커뮤니티)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        btnChat = findViewById(R.id.btnChat);
        imgBack = findViewById(R.id.imgBack);

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 채팅방으로 이동
                Intent intent;
                intent = new Intent(FriendActivity.this, ChatRoomActivity.class);
                startActivity(intent);
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