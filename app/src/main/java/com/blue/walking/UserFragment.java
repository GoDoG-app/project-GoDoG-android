package com.blue.walking;

import static android.content.Context.MODE_PRIVATE;

import static java.util.Calendar.getInstance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.PetApi;
import com.blue.walking.api.UserApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.Pet;
import com.blue.walking.model.PetList;
import com.blue.walking.model.UserInfo;
import com.blue.walking.model.UserList;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    /** 화면뷰 */
    ImageView imgSetting;  // 설정 아이콘

    TextView userNickname;  // 유저 닉네임
    TextView userInfo;   // 유저 성별과 나이
    TextView userPlace;  // 유저 지역
    TextView userComment;  // 유저 한 줄 소개
    TextView userUpdate;  // 유저 프로필 수정
    ImageView imgUser;  // 유저 프로필 사진

    TextView userTemp;  // 온도 숫자 표시
    ProgressBar progressBar;  // 온도 표시

    TextView userScore1;  // 내가 받은 후기1
    TextView userScore2;  // 내가 받은 후기2

    ImageView imgPetAddition;  // 반려가족 등록+

    ImageView imgPet;  // 동물 프로필 사진
    TextView petNickname;  // 동물 이름
    TextView petInfo;  // 동물 성별과 나이
    TextView petComment;  // 동물 한 줄 소개
    TextView petUpdate;  // 동물 프로필 수정

    Button btnFollowList;  // 산책 파트너 목록
    Button btnWalkList;    // 산책 기록
    Button btnCommuList;   // 내가 쓴 글(커뮤니티)


    String token;  // 토큰
    ArrayList<Pet> petArrayList = new ArrayList<>();
    ArrayList<UserInfo> userInfoArrayList= new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_user, container, false);

        imgSetting = rootView.findViewById(R.id.imgSetting);
        userNickname = rootView.findViewById(R.id.userNickname);
        userInfo = rootView.findViewById(R.id.userInfo);
        userPlace = rootView.findViewById(R.id.userPlace);
        userComment = rootView.findViewById(R.id.userComment);
        userUpdate = rootView.findViewById(R.id.userUpdate);
        userTemp = rootView.findViewById(R.id.userTemp);
        userScore1 = rootView.findViewById(R.id.userScore1);
        userScore2 = rootView.findViewById(R.id.userScore2);

        imgUser = rootView.findViewById(R.id.imgUser);
        imgUser.setClipToOutline(true);  // 둥근 테두리 적용

        imgPet = rootView.findViewById(R.id.imgPet);
        imgPet.setClipToOutline(true);  // 둥근 테두리 적용

        petNickname = rootView.findViewById(R.id.petNickname);
        petInfo = rootView.findViewById(R.id.petInfo);
        petComment = rootView.findViewById(R.id.petComment);
        petUpdate = rootView.findViewById(R.id.petUpdate);
        imgPetAddition = rootView.findViewById(R.id.imgPetAddition);

        btnFollowList = rootView.findViewById(R.id.btnFollowList);
        btnWalkList = rootView.findViewById(R.id.btnWalkList);
        btnCommuList = rootView.findViewById(R.id.btnCommuList);

        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setProgress((int) 36.5);
        // 프로그래스바(온도) 초기값
        // (소숫점을 붙이니 int 로만 가능한거 같음. (float)를 해봐도 int 로 고쳐진다)


        // 정보 수정하고 나오면 바로 반영될 수 있도록 자동 새로고침
        onResume();


        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });

        imgPetAddition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 반려가족 등록
                Intent intent;
                intent = new Intent(getActivity(), PetRegisterActivity.class);
                // fragment 에서는 this 사용이 불가능해서 getActivity 를 이용
                startActivity(intent);
            }
        });

        petUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 반려가족 수정
                Intent intent;
                intent = new Intent(getActivity(), PetUpdateActivity.class);
                startActivity(intent);
            }
        });

        userUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 프로필 수정
                Intent intent;
                intent = new Intent(getActivity(), UserUpdateActivity.class);
                startActivity(intent);
            }
        });

        btnWalkList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 산책 기록 보기
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Walking_2 walking_2 = new Walking_2();
                // 프레그먼트 화면을 walkingFragment 로 활성화
                transaction.replace(R.id.containers, walking_2);
                // 꼭 commit 을 해줘야 바뀜
                transaction.commit();
            }
        });

        btnCommuList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 내가 쓴 커뮤니티 글 보기
                Intent intent;
                intent = new Intent(getActivity(), MyPostActivity.class);
                startActivity(intent);
            }
        });

        btnFollowList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 산책 파트너(친구) 목록 보기
                Intent intent;
                intent = new Intent(getActivity(), FriendListActivity.class);
                startActivity(intent);

            }
        });

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        /** API 호출 */
        userInfoApi();
        petInfoApi();
    }

    private void petInfoApi() {
        /** 내 펫 정보 API */
        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        PetApi api = retrofit.create(PetApi.class);

        Log.i("pet","내 펫 정보 API 실행");

        // 유저 토큰
        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString(Config.ACCESS_TOKEN, "");

        Call<PetList> call = api.petInfo("Bearer " + token);
        call.enqueue(new Callback<PetList>() {
            @Override
            public void onResponse(Call<PetList> call, Response<PetList> response) {
                if (response.isSuccessful()) {

                    petArrayList.clear(); // 초기화

                    // 서버에서 받아온 데이터를 리스트에 넣고, 각각 적용시키기
                    PetList petList = response.body();
                    petArrayList.addAll(petList.items);

                    if (petArrayList.size() != 0) {
                        // 리스트의 사이즈가 0이 아닐때만 화면에 적용 실행

                        Log.i("pet", "펫 정보 불러오기 완료");

                        String gender;  // 성별 확인
                        if (Integer.valueOf(petArrayList.get(0).petGender) == 0) {
                            gender = "여";
                        } else {
                            gender = "남";
                        }

                        petNickname.setText(petArrayList.get(0).petName);
                        petInfo.setText("(" + gender + ", " + petArrayList.get(0).petAge + "살)");
                        petComment.setText(petArrayList.get(0).oneliner);
                        Glide.with(UserFragment.this).load(petArrayList.get(0).petProUrl).into(imgPet);

                    } else {
                        return;
                    }

                } else {
                    Log.i("pet","펫 정보 불러오기 실패");
                }
            }

            @Override
            public void onFailure(Call<PetList> call, Throwable t) {
                Log.i("pet","펫 정보 불러오기 실패");
            }
        });
    }


    private void userInfoApi() {
        /** 내 정보 API */
        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        UserApi api = retrofit.create(UserApi.class);

        Log.i("user","내 정보 API 실행");

        // 유저 토큰
        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString(Config.ACCESS_TOKEN, "");

        Call<UserList> call = api.getUserInfo("Bearer " + token);
        call.enqueue(new Callback<UserList>() {
            @Override
            public void onResponse(Call<UserList> call, Response<UserList> response) {
                if (response.isSuccessful()){

                    userInfoArrayList.clear(); // 초기화

                    UserList userList = response.body();
                    userInfoArrayList.addAll(userList.info);

                    // 서버에서 받아온 데이터를 리스트에 넣고, 각각 적용시키기
                    if (userInfoArrayList.size() != 0) {
                        // 리스트의 사이즈가 0이 아닐때만 화면에 적용 실행

                        Log.i("user", "내 정보 불러오기 완료");

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

                        if (userInfoArrayList.get(0).userImgUrl == null){
                            Glide.with(UserFragment.this).load(R.drawable.group_26).into(imgUser);

                        } else {
                            Glide.with(UserFragment.this).load(userInfoArrayList.get(0).userImgUrl).into(imgUser);
                        }

                        userNickname.setText(userInfoArrayList.get(0).userNickname);
                        userInfo.setText("(" + gender + ", " + strAge + ")");
                        userComment.setText(userInfoArrayList.get(0).userOneliner);
                        userPlace.setText(userInfoArrayList.get(0).userAddress);

                    } else {
                        return;
                    }
                } else {
                    Log.i("user", "내 정보 불러오기 실패");
                }
            }

            @Override
            public void onFailure(Call<UserList> call, Throwable t) {
                Log.i("user", "내 정보 불러오기 실패");
            }
        });
    }



}