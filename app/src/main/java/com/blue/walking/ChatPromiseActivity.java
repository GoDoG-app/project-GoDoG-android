package com.blue.walking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatPromiseActivity extends AppCompatActivity {

    /** 화면뷰 */
    ImageView imgBack;  // 뒤로가기
    Button btnDate;   // 날짜 선택
    Button btnTime;   // 시간 선택
    TextView txtPlace;  // 장소 선택
    Button btnPromise;  // 약속 잡기


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_promise);


    }
}