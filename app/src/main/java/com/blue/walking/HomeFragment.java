package com.blue.walking;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    ImageView imgPet;   // 강아지 사진
    ImageView imgStart; // 산책 시작 버튼
    TextView txtStart;  // 강아지 이름+산책을 시작해보세요~
    TextView txtCount;  // 총 횟수
    TextView txtDistance;  // 총 거리
    TextView txtTime;   // 총 시간

    ImageView imgPlace1;  // 산책로 추천1
    TextView txtPlaceName1;  // 산책로 추천지 이름1
    TextView txtPlace1;   // 산책로 추천지 주소1
    ImageView imgPlace2;  // 산책로 추천2
    TextView txtPlaceName2;  // 산책로 추천지 이름2
    TextView txtPlace2;   // 산책로 추천지 주소2

    ImageView imgWalkPet1;  // 산책중인 친구1
    TextView txtWalkPet1;  // 산책중인 친구 이름1
    ImageView imgWalkPet2;  // 산책중인 친구2
    TextView txtWalkPet2;  // 산책중인 친구 이름2

    ImageView imgLoop;  // 추천 친구 새로고침
    ImageView imgFollowPet1;  // 추천 친구1
    TextView txtFollowPet1;   // 추천 친구 이름1
    ImageView imgFollowPet2;  // 추천 친구2
    TextView txtFollowPet2;   // 추천 친구 이름2
    ImageView imgFollowPet3;  // 추천 친구3
    TextView txtFollowPet3;   // 추천 친구 이름3

    EditText editSearch;  // 유저 검색

    ImageView imgCommunity;  // 커뮤니티 +
    TextView txtCommunity1;  // 커뮤니티 글1
    TextView txtCommunity2;  // 커뮤니티 글2
    TextView txtCommuCount1; // 커뮤니티 글1의 댓글? 카운트 수
    TextView txtCommuCount2; // 커뮤니티 글2의 댓글? 카운트 수

    ImageView imgNotices;  // 공지사항 +
    TextView txtNotices1;  // 공지사항 글1
    TextView txtNotices2;  // 공지사항 글2

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);


        imgPet = rootView.findViewById(R.id.imgPet);
        imgPet.setClipToOutline(true);  // 둥근 테두리 적용

        imgPlace1 = rootView.findViewById(R.id.imgPlace1);
        imgPlace1.setClipToOutline(true);  // 둥근 테두리 적용

        imgPlace2 = rootView.findViewById(R.id.imgPlace2);
        imgPlace2.setClipToOutline(true);  // 둥근 테두리 적용

        imgWalkPet1 = rootView.findViewById(R.id.imgWalkPet1);
        imgWalkPet1.setClipToOutline(true);  // 둥근 테두리 적용

        imgWalkPet2 = rootView.findViewById(R.id.imgWalkPet2);
        imgWalkPet2.setClipToOutline(true);  // 둥근 테두리 적용

        imgFollowPet1 = rootView.findViewById(R.id.imgFollowPet1);
        imgFollowPet1.setClipToOutline(true);  // 둥근 테두리 적용

        imgFollowPet2 = rootView.findViewById(R.id.imgFollowPet2);
        imgFollowPet2.setClipToOutline(true);  // 둥근 테두리 적용

        imgFollowPet3 = rootView.findViewById(R.id.imgFollowPet3);
        imgFollowPet3.setClipToOutline(true);  // 둥근 테두리 적용


        imgStart = rootView.findViewById(R.id.imgStart);
        imgStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** Fragment 끼리 이동하기 위해서는 Activity 내 Fragment manager 를 통해 이동하게 되며,
                 * Activity 위에서 이동하기 때문에 intent 가 아닌 메소드를 통해서 이동하게 된다. */

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                WalkingFragment walkingFragment = new WalkingFragment();
                // 프레그먼트 화면을 walkingFragment 로 활성화
                transaction.replace(R.id.containers, walkingFragment);
                // 꼭 commit 을 해줘야 바뀜
                transaction.commit();
            }
        });


        return rootView;
    }
}