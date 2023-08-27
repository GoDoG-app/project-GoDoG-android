package com.blue.walking;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;

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
    TextView txtTime;    // 산책 시간
    TextView txtDistance;  // 산책 거리
    Button btnReset;  // 산책 초기화
    TextView txtPlace;  // 산책로 추천을 통해 산책하기를 시작했을때, 나오는 추천 장소 이름

    boolean i = true;  // 산책 시작 이미지 변경에 사용

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_walking_1, container, false);

        imgStart = rootView.findViewById(R.id.imgStart);
        btnReset = rootView.findViewById(R.id.btnReset);

        // 시작을 클릭했을 때
        imgStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnReset.setVisibility(View.VISIBLE); // 초기화 버튼 띄우기


                if (i == true){
                    // 정지로 변경
                    imgStart.setImageResource(R.drawable.baseline_stop_24);
                    i = false;

                } else {
                    // 정지 눌렀을때 다시 시작으로 변경 + 초기화 버튼 숨기기
                    imgStart.setImageResource(R.drawable.baseline_play_arrow_24);
                    i = true;
                    btnReset.setVisibility(View.GONE);
                }
            }
        });

        return rootView;
    }
}