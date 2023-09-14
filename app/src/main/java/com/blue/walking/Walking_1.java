package com.blue.walking;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.UserApi;
import com.blue.walking.api.WalkingApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.ResultRes;
import com.blue.walking.model.User;
import com.blue.walking.model.UserInfo;
import com.blue.walking.model.UserList;
import com.blue.walking.model.WalkingList;
import com.google.android.gms.maps.MapView;
import com.google.android.material.snackbar.Snackbar;
import com.skt.tmap.TMapPoint;
import com.skt.tmap.TMapView;
import com.skt.tmap.overlay.TMapMarkerItem;
import com.skt.tmap.overlay.TMapPolyLine;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Walking_1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Walking_1 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Walking_1() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Walking_1.
     */
    // TODO: Rename and change types and number of parameters
    public static Walking_1 newInstance(String param1, String param2) {
        Walking_1 fragment = new Walking_1();
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
    MapView mapView;  // 지도 화면
    ImageView imgStart;  // 산책 시작
    TextView txtDistance;  // 산책 거리
    String token;
    ArrayList<UserInfo> userInfoArrayList= new ArrayList<>();
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

    double distance = 0.0 ;

    TMapPolyLine tMapPolyLine1 ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_walking_1, container, false);

        imgStart = rootView.findViewById(R.id.imgStart);
        btnReset = rootView.findViewById(R.id.btnReset);
        chronometer = rootView.findViewById(R.id.chronometer);
        txtDistance = rootView.findViewById(R.id.txtDistance);
        // 지도가 나타날 화면
        tmapViewContainer = rootView.findViewById(R.id.tmapViewContainer);
        // 현재 위치 버튼
        btnLocation = rootView.findViewById(R.id.btnLocation);
        // 맵 로딩 프로그레스바
        progressBar = rootView.findViewById(R.id.progressBar);

        btnLine = rootView.findViewById(R.id.btnLine);


        // 권한(permission)상태 확인
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 경우 권한을 요청하는 다이얼로그 표시
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    REQUEST_LOCATION_PERMISSION); // 권한 요청 코드 사용
        }

        // locationManager 객체 생성 및 변수 할당
        locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        // 실시간 위치
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                lat = location.getLatitude();
                lng = location.getLongitude();
                Log.i("위치 확인", "위도 + 경도 : " + lat + " " +lng);
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
                    Bitmap myMarker = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.mymarker);
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
        if (ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    -1,
                    4,
                    locationListener);
            // minTimeMs를 -1로 하고 minDistanceM을 양수로 하면 이동 거리(m)마다 위치를 가져온다.
            // 반대로 minTimeMs를 양수로 하고 minDistanceM을 -1로 하면 MinTimeMs에 지정한 초 마다 위치를 가져온다.
        }

        // TmapView 객체(context로 하면 안나옴)
        // 프래그먼트는 activity가 아니라서 activity를 가져와야함.
        tMapView = new TMapView(getActivity());
        // tmapViewContainer(FrameLayout)에 TmapView 추가.
        tmapViewContainer.addView(tMapView);
        // AppKey 가져오기
        tMapView.setSKTMapApiKey(Config.getAppKey());


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
                        Bitmap myMarker = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.mymarker);
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
                                showAlertDialog();

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
        return rootView;
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
            Bitmap myMarker = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.mymarker);
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
                if (ContextCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // 권한이 없는 경우 다시 권한을 요청하는 다이얼로그 표시
                    ActivityCompat.requestPermissions(getActivity(),
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
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION
                        },
                        REQUEST_LOCATION_PERMISSION); // 권한 요청 코드 사용
            }
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("[안내]");
        builder.setMessage("산책 기록을 저장하시겠습니까?");

        builder.setCancelable(false); // 아래 둘 중 한개의 버튼을 꼭 누르게 해줌(외각을 클릭해도 안사라지게)

        builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 산책기록 저장
                SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                token = sp.getString(Config.ACCESS_TOKEN, "");
                Retrofit retrofit1 = NetworkClient.getRetrofitClient(getActivity());
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
                                Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("[지도]");
        builder.setMessage("지도를 초기화 하시겠습니까?");

        builder.setCancelable(false); // 아래 둘 중 한개의 버튼을 꼭 누르게 해줌(외각을 클릭해도 안사라지게)

        builder.setPositiveButton("초기화", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // 초기화 누를 경우
                /** Fragment 끼리 이동하기 위해서는 Activity 내 Fragment manager 를 통해 이동하게 되며,
                 * Activity 위에서 이동하기 때문에 intent 가 아닌 메소드를 통해서 이동하게 된다. */

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                WalkingFragment walkingFragment = new WalkingFragment();
                // 프레그먼트 화면을 walkingFragment 로 활성화
                transaction.replace(R.id.containers, walkingFragment);
                // 꼭 commit 을 해줘야 바뀜
                transaction.commit();

                // 스낵바 메세지
                Snackbar.make(btnLine,
                        "지도를 초기화 했습니다.",
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

    // 위치 리스너를 제거, 메모리 누수를 방지
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 위치 리스너 제거
        locationManager.removeUpdates(locationListener);
    }


}