package com.blue.walking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blue.walking.config.Config;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.skt.tmap.TMapPoint;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /** 화면뷰 */
    BottomNavigationView bottomNavigationView;

    /** 프레그먼트 */
    HomeFragment homeFragment;
    CommunityFragment communityFragment;
    WalkingFragment walkingFragment;
    ChatFragment chatFragment;
    UserFragment userFragment;

    String token;

    ArrayList<TMapPoint> linePointList1 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linePointList1 = (ArrayList<TMapPoint>) getIntent().getSerializableExtra("put");
        Log.i("test", linePointList1+"");


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        homeFragment = new HomeFragment();
        communityFragment = new CommunityFragment();
        walkingFragment = new WalkingFragment();
        chatFragment = new ChatFragment();
        userFragment = new UserFragment();

        // 초기화면을 홈으로 설정
        getSupportFragmentManager().beginTransaction().replace(R.id.containers, homeFragment).commit();

        // 탭바 기능 구현(프레그먼트 화면 띄우기)
        NavigationBarView navigationBarView = findViewById(R.id.bottomNavigationView);
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    // 홈
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, homeFragment)
                            .commit();
                    return true;

                } else if (itemId == R.id.community) {
                    // 커뮤니티
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, communityFragment)
                            .commit();
                    return true;

                } else if (itemId == R.id.walking) {
                    // 산책하기
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, walkingFragment)
                            .commit();
                    return true;

                } else if (itemId == R.id.chat) {
                    // 채팅
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, chatFragment)
                            .commit();
                    return true;

                } else if (itemId == R.id.user) {
                    // 내 정보
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, userFragment)
                            .commit();
                    return true;
                }
                return false;
            }
        });



        // 토큰 발급
        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString(Config.ACCESS_TOKEN, "");

//        Log.i("token", "로그인 토큰 정보");
//        Log.i("token", token);

        // 토큰 확인
        if(token.isEmpty()){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);

            finish();
            return;
        }

    }



}