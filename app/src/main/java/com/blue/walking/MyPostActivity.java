package com.blue.walking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MyPostActivity extends AppCompatActivity {

    /** 화면뷰 */
    ImageView imgBack;
    BottomNavigationView bottomNavigationView;

    /** 프레그먼트 */
    MyPostFragment_1 myPostFragment_1;
    MyPostFragment_2 myPostFragment_2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        imgBack = findViewById(R.id.imgBack);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        myPostFragment_1 = new MyPostFragment_1();
        myPostFragment_2 = new MyPostFragment_2();

        // 초기화면을 작성글로 설정
        getSupportFragmentManager().beginTransaction().replace(R.id.containers, myPostFragment_1).commit();

        // 탭바 기능 구현(프레그먼트 화면 띄우기)
        NavigationBarView navigationBarView = findViewById(R.id.bottomNavigationView);
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();
                if (itemId == R.id.post) {
                    // 작성글
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, myPostFragment_1)
                            .commit();
                    return true;

                } else if (itemId == R.id.Comment) {
                    // 댓글
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, myPostFragment_2)
                            .commit();
                    return true;

                }
                return false;
            }
        });
    }
}