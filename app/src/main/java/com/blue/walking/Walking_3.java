package com.blue.walking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blue.walking.model.Park;
import com.google.android.gms.maps.MapView;
import com.skt.tmap.TMapPoint;
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

    MapView mapView1;   // 첫번째 산책로 지도
    TextView txtPlace1; // 첫번재 산책로 이름
    TextView txtTime1;  // 첫번째 산책로 이동 시간
    TextView txtKm1;    // 첫번째 산책로 거리

    MapView mapView2;   // 두번째 산책로 지도
    TextView txtPlace2; // 두번째 산책로 이름
    TextView txtTime2;  // 두번째 산책로 이동 시간
    TextView txtKm2;    // 두번째 산책로 거리


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup)  inflater.inflate(R.layout.fragment_walking_3, container, false);

        // 데이터를 받는 프래그먼트 (Walking_3)
        Bundle args = getArguments();
        if (args != null) {
            ArrayList<Park> receivedParkArrayList = (ArrayList<Park>) args.getSerializable("parkList");
            if (receivedParkArrayList != null) {
                // 데이터 사용
                // for 루프 외부에서 parkArrayList를 출력
                for (Park park : receivedParkArrayList) {
                    Log.i("워킹3프래그", "이름: " + park.getName() + ", 주소: " + park.getAddress() + ", 입구 좌표: " + park.getLat_lng());
                }
                // parkArrayList에 들어있는 데이터 확인
                Log.i("워킹3프래그", "parkArrayList 크기: " + receivedParkArrayList.size());
            }
        }

        mapView1 = rootView.findViewById(R.id.mapView1);
        mapView2 = rootView.findViewById(R.id.mapView2);

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