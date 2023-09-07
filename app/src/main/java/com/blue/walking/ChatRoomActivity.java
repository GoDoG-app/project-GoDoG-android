package com.blue.walking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blue.walking.adapter.ChatAdapter;
import com.blue.walking.adapter.ChatRoomAdapter;
import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.UserApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.Chat;
import com.blue.walking.model.ChatRoom;
import com.blue.walking.model.User;
import com.blue.walking.model.UserInfo;
import com.blue.walking.model.UserList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChatRoomActivity extends AppCompatActivity {

    /** 화면뷰 */
    TextView txtUserName;  // 상대 유저 이름
    EditText editChat;  // 채팅 입력
    ImageView imgSend;  // 채팅 보내기
    ImageView imgBack;  // 뒤로가기
    Button btnPromise;  // 약속 잡기
    String token;
    RecyclerView recyclerView;
    ChatAdapter adapter;
    ArrayList<Chat> chatArrayList = new ArrayList<>();
    ArrayList<UserInfo> userInfoArrayList = new ArrayList<>();
    Firebase firebase;
    FirebaseFirestore db;
    Chat chat;
    UserInfo userInfo2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        imgBack = findViewById(R.id.imgBack);
        btnPromise = findViewById(R.id.btnPromise);
        editChat = findViewById(R.id.editChat);
        imgSend = findViewById(R.id.imgSend);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatRoomActivity.this));


        // 어댑터 초기화
        adapter = new ChatAdapter(ChatRoomActivity.this, chatArrayList);
        recyclerView.setAdapter(adapter);

        // 파이어베이스 초기화
        db = FirebaseFirestore.getInstance();

        // 뒤로가기
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // 약속하기로 이동
        btnPromise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(ChatRoomActivity.this, ChatPromiseActivity.class);
                startActivity(intent);
            }
        });

        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("chat").document().collection("chatList").get().addOnCompleteListener(ChatRoomActivity.this, new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            sendMessage();

                        } else {

                        }
                    }
                });

            }
        });

        loadMessage();


    }
    public void sendMessage(){

        // 입력한 메세지 가져오기
        String chatMessage = editChat.getText().toString().trim();

        if (chatMessage.isEmpty()){
            Snackbar.make(imgSend,
                    "텍스트를 입력하세요.",
                    Snackbar.LENGTH_SHORT).show();
            return;
        } else {
            // 타 유져정보 가져오기
            Retrofit retrofit2 = NetworkClient.getRetrofitClient(ChatRoomActivity.this);
            UserApi api2 = retrofit2.create(UserApi.class);

            Call<UserList> call2 = api2.getFriendInfo(userInfo2.id);

            call2.enqueue(new Callback<UserList>() {
                @Override
                public void onResponse(Call<UserList> call, Response<UserList> response) {
                    if (response.isSuccessful()){
                        UserInfo userInfo2 = userInfoArrayList.get(0);
                        int friendId = userInfo2.id;
                        String friendImgUrl = userInfo2.userImgUrl;
                        String friendNickname = userInfo2.userNickname;
                        chat = new Chat(friendId, friendNickname, friendImgUrl, chatMessage, FieldValue.serverTimestamp().toString());
                    }
                }

                @Override
                public void onFailure(Call<UserList> call, Throwable t) {

                }
            });


            // 유저 정보 가져오기
            Retrofit retrofit = NetworkClient.getRetrofitClient(ChatRoomActivity.this);
            UserApi api = retrofit.create(UserApi.class);

            SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
            token = sp.getString(Config.ACCESS_TOKEN, "");

            Call<UserList> call = api.getUserInfo("Bearer " + token);

            call.enqueue(new Callback<UserList>() {
                @Override
                public void onResponse(Call<UserList> call, Response<UserList> response) {
                    if (response.isSuccessful()) {
                        UserList userList = response.body();
                        ArrayList<UserInfo> userInfoArrayList = userList.info;
                        if (!userInfoArrayList.isEmpty()) {
                            UserInfo userInfo = userInfoArrayList.get(0);

                            String userImgUrl = userInfo.userImgUrl;
                            String userNickname = userInfo.userNickname;
                            int userId = userInfo.id;

                            chat = new Chat(userId, userNickname, userImgUrl, chatMessage, FieldValue.serverTimestamp().toString());


                            // 마지막 초기화
                            editChat.setText("");
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserList> call, Throwable t) {

                }
            });

        }

    }
    public void loadMessage(){

        DocumentReference chatRef = db.collection("chat").document();
        DocumentReference chatMessageRef = db.collection("chat").document().collection("chatMessage").document();

        db.collection("chat").document().collection("chatMessage").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Toast.makeText(ChatRoomActivity.this,
                            "메세지 로드 에러"+error,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // 리스트 초기화
                chatArrayList.clear();

                for (DocumentSnapshot document : value.getDocuments()) {
                    chat = document.toObject(Chat.class);
                    if (chat != null) {
                        // 리스트에 추가
                        chatArrayList.add(chat);
                    }
                }
                // 인덱스는 0부터 시작하기 때문에 마지막 값을 가져오기 위해 -1을 함
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }


}