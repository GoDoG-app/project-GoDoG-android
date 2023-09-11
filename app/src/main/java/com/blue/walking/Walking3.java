package com.blue.walking;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.blue.walking.config.Config;
import com.blue.walking.model.Park;
import com.blue.walking.model.UserInfo;
import com.skt.tmap.TMapData;
import com.skt.tmap.TMapPoint;
import com.skt.tmap.TMapView;
import com.skt.tmap.overlay.TMapPolyLine;

import java.util.ArrayList;

public class Walking3 extends AppCompatActivity {


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

    Park park;
    UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking3);

        // 유저가 선택한 공원 정보 데이터 받기
        Intent intent = getIntent();
        if (intent != null) {
            userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");
            park = (Park) getIntent().getSerializableExtra("walkingRoute");
            }
        Log.e("공원정보 로그", "잘받았음?" + park.getName());

        if (userInfo != null) {
            Log.e("UserInfo ", "User lat_lng : " + userInfo.lat + "_" + userInfo.lng);
        } else {
            // userInfo가 null인 경우 처리
            Log.e("UserInfo", "UserInfo null");
        }


        mapView1 = findViewById(R.id.mapView1);
        mapView2 = findViewById(R.id.mapView2);

        // 유저가 선택한 경로 정보 intent 받기


        // 경로 추천용 두개의 지도
        // TmapView1 객체(context로 하면 안나옴)
        TMapView tMapView1 = new TMapView(this);
        // AppKey 가져오기
        tMapView1.setSKTMapApiKey(Config.getAppKey());
        // FrameLayout에 TmapView 추가.
        mapView1.addView(tMapView1);

        // TmapView2 객체(context로 하면 안나옴)
        TMapView tMapView2 = new TMapView(this);
        // AppKey 가져오기
        tMapView2.setSKTMapApiKey(Config.getAppKey());
        // FrameLayout에 TmapView 추가.
        mapView2.addView(tMapView2);


        tMapView1.setOnMapReadyListener(new TMapView.OnMapReadyListener() {
            @Override
            public void onMapReady() {
                // todo 맵이 준비 되었다면 여기서 작성

                // 에러 방지용 더미 변수
//                double startLat = 37.5455; // 출발지
//                double startLng = 126.6758;
//
//                double endLat = 37.5475394; // 도착지
//                double endLng = 126.6665509;

//        passList.add(new TMapPoint( 37.5455, 126.6758)); // 경유지 서구청
//        passList.add(new TMapPoint( 37.5475394, 126.6665509)); // 경유지 아시아드

                double startLat = userInfo.lat; // 출발지
                double startLng = userInfo.lng;

                double endLat = park.getLatitude(); // 도착지
                double endLng = park.getLongitude();


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
//                                // 중간 지점 계산
                                TMapPoint middlePoint = getMidPoint(startPoint, endPoint);

                                // 중간 지점을 지도의 중심으로 설정.
                                tMapView1.setCenterPoint(middlePoint.getLatitude(), middlePoint.getLongitude());
                                tMapView1.setZoomLevel(17); // 줌 레벨 설정

                                tMapPolyLine.setLineColor(Color.DKGRAY); // 경로 폴리라인 색상
                                tMapPolyLine.setLineWidth(3); // 경로 폴리라인 두께
                                tMapView1.addTMapPolyLine(tMapPolyLine);
                            }
                        });

//                TMapPoint midPoint = startPoint.get // 중간 지점 계산

            }
        });


        tMapView2.setOnMapReadyListener(new TMapView.OnMapReadyListener() {
            @Override
            public void onMapReady() {

            }
        });

        mapView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showAlertDialog();
            }
        });

        mapView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showAlertDialog();
            }
        });
    }

    // 알러트 다이얼로그
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("[안내]");
        builder.setMessage("산책을 시작하시겠습니까?");

        builder.setCancelable(false); // 아래 둘 중 한개의 버튼을 꼭 누르게 해줌(외각을 클릭해도 안사라지게)

        // 긍정버튼
        builder.setPositiveButton("시작", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // 지도 프래그먼트로 보내줄 준비
//                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                WalkingFragment walkingFragment = new WalkingFragment();
//                // 프레그먼트 화면을 walkingFragment 로 활성화
//                transaction.replace(R.id.containers, walkingFragment);
//                // 꼭 commit 을 해줘야 바뀜
//                transaction.commit();
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
    // 출발지와 도착지의 중간 지점을 계산하는 메서드
    private TMapPoint getMidPoint(TMapPoint startPoint, TMapPoint endPoint) {
        double midLat = (startPoint.getLatitude() + endPoint.getLatitude()) / 2;
        double midLon = (startPoint.getLongitude() + endPoint.getLongitude()) / 2;
        return new TMapPoint(midLat, midLon);
    }
}
