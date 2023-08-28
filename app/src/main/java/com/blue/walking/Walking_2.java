package com.blue.walking;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Walking_2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Walking_2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Walking_2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Walking_2.
     */
    // TODO: Rename and change types and number of parameters
    public static Walking_2 newInstance(String param1, String param2) {
        Walking_2 fragment = new Walking_2();
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
    TextView txtName;  // 강아지 이름 + ~의 산책기록
    ImageView imgPet;  // 강아지 사진
    TextView txtCount;  // 총 횟수
    TextView txtDistance;  // 총 거리
    TextView txtTime;   // 총 시간

    TextView txtDate;   // 산책일지 날짜
    CalendarView calendarView;  // 달력

    RecyclerView recyclerView;  // 산책일지 목록 띄우는 리사이클러뷰


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_walking_2, container, false);

        txtDate = rootView.findViewById(R.id.txtDate);
        calendarView = rootView.findViewById(R.id.calendarView);


        // 캘린더 뷰에서 날짜 클릭하면 해당 년도와 월을 보여줌
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                month = month+1;
                String month2;
                if (month < 10){
                    month2 = "0"+month;
                }else {
                    month2 = ""+month;
                }

                String date = year + "." + (month2);
                txtDate.setText(date+" 산책일지");
            }
        });


        return rootView;
    }
}