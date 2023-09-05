package com.blue.walking;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.PetApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.Pet;
import com.blue.walking.model.PetList;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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
//    ImageView imgWalkPet2;  // 산책중인 친구2
//    TextView txtWalkPet2;  // 산책중인 친구 이름2

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


    String token;  // 토큰
    ArrayList<Pet> petArrayList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        txtStart = rootView.findViewById(R.id.txtStart);

        imgPet = rootView.findViewById(R.id.imgPet);
        imgPet.setClipToOutline(true);  // 둥근 테두리 적용

        imgPlace1 = rootView.findViewById(R.id.imgPlace1);
        imgPlace1.setClipToOutline(true);  // 둥근 테두리 적용

        imgPlace2 = rootView.findViewById(R.id.imgPlace2);
        imgPlace2.setClipToOutline(true);  // 둥근 테두리 적용

        imgWalkPet1 = rootView.findViewById(R.id.imgWalkPet1);
        imgWalkPet1.setClipToOutline(true);  // 둥근 테두리 적용

//        imgWalkPet2 = rootView.findViewById(R.id.imgWalkPet2);
//        imgWalkPet2.setClipToOutline(true);  // 둥근 테두리 적용

        imgFollowPet1 = rootView.findViewById(R.id.imgFollowPet1);
        imgFollowPet1.setClipToOutline(true);  // 둥근 테두리 적용

        imgFollowPet2 = rootView.findViewById(R.id.imgFollowPet2);
        imgFollowPet2.setClipToOutline(true);  // 둥근 테두리 적용

        imgFollowPet3 = rootView.findViewById(R.id.imgFollowPet3);
        imgFollowPet3.setClipToOutline(true);  // 둥근 테두리 적용


        /** 내 펫 정보 API */
        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        PetApi api = retrofit.create(PetApi.class);

        Log.i("pet", "내 펫 정보 API 실행");

        // 유저 토큰
        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString(Config.ACCESS_TOKEN, "");

        Call<PetList> call = api.petInfo("Bearer " + token);
        call.enqueue(new Callback<PetList>() {
            @Override
            public void onResponse(Call<PetList> call, Response<PetList> response) {
                if (response.isSuccessful()) {

                    // 서버에서 받아온 데이터를 리스트에 넣고, 각각 적용시키기
                    PetList petList = response.body();
                    petArrayList.addAll(petList.items);

                    if (petArrayList.size() != 0) {
                        // 리스트의 사이즈가 0이 아닐때만 화면에 적용 실행

                        Log.i("pet", "펫 정보 불러오기 완료");

                        txtStart.setText(petArrayList.get(0).petName + "와 산책을 시작해보세요~");
                        Glide.with(getActivity()).load(petArrayList.get(0).petProUrl).into(imgPet);

                    } else {
                        return;
                    }

                } else {
                    Log.i("pet", "펫 정보 불러오기 실패");
                }
            }

            @Override
            public void onFailure(Call<PetList> call, Throwable t) {
                Log.i("pet", "펫 정보 불러오기 실패");
            }
        });


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

        imgPlace1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Walking_3 walking_3 = new Walking_3();
                // 프레그먼트 화면을 walking_3 로 활성화
                transaction.replace(R.id.containers, walking_3);
                transaction.commit();
            }
        });

        imgPlace2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Walking_3 walking_3 = new Walking_3();
                // 프레그먼트 화면을 walking_3 로 활성화
                transaction.replace(R.id.containers, walking_3);
                transaction.commit();
            }
        });

        imgFollowPet1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 친구 프로필로 이동
                Intent intent;
                intent = new Intent(getActivity(), FriendActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}