package com.blue.walking;

import static android.view.View.GONE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.UserApi;
import com.blue.walking.api.WalkingApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.ResultRes;
import com.blue.walking.model.UserInfo;
import com.blue.walking.model.UserList;
import com.blue.walking.model.WalkingList;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.skt.tmap.TMapData;
import com.skt.tmap.TMapPoint;
import com.skt.tmap.TMapView;
import com.skt.tmap.overlay.TMapMarkerItem;
import com.skt.tmap.overlay.TMapPolyLine;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WalkingRecoActivity extends AppCompatActivity {
    ImageView imgStart;  // 산책 시작
    TextView txtDistance;  // 산책 거리
    Button btnReset;  // 산책 초기화
    Chronometer chronometer; // 타이머
    ImageView btnLocation; // 현재 위치 이동 및 마커용 버튼
    ProgressBar progressBar; // 맵 로딩
    Button btnMapReset; // 경로 삭제
    boolean i = true;  // 산책 시작 이미지 변경에 사용
    String token; // 토큰
    ArrayList<UserInfo> userInfoArrayList= new ArrayList<>(); // 레트로핏 유저 id
    Double time; // 타이머 정치 초

    // 실시간 현재 위치
    double lat; // 위도
    double lng; // 경도
    boolean locationReady = false; // 위치 정보가 준비 되었는 지 여부
    boolean mapReady = false; // 맵이 준비 되었는지 확인
    // 내 위치를 받기
    LocationManager locationManager;
    LocationListener locationListener;

    // 티맵 뷰 (지도 표시에 필요한 라이브러리)
    // TmapView 객체
    TMapView tMapView;

    // 지도가 나타날 화면 뷰
    FrameLayout tmapViewContainer;

    // 네비바
    BottomNavigationView bottomNavigationView;

    // 폴리라인을 그리기 위한 ArrayList
    ArrayList<TMapPoint> pointList = new ArrayList<>();
    TMapPolyLine line; // 폴리라인 객체
    TMapMarkerItem marker; // 마커 객체
    private static final int REQUEST_LOCATION_PERMISSION = 1; // 권한 요청 코드
    double distance = 0.0; // 거리
    // 추천 경로
    TMapPoint startPoint;
    TMapPoint endPoint;
    TMapPoint randomPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking_reco);
        // tmapview 객체 초기화
        tMapView = new TMapView(this);

        imgStart = findViewById(R.id.imgStart);
        btnReset = findViewById(R.id.btnReset);
        chronometer = findViewById(R.id.chronometer);
        txtDistance = findViewById(R.id.txtDistance);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        // 지도가 나타날 화면
        tmapViewContainer = findViewById(R.id.tmapViewContainer);
        // 현재 위치 버튼
        btnLocation = findViewById(R.id.btnLocation);
        // 맵 로딩 프로그레스바
        progressBar = findViewById(R.id.progressBar);
        // 지도(화면) 초기화 버튼
        btnMapReset = findViewById(R.id.btnMapReset);
        // AppKey 가져오기
        tMapView.setSKTMapApiKey(Config.getAppKey());


        // Intent에서 데이터를 가져옵니다.
        Intent intent = getIntent();
        if (intent!=null) {
            double startPointLat = getIntent().getDoubleExtra("startPointLat", 0.0);
            double startPointLng = getIntent().getDoubleExtra("startPointLng", 0.0);
            double endPointLat = getIntent().getDoubleExtra("endPointLat", 0.0);
            double endPointLng = getIntent().getDoubleExtra("endPointLng", 0.0);
            double randomPointLat = getIntent().getDoubleExtra("randomPointLat", 0.0);
            double randomPointLng = getIntent().getDoubleExtra("randomPointLng", 0.0);
            Log.d("받은 데이터 로그", "start"+startPointLat+startPointLng);

            // TMapPoint로 변환.
            startPoint = new TMapPoint(startPointLat, startPointLng);
            endPoint = new TMapPoint(endPointLat, endPointLng);
            randomPoint = new TMapPoint(randomPointLat, randomPointLng);
        }else{
            Log.d("받을 데이터 없음", "데이터를 확인하세요");
        }


        // 경유지 (새로 루트 한다..)
        ArrayList<TMapPoint> passList = new ArrayList<>();
        passList.add(randomPoint);

        // 권한(permission)상태 확인
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 경우 권한을 요청하는 다이얼로그 표시
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    REQUEST_LOCATION_PERMISSION); // 권한 요청 코드 사용
        }

        // locationManager 객체 생성 및 변수 할당
        locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        // 실시간 위치
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                lat = location.getLatitude();
                lng = location.getLongitude();
                Log.i("위치 확인", "위도 + 경도 : " + lat + " " + lng);
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
                    marker.setId("marker1");
                    // 마커 이미지 지정 및 설정
                    Bitmap myMarker = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.mymarker);
                    myMarker = Bitmap.createScaledBitmap(myMarker, 90, 90,false);
                    marker.setIcon(myMarker); // 마커 표시
                    // 최초 위치에 마커 추가
                    marker.setTMapPoint(new TMapPoint(lat, lng));
                    tMapView.addTMapMarkerItem(marker);
                    mapReady = false;

                }
            }
        };

        // 위치 권한이 허용된 경우에만 실행 (사용자가 위치 권한을 허용한 경우)
        // onRequestPermissionsResult를 기다리지 않고 위 권한을 바로 확인하고,
        // 권한이 허용되었다면 위치 업데이트를 요청.
        // 초기 위치 업데이트를 요청할 때 권한을 확인하고자 할 때 유용.
        // 사용자에게 권한 요청 대화 상자를 표시하지 않고 권한 상태를 검사할 때 사용.
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    -1,
                    4,
                    locationListener);
            // minTimeMs를 -1로 하고 minDistanceM을 양수로 하면 이동 거리(m)마다 위치를 가져온다.
            // 반대로 minTimeMs를 양수로 하고 minDistanceM을 -1로 하면 MinTimeMs에 지정한 초 마다 위치를 가져온다.
        }

        // 초기화 버튼을 눌렀을 때
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.setBase(SystemClock.elapsedRealtime()); // 타이머 리셋
            }
        });

        if(tMapView!=null){

            // tmapViewContainer(FrameLayout)에 TmapView 추가.
            tmapViewContainer.addView(tMapView);
            // 레이아웃에 지도를 구현
            tMapView.setOnMapReadyListener(new TMapView.OnMapReadyListener() {
                @Override
                public void onMapReady() {
                    //todo 맵 로딩 완료 후 구현

                    // 지도 중심점 표시
                    tMapView.setCenterPoint(lat, lng); // 지도 중심 설정
                    tMapView.setZoomLevel(16); // 줌 레벨 설정

                    TMapData tMapData = new TMapData();
                    tMapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH,
                            startPoint, endPoint, passList, 0, new TMapData.OnFindPathDataWithTypeListener() {
                                @Override
                                public void onFindPathDataWithType(TMapPolyLine tMapPolyLine) {
                                    tMapPolyLine.setLineColor(Color.BLUE); // 경로 폴리라인 색상
                                    tMapPolyLine.setLineWidth(3); // 경로 폴리라인 두께
                                    tMapView.addTMapPolyLine(tMapPolyLine);
                                }
                            });
                    tMapView.removeAllTMapMarkerItem();// 이전 마커 삭제

                    // 마커에 표시할 이미지 크기 조절
                    Bitmap startMarker = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.startmarker);
                    startMarker = Bitmap.createScaledBitmap(startMarker, 90, 90,false);
                    Bitmap turnMarker = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.turnmarker);
                    turnMarker = Bitmap.createScaledBitmap(turnMarker, 60, 60,false);
                    Bitmap finishMarker = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.finishmarker);
                    finishMarker = Bitmap.createScaledBitmap(finishMarker, 90, 90,false);

                    TMapMarkerItem markerStart = new TMapMarkerItem(); // 마커 객체 초기화
                    TMapMarkerItem markerRandom = new TMapMarkerItem(); // 마커 객체 초기화
                    TMapMarkerItem markerEnd = new TMapMarkerItem(); // 마커 객체 초기화
                    markerStart.setId("markerStart"); // 마커 이름 지정
                    markerRandom.setId("markerRandom"); // 마커 이름 지정
                    markerEnd.setId("markerEnd"); // 마커 이름 지정
                    markerStart.setIcon(startMarker); // 마커 아이콘
                    markerStart.setName("출발"); // 마커의 타이틀 지정
                    markerRandom.setIcon(turnMarker); // 마커 아이콘
                    markerStart.setName("경유지"); // 마커의 타이틀 지정
                    markerEnd.setIcon(finishMarker); // 마커 아이콘
                    markerEnd.setName("도착"); // 마커의 타이틀 지정
                    markerStart.setTMapPoint(new TMapPoint(startPoint.getLatitude(), startPoint.getLongitude())); // 마커 표시할 위치
                    markerRandom.setTMapPoint(new TMapPoint(randomPoint.getLatitude(), randomPoint.getLongitude())); // 마커 표시할 위치
                    markerEnd.setTMapPoint(new TMapPoint(endPoint.getLatitude(), endPoint.getLongitude())); // 마커 표시할 위치

                    tMapView.addTMapMarkerItem(markerStart); // 마커 적용
                    tMapView.addTMapMarkerItem(markerRandom); // 마커 적용
                    tMapView.addTMapMarkerItem(markerEnd); // 마커 적용

                    mapReady = true;

                    // GPS 버튼 눌렀을 때
                    btnLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (lat == 0.0) {
                                Snackbar.make(btnLocation,
                                        "현재 위치 가져오는 중. 잠시만 기다려 주세요.",
                                        Snackbar.LENGTH_SHORT).show();
                            }else{
                                tMapView.removeTMapMarkerItem("marker1");
                                marker = new TMapMarkerItem(); // 마커 객체 초기화
                                marker.setId("marker1"); // 마커 이름 지정
                                // 마커 이미지 지정 및 설정
                                Bitmap myMarker = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.mymarker);
                                myMarker = Bitmap.createScaledBitmap(myMarker, 90, 90,false);
                                marker.setIcon(myMarker); // 마커 표시
                                marker.setTMapPoint(new TMapPoint(lat, lng)); // 마커 표시할 위치
                                tMapView.addTMapMarkerItem(marker); // 마커 적용

                                // 지도 중심점 표시( 버튼 누르면)
                                tMapView.setCenterPoint(lat, lng); // 지도 중심 설정
                                tMapView.setZoomLevel(16); // 줌 레벨 설정
                            }
                        }
                    });
                    // 시작 버튼을 눌렀을 때
                    imgStart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (locationReady == true) {

                                if (i == true) {
                                    btnMapReset.setVisibility(GONE);
                                    btnReset.setVisibility(View.VISIBLE); // 초기화 버튼 띄우기

                                    // 정지로 변경
                                    imgStart.setImageResource(R.drawable.baseline_stop_24);
                                    i = false;
                                    chronometer.setBase(SystemClock.elapsedRealtime());  // 타이머 리셋
                                    chronometer.start();  // 타이머 재생

                                } else {
                                    // 산책 기록 저장 여부 물어보기
                                    showAlertDialog();

                                    // 정지 눌렀을 때 다시 시작으로 변경 + 초기화 버튼 숨기기 + 타이머 종료
                                    imgStart.setImageResource(R.drawable.baseline_play_arrow_24);
                                    i = true;
                                    btnReset.setVisibility(View.GONE); // 초기화 버튼 숨기기
                                    btnMapReset.setVisibility(View.VISIBLE); // 경로 삭제 버튼 띄우기
                                    chronometer.stop();
                                    // 현재 타이머 초 저장
                                    String chronometerText = chronometer.getText().toString();
                                    String[] timeParts = chronometerText.split(":");
                                    int hours = Integer.parseInt(timeParts[0]);
                                    int minutes = Integer.parseInt(timeParts[1]);
                                    time = Double.valueOf((hours * 60) + minutes);
                                }
                            } else {
                                Snackbar.make(imgStart,
                                        "아직 로딩 중 입니다. 잠시만 기다려 주세요.",
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });

