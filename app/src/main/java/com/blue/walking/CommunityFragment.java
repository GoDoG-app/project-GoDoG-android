package com.blue.walking;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStateManagerControl;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommunityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunityFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CommunityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommunityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommunityFragment newInstance(String param1, String param2) {
        CommunityFragment fragment = new CommunityFragment();
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
    BottomNavigationView bottomNavigationView;

    /** 프레그먼트 */
    CommuFragment_1 commuFragment_1;  // 커뮤 전체
    CommuFragment_2 commuFragment_2;  // 커뮤 일상
    CommuFragment_3 commuFragment_3;  // 커뮤 정보
    CommuFragment_4 commuFragment_4;  // 커뮤 산책

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_community, container, false);

        bottomNavigationView = rootView.findViewById(R.id.bottomNavigationView);
        commuFragment_1 = new CommuFragment_1();
        commuFragment_2 = new CommuFragment_2();
        commuFragment_3 = new CommuFragment_3();
        commuFragment_4 = new CommuFragment_4();

        // 시작화면은 전체 카테고리로
        getChildFragmentManager().beginTransaction().replace(R.id.containers, commuFragment_1).commit();

        // 탭바 기능 구현(프레그먼트 화면 띄우기)
        NavigationBarView navigationBarView = rootView.findViewById(R.id.bottomNavigationView);
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();
                if (itemId == R.id.Commu1) {
                    // 전체로 이동
                    // getSupportFragmentManager(메인엑티비티) -> getChildFragmentManager(메인 안 하위 프레그먼트)
                    getChildFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, commuFragment_1)
                            .commit();
                    return true;

                } else if (itemId == R.id.Commu2) {
                    // 일상으로 이동
                    getChildFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, commuFragment_2)
                            .commit();
                    return true;

                } else if (itemId == R.id.Commu3) {
                    // 정보로 이동
                    getChildFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, commuFragment_3)
                            .commit();
                    return true;

                } else if (itemId == R.id.Commu4) {
                    // 산책으로 이동
                    getChildFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, commuFragment_4)
                            .commit();
                    return true;

                }
                return false;
            }
        });


        return rootView;
    }
}