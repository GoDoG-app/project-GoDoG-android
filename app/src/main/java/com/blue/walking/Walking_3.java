package com.blue.walking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.blue.walking.config.Config;
import com.blue.walking.model.Park;
import com.blue.walking.model.RandomFriend;
import com.google.android.gms.maps.MapView;
import com.skt.tmap.TMapData;
import com.skt.tmap.TMapPoint;
import com.skt.tmap.TMapView;
import com.skt.tmap.overlay.TMapPolyLine;
import com.skt.tmap.poi.TMapPOIItem;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Walking_3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Walking_3 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Walking_3() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Walking_3.
     */
    // TODO: Rename and change types and number of parameters
    public static Walking_3 newInstance(String param1, String param2) {
        Walking_3 fragment = new Walking_3();
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



    ImageView imgLoop;  // 산책로 새로고침
    TextView txtRegion;  // 현재 위치 주소

    FrameLayout mapView1;   // 첫번째 산책로 지도
    TextView txtPlace1; // 첫번재 산책로 이름
    TextView txtTime1;  // 첫번째 산책로 이동 시간
    TextView txtKm1;    // 첫번째 산책로 거리

    FrameLayout mapView2;   // 두번째 산책로 지도
    TextView txtPlace2; // 두번째 산책로 이름
    TextView txtTime2;  // 두번째 산책로 이동 시간
    TextView txtKm2;    // 두번째 산책로 거리

    Park park ;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup)  inflater.inflate(R.layout.fragment_walking_3, container, false);

        // 데이터를 받는 프래그먼트 (Walking_3)
//        Bundle args = getArguments();
//        if (args != null) {
//            ArrayList<Park> receivedParkArrayList = (ArrayList<Park>) args.getSerializable("parkList");
//            if (receivedParkArrayList != null) {
//                // 데이터 사용
//                // for 루프 외부에서 parkArrayList를 출력
//                for (Park park : receivedParkArrayList) {
//                    Log.i("워킹3프래그", "이름: " + park.getName() + ", 주소: " + park.getAddress() + ", 입구 좌표: " + park.getLat_lng());
//                }
//                // parkArrayList에 들어있는 데이터 확인
//                Log.i("워킹3프래그", "parkArrayList 크기: " + receivedParkArrayList.size());
//            }
//        }

        // walking_1 프래그먼트로부터 받을 데이터
        Bundle bundle = getArguments();
        if (bundle != null) {
            // 번들로부터 데이터 추출
            Park park = (Park) bundle.getSerializable("walkingRoute");

            // 데이터를 사용하여 원하는 작업 수행
            if (park != null) {
                // park 객체로부터 필요한 정보 추출
                String parkName = park.getName();
                String parkAddress = park.getAddress();
                double parkLat = park.getLatitude();
                double parkLng = park.getLongitude();
                Log.i("워킹3프래그", " 가져온 데이터 : " + "공원이름: "+parkName+", 공원 주소: "+ parkAddress+", 공원 위도,경도: "+parkLat+parkLng);

            }
        }
        mapView1 = rootView.findViewById(R.id.mapView1);
        mapView2 = rootView.findViewById(R.id.mapView2);

        // 유저가 선택한 경로 정보 intent 받기


        // 경로 추천용 두개의 지도
        // TmapView1 객체(context로 하면 안나옴)
        TMapView tMapView1 = new TMapView(getActivity());
        // AppKey 가져오기
        tMapView1.setSKTMapApiKey(Config.getAppKey());
        // FrameLayout에 TmapView 추가.
        mapView1.addView(tMapView1);

        // TmapView2 객체(context로 하면 안나옴)
        TMapView tMapView2 = new TMapView(getActivity());
        // AppKey 가져오기
        tMapView2.setSKTMapApiKey(Config.getAppKey());
        // FrameLayout에 TmapView 추가.
        mapView2.addView(tMapView1);


        tMapView1.setOnMapReadyListener(new TMapView.OnMapReadyListener() {
            @Override
            public void onMapReady() {
                // todo 맵이 준비 되었다면 여기서 작성

                // 에러 방지용 더미 변수
                double startLat = 37.5455; // 출발지
                double startLng = 126.6758;

                double endLat = 37.5475394; // 도착지
                double endLng = 126.6665509;

//        passList.add(new TMapPoint( 37.5455, 126.6758)); // 경유지 서구청
//        passList.add(new TMapPoint( 37.5475394, 126.6665509)); // 경유지 아시아드

                // 추천 경로 그리기 1번
                ArrayList<TMapPoint> passList = new ArrayList<>();
                TMapData tMapData1 = new TMapData();
                TMapPoint startPoint = new TMapPoint(startLat, startLng); // 출발지
                TMapPoint endPoint =  new TMapPoint(endLat, endLng); // 도착지

                // searchOption string 0: 추천 (기본값), 4: 추천+대로우선, 10: 최단, 30: 최단거리+계단제외

                tMapData1.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH,
                        startPoint, endPoint, null, 0, new TMapData.OnFindPathDataWithTypeListener() {
                            @Override
                            public void onFindPathDataWithType(TMapPolyLine tMapPolyLine) {

                                tMapPolyLine.setLineColor(Color.DKGRAY); // 경로 폴리라인 색상
                                tMapPolyLine.setLineWidth(3); // 경로 폴리라인 두께
                                tMapView1.addTMapPolyLine(tMapPolyLine);
                            }
                        });

//                TMapPoint midPoint = startPoint.get // 중간 지점 계산

            }
        });


        tMapView1.setOnMapReadyListener(new TMapView.OnMapReadyListener() {
            @Override
            public void onMapReady() {

            }
        });

        mapView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        mapView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        return rootView;
    }

    // 알러트 다이얼로그
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("[안내]");
        builder.setMessage("산책을 시작하시겠습니까?");

        builder.setCancelable(false); // 아래 둘 중 한개의 버튼을 꼭 누르게 해줌(외각을 클릭해도 안사라지게)

        // 긍정버튼
        builder.setPositiveButton("시작", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                WalkingFragment walkingFragment = new WalkingFragment();
                // 프레그먼트 화면을 walkingFragment 로 활성화
                transaction.replace(R.id.containers, walkingFragment);
                // 꼭 commit 을 해줘야 바뀜
                transaction.commit();
            }
        });

        // 부정버튼
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.show();
    }
}