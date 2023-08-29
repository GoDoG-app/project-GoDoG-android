package com.blue.walking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatRoomActivity extends AppCompatActivity {

    /** 화면뷰 */
    TextView txtPetName;
    EditText editChat;
    ImageView imgMenu;
    ImageView imgSend;
    ImageView imgBack;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        imgMenu = findViewById(R.id.imgMenu);
        imgBack = findViewById(R.id.imgBack);

        // TODO : 약속하기와 파일첨부 메뉴 띄우기 (엑티비티가 다이얼로그로 띄워지는지 아직 확인 안해봄)
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(ChatRoomActivity.this, ChatMenuActivity.class);
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