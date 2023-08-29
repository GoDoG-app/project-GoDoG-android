package com.blue.walking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class ChatMenuActivity extends AppCompatActivity {

    Button btn1;
    Button btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_menu);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀바 사라짐

        // TODO: 약속하기 버튼을 누르면 약속하기 엑티비티로 이동 (아직 확인 안해봄)
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(ChatMenuActivity.this, ChatPromiseActivity.class);
                startActivity(intent);
            }
        });

    }
}