//                    // 경로 그리기
//                    tMapView.addTMapPolyLine(tMapPolyLine);

                    if (i==true) {
                        // 지도 초기화(이전 경로 삭제 및 현 위치 중심+마커)
                        btnMapReset.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showAlertDialog2();
                            }
                        });
                    }
                }
            });
        }
        bottomNavigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalkingRecoActivity.this, WalkingNoteActivity.class);
                startActivity(intent);
            }
        });
    }


    public void gpsLine() {

        if (btnMapReset.getVisibility() == GONE) {

            // 현재 위치를 TMapPoint 객체로 변환
            TMapPoint currentPoint = new TMapPoint(lat, lng);
            pointList.add(currentPoint);  // 경로에 현재 위치 추가

            // 이전 경로 및 마커 삭제
//            tMapView.removeAllTMapPolyLine();
            tMapView.removeTMapPolyLine("path1");
            tMapView.removeTMapMarkerItem("marker");
            tMapView.removeTMapMarkerItem("marker1");

            // 새로운 폴리라인 생성 및 추가
            line = new TMapPolyLine("path1", pointList);
            line.setLineColor(Color.RED);
            line.setLineWidth(4); // 두께 설정
            tMapView.addTMapPolyLine(line);

            // 지도 중심점 표시
            tMapView.setCenterPoint(lat, lng); // 지도 중심 설정
            tMapView.setZoomLevel(17); // 줌 레벨 설정

            // 마커 생성 및 설정
            marker = new TMapMarkerItem();
            marker.setId("marker1");
            // 마커 이미지 지정 및 설정
            Bitmap myMarker = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.mymarker);
            myMarker = Bitmap.createScaledBitmap(myMarker, 90, 90,false);
            marker.setIcon(myMarker); // 마커 표시

            Log.d("라인길이", "라인 거리" + line.getDistance());


            // 현재 위치에 마커 추가
            marker.setTMapPoint(new TMapPoint(lat, lng));
            tMapView.addTMapMarkerItem(marker);

            distance = (distance + 0.004);
            if (distance < 1.0) {
                txtDistance.setText(Math.round(distance * 1000) + "m");
            } else {
                txtDistance.setText(String.format("%.2fkm", distance));
            }
            Log.d("이동 거리", "현재 걸은 거리: " + distance);
        }
    }

    //     권한이 허용되지 않은 경우를 처리 (여기서는 위치권한)
