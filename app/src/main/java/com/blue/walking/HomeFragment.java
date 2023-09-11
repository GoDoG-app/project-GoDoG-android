package com.blue.walking;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blue.walking.adapter.RandomAdapter;
import com.blue.walking.adapter.RecommendAdapter;
import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.PetApi;
import com.blue.walking.api.RandomFriendAPI;
import com.blue.walking.api.UserApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.Park;
import com.blue.walking.model.Pet;
import com.blue.walking.model.PetList;
import com.blue.walking.model.RandomFriend;
import com.blue.walking.model.RandomFriendRes;
import com.blue.walking.model.UserInfo;
import com.blue.walking.model.UserList;
import com.bumptech.glide.Glide;
import com.skt.tmap.TMapData;
import com.skt.tmap.TMapPoint;
import com.skt.tmap.TMapTapi;
import com.skt.tmap.poi.TMapPOIItem;

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

    ImageView imgWalkPet1;  // 산책중인 친구1
    TextView txtWalkPet1;  // 산책중인 친구 이름1
//    ImageView imgWalkPet2;  // 산책중인 친구2
//    TextView txtWalkPet2;  // 산책중인 친구 이름2


    EditText editSearch;  // 유저 검색

    ImageView imgCommunity;  // 커뮤니티 +
    TextView txtCommunity1;  // 커뮤니티 글1
    TextView txtCommunity2;  // 커뮤니티 글2
    TextView txtCommuCount1; // 커뮤니티 글1의 댓글? 카운트 수
    TextView txtCommuCount2; // 커뮤니티 글2의 댓글? 카운트 수
    ImageView imgNotices;  // 공지사항 +
    TextView txtNotices1;  // 공지사항 글1
    TextView txtNotices2;  // 공지사항 글2

    // 산책 추천
    RecyclerView walkingRecyclerView; // 산책 추천
    RecommendAdapter recommendAdapter; // 산책 추천용 리사이클러뷰
    ArrayList<Park> parkArrayList = new ArrayList<>(); // 공원 정보를 저장할 ArrayList


    // 친구추천
    RecyclerView recyclerViewRandom;
    ArrayList<RandomFriend> randomFriendArrayList = new ArrayList<>();
    RandomAdapter randomAdapter;
    int offset = 0;
    int limit = 300;

    String token;  // 토큰
    ArrayList<Pet> petArrayList = new ArrayList<>(); // 펫 정보 가져오기
    ArrayList<UserInfo> userInfoArrayList = new ArrayList<>(); // 유저 정보(주소) 가져오기
    private static final int REQUEST_LOCATION_PERMISSION = 1; // 권한 요청 코드
    double first_lat ; // 처음 위도
    double first_lng ; // 처음 경도
    TMapTapi tMapTapi; // 티맵 사용
    Park park;
    String name;
    // 공원 주소
    String address;
    // 공원 좌표
    TMapPoint lat_lng;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        // TMapView 초기화 및 설정
        tMapTapi = new TMapTapi(getActivity());
        tMapTapi.setSKTmapAuthentication(Config.getAppKey());

        // 권한(permission)상태 확인
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // 권한을 요청하는 다이얼로그 표시
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    REQUEST_LOCATION_PERMISSION); // 권한 요청 코드 사용
        }
        txtStart = rootView.findViewById(R.id.txtStart);

        imgPet = rootView.findViewById(R.id.imgPet);
        imgPet.setClipToOutline(true);  // 둥근 테두리 적용

        imgWalkPet1 = rootView.findViewById(R.id.imgWalkPet1);
        imgWalkPet1.setClipToOutline(true);  // 둥근 테두리 적용

