package com.blue.walking;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.blue.walking.model.Park;
import com.skt.tmap.TMapGpsManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Walking_3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Walking_3 extends Fragment implements TMapGpsManager.OnLocationChangedListener {

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





        return rootView;
    }

    @Override
    public void onLocationChange(Location location) {

    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }
}