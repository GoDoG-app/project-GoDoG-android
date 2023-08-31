package com.blue.walking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatRoomActivity extends AppCompatActivity {

    /** 화면뷰 */
    TextView txtUserName;  // 상대 유저 이름
    EditText editChat;  // 채팅 입력
    ImageView imgSend;  // 채팅 보내기
    ImageView imgBack;  // 뒤로가기
    Button btnPromise;  // 약속 잡기
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        imgBack = findViewById(R.id.imgBack);
        btnPromise = findViewById(R.id.btnPromise);

        // 뒤로가기
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // 약속하기로 이동
        btnPromise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(ChatRoomActivity.this, ChatPromiseActivity.class);
                startActivity(intent);
            }
        });




    }
}