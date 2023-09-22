package com.blue.walking;

import static android.view.View.GONE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blue.walking.config.Config;
import com.blue.walking.model.UserInfo;
import com.google.android.gms.maps.MapView;
import com.google.android.material.snackbar.Snackbar;
import com.skt.tmap.TMapGpsManager;
import com.skt.tmap.TMapPoint;
import com.skt.tmap.TMapView;
import com.skt.tmap.overlay.TMapMarkerItem;
import com.skt.tmap.overlay.TMapPolyLine;

import java.util.ArrayList;


public class TestActivity extends AppCompatActivity implements TMapGpsManager.OnLocationChangedListener {

    /**
     * 화면뷰
     */
    MapView mapView;  // 지도 화면
    ImageView imgStart;  // 산책 시작
    TextView txtDistance;  // 산책 거리
    String token;
    ArrayList<UserInfo> userInfoArrayList = new ArrayList<>();
    Double time; // 타이머 정치 초
    Button btnReset;  // 산책 초기화
    TextView txtPlace;  // 산책로 추천을 통해 산책하기를 시작했을때, 나오는 추천 장소 이름
    Chronometer chronometer; // 타이머
    ImageView btnLocation; // 현재 위치 이동 및 마커용 버튼
    ProgressBar progressBar; // 맵 로딩
    Button btnLine; // 경로 삭제
    boolean i = true;  // 산책 시작 이미지 변경에 사용

    // 실시간 현재 위치
    double lat; // 위도
    double lng; // 경도
    boolean locationReady = false; // 위치 정보가 준비 되었는 지 여부
    boolean mapReady = false; // 맵이 준비 되었는지 확인
    // 내 위치를 받기
    LocationManager locationManager;
    LocationListener locationListener;

    // 티맵 뷰 (지도 표시에 필요한 라이브러리)
    TMapView tMapView;

    // 지도가 나타날 화면 뷰
    FrameLayout tmapViewContainer;

    // 폴리라인을 그리기 위한 ArrayList
    ArrayList<TMapPoint> pointList = new ArrayList<>();
    TMapPolyLine line; // 폴리라인 객체
    TMapMarkerItem marker; // 마커 객체
    private static final int REQUEST_LOCATION_PERMISSION = 1; // 권한 요청 코드

    double distance = 0.0;

    TMapPolyLine tMapPolyLine1;

    // 위치 권한 요청 코드
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;

    TMapGpsManager gps ;
    TMapPoint point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        imgStart = findViewById(R.id.imgStart);
        btnReset = findViewById(R.id.btnReset);
        chronometer = findViewById(R.id.chronometer);
        txtDistance = findViewById(R.id.txtDistance);
        // 지도가 나타날 화면
        tmapViewContainer = findViewById(R.id.tmapViewContainer);
        // 현재 위치 버튼
        btnLocation = findViewById(R.id.btnLocation);
        // 맵 로딩 프로그레스바
        progressBar = findViewById(R.id.progressBar);

        btnLine = findViewById(R.id.btnLine);

        // TmapView 객체(context로 하면 안나옴)
        // 프래그먼트는 activity가 아니라서 activity를 가져와야함.
        tMapView = new TMapView(this);
        // tmapViewContainer(FrameLayout)에 TmapView 추가.
        tmapViewContainer.addView(tMapView);
        // AppKey 가져오기
        tMapView.setSKTMapApiKey(Config.getAppKey());

        gps = new TMapGpsManager(this);
        gps.setMinTime(-1);
        gps.setMinDistance(4);
        gps.setProvider(TMapGpsManager.PROVIDER_GPS);
        gps.openGps();

