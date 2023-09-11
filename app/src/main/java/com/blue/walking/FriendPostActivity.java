package com.blue.walking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.blue.walking.model.RandomFriend;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class FriendPostActivity extends AppCompatActivity {

    /** 화면뷰 */
    ImageView imgBack;
    BottomNavigationView bottomNavigationView;

    /** 프레그먼트 */
    FriendPostFragment_1 friendPostFragment_1;
    FriendPostFragment_2 friendPostFragment_2;


    RandomFriend randomFriend;
    int friendId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_post);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        imgBack = findViewById(R.id.imgBack);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // FriendActivity 에게 친구 아이디 값을 받아옴
        randomFriend = (RandomFriend) getIntent().getSerializableExtra("friendId");
        friendId = randomFriend.id;
        Log.i("test", randomFriend.nickname+"");

        // 친구 아이디 값을 프레그먼트에게 각각 보내줌
        Bundle bundle = new Bundle();
        bundle.putInt("friendId", friendId);
        friendPostFragment_1 = new FriendPostFragment_1();
        friendPostFragment_2 = new FriendPostFragment_2();
        friendPostFragment_1.setArguments(bundle);
        friendPostFragment_2.setArguments(bundle);


        // 초기화면을 작성글로 설정
        getSupportFragmentManager().beginTransaction().replace(R.id.containers, friendPostFragment_1).commit();

        // 탭바 기능 구현(프레그먼트 화면 띄우기)
        NavigationBarView navigationBarView = findViewById(R.id.bottomNavigationView);
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();
                if (itemId == R.id.post) {
                    // 친구의 작성글

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, friendPostFragment_1)
                            .commit();
                    return true;

                } else if (itemId == R.id.Comment) {
                    // 친구의 댓글
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, friendPostFragment_2)
                            .commit();
                    return true;

                }
                return false;
            }
        });
    }
}