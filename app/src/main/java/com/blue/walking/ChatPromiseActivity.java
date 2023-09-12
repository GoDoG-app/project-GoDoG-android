package com.blue.walking;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blue.walking.api.NetworkStatus;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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

        imgBack = findViewById(R.id.imgBack);

        // 뒤로가기
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}