package com.blue.walking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
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

import com.blue.walking.adapter.ChatRoomAdapter;
import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.UserApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.ChatRoom;
import com.blue.walking.model.RandomFriend;
import com.blue.walking.model.UserInfo;
import com.blue.walking.model.UserList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChatRoomActivity extends AppCompatActivity {

    /** 1:1 채팅 화면뷰 */
    TextView txtUserName;  // 상대 유저 이름
    EditText editChat;  // 채팅 입력
    ImageView imgSend;  // 채팅 보내기
    ImageView imgBack;  // 뒤로가기
    Button btnPromise;  // 약속 잡기
    String token;
    RecyclerView recyclerView;
    ChatRoomAdapter adapter;
    ArrayList<ChatRoom> chatRoomArrayList = new ArrayList<>();
    FirebaseFirestore db;
    ChatRoom chatRoom;
    RandomFriend randomFriend;
    int receiverId;
    int id;
    String userImgUrl;
    String userNickname;
    String chatMessage;
    String roomName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        imgBack = findViewById(R.id.imgBack);
        btnPromise = findViewById(R.id.btnPromise);
        editChat = findViewById(R.id.editChat);
        imgSend = findViewById(R.id.imgSend);
        txtUserName = findViewById(R.id.txtUserName);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatRoomActivity.this));

        // 친구 프로필에서 받아온 정보
        randomFriend = (RandomFriend) getIntent().getSerializableExtra("friend");
        txtUserName.setText(randomFriend.nickname);
        receiverId = randomFriend.id;

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

                        userImgUrl = userInfo.userImgUrl;
                        userNickname = userInfo.userNickname;
                        id = userInfo.id;


                        // id 값을 설정한 후에 roomName 초기화
                        if (id<receiverId){
                            roomName = id + "_" + receiverId;
                        } else if(id>receiverId) {
                            roomName = receiverId + "_" + id;
                        }

                        Log.i("roomName1", roomName + "");

                        // 채팅 메시지 로드
                        loadMessages(roomName);

                    }
                }
            }

            @Override
            public void onFailure(Call<UserList> call, Throwable t) {

            }
        });


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
                db.collection("chat").document().collection("chatMessage")
                        .get().addOnCompleteListener(ChatRoomActivity.this, new OnCompleteListener<QuerySnapshot>() {
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


    }

    public void sendMessage(){


        // 입력한 메세지 가져오기
        chatMessage = editChat.getText().toString().trim();

        if (chatMessage.isEmpty()){
            Snackbar.make(imgSend,
                    "텍스트를 입력하세요.",
                    Snackbar.LENGTH_SHORT).show();
            return;
        } else {

            Timestamp timestamp = Timestamp.now();

            Log.i("test", id+userNickname+userImgUrl+chatMessage+timestamp.toDate());

            // 대화 상대의 user id를 배열로 저장
            List<Integer> user = Arrays.asList(id, receiverId);

            // Firestore에 저장할 데이터 생성
            Map<String, Object> chatRoomData = new HashMap<>();
            chatRoomData.put("user", user);
            // Firestore에 데이터를 쓸 때 서버 타임스탬프를 할당
            chatRoom = new ChatRoom(id, userNickname, userImgUrl, chatMessage, timestamp);

            DocumentReference chatDocRef = db.collection("chat").document(roomName);
            Log.i("roomName2", roomName+"");
            // 문서 참조를 사용하여 데이터 설정
            chatDocRef.set(chatRoomData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // 채팅 방 문서 생성 성공
                            // chatMessage 컬렉션에 저장할 채팅 메시지 데이터 생성
                            ChatRoom chatRoom = new ChatRoom(id, userNickname, userImgUrl, chatMessage, timestamp);

                            // chatMessage 컬렉션에 새로운 문서 추가
                            chatDocRef.collection("chatMessage")
                                    .add(chatRoom)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference messageDocRef) {
                                            Log.i("test", "메시지 문서 생성 성공");

                                            // 마지막 초기화
                                            editChat.setText("");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.i("test", "메시지 문서 생성 실패: " + e.getMessage());
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // 채팅 방 문서 생성 실패
                        }
                    });

        }

    }

    // Firestore에서 채팅 메시지를 가져와 리사이클러뷰에 표시하는 함수
    private void loadMessages(String roomName) {
        db = FirebaseFirestore.getInstance();

        // chatMessage 컬렉션 내의 문서를 쿼리합니다.
        db.collection("chat").document(roomName).collection("chatMessage").orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(ChatRoomActivity.this,
                                    "메세지 로드 에러: " + error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // 리스트 초기화
                        chatRoomArrayList.clear();

                        // Firestore에서 가져온 데이터를 chatRoomArrayList에 추가합니다.
                        for (DocumentSnapshot document : value.getDocuments()) {
                            ChatRoom chatRoom = document.toObject(ChatRoom.class);
                            if (chatRoom != null) {
                                chatRoomArrayList.add(chatRoom);
                            }
                        }

                        // 어댑터에 데이터 변경을 알립니다.
                        adapter = new ChatRoomAdapter(ChatRoomActivity.this, chatRoomArrayList);
                        // 어댑터 연결
                        recyclerView.setAdapter(adapter);
                        // 리사이클러뷰 어레이 리스트의 크기의 -1 위치로 이동
                        recyclerView.scrollToPosition(chatRoomArrayList.size()-1);
                        adapter.notifyDataSetChanged();
                    }
                });
    }


