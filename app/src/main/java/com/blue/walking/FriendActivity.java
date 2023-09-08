package com.blue.walking;

import static java.util.Calendar.getInstance;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.PetApi;
import com.blue.walking.api.UserApi;
import com.blue.walking.model.FriendsInfo;
import com.blue.walking.model.Pet;
import com.blue.walking.model.PetList;
import com.blue.walking.model.RandomFriend;
import com.blue.walking.model.UserInfo;
import com.blue.walking.model.UserList;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FriendActivity extends AppCompatActivity {

    /** 화면뷰 */
    Button btnChat;   // 채팅 보내기
    Button btnFollower;  // 친구 등록

    ImageView imgBack;  // 뒤로가기
    TextView userNickname;  // 유저 닉네임
    TextView userInfo;   // 유저 성별과 나이
    TextView userPlace;  // 유저 지역
    TextView userComment;  // 유저 한 줄 소개
    ImageView imgUser;  // 유저 프로필 사진

    TextView userTemp;  // 온도 숫자 표시
    ProgressBar progressBar;  // 온도 표시
    TextView userScore1;  // 내가 받은 후기1
    TextView userScore2;  // 내가 받은 후기2

    ImageView imgPet;  // 동물 프로필 사진
    TextView petNickname;  // 동물 이름
    TextView petInfo;  // 동물 성별과 나이
    TextView petComment;  // 동물 한 줄 소개

    Button btnWalkList;    // 산책 기록
    Button btnCommuList;   // 게시글(커뮤니티)

    int friendId;
    ArrayList<UserInfo> userInfoArrayList= new ArrayList<>();
    ArrayList<Pet> petArrayList = new ArrayList<>();
    RandomFriend randomFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        btnChat = findViewById(R.id.btnChat);
        imgBack = findViewById(R.id.imgBack);
        imgUser = findViewById(R.id.imgUser);
        imgPet = findViewById(R.id.imgPet);

        // 유저정보
        userNickname = findViewById(R.id.userNickname);
        userInfo = findViewById(R.id.userInfo); // 연령대, 성별
        userPlace = findViewById(R.id.userPlace);
        userComment = findViewById(R.id.userComment);
        userTemp = findViewById(R.id.userTemp); // 매너온도

        // 펫정보
        petNickname = findViewById(R.id.petNickname);
        petInfo = findViewById(R.id.petInfo);
        petComment = findViewById(R.id.petComment);


        // 프로그래스바(온도) 기본값
        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress((int) 36.5);

        imgUser.setClipToOutline(true);  // 둥근 테두리 적용
        imgPet.setClipToOutline(true);  // 둥근 테두리 적용

        // RandomFriend에서 받아온 id
        randomFriend = (RandomFriend) getIntent().getSerializableExtra("Friend");
        friendId = randomFriend.id;


        // 랜덤친구 정보
        randomFriendInfoAPI();

        // 펫정보
        randomFriendPetInfoAPI();



        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 채팅방으로 이동
                Intent intent = new Intent(FriendActivity.this, ChatRoomActivity.class);
                intent.putExtra("randomFriend", randomFriend);
                startActivity(intent);
            }
        });

        // 뒤로가기
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void randomFriendInfoAPI(){

        Retrofit retrofit1 = NetworkClient.getRetrofitClient(FriendActivity.this);
        UserApi api1 = retrofit1.create(UserApi.class);
        Call<UserList> call1 = api1.getFriendInfo(friendId);
        call1.enqueue(new Callback<UserList>() {
            @Override
            public void onResponse(Call<UserList> call, Response<UserList> response) {
                if (response.isSuccessful()){

                    userInfoArrayList.clear(); // 초기화

                    UserList userList = response.body();
                    userInfoArrayList.addAll(userList.info);

                    if (userInfoArrayList.size() != 0){

                        String gender;  // 성별 확인
                        if (Integer.valueOf(userInfoArrayList.get(0).userGender) == 0) {
                            gender = "여";
                        } else {
                            gender = "남";
                        }

                        // 나이 확인
                        int year = Integer.parseInt(userInfoArrayList.get(0).userBirth.substring(0,4));
                        Calendar now = getInstance();
                        int currentYear = now.get(Calendar.YEAR);
                        int age = currentYear - year;  // 현재 년도 - 등록한 년도

                        Log.i("user", String.valueOf(age));

                        String strAge = "";
                        if (age > 10 && age < 20) {
                            strAge = "10대";
                        } else if (age > 20 && age < 30) {
                            strAge = "20대";
                        } else if (age > 30 && age < 40) {
                            strAge = "30대";
                        } else if (age > 40 && age < 50) {
                            strAge = "40대";
                        } else if (age > 50 && age < 60) {
                            strAge = "50대";
                        } else if (age > 60 && age < 70) {
                            strAge = "60대";
                        }

                        userNickname.setText(userInfoArrayList.get(0).userNickname);
                        userInfo.setText("(" + gender + ", " + strAge + ")");
                        userComment.setText(userInfoArrayList.get(0).userOneliner);
                        userPlace.setText(userInfoArrayList.get(0).userAddress);
                        Glide.with(FriendActivity.this)
                                .load(userInfoArrayList.get(0).userImgUrl)
                                .into(imgUser);

                    }

                }
            }

            @Override
            public void onFailure(Call<UserList> call, Throwable t) {

            }
        });

    }
    private void randomFriendPetInfoAPI(){

        Retrofit retrofit2 = NetworkClient.getRetrofitClient(FriendActivity.this);
        PetApi api2 = retrofit2.create(PetApi.class);

        Call<PetList> call2 = api2.getFriendPetInfo(friendId);
        Log.i("test!!", friendId+"");
        call2.enqueue(new Callback<PetList>() {
            @Override
            public void onResponse(Call<PetList> call, Response<PetList> response) {
                if (response.isSuccessful()) {

                    petArrayList.clear();

                    PetList petList = response.body();
                    petArrayList.addAll(petList.items);
                    if (petArrayList.size() != 0) {
                        String gender;  // 성별 확인
                        if (Integer.valueOf(petArrayList.get(0).petGender) == 0) {
                            gender = "여";
                        } else {
                            gender = "남";
                        }

                        petNickname.setText(petArrayList.get(0).petName);
                        petInfo.setText("(" + gender + ", " + petArrayList.get(0).petAge + "살)");
                        petComment.setText(petArrayList.get(0).oneliner);
                        Glide.with(FriendActivity.this).load(petArrayList.get(0).petProUrl).into(imgPet);

                    } else {
                        petNickname.setText("반려가족이 없습니다");
                        petInfo.setText("");
                        petComment.setText("");
                        Glide.with(FriendActivity.this).load(R.drawable.group_26).into(imgPet);
                    }
                }
            }

            @Override
            public void onFailure(Call<PetList> call, Throwable t) {

            }
        });


    }

}