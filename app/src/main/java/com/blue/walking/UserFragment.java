package com.blue.walking;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    ImageView imgPetAddition;  // 반려가족 추가+

    ImageView imgPet;  // 동물 프로필 사진
    TextView petNickname;  // 동물 이름
    TextView petInfo;  // 동물 성별과 나이
    TextView petComment;  // 동물 한 줄 소개
    TextView petUpdate;  // 동물 프로필 수정

    Button btnFollowList;  // 산책 파트너 목록
    Button btnWalkList;    // 산책 기록
    Button btnCommuList;   // 내가 쓴 글(커뮤니티)

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

        imgPetAddition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 반려가족 추가
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


        return rootView;
    }
}