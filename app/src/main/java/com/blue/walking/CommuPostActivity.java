package com.blue.walking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class CommuPostActivity extends AppCompatActivity {

    /** 화면뷰 */
    ImageView imgPet;  // 프로필 사진
    TextView txtPetName;  // 이름
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commu_post);


    }
}