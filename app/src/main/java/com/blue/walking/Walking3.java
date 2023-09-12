package com.blue.walking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Walking3 extends AppCompatActivity {

    TextView txtRegion;  // 현재 위치 주소

    FrameLayout mapView1; // 1번 산책로 지도
    TextView txtPlace1; // 1번 산책지 이름
    TextView txtRoute1; // 1번 산책지 첫 번째 경로 이름
    TextView txtTime1;  // 1번 산책로 소요 시간
    TextView txtKm1;    // 1번 산책로 거리
    ImageView imgRoute1;  // 1번 산책경로 버튼

    FrameLayout mapView2; // 2번 지도
    TextView txtPlace2; // 2번 산책지 이름
    TextView txtRoute2; // 2번 산책지 두 번째 경로 이름
    TextView txtTime2;  // 2번 산책로 소요 시간
    TextView txtKm2;    // 2번 산책로 거리
    ImageView imgRoute2;  // 2번 산책경로 버튼

    Park park; // 유저가 선택한 공원 정보
    UserInfo userInfo; // 유저의 정보
    double startLat; // 유저의 출발지 & 도착지
    double startLng;
    double endLat; // 공원 위도 경도
    double endLng;

    // 경로 1,2의 라인
    TMapPolyLine tMapPolyLine1;
    TMapPolyLine tMapPolyLine2;

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
        double randomAngle = Math.toRadians(new Random().nextDouble() * 30.0); //반경 각도
        double randomLatOffset = randomRadius * Math.cos(randomAngle);
        double randomLngOffset = randomRadius * Math.sin(randomAngle);
        TMapPoint randomPoint = new TMapPoint(endLat + randomLatOffset, endLng + randomLngOffset);
        passList.add(randomPoint); // 랜덤 포인트 가까운거리
        passList.add(endPoint); // 경유지에 유저가 선택한 공원

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
        imgRoute1 = findViewById(R.id.imgRoute1);
        imgRoute2 = findViewById(R.id.imgRoute2);
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

        tMapPolyLine1 = new TMapPolyLine();
        tMapView1.setOnMapReadyListener(new TMapView.OnMapReadyListener() {
            @Override
            public void onMapReady() {
                // todo 맵이 준비 되었다면 여기서 작성

                // searchOption string 0: 추천 (기본값), 4: 추천+대로우선, 10: 최단, 30: 최단거리+계단제외
                tMapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH,
                        startPoint, startPoint, passList, 0, new TMapData.OnFindPathDataWithTypeListener() {
                            @Override
                            public void onFindPathDataWithType(TMapPolyLine tMapPolyLine1) {

                                // 중간 지점을 지도의 중심으로 설정.
                                tMapView1.setCenterPoint(middlePoint.getLatitude(), middlePoint.getLongitude());
                                tMapView1.setZoomLevel(15); // 줌 레벨 설정

                                tMapPolyLine1.setLineColor(Color.DKGRAY); // 경로 폴리라인 색상
                                tMapPolyLine1.setLineWidth(3); // 경로 폴리라인 두께
                                tMapView1.addTMapPolyLine(tMapPolyLine1);
                                Log.d("폴리의 값1", "폴리1"+tMapPolyLine1);
                                Log.d("폴리의 값1", "폴리1pointList"+tMapPolyLine1.getLinePointList());
                                Log.d("폴리의 값1", "폴리1passList"+tMapPolyLine1.getPassPointList());


                                // 거리 구하기
                                double dist1 = calculateDistance(startLat, startLng, randomPoint.getLatitude(), randomPoint.getLongitude());
                                double dist2 = calculateDistance(randomPoint.getLatitude(), randomPoint.getLongitude(), endLat, endLng );
                                double dist3 = calculateDistance(endLat, endLng, startLat, startLng);
                                double totalDistance = dist1 + dist2 + dist3;

                                // 소요 시간 구하기
                                double walkingSpeedKmph = 3.0; // 시속 3km
                                double walkingSpeedKmPerMinute = walkingSpeedKmph / 60.0; // 분 단위 속도로 변환
                                double walkingTimeMinutes = totalDistance / walkingSpeedKmPerMinute;

                                // 시간을 시간과 분으로 분리
                                int walkingHours = (int) walkingTimeMinutes / 60;
                                int walkingMinutes = (int) walkingTimeMinutes % 60;

                                String walkingTime;
                                if (walkingHours > 0) {
                                    walkingTime = walkingHours + "시간 " + walkingMinutes + "분";
                                } else {
                                    walkingTime = walkingMinutes + "분";
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // UI 업데이트 코드를 메인 스레드에서 실행
                                        txtKm1.setText(String.format("%.1f km", totalDistance));
                                        txtTime1.setText(walkingTime);
                                        imgRoute1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                showAlertDialog1();
                                            }
                                        });
                                    }
                                });


                            }
                        });
            }
        });


        Log.d("폴리의 값2", "폴리1"+tMapPolyLine1);
        Log.d("폴리의 값2", "폴리1pointList"+tMapPolyLine1.getLinePointList());
        Log.d("폴리의 값2", "폴리1passList"+tMapPolyLine1.getPassPointList());


        // 랜덤 포인트 생성 (중간 지점 주변)
        ArrayList<TMapPoint> passList1 = new ArrayList<>();
        double randomRadius1 = 0.0030; // 반경 설정 (임의로 조정)
        double randomAngle1 = Math.toRadians(30 + new Random().nextDouble() * 30.0);
        double randomLatOffset1 = randomRadius1 * Math.cos(randomAngle1);
        double randomLngOffset1 = randomRadius1 * Math.sin(randomAngle1);
        TMapPoint randomPoint1 = new TMapPoint(endLat + randomLatOffset1, endLng + randomLngOffset1);
        passList1.add(randomPoint1); // 랜덤포인트(경유지) 추가 먼거리
        passList1.add(endPoint); // 경유지에 유저가 선택한 공원

        tMapPolyLine2 = new TMapPolyLine();
        tMapView2.setOnMapReadyListener(new TMapView.OnMapReadyListener() {
            @Override
            public void onMapReady() {
                // 추천 경로 그리기 2번

                // searchOption string 0: 추천 (기본값), 4: 추천+대로우선, 10: 최단, 30: 최단거리+계단제외
                tMapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH,
                        startPoint, startPoint, passList1, 0, new TMapData.OnFindPathDataWithTypeListener() {
                            @Override
                            public void onFindPathDataWithType(TMapPolyLine tMapPolyLine2) {

                                // 중간 지점을 지도의 중심으로 설정.
                                tMapView2.setCenterPoint(middlePoint.getLatitude(), middlePoint.getLongitude());
                                tMapView2.setZoomLevel(15); // 줌 레벨 설정

                                tMapPolyLine2.setLineColor(Color.DKGRAY); // 경로 폴리라인 색상
                                tMapPolyLine2.setLineWidth(3); // 경로 폴리라인 두께
                                tMapView2.addTMapPolyLine(tMapPolyLine2);
                                Log.d("폴리의 값", "폴리2"+tMapPolyLine2);

                                // 거리 구하기
                                double dist1 = calculateDistance(startLat, startLng, randomPoint1.getLatitude(), randomPoint1.getLongitude());
                                double dist2 = calculateDistance(randomPoint1.getLatitude(), randomPoint1.getLongitude(), endLat, endLng );
                                double dist3 = calculateDistance(endLat, endLng, startLat, startLng);
                                double totalDistance = dist1 + dist2 + dist3;

                                // 소요 시간 구하기
                                double walkingSpeedKmph = 3.0; // 시속 3km
                                double walkingSpeedKmPerMinute = walkingSpeedKmph / 60.0; // 분 단위 속도로 변환
                                double walkingTimeMinutes = totalDistance / walkingSpeedKmPerMinute;

                                // 시간을 시간과 분으로 분리
                                int walkingHours = (int) walkingTimeMinutes / 60;
                                int walkingMinutes = (int) walkingTimeMinutes % 60;

                                String walkingTime;
                                if (walkingHours > 0) {
                                    walkingTime = walkingHours + "시간 " + walkingMinutes + "분";
                                } else {
                                    walkingTime = walkingMinutes + "분";
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // UI 업데이트 코드를 메인 스레드에서 실행
                                        txtKm2.setText(String.format("%.1f km", totalDistance));
                                        txtTime2.setText(walkingTime);
                                        imgRoute2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                showAlertDialog2();
                                            }
                                        });
                                    }
                                });

                            }
                });
           }
        });

    }
    // 알러트 다이얼로그1
    private void showAlertDialog1() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("[안내]");
        builder.setMessage("산책을 시작하시겠습니까?");
        builder.setCancelable(false); // 아래 둘 중 한개의 버튼을 꼭 누르게 해줌(외각을 클릭해도 안사라지게)
        // 긍정버튼
        builder.setPositiveButton("시작", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 경로 좌표 리스트 추출
                        ArrayList<TMapPoint> linePointList1 = new ArrayList<>(tMapPolyLine1.getLinePointList());
                        ArrayList<TMapPoint> passPointList1 = new ArrayList<>(tMapPolyLine1.getPassPointList());
                        // 데이터를 담을 Bundle 생성
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Line", linePointList1); // tMapPolyLine1을 직렬화하여 Bundle에 추가
                        bundle.putSerializable("line1", passPointList1);
//
                        // WalkingFragment에 데이터 전달
                        WalkingFragment walkingFragment = WalkingFragment.newInstance(linePointList1, passPointList1);


                        // 프래그먼트를 화면에 추가 또는 대체
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.conta3, walkingFragment); // R.id.containers는 프래그먼트를 표시할 레이아웃의 ID
                        transaction.commit();

                        // Walking1 프래그먼트 생성
