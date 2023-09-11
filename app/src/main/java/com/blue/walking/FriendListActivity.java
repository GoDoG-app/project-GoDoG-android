package com.blue.walking;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blue.walking.adapter.FriendListAdapter;
import com.blue.walking.api.FriendsAPi;
import com.blue.walking.api.NetworkClient;
import com.blue.walking.config.Config;
import com.blue.walking.model.Friends;
import com.blue.walking.model.FriendsInfo;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FriendListActivity extends AppCompatActivity {

    ImageView imgBack;
    TextView txtTitle;

    RecyclerView recyclerView;
    FriendListAdapter adapter;
    ArrayList<FriendsInfo> friendsInfoArrayList = new ArrayList<>();

    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        imgBack = findViewById(R.id.imgBack);
        txtTitle = findViewById(R.id.txtTitle);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(FriendListActivity.this));

        // 토큰 발급
        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString(Config.ACCESS_TOKEN, "");


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("ONRESUME","ONRESUME 실행됨");


        FriendList();


    }

    private void FriendList(){

        friendsInfoArrayList.clear();

        Retrofit retrofit = NetworkClient.getRetrofitClient(FriendListActivity.this);

        FriendsAPi api = retrofit.create(FriendsAPi.class);

        Call<Friends> call = api.getFriends("Bearer " + token);

        call.enqueue(new Callback<Friends>() {
            @Override
            public void onResponse(Call<Friends> call, Response<Friends> response) {

                if(response.isSuccessful()){



                    Friends friends = response.body();

                    friendsInfoArrayList.addAll(friends.items);
                    adapter = new FriendListAdapter(FriendListActivity.this,friendsInfoArrayList);

                    recyclerView.setAdapter(adapter);

                }


            }

            @Override
            public void onFailure(Call<Friends> call, Throwable t) {

            }
        });

    }

}
