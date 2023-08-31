package com.blue.walking;

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

import com.blue.walking.adapter.CommuAdapter;
import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.PostApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.Post;
import com.blue.walking.model.ResultRes;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CommuPostActivity extends AppCompatActivity {

    /** 화면뷰 */
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


    CommuAdapter adapter;
    ArrayList<Post> postArrayList = new ArrayList<>();

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
        Post post = (Post) getIntent().getSerializableExtra("post");

        // 화면 셋팅
        txtUserName.setText(post.user_nickname);
        txtContent.setText(post.postContent);
        txtPlace.setText(post.user_region);
        txtLike.setText("좋아요 "+post.isLike);
        txtComment.setText("댓글 "+post.post_likes_count);
        txtTime.setText(post.createdAt);
        Glide.with(CommuPostActivity.this).load(post.postImgUrl).into(imgContent);
        Glide.with(CommuPostActivity.this).load(post.user_profile_image).into(imgUser);

        if(post.isLike == 0){
            // 좋아요가 0 이면 빈 하트 사진으로
            imgLike.setImageResource(R.drawable.baseline_favorite_border_24);

        } else if(post.isLike == 1){
            // 좋아요가 1이면 채워진 하트 사진으로
            imgLike.setImageResource(R.drawable.baseline_favorite_24);
        }


    }
}