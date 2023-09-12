package com.blue.walking;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.skt.tmap.TMapPoint;
import com.skt.tmap.overlay.TMapPolyLine;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WalkingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalkingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WalkingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WalkingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WalkingFragment newInstance(String param1, String param2) {
        WalkingFragment fragment = new WalkingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static WalkingFragment newInstance(ArrayList<TMapPoint> linePointList1, ArrayList<TMapPoint> passPointList) {
        WalkingFragment fragment = new WalkingFragment();
        Bundle args = new Bundle();
        args.putSerializable("Line", linePointList1);
        args.putSerializable("line1", passPointList);
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
    BottomNavigationView bottomNavigationView;

    /** 프레그먼트 */
    Walking_1 walking_1;
    Walking_2 walking_2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_walking, container, false);

        bottomNavigationView = rootView.findViewById(R.id.bottomNavigationView);
        walking_1 = new Walking_1();
        walking_2 = new Walking_2();

        // 산책하기를 시작화면으로
        getChildFragmentManager().beginTransaction().replace(R.id.containers, walking_1).commit();

        // 탭바 기능 구현(프레그먼트 화면 띄우기)
        NavigationBarView navigationBarView = rootView.findViewById(R.id.bottomNavigationView);
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();
                if (itemId == R.id.walking1) {
                    // 산책하기로 이동
                    // getSupportFragmentManager(메인엑티비티) -> getChildFragmentManager(메인 안 하위 프레그먼트)
                    getChildFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, walking_1)
                            .commit();
                    return true;

                } else if (itemId == R.id.walking2) {
                    // 산책기록으로 이동
                    getChildFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, walking_2)
                            .commit();
                    return true;

                }
                return false;
            }
        });


        return rootView;
    }

}