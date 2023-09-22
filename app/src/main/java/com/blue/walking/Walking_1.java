package com.blue.walking;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


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


    /**
     * 화면뷰
     */

    ImageView markerGirl;
    ImageView markerBoy;
    ImageView imgStart;
    RecyclerView recyclerView;

    boolean isGirl; // 클릭시 테두리(girl)
    boolean isBoy; // 클릭시 테두리(boy)

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_walking_1, container, false);

        markerGirl = rootView.findViewById(R.id.markerGirl);
        markerBoy = rootView.findViewById(R.id.markerBoy);
        imgStart = rootView.findViewById(R.id.imgStart);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        markerGirl.setClipToOutline(true);  // 둥근 테두리 적용
        markerBoy.setClipToOutline(true);  // 둥근 테두리 적용

        markerGirl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // girl 이미지뷰를 클릭할 때마다 girl의 테두리 상태를 토글
                isGirl = !isGirl; // true일땐 false, false일땐 true로 반전

                if (isGirl) {
                    markerGirl.setBackgroundResource(R.drawable.border); // girl에 테두리를 추가
                    markerBoy.setBackgroundResource(0); // boy의 테두리 제거
                } else {
                    markerGirl.setBackgroundResource(0); // girl의 테두리를 제거
                }
            }
        });

        markerBoy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // boy 이미지뷰를 클릭할 때마다 boy의 테두리 상태를 토글
                isBoy = !isBoy; // true일땐 false, false일땐 true로 반전

                if (isBoy == true) {
                    markerBoy.setBackgroundResource(R.drawable.border); // boy에 테두리를 추가
                    markerGirl.setBackgroundResource(0); // girl의 테두리 제거
                } else {
                    markerBoy.setBackgroundResource(0); // boy의 테두리를 제거
                }
            }
        });

        imgStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WalkingActivity_1.class );
                if (isBoy != true && isGirl != true){
                    Toast.makeText(getActivity(), "마커를 선택하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isGirl == true){
                    intent.putExtra("마커선택", 1);
                    startActivity(intent);
                } else if (isBoy == true){
                    intent.putExtra("마커선택", 2);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }
}
