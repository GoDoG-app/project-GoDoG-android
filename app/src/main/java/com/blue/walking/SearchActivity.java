package com.blue.walking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.blue.walking.adapter.FriendListAdapter;
import com.blue.walking.adapter.SearchUserAdapter;
import com.blue.walking.api.FriendsAPi;
import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.SearchApi;
import com.blue.walking.model.Friends;
import com.blue.walking.model.FriendsInfo;
import com.blue.walking.model.SearchUser;
import com.blue.walking.model.SearchUserItems;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchActivity extends AppCompatActivity {


    ImageView imgBack;
    ImageView imgSearch;
    EditText editSearch;

    String search;

    RecyclerView recyclerView;
    SearchUserAdapter adapter;
    ArrayList<SearchUserItems> searchUserItemsArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        imgBack = findViewById(R.id.imgBack);
        imgSearch = findViewById(R.id.imgSearch);
        editSearch = findViewById(R.id.editSearch);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SearchList();

            }
        });

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        SearchList();
//
//    }

    private void SearchList(){

        searchUserItemsArrayList.clear();

        Retrofit retrofit = NetworkClient.getRetrofitClient(SearchActivity.this);

        SearchApi api = retrofit.create(SearchApi.class);

        search = editSearch.getText().toString().trim();

        Call<SearchUser> call = api.getSearchUser(search);

        call.enqueue(new Callback<SearchUser>() {
            @Override
            public void onResponse(Call<SearchUser> call, Response<SearchUser> response) {

                if(response.isSuccessful()){

                    SearchUser searchUser = response.body();

                    searchUserItemsArrayList.addAll(searchUser.items);
                    adapter = new SearchUserAdapter(SearchActivity.this,searchUserItemsArrayList);

                    recyclerView.setAdapter(adapter);



                }

            }

            @Override
            public void onFailure(Call<SearchUser> call, Throwable t) {

            }
        });


    }

}