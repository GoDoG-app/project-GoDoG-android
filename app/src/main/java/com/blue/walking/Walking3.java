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
import com.google.android.gms.maps.model.LatLng;
import com.skt.tmap.TMapData;
import com.skt.tmap.TMapPoint;
import com.skt.tmap.TMapView;
import com.skt.tmap.overlay.TMapPolyLine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Walking3 extends AppCompatActivity {


    ImageView imgLoop;  // 산책로 새로고침
    TextView txtRegion;  // 현재 위치 주소

    FrameLayout mapView1; // 1번 산책로 지도
    TextView txtPlace1; // 1번 산책지 이름
    TextView txtRoute1; // 1번 산책지 첫 번째 경로 이름
    TextView txtTime1;  // 첫번째 산책로 이동 시간
    TextView txtKm1;    // 첫번째 산책로 거리

    FrameLayout mapView2;   // 두번째 산책로 지도
    TextView txtPlace2; // 2번 산책지 이름
    TextView txtRoute2; // 2번 산책지 두 번째 경로 이름
    TextView txtTime2;  // 두번째 산책로 이동 시간
    TextView txtKm2;    // 두번째 산책로 거리

    Park park; // 유저가 선택한 공원 정보
    UserInfo userInfo; // 유저의 정보
    double startLat; // 유저의 출발지
    double startLng;
    double endLat; // 유저의 도착지
    double endLng;

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
        startLat = userInfo.lat; // 출발지
        startLng = userInfo.lng;

        endLat = park.getLatitude(); // 도착지
        endLng = park.getLongitude();

        TMapData tMapData = new TMapData(); // 경로를 그릴 데이터 생성
        TMapPoint startPoint = new TMapPoint(startLat, startLng); // 출발지
        TMapPoint endPoint =  new TMapPoint(endLat, endLng); // 도착지
        TMapPoint middlePoint = getMidPoint(startPoint, endPoint); // 중간 지점 계산


        // 랜덤 포인트를 ArrayList(경유지)에 넣기
        ArrayList<TMapPoint> passList = new ArrayList<>();
        double randomRadius = 0.0015; // 반경 설정 (임의로 조정)
        double randomAngle = Math.toRadians(new Random().nextDouble() * 360.0);
        double randomLatOffset = randomRadius * Math.cos(randomAngle);
        double randomLngOffset = randomRadius * Math.sin(randomAngle);
        TMapPoint randomPoint = new TMapPoint(endLat + randomLatOffset, endLng + randomLngOffset);
        passList.add(endPoint); // 경유지에 유저가 선택한 공원
        passList.add(randomPoint); // 랜덤 포인트 가까운거리



        mapView1 = findViewById(R.id.mapView1);
        mapView2 = findViewById(R.id.mapView2);
        txtRegion = findViewById(R.id.txtRegion);
        txtPlace1 = findViewById(R.id.txtPlace1);
        txtPlace2 = findViewById(R.id.txtPlace2);
        txtRoute1 = findViewById(R.id.txtRoute1);
        txtRoute2 = findViewById(R.id.txtRoute2);
        txtTime1 = findViewById(R.id.txtTime1);
        txtTime2 = findViewById(R.id.txtTime2);
        txtKm1 = findViewById(R.id.txtKm1);
        txtKm2 = findViewById(R.id.txtKm2);
        txtRegion.setText(userInfo.userAddress);
        txtPlace1.setText(park.getName()+"(빠른 경로)");
        txtPlace2.setText(park.getName()+"(느린 경로)");




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

                // searchOption string 0: 추천 (기본값), 4: 추천+대로우선, 10: 최단, 30: 최단거리+계단제외
                tMapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH,
                        startPoint, startPoint, passList, 0, new TMapData.OnFindPathDataWithTypeListener() {
                            @Override
                            public void onFindPathDataWithType(TMapPolyLine tMapPolyLine) {

                                // 중간 지점을 지도의 중심으로 설정.
                                tMapView1.setCenterPoint(middlePoint.getLatitude(), middlePoint.getLongitude());
                                tMapView1.setZoomLevel(15); // 줌 레벨 설정

                                tMapPolyLine.setLineColor(Color.DKGRAY); // 경로 폴리라인 색상
                                tMapPolyLine.setLineWidth(3); // 경로 폴리라인 두께
                                tMapView1.addTMapPolyLine(tMapPolyLine);

                                double distance = tMapPolyLine.getDistance(); // 총 거리 (미터)
                                Log.i("거리", "distance" + distance);
                                double time = tMapPolyLine.getDistance() / 1000.0 / 5.0; // 예상 시간 (시간, 평균 보행 속도 5km/h 기준)

                                // UI 업데이트를 메인 스레드에서 실행
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        txtTime1.setText(String.valueOf(time));
                                        txtKm1.setText(String.valueOf(distance/1000.0));
                                    }
                                });

                            }
                        });
            }
        });

        // 랜덤 포인트 생성 (중간 지점 주변)
        ArrayList<TMapPoint> passList1 = new ArrayList<>();
        double randomRadius1 = 0.0030; // 반경 설정 (임의로 조정)
        double randomAngle1 = Math.toRadians(new Random().nextDouble() * 360.0);
        double randomLatOffset1 = randomRadius1 * Math.cos(randomAngle1);
        double randomLngOffset1 = randomRadius1 * Math.sin(randomAngle1);
        TMapPoint randomPoint1 = new TMapPoint(startLat + randomLatOffset1, startLng + randomLngOffset1);
        passList1.add(endPoint); // 경유지에 유저가 선택한 공원
        passList1.add(randomPoint1); // 랜덤포인트(경유지) 추가 먼거리

        tMapView2.setOnMapReadyListener(new TMapView.OnMapReadyListener() {
            @Override
            public void onMapReady() {
                // 추천 경로 그리기 2번

                // searchOption string 0: 추천 (기본값), 4: 추천+대로우선, 10: 최단, 30: 최단거리+계단제외
                tMapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH,
                        startPoint, startPoint, passList1, 0, new TMapData.OnFindPathDataWithTypeListener() {
                            @Override
                            public void onFindPathDataWithType(TMapPolyLine tMapPolyLine) {
                                // 중간 지점 계산
                                TMapPoint middlePoint = getMidPoint(startPoint, endPoint);

                                // 중간 지점을 지도의 중심으로 설정.
                                tMapView2.setCenterPoint(middlePoint.getLatitude(), middlePoint.getLongitude());
                                tMapView2.setZoomLevel(15); // 줌 레벨 설정

                                tMapPolyLine.setLineColor(Color.DKGRAY); // 경로 폴리라인 색상
                                tMapPolyLine.setLineWidth(3); // 경로 폴리라인 두께
                                tMapView2.addTMapPolyLine(tMapPolyLine);

                                double distance = tMapPolyLine.getDistance(); // 총 거리 (미터)
                                double time = tMapPolyLine.getDistance() / 1000.0 / 5.0; // 예상 시간 (시간, 평균 보행 속도 5km/h 기준)

                                // UI 업데이트를 메인 스레드에서 실행
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        txtTime2.setText(String.valueOf(time));
                                        txtKm2.setText(String.valueOf(distance));
                                    }
                                });

                            }
                });
           }
        });

//        OkHttpClient client = new OkHttpClient();
//
//        MediaType mediaType = MediaType.parse("application/json");
//        RequestBody body = RequestBody.create(mediaType,
//                "{\"startX\":126.92365493654832," +
//                        "\"startY\":37.556770374096615," +
//                        "\"angle\":20," +
//                        "\"speed\":30," +
//                        "\"endPoiId\":\"10001\"," +
//                        "\"endX\":126.92432158129688," +
//                        "\"endY\":37.55279861528311," +
//                        "\"passList\":\"126.92774822,37.55395475_126.92577620,37.55337145\",\"reqCoordType\":\"WGS84GEO\",\"startName\":\"%EC%B6%9C%EB%B0%9C\",\"endName\":\"%EB%8F%84%EC%B0%A9\",\"searchOption\":\"0\",\"resCoordType\":\"WGS84GEO\",\"sort\":\"index\"}");
//        Request request = new Request.Builder()
//                .url("https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&callback=function")
//                .post(body)
//                .addHeader("accept", "application/json")
//                .addHeader("content-type", "application/json")
//                .addHeader("appKey", Config.getAppKey())
//                .build();
//
//        try {
//            Response response = client.newCall(request).execute();
//
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

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