        // 초기화 버튼을 눌렀을 때
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.setBase(SystemClock.elapsedRealtime()); // 타이머 리셋
            }
        });

        // 레이아웃에 지도를 구현
        tMapView.setOnMapReadyListener(new TMapView.OnMapReadyListener() {
            @Override
            public void onMapReady() {
                //todo 맵 로딩 완료 후 구현

                if (tMapPolyLine1 != null){
                    tMapPolyLine1.setLineColor(Color.BLUE);
                    tMapPolyLine1.setLineWidth(5);
                    tMapView.addTMapPolyLine(tMapPolyLine1);
                }


                mapReady = true;

                // GPS 버튼 눌렀을 때
                btnLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (lat==0.0){
                            Snackbar.make(btnLocation,
                                    "현재 위치 가져오는 중. 잠시만 기다려 주세요.",
                                    Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        tMapView.removeAllTMapMarkerItem();// 이전 마커 삭제
                        marker = new TMapMarkerItem(); // 마커 객체 초기화
                        marker.setId("marker1"); // 마커 이름 지정
                        // 마커 이미지 지정 및 설정
                        Bitmap myMarker = BitmapFactory.decodeResource(getResources(), R.drawable.marker_girl);
                        myMarker = Bitmap.createScaledBitmap(myMarker, 90, 90,false);
                        marker.setTMapPoint(new TMapPoint(lat, lng)); // 마커 표시할 위치
                        tMapView.addTMapMarkerItem(marker); // 마커 적용

                        // 지도 중심점 표시( 버튼 누르면)
                        tMapView.setCenterPoint(lat, lng); // 지도 중심 설정
                        tMapView.setZoomLevel(16); // 줌 레벨 설정
                    }
                });
                // 시작 버튼을 눌렀을 때
                imgStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // 이전 경로 지우기 (라인 remove가 안되서..)
                        btnLine.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showAlertDialog2();
                            }
                        });

                        if(locationReady==true){

                            if (i == true) {
                                btnLine.setVisibility(GONE);
                                btnReset.setVisibility(View.VISIBLE); // 초기화 버튼 띄우기

                                // 정지로 변경
                                imgStart.setImageResource(R.drawable.baseline_stop_24);
                                i = false;
                                chronometer.setBase(SystemClock.elapsedRealtime());  // 타이머 리셋
                                chronometer.start();  // 타이머 재생

                            } else {
                                // 산책 기록 저장 여부 물어보기
                                showAlertDialog1();

                                // 정지 눌렀을 때 다시 시작으로 변경 + 초기화 버튼 숨기기 + 타이머 종료
                                imgStart.setImageResource(R.drawable.baseline_play_arrow_24);
                                i = true;
                                btnReset.setVisibility(View.GONE); // 초기화 버튼 숨기기
                                btnLine.setVisibility(View.VISIBLE); // 경로 삭제 버튼 띄우기
                                chronometer.stop();
                                // 현재 타이머 초 저장
                                String chronometerText = chronometer.getText().toString();
                                String[] timeParts = chronometerText.split(":");
                                int hours = Integer.parseInt(timeParts[0]);
                                int minutes = Integer.parseInt(timeParts[1]);
                                time = Double.valueOf((hours * 60) + minutes);
                            }
                        }else{
                            Snackbar.make(imgStart,
                                    "아직 로딩 중 입니다. 잠시만 기다려 주세요.",
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onLocationChange(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
        point = new TMapPoint(lat, lng);

        locationReady = true;

        //  완료라면 프로그레스바 숨김
        progressBar.setVisibility(GONE);
        gpsLine();

        if (mapReady) {

            // 지도 중심점 표시(처음 한 번)
            tMapView.setCenterPoint(lat, lng); // 지도 중심 설정
            tMapView.setZoomLevel(16); // 줌 레벨 설정

            // 최초 마커, 마커 생성 및 설정
            marker = new TMapMarkerItem();
            marker.setId("marker");
            // 마커 이미지 지정 및 설정
            Bitmap myMarker = BitmapFactory.decodeResource(this.getResources(), R.drawable.marker_girl);
            myMarker = Bitmap.createScaledBitmap(myMarker, 90, 90, false);
            marker.setIcon(myMarker); // 마커 표시
            // 최초 위치에 마커 추가
            marker.setTMapPoint(new TMapPoint(lat, lng));
            tMapView.addTMapMarkerItem(marker);
            mapReady = false;
        }
    }
        @Override
        public void onPointerCaptureChanged ( boolean hasCapture){
            super.onPointerCaptureChanged(hasCapture);
        }

        // 위치 권한 요청 메서드
        private void requestLocationPermission () {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }

        // 권한 요청 결과 처리
        @Override
        public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults){
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한이 허용된 경우 위치 정보를 가져올 수 있습니다.
                    // 위치 정보 가져오기 코드를 이곳에 추가하세요.
                } else {
                    // 사용자가 권한을 거부한 경우 처리
                }
            }
        }

    public void gpsLine(){

        if (btnLine.getVisibility() == GONE){

            // 현재 위치를 TMapPoint 객체로 변환
            TMapPoint currentPoint = new TMapPoint(lat, lng);

            pointList.add(currentPoint);  // 경로에 현재 위치 추가

            // 이전 경로 및 마커 삭제
            tMapView.removeAllTMapPolyLine();
            tMapView.removeTMapPolygon("path1");
            tMapView.removeAllTMapMarkerItem();
            tMapView.removeTMapMarkerItem("marker");
            tMapView.removeTMapMarkerItem("marker1");

            // 새로운 폴리라인 생성 및 추가
            line = new TMapPolyLine("path1", pointList);
            line.setLineWidth(5);
            line.setLineColor(Color.RED);
            tMapView.addTMapPolyLine(line);

            // 지도 중심점 표시
            tMapView.setCenterPoint(lat, lng); // 지도 중심 설정
            tMapView.setZoomLevel(17); // 줌 레벨 설정

            // 마커 생성 및 설정
            marker = new TMapMarkerItem();
            marker.setId("marker1");
            // 마커 이미지 지정 및 설정
            Bitmap myMarker = BitmapFactory.decodeResource(this.getResources(), R.drawable.marker_girl);
            myMarker = Bitmap.createScaledBitmap(myMarker, 90, 90,false);
            marker.setIcon(myMarker); // 마커 아이콘 적용

            marker.setTMapPoint(new TMapPoint(lat, lng)); // 마커 위치
            tMapView.addTMapMarkerItem(marker); // 마커 추가

            distance = (distance + 0.004);
            if (distance<1.0){
                txtDistance.setText(Math.round(distance*1000)+"m");
            }else{
                txtDistance.setText(String.format("%.2fkm", distance));
            }
            Log.d("이동 거리","현재 걸은 거리: "+distance);
        }
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
                        // WalkingRecoActivity를 시작하기 위한 Intent를 생성합니다.
                        Intent intent = new Intent(TestActivity.this, WalkingRecoActivity.class);

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

}