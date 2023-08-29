package com.blue.walking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

public class CommuUpPostActivity extends AppCompatActivity {

    /** 화면 뷰 */
    ImageView imgBack;  // 뒤로가기
    Spinner spinner;  // 카테고리 목록
    EditText editContent;  // 업로드할 내용 입력
    ImageView imgContent;  // 업로드할 사진 선택
    Button btnUpload;   // 게시글 업로드

//    CommunityFragment communityFragment;

    String[] items = {"전체","일상","정보","산책"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commu_up_post);

        imgBack = findViewById(R.id.imgBack);
        spinner = findViewById(R.id.spinner);

        // 카테고리 목록 띄우기
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // 카테고리 선택하면 실행할 부분
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //이 오버라이드 메소드에서 i 는 몇번째 값이 클릭됬는지 알 수 있음

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }
}