//    수정전
//    public void sendMessage(){
//
//        // 입력한 메세지 가져오기
//        chatMessage = editChat.getText().toString().trim();
//
//        if (chatMessage.isEmpty()){
//            Snackbar.make(imgSend,
//                    "텍스트를 입력하세요.",
//                    Snackbar.LENGTH_SHORT).show();
//            return;
//        } else {
//            // 나와 채팅하는 유저정보 받아오기
//            receiverId = randomFriend.id;
//
//            // 유저 정보 가져오기
//            Retrofit retrofit = NetworkClient.getRetrofitClient(ChatRoomActivity.this);
//            UserApi api = retrofit.create(UserApi.class);
//
//            SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
//            token = sp.getString(Config.ACCESS_TOKEN, "");
//
//            Call<UserList> call = api.getUserInfo("Bearer " + token);
//
//            call.enqueue(new Callback<UserList>() {
//                @Override
//                public void onResponse(Call<UserList> call, Response<UserList> response) {
//                    if (response.isSuccessful()) {
//                        UserList userList = response.body();
//                        ArrayList<UserInfo> userInfoArrayList = userList.info;
//                        if (!userInfoArrayList.isEmpty()) {
//                            UserInfo userInfo = userInfoArrayList.get(0);
//
//                            userImgUrl = userInfo.userImgUrl;
//                            userNickname = userInfo.userNickname;
//                            id = userInfo.id;
//                            Log.i("test", id+userNickname+userImgUrl+chatMessage+ serverTimestamp());
//
//
//                            chatRoom = new ChatRoom(id, userNickname, userImgUrl, chatMessage, serverTimestamp().toString());
//                            String roomName = id+"_"+receiverId;
//
//                            // chatMessage 컬렉션에 새로운 문서 추가
//                            db.collection("chat").document(roomName).collection("chatMessage")
//                                    .add(chatRoom)
//                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                        @Override
//                                        public void onSuccess(DocumentReference messageDocRef) {
//                                            Log.i("test", "메시지 문서 생성 성공");
//                                            chatRoom = new ChatRoom(id, userNickname, userImgUrl, chatMessage, serverTimestamp().toString());
//                                            // 마지막 초기화
//                                            editChat.setText("");
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Log.i("test", "메시지 문서 생성 실패: " + e.getMessage());
//                                        }
//                                    });
//
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<UserList> call, Throwable t) {
//
//                }
//            });
//
//        }
//
//    }



}