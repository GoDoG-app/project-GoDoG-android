package com.blue.walking;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.PetApi;
import com.blue.walking.api.UserApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.Park;
import com.blue.walking.model.Pet;
import com.blue.walking.model.PetList;
import com.blue.walking.model.UserInfo;
import com.blue.walking.model.UserList;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.auth.User;
import com.skt.tmap.TMapData;
import com.skt.tmap.TMapPoint;
import com.skt.tmap.TMapView;
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
    ArrayList<UserInfo> userInfoArrayList = new ArrayList<>();
    LocationManager locationManager; // 유저 위치를 위한 로케이션 매니저
    private static final int REQUEST_LOCATION_PERMISSION = 1; // 권한 요청 코드
    double first_lat ; // 처음 위도
    double first_lng ; // 처음 경도
    TMapView tMapView; // 티맵 사용
    RecyclerView RecommndRecyclerView; // 산책로 추천용

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        // TMapView 초기화 및 설정
        tMapView = new TMapView(getActivity());
        tMapView.setSKTMapApiKey(Config.getAppKey());

        // 로케이션매니저 초기화
        locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);

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


        // 기기의 마지막 위치 가져오기( 앱 켰을 때 최초 위치)
        Location first_location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        first_lat = first_location.getLatitude(); // 마지막 위도
        first_lng = first_location.getLongitude(); // 마지막 경도
        Log.i("최초 위치 ", "최초 위치 first_lat+lon :" + first_lat + first_lng);


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

                    petArrayList.clear(); //초기화

                    // 서버에서 받아온 데이터를 리스트에 넣고, 각각 적용시키기
                    PetList petList = response.body();
                    petArrayList.addAll(petList.items);

                    if (petArrayList.size() != 0) {
                        // 리스트의 사이즈가 0이 아닐때만 화면에 적용 실행

                        Log.i("pet", "펫 정보 불러오기 완료");

                        txtStart.setText(petArrayList.get(0).petName + "와 산책을 시작해보세요~");
                        Log.i("pet", petArrayList.get(0).petProUrl);
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

//        todo-- 유저 정보를 가져와서 추천해줄지.. // 유저 주소 기반 산책로 추천(유저 주소 가져오기)
//
//        UserApi api2 = retrofit.create(UserApi.class); // API 인터페이스를 구현한 객체를 생성
//        Call<UserList> call2 = api2.getUserInfo("Bearer " + token); // 서버에 HTTP 요청을 생성
//
//        // HTTP 요청을 서버로 보냄
//        call2.enqueue(new Callback<UserList>() {
//            @Override
//            public void onResponse(Call<UserList> call, Response<UserList> response) {
//                // 서버 응답을 처리하는 코드
//                if (response.isSuccessful()){
//                    // 서버에서 받아온 데이터를 리스트에 넣고, 각각 적용
//                    UserList userList = response.body();
//                    userInfoArrayList.addAll(userList.info);
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<UserList> call, Throwable t) {
//                // 서버 요청 실패를 처리하는 코드
//            }
//        });




        // TMapData 객체 생성
        TMapData tMapData = new TMapData();

        // 내 위치 설정
        TMapPoint myLocation = new TMapPoint(first_lat, first_lng);
        tMapData.findAroundNamePOI(myLocation, "공원", 5, 20, new TMapData.OnFindAroundNamePOIListener() {
            @Override
            public void onFindAroundNamePOI(ArrayList<TMapPOIItem> arrayList) {


                // 검색 결과로 받은 공원 리스트 처리
                ArrayList<Park> parkArrayList = new ArrayList<>(); // 공원 정보를 저장할 ArrayList

                // 검색 결과로 받은 공원 리스트 처리
                for (TMapPOIItem item : arrayList) {

                    // 공원 이름
                    String name = item.getPOIName();
                    // 공원 주소
                    String address = item.getPOIAddress();
                    // 공원 좌표
                    TMapPoint lat_lng = item.getPOIPoint();
                    // 필요한 정보 처리
                    Log.i("공원 정보", "이름: " + name + ", 주소: " + address +", 입구 좌표: " + lat_lng );

                    // Park 객체 생성 및 정보 저장
                    Park park = new Park(name, address, lat_lng);

                    // Park 객체를 ArrayList에 추가
                    parkArrayList.add(park);

                    Log.i("히히", ""+park);
                    Log.i("히히", ""+parkArrayList.get(0));
                }
                // for 루프 외부에서 parkArrayList를 출력
                for (Park park : parkArrayList) {
                    Log.i("히히", "이름: " + park.getName() + ", 주소: " + park.getAddress() + ", 입구 좌표: " + park.getLat_lng());
                }
                // 데이터를 전달하는 프래그먼트
                Bundle bundle = new Bundle();
                bundle.putSerializable("parkList", parkArrayList);
                Walking_3 walking_3 = new Walking_3();
                walking_3.setArguments(bundle);

                // parkArrayList에 들어있는 데이터 확인
                Log.i("히히", "parkArrayList 크기: " + parkArrayList.size());
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

    // onRequestPermissionsResult 메서드는 onCreateView 밖에서 정의
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

}