//     사용자가 권한 요청 대화 상자에서 권한을 허용하거나, 거부한 후 호출되는 콜백
//     권한을 허용하지 않은 경우 권한 요청을 다시 수행하고, 권한이 허용되면 위치 업데이트를 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            // 권한 요청의 결과를 처리하는 부분.

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 부여된 경우

                // 권한(permission)상태를 다시 확인
                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // 권한이 없는 경우 다시 권한을 요청하는 다이얼로그 표시
                    ActivityCompat.requestPermissions(this,
                            new String[]{
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                            },
                            REQUEST_LOCATION_PERMISSION); // 권한 요청 코드 사용
                } else {
                    // 권한이 이미 부여되었을 때
                }
            } else {
                // 권한을 부여하지 않았을 때
                // 다시 권한을 요청하는 다이얼로그 표시
                // 권한이 없는 경우 다시 권한을 요청하는 다이얼로그 표시
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION
                        },
                        REQUEST_LOCATION_PERMISSION); // 권한 요청 코드 사용
            }
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("[안내]");
        builder.setMessage("산책 기록을 저장하시겠습니까?");

        builder.setCancelable(false); // 아래 둘 중 한개의 버튼을 꼭 누르게 해줌(외각을 클릭해도 안사라지게)

        builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 산책기록 저장
                SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                token = sp.getString(Config.ACCESS_TOKEN, "");
                Retrofit retrofit1 = NetworkClient.getRetrofitClient(WalkingRecoActivity.this);
                UserApi api1 = retrofit1.create(UserApi.class);
                Call<UserList> call1 = api1.getUserInfo("Bearer "+token);
                call1.enqueue(new Callback<UserList>() {
                    @Override
                    public void onResponse(Call<UserList> call1, Response<UserList> response) {
                        if (response.isSuccessful()) {

                            userInfoArrayList.clear(); // 초기화

                            UserList userList = response.body();
                            userInfoArrayList.addAll(userList.info);

                            // 서버에서 받아온 데이터를 리스트에 넣고, 각각 적용시키기
                            if (userInfoArrayList.size() != 0) {
                                // 리스트의 사이즈가 0이 아닐때만 화면에 적용 실행
                                int userId = userInfoArrayList.get(0).id;
                                Log.i("test", "성공"+time+" "+distance+" "+userId);
                                Retrofit retrofit = NetworkClient.getRetrofitClient(WalkingRecoActivity.this);
                                WalkingApi api = retrofit.create(WalkingApi.class);
                                WalkingList walkingList = new WalkingList(userId, time, distance);
                                Call<ResultRes> call = api.getWalkingList("Bearer "+token, walkingList);
                                call.enqueue(new Callback<ResultRes>() {
                                    @Override
                                    public void onResponse(Call<ResultRes> call, Response<ResultRes> response) {
                                        if (response.isSuccessful()){
                                            Log.i("test", "성공"+time+distance+userId);
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<ResultRes> call, Throwable t) {
                                        Log.i("test", "실패"+time+distance+userId);
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserList> call1, Throwable t) {

                    }
                });

                // 스낵바 메세지
                Snackbar.make(imgStart,
                        "저장되었습니다",
                        Snackbar.LENGTH_SHORT).show();
                return;
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.show();
    }

    private void showAlertDialog2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("[지도]");
        builder.setMessage("지도를 초기화 하시겠습니까?");

        builder.setCancelable(false); // 아래 둘 중 한개의 버튼을 꼭 누르게 해줌(외각을 클릭해도 안사라지게)

        builder.setPositiveButton("초기화", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // 폴리라인 및 마커를 제거합니다.
                tMapView.removeAllTMapPolyLine();
                tMapView.removeTMapPolygon("path1");
                tMapView.removeAllTMapMarkerItem();
                tMapView.removeTMapMarkerItem("marker");
                tMapView.removeTMapMarkerItem("marker1");

                // 지도 중심점과 줌 레벨을 초기화합니다.
                tMapView.setCenterPoint(lat, lng); // 초기 중심점 설정 (lat와 lng는 초기 중심점 좌표)
                tMapView.setZoomLevel(16); // 초기 줌 레벨 설정 (원하는 값으로 변경)

                // 마커 생성 및 설정
                marker = new TMapMarkerItem();
                marker.setId("marker1");
                // 마커 이미지 지정 및 설정
                Bitmap myMarker = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.mymarker);
                myMarker = Bitmap.createScaledBitmap(myMarker, 90, 90,false);
                marker.setIcon(myMarker); // 마커 표시
                marker.setTMapPoint(new TMapPoint(lat, lng));
                tMapView.addTMapMarkerItem(marker);

                // 스낵바 메세지
                Snackbar.make(btnMapReset,
                        "지도를 초기화 했습니다.",
                        Snackbar.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.show();
    }
}
