package com.blue.walking;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
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

import com.blue.walking.adapter.CommnetAdapter;
import com.blue.walking.adapter.CommuAdapter;
import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.PostApi;
import com.blue.walking.api.UserApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.Firebase;
import com.blue.walking.model.PetList;
import com.blue.walking.model.Post;
import com.blue.walking.model.ResultRes;
import com.blue.walking.model.User;
import com.blue.walking.model.UserInfo;
import com.blue.walking.model.UserList;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Comment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CommuPostActivity extends AppCompatActivity {

    /**
     * 화면뷰
     */
    ImageView imgUser;  // 프로필 사진
    TextView txtUserName;  // 이름
    TextView txtPlace;  // 유저 지역 이름
    TextView txtTime;   // 업로드 일시
    TextView txtContent;  // 내용
    ImageView imgContent; // 이미지
    ImageView imgLike;  // 좋아요
    TextView txtLike;  // 좋아요 수
    TextView txtComment;  // 댓글 수
    EditText editComment;  // 댓글 작성
    Button btnComment;   // 댓글 전송
    RecyclerView recyclerView;  // 댓글 리스트
    CommnetAdapter adapter;
    ArrayList<Firebase> firebaseArrayList = new ArrayList<>();

    // 시간 로컬타임 변수들을 멤버변수로 뺌
    SimpleDateFormat sf;
    SimpleDateFormat df;

    String token;

    ArrayList<UserInfo> userInfoArrayList = new ArrayList<>();

    String userNickname;
    String userAddress;
    String userImgUrl;

    Post post;
    int postId;

    FirebaseFirestore db;
    Firebase firebase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commu_post);

        imgUser = findViewById(R.id.imgUser);
        txtUserName = findViewById(R.id.txtUserName);
        txtPlace = findViewById(R.id.txtPlace);
        txtTime = findViewById(R.id.txtTime);
        txtContent = findViewById(R.id.txtContent);
        imgContent = findViewById(R.id.imgContent);
        imgLike = findViewById(R.id.imgLike);
        txtLike = findViewById(R.id.txtLike);
        txtComment = findViewById(R.id.txtComment);
        editComment = findViewById(R.id.editComment);
        btnComment = findViewById(R.id.btnComment);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(CommuPostActivity.this));

        imgContent.setClipToOutline(true);  // 둥근 테두리 적용
        imgUser.setClipToOutline(true);  // 둥근 테두리 적용


        // 카드뷰를 누르면 해당 게시물의 내용을 상세보기로 이동
        // CommuAdapter 에서 보내준걸 받아옴
        post = (Post) getIntent().getSerializableExtra("post");
        postId = post.post_id;
        // 화면 셋팅
        txtUserName.setText(post.user_nickname);
        txtContent.setText(post.postContent);
        txtPlace.setText(post.user_region);
        txtLike.setText("좋아요 " + post.post_likes_count);
//        txtComment.setText("댓글 "+);
        Glide.with(CommuPostActivity.this).load(post.postImgUrl).into(imgContent);
        Glide.with(CommuPostActivity.this).load(post.user_profile_image).into(imgUser);

        if (post.isLike == 0) {
            // 좋아요가 0 이면 빈 하트 사진으로
            imgLike.setImageResource(R.drawable.baseline_favorite_border_24);

        } else if (post.isLike == 1) {
            // 좋아요가 1이면 채워진 하트 사진으로
            imgLike.setImageResource(R.drawable.baseline_favorite_24);
        }

        sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm");
        sf.setTimeZone(TimeZone.getTimeZone("UTC")); // UTC 글로벌 시간
        df.setTimeZone(TimeZone.getDefault());

        try {
            Date date = sf.parse(post.createdAt); // 자바가 이해하는 시간으로 바꾸기
            String localTime = df.format(date); // 자바가 이해한 시간을 사람이 이해할 수 있는 시간으로 바꾸기
            txtTime.setText(localTime.substring(6)); // 년 제외 몇월 몇일 몇시

        } catch (ParseException e) {
            Log.i("walking", e.toString());
        }

        // 파이어베이스 객체 생성
        db = FirebaseFirestore.getInstance();

        // 어댑터 초기화
        adapter = new CommnetAdapter(CommuPostActivity.this, firebaseArrayList);
        recyclerView.setAdapter(adapter);


        // 채팅 전송 버튼을 클릭할 때
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Retrofit retrofit = NetworkClient.getRetrofitClient(CommuPostActivity.this);

                // 인터페이스를 클래스로 바꿈
                UserApi api = retrofit.create(UserApi.class);

                SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                token = sp.getString(Config.ACCESS_TOKEN, "");

                Call<UserList> call = api.getUserInfo("Bearer " + token);

                call.enqueue(new Callback<UserList>() {
                    @Override
                    public void onResponse(Call<UserList> call, Response<UserList> response) {

                        if (response.isSuccessful()) {

                            UserList userList = response.body();
                            userInfoArrayList.addAll(userList.info);

                            userNickname = userInfoArrayList.get(0).userNickname;
                            userAddress = userInfoArrayList.get(0).userAddress;
                            userImgUrl = userInfoArrayList.get(0).userImgUrl;

                            // 데이터 한 번 초기화
                            firebaseArrayList.clear();

                            String commentContent = editComment.getText().toString().trim();

                            if (!commentContent.isEmpty()) {
                                // 댓글을 Firebase Firestore에 추가
                                addCommentToFirestore(commentContent);
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<UserList> call, Throwable t) {

                    }
                });


            }

        });


        loadMessages();
        }

    private void addCommentToFirestore(String commentContent) {

        // 현재 시간 정보 가져오기
        Timestamp timestamp = Timestamp.now();


        // Firebase Firestore에 댓글 추가
        Firebase comment = new Firebase(userNickname, userImgUrl, userAddress, commentContent, timestamp, timestamp);
        comment.postId = postId;
        db = FirebaseFirestore.getInstance();
        db.collection("comments")
                .add(comment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // 댓글 추가 성공
                        editComment.setText(""); // 댓글 입력창 초기화
                        // TODO: 댓글 추가 후 화면 갱신 또는 필요한 작업 수행
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 댓글 추가 실패
                        Log.e(TAG, "댓글을 Firebase Firestore에 추가하는 중 오류 발생", e);
                    }
                });

    }

    public void loadMessages() {
        db.collection("comments")
                .orderBy("serverTimestamp", Query.Direction.ASCENDING)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                // sendMessages에서 설명 했던 대로 serverTimeStamp를 사용하면
                // second 아래 단위 까지 정밀하게 orderBy가 가능하다
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        // onEvent 데이터베이스의 데이터가 변경되었을 때


                        if (e != null) {
                            Toast.makeText(CommuPostActivity.this,
                                    "message Load Error: " + e,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // 리스트 초기화
                        firebaseArrayList.clear();
                        // 문서의 쿼리스냅샷 가져옴
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            firebase = document.toObject(Firebase.class);
//                            Log.i("테스트",firebase.imgUrl);
                            if (firebase != null) {
                                // 리스트에 추가
                                firebaseArrayList.add(firebase);
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
