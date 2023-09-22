package com.blue.walking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.blue.walking.adapter.PromiseListAdapter;
import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.PostApi;
import com.blue.walking.api.PromiseApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.PostList;
import com.blue.walking.model.Promise;
import com.blue.walking.model.PromiseList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PromiseListActivity extends AppCompatActivity {

    ImageView imgBack;
    RecyclerView recyclerView;

    String token;
    ArrayList<Promise> promiseArrayList = new ArrayList<>();
    PromiseListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promise_list);

        imgBack = findViewById(R.id.imgBack);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /** 약속목록 API */
        Log.i("api", "약속목록 api 실행");
        Retrofit retrofit = NetworkClient.getRetrofitClient(PromiseListActivity.this);

        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString(Config.ACCESS_TOKEN, "");

        PromiseApi api = retrofit.create(PromiseApi.class);

        Call<PromiseList> call = api.promiselist("Bearer "+token);
        call.enqueue(new Callback<PromiseList>() {
            @Override
            public void onResponse(Call<PromiseList> call, Response<PromiseList> response) {
                if (response.isSuccessful()){
                    Log.i("Call", "서버 실행 성공");

                    PromiseList promiseList = response.body();

                    promiseArrayList.addAll(promiseList.items);
                    adapter = new PromiseListAdapter(PromiseListActivity.this, promiseArrayList);

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PromiseListActivity.this);
                    recyclerView.setLayoutManager(layoutManager);

                    recyclerView.setAdapter(adapter);
                } else{
                    Log.i("Call", "서버 실행 실패");
                }
            }

            @Override
            public void onFailure(Call<PromiseList> call, Throwable t) {
                Log.i("Call", "서버 실행 실패");
            }
        });



        // 뒤로가기
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}