//                        WalkingFragment walkingFragment = new WalkingFragment();
//                        walkingFragment.setArguments(bundle); // Bundle을 프래그먼트에 전달
//                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                        // 프레그먼트 화면을 walking_1 로 활성화
//                        transaction.replace(R.id.contaa, walkingFragment); // 프래그먼트가 표시될 레이아웃의 ID
//                        // 꼭 commit 을 해줘야 바뀜
//                        transaction.commit();
                    }
                });

            }
        });
        // 부정버튼
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    // 알러트 다이얼로그2
    private void showAlertDialog2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("[안내]");
        builder.setMessage("산책을 시작하시겠습니까?");
        builder.setCancelable(false); // 아래 둘 중 한개의 버튼을 꼭 누르게 해줌(외각을 클릭해도 안사라지게)
        // 긍정버튼
        builder.setPositiveButton("시작", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 경로 좌표 리스트 추출
                        TMapPolyLine pathPoints2 = tMapPolyLine2;
                        // 데이터를 담을 Bundle 생성
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Line2", (Serializable) pathPoints2); // tMapPolyLine1을 직렬화하여 Bundle에 추가
                        // Walking1 프래그먼트 생성
                        WalkingFragment walkingFragment = new WalkingFragment();
                        walkingFragment.setArguments(bundle); // Bundle을 프래그먼트에 전달
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        // 프레그먼트 화면을 walking_1 로 활성화
                        transaction.replace(R.id.conta3, walkingFragment); // 프래그먼트가 표시될 레이아웃의 ID
                        // 꼭 commit 을 해줘야 바뀜
                        transaction.commit();
                    }
                });

            }
        });

        // 부정버튼
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    // 좌표 간의 거리를 구하는 메서드
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // 지구 반지름 (단위: km)
        double dLat = deg2rad(lat2 - lat1);
        double dLon = deg2rad(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // 두 지점 간의 거리 (단위: km)
        // 거리를 소수점 한 자리까지 포맷팅 ( 100m 단위도 KM 로 보여주기)
        return Math.round(distance * 10.0) / 10.0;
    }

    // 각도를 라디안으로 변환하는 메서드
    public static double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }

    // 중간 지점과 endpoint 사이의 방향을 계산하는 메서드
    private double calculateDirection(TMapPoint middlePoint, TMapPoint endPoint) {
        double lat1 = Math.toRadians(middlePoint.getLatitude());
        double lon1 = Math.toRadians(middlePoint.getLongitude());
        double lat2 = Math.toRadians(endPoint.getLatitude());
        double lon2 = Math.toRadians(endPoint.getLongitude());
        double dLon = lon2 - lon1;
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
        double initialBearing = Math.atan2(y, x);
        // 방위각을 도 단위로 변환
        return Math.toDegrees(initialBearing);
    }

}
