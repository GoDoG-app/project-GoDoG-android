package com.blue.walking;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.PetApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.Pet;
import com.blue.walking.model.PetList;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WalkingNoteActivity extends AppCompatActivity {
    TextView txtName;  // 강아지 이름 + ~의 산책기록
    ImageView imgPet;  // 강아지 사진
    TextView txtCount;  // 총 횟수
    TextView txtDistance;  // 총 거리
    TextView txtTime;   // 총 시간
    TextView txtDate;   // 산책일지 날짜
    CalendarView calendarView;  // 달력
    RecyclerView recyclerView;  // 산책일지 목록 띄우는 리사이클러뷰

    String token;
    ArrayList<Pet> petArrayList = new ArrayList<>();

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking_note);

        txtName = findViewById(R.id.txtName);
        txtCount = findViewById(R.id.txtCount);
        txtDistance = findViewById(R.id.txtDistance);
        txtTime = findViewById(R.id.txtTime);
        txtDate = findViewById(R.id.txtDate);
        calendarView = findViewById(R.id.calendarView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        imgPet = findViewById(R.id.imgPet);
        imgPet.setClipToOutline(true);  // 둥근 테두리 적용

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // 산책 기록 펫 정보 띄우기
        /** 내 펫 정보 API */
        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        PetApi api = retrofit.create(PetApi.class);

        Log.i("pet","내 펫 정보 API 실행");

        // 유저 토큰
        SharedPreferences sp = this.getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString(Config.ACCESS_TOKEN, "");

        Call<PetList> call = api.petInfo("Bearer " + token);
        call.enqueue(new Callback<PetList>() {
            @Override
            public void onResponse(Call<PetList> call, Response<PetList> response) {
                if (response.isSuccessful()) {

                    petArrayList.clear(); // 초기화

                    // 서버에서 받아온 데이터를 리스트에 넣고, 각각 적용시키기
                    PetList petList = response.body();
                    petArrayList.addAll(petList.items);

                    if (petArrayList.size() != 0) {
                        // 리스트의 사이즈가 0이 아닐때만 화면에 적용 실행

                        Log.i("pet", "펫 정보 불러오기 완료");
                        txtName.setText(petArrayList.get(0).petName + "의 산책 기록");
                        Glide.with(WalkingNoteActivity.this).load(petArrayList.get(0).petProUrl).into(imgPet);

                    } else {
                        return;
                    }

                } else {
                    Log.i("pet","펫 정보 불러오기 실패");
                }
            }
            @Override
            public void onFailure(Call<PetList> call, Throwable t) {
                Log.i("pet","펫 정보 불러오기 실패");
            }
        });

        // 캘린더 뷰에서 날짜 클릭하면 해당 년도와 월을 보여줌
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                month = month+1;
                String month2;
                if (month < 10){
                    month2 = "0"+month;
                }else {
                    month2 = ""+month;
                }

                String date = year + "." + (month2);
                txtDate.setText(date+" 산책일지");
            }
        });

        bottomNavigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (WalkingNoteActivity.this, WalkingRecoActivity.class);
                startActivity(intent);
            }
        });

    }

}