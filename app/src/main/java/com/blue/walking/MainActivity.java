package com.blue.walking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    /** 화면뷰 */
    BottomNavigationView bottomNavigationView;

    /** 프레그먼트 */
    HomeFragment homeFragment;
    CommunityFragment communityFragment;
    WalkingFragment walkingFragment;
    ChatFragment chatFragment;
    UserFragment userFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, homeFragment)
                            .commit();
                    getSupportActionBar().setTitle("홈");
                    return true;

                } else if (itemId == R.id.community) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, communityFragment)
                            .commit();
                    getSupportActionBar().setTitle("커뮤니티");
                    return true;

                } else if (itemId == R.id.walking) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, walkingFragment)
                            .commit();
                    getSupportActionBar().setTitle("산책");
                    return true;

                } else if (itemId == R.id.chat) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, chatFragment)
                            .commit();
                    getSupportActionBar().setTitle("채팅");
                    return true;

                } else if (itemId == R.id.user) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, userFragment)
                            .commit();
                    getSupportActionBar().setTitle("내 정보");
                    return true;
                }
                return false;
            }
        });
    }

}