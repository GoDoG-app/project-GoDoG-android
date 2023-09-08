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
import com.blue.walking.model.RandomFriend;
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

import java.lang.reflect.Array;
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
    ChatAdapter adapter;
    ArrayList<Chat> chatArrayList = new ArrayList<>();
    ArrayList<UserInfo> userInfoArrayList = new ArrayList<>();
    Firebase firebase;
    FirebaseFirestore db;
    Chat chat;
    UserInfo userInfo2;
    DocumentReference chatRef;
    DocumentReference chatMessageRef;
    RandomFriend randomFriend;
    int reciverId;
    int senderId;



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
            // 나와 채팅하는 유저정보 받아오기
            randomFriend = (RandomFriend) getIntent().getSerializableExtra("randomFriend");
            reciverId = randomFriend.id;

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
                            senderId = userInfo.id;

                            chat = new Chat(senderId, reciverId, userNickname, userImgUrl, chatMessage, FieldValue.serverTimestamp().toString());

                            chatMessageRef.collection("chat").document()
                                    .collection("chatMessage").document()
                                    .set(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {


                                    // 마지막 초기화
                                    editChat.setText("");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

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

        // 채팅목록 DB
        chatRef = db.collection("chat").document();
        // 1:1 채팅방 DB
        chatMessageRef = db.collection("chat").document().collection("chatMessage").document();

        // db에 저장한 것 가지고오기
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
    public void createChatDocumentAndAddMessage(String receiverId, String senderId, String createdAt, String message, String userImgUrl, String userNickname) {
        // chatRef는 Firestore에서 채팅 데이터에 대한 참조를 나타냅니다.
        // 채팅목록 DB
        chatRef = db.collection("chat").document();
        // 1:1 채팅방 DB
        chatMessageRef = db.collection("chat").document().collection("chatMessage").document();

        // "user" 필드에 추가할 유저 ID 배열을 만듭니다.
        List<String> users = new ArrayList<>();
        users.add(receiverId);
        users.add(senderId);

        // chatRef.document()를 사용하여 새로운 채팅 문서를 생성합니다.
        chatRef = db.collection("chat").document();

        // "user" 필드를 포함하는 데이터 맵을 생성합니다.
        Map<String, Object> chatData = new HashMap<>();
        chatData.put("user", users);

        // chatRef.document()에 "user" 필드를 설정하고 성공/실패 리스너를 추가합니다.
        chatRef.set(chatData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("test", "저장 성공");

                        // 채팅 문서가 생성되었으므로 "chatMessage" 컬렉션에 메시지를 추가합니다.
                        addMessageToChatCollection(chatRef, createdAt, receiverId, senderId, message, userImgUrl, userNickname);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("test", "저장 실패");
                    }
                });
    }

    public void addMessageToChatCollection(DocumentReference chatDocumentRef, String createdAt, String receiverId, String senderId, String message, String userImgUrl, String userNickname) {
        // chatDocumentRef는 Firestore에서 채팅 문서를 나타냅니다.

        // "chatMessage" 컬렉션에 추가할 메시지 데이터를 만듭니다.
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("createdAt", createdAt);
        messageData.put("receiverId", receiverId);
        messageData.put("senderId", senderId);
        messageData.put("message", message);
        messageData.put("userImgUrl", userImgUrl);
        messageData.put("userNickname", userNickname);

        // chatDocumentRef.collection("chatMessage")를 사용하여 "chatMessage" 컬렉션에 새로운 문서를 추가합니다.
        chatDocumentRef.collection("chatMessage").add(messageData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i("test", "메시지 저장 성공");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("test", "메시지 저장 실패");
                    }
                });
    }


}