//        imgWalkPet2 = rootView.findViewById(R.id.imgWalkPet2);
//        imgWalkPet2.setClipToOutline(true);  // 둥근 테두리 적용

        // 친구추천 리사이클러뷰 초기화
        recyclerViewRandom = rootView.findViewById(R.id.recyclerViewRandom);
        recyclerViewRandom.setHasFixedSize(true);
        // 리사이클러뷰 가로모드
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewRandom.setLayoutManager(layoutManager);

        // 산책 공원 추천 리사이클러뷰
        walkingRecyclerView = rootView.findViewById(R.id.walkingRecyclerView);
        walkingRecyclerView.setHasFixedSize(true);

        // 리사이클러뷰 세로모드에 맞게 LinearLayoutManager 설정
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);
        walkingRecyclerView.setLayoutManager(layoutManager1);

        tMapTapi.setOnAuthenticationListenerCallback(new TMapTapi.OnAuthenticationListenerCallback() {
            @Override
            public void SKTMapApikeySucceed() {
                // 인증 성공
                // 여기에서 관련 작업을 수행
                Log.v("TMAP KEY 성공 : ", "성공, KEY 인증 완료.");

                // 유저 토큰
                SharedPreferences sp1 = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                token = sp1.getString(Config.ACCESS_TOKEN, "");

                // 유저 정보에서 lat, lng을 가져온다.
                Retrofit retrofit1 = NetworkClient.getRetrofitClient(getActivity());
                UserApi api2 = retrofit1.create(UserApi.class); // API 인터페이스를 구현한 객체를 생성
                Call<UserList> call2 = api2.getUserInfo("Bearer " + token); // 서버에 HTTP 요청을 생성

                // HTTP 요청을 서버로 보냄
                call2.enqueue(new Callback<UserList>() {
                    @Override
                    public void onResponse(Call<UserList> call, Response<UserList> response) {
                        // 서버 응답을 처리하는 코드
                        if (response.isSuccessful()) {

                            userInfoArrayList.clear(); //초기화

                            // 서버에서 받아온 데이터를 리스트에 넣고, 각각 적용
                            UserList userList = response.body();
                            userInfoArrayList.addAll(userList.info);

                            if (userInfoArrayList.size() != 0) {
                                // 리스트의 사이즈가 0이 아닐때만 화면에 적용 실행
                                Log.i("SUCCESS", "유저 정보 불러오기 완료");
                                for (int i = 0; i < userInfoArrayList.size(); i++) {
                                    Log.i("유저 정보", "유저 전체 정보: " + userInfoArrayList.get(i));
//                                    Log.i("유저 이름", "유저 정보 이름: " + userInfoArrayList.get(i).userNickname);
//                                    Log.i("유저 주소", "유저 정보 주소: " + userInfoArrayList.get(i).userAddress);
                                    Log.i("유저 위도", "유저 정보 위도: " + userInfoArrayList.get(i).lat);
                                    Log.i("유저 경도", "유저 정보 경도: " + userInfoArrayList.get(i).lng);
                                }

                                first_lat = userInfoArrayList.get(0).lat;
                                first_lng = userInfoArrayList.get(0).lng;
                                Log.i("유저  위치 ", "유저 위치 :" + first_lat + " " + first_lng);
//
//                                UserInfo userInfo = userInfoArrayList.get(0);
//                                Intent intent = new Intent(getActivity(), Walking3.class);
//                                intent.putExtra("userInfo", userInfo);
//                                Log.i("ㅇ","ㅇ"+userInfo.userAddress);
//                                startActivity(intent);


                                // TMapData 객체 생성
                                TMapData tMapData = new TMapData();
                                // 내 위치 설정
                                TMapPoint myLocation = new TMapPoint(first_lat, first_lng);
                                tMapData.findAroundNamePOI(myLocation, "공원", 5, 30, new TMapData.OnFindAroundNamePOIListener() {
                                    @Override
                                    public void onFindAroundNamePOI(ArrayList<TMapPOIItem> arrayList) {

                                        if (arrayList != null && !arrayList.isEmpty()) {
                                            // arrayList가 null이 아니고 비어있지 않은 경우에만 처리
                                            Log.i("검색 결과", "검색 완료.");

                                            parkArrayList.clear(); // 공원 정보를 저장할 ArrayList

                                            // 검색 결과로 받은 공원 리스트 처리
                                            for (TMapPOIItem item : arrayList) {
                                                // 공원 이름
                                                name = item.getPOIName();
                                                // 공원 주소
                                                address = item.getPOIAddress();
                                                // 공원 좌표
                                                lat_lng = item.getPOIPoint();

                                                // latitude와 longitude 값을 추출
                                                double latitude = lat_lng.getLatitude();
                                                double longitude = lat_lng.getLongitude();

                                                // 필요한 정보 처리
                                                Log.i("공원 정보", "이름: " + name + ", 주소: " + address +", 입구 좌표: " + latitude + ", " + longitude );
                                                // Park 객체 생성 및 정보 저장
                                                park = new Park(name, address, latitude, longitude);

                                                // Park 객체를 ArrayList에 추가
                                                parkArrayList.add(park);
                                            }

                                            // 번들에 담아서 보낸다?
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    // parkArrayList에 들어있는 데이터 확인
                                                    Log.i("히히", "parkArrayList 크기: " + parkArrayList.size());
                                                    Log.i("히히", "parkArrayList 크기: " + park);
                                                    // 어댑터에 데이터 넣기
                                                    recommendAdapter = new RecommendAdapter(getActivity(), parkArrayList, userInfoArrayList);
                                                    // 어댑터 연결
                                                    walkingRecyclerView.setAdapter(recommendAdapter);

                                                }
                                            });
                                        } else {
                                            // arrayList가 null 또는 비어있는 경우에 대한 처리
                                            Log.i("검색 결과", "검색 결과가 없습니다.");
                                        }
                                    }
                                });
                            }
                        }

                    }
                    @Override
                    public void onFailure(Call<UserList> call, Throwable t) {
                        Log.e("ERROR", "유저 주소 불러오기 실패");
                    }



                });

            }
            @Override
            public void SKTMapApikeyFailed(String s) {
                // 인증 실패
                // AppKey 인증 오류 처리를 여기에 추가
                Log.e("TMAP KEY 실패 : ", "실패, Key 인증 실패.");
            }

        });





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
                    Log.i("pet", "펫 정보 불러오기 완료");

                    petArrayList.clear(); //초기화

                    // 서버에서 받아온 데이터를 리스트에 넣고, 각각 적용시키기
                    PetList petList = response.body();
                    petArrayList.addAll(petList.items);

                    if (petArrayList.size() != 0) {
                        // 리스트의 사이즈가 0이 아닐때만 화면에 적용 실행

                        txtStart.setText(petArrayList.get(0).petName + "와 산책을 시작해보세요~");
                        Log.i("pet", petArrayList.get(0).petProUrl);
                        Glide.with(getActivity()).load(petArrayList.get(0).petProUrl).into(imgPet);
                    }
                }
            }
            @Override
            public void onFailure(Call<PetList> call, Throwable t) {
                Log.i("pet", "펫 정보 불러오기 실패");
            }
        });


        // 친구 추천
        Retrofit retrofitRandom = NetworkClient.getRetrofitClient(getActivity());
        RandomFriendAPI randomApi = retrofitRandom.create(RandomFriendAPI.class);

        Call<RandomFriendRes> randomCall = randomApi.getRandomFriend(offset, limit, "Bearer "+token);
        randomCall.enqueue(new Callback<RandomFriendRes>() {
            @Override
            public void onResponse(Call<RandomFriendRes> call, Response<RandomFriendRes> response) {
                if (response.isSuccessful()){

                    randomFriendArrayList.clear();
                    RandomFriendRes randomFriendRes = response.body();
                    Log.i("test", ""+randomFriendRes.items);
                    randomFriendArrayList.addAll(0, randomFriendRes.items);
                    randomAdapter = new RandomAdapter(getActivity(), randomFriendArrayList);
                    recyclerViewRandom.setAdapter(randomAdapter);

                } else {

                    Log.i("test", "에러");

                }
            }

            @Override
            public void onFailure(Call<RandomFriendRes> call, Throwable t) {

                Log.i("test", "에러"+t);

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


        return rootView;
    }

    // 권한
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 부여된 경우
            } else {
                // 권한이 거부된 경우
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}