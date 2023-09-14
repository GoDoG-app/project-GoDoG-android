package com.blue.walking;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import com.blue.walking.adapter.ChatRoomAdapter;
import com.blue.walking.adapter.WalkingAdapter;
import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.PetApi;
import com.blue.walking.api.WalkingApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.Pet;
import com.blue.walking.model.PetList;
import com.blue.walking.model.WalkingList;
import com.blue.walking.model.WalkingRes;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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
    ArrayList<WalkingList> walkingListArrayList = new ArrayList<>();
    WalkingAdapter adapter;

    String token;
    ArrayList<Pet> petArrayList = new ArrayList<>();
    ArrayList<WalkingList> walkingLists = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_walking_2, container, false);

        txtName = rootView.findViewById(R.id.txtName);
        txtCount = rootView.findViewById(R.id.txtCount);
        txtDistance = rootView.findViewById(R.id.txtDistance);
        txtTime = rootView.findViewById(R.id.txtTime);
        txtDate = rootView.findViewById(R.id.txtDate);
        calendarView = rootView.findViewById(R.id.calendarView);

        imgPet = rootView.findViewById(R.id.imgPet);
        imgPet.setClipToOutline(true);  // 둥근 테두리 적용

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        // 산책 기록 펫 정보 띄우기
        /** 내 펫 정보 API */
        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        PetApi api = retrofit.create(PetApi.class);

        Log.i("pet","내 펫 정보 API 실행");

        // 유저 토큰
        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString(Config.ACCESS_TOKEN, "");

        Call<PetList> call = api.petInfo("Bearer " + token);
        call.enqueue(new Callback<PetList>() {
            @Override
            public void onResponse(Call<PetList> call, Response<PetList> response) {
                if (response.isSuccessful()) {

                    petArrayList.clear(); // 초기화

                    // 서버에서 받아온 데이터를 리스트에 넣고, 각각 적용시키기
                    PetList petList = response.body();
                    petArrayList.addAll(petList.items);

                    if (petArrayList.size() != 0) {
                        // 리스트의 사이즈가 0이 아닐때만 화면에 적용 실행

                        Log.i("pet", "펫 정보 불러오기 완료");
                        txtName.setText(petArrayList.get(0).petName + "의 산책 기록");
                        Glide.with(Walking_2.this).load(petArrayList.get(0).petProUrl).into(imgPet);

                        Retrofit retrofit2 = NetworkClient.getRetrofitClient(getActivity());
                        WalkingApi api2 = retrofit2.create(WalkingApi.class);

                        Call<WalkingRes> call2 = api2.getMyWalkingList("Bearer "+token);
                        call2.enqueue(new Callback<WalkingRes>() {
                            @Override
                            public void onResponse(Call<WalkingRes> call, Response<WalkingRes> response) {

                                if (response.isSuccessful()){
                                    // 이전데이터 초기화
                                    walkingLists.clear();
                                    WalkingRes walkingList = response.body();
                                    Log.i("test22", ""+response.body());
                                    walkingLists.addAll(walkingList.items);
                                    // 카운트
                                    txtCount.setText(walkingList.count+"회");

                                    // 시간
                                    Log.i("test22", ""+walkingList.count);
                                    int totalTime = 0;
                                    for (WalkingList walkingItem : walkingLists) {
                                        int timeInSeconds = (int) walkingItem.time; // time 데이터를 초 단위로 가져옴
                                        totalTime += timeInSeconds;
                                        // 초를 시, 분, 초로 분해
                                        int hours = totalTime / 3600;  // 1 시간은 3600 초입니다.
                                        int minutes = (totalTime % 3600) / 60;  // 1 분은 60 초입니다.
                                        int seconds = totalTime % 60;

                                        // 시간, 분, 초를 HH:mm:ss 형식의 문자열로 포맷
                                        String timeStr = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                                        txtTime.setText(timeStr);
                                    }

                                    // 거리
                                    double totalDistance = 0;
                                    for (WalkingList walkingItem : walkingLists) {
                                        double distance = walkingItem.distance;
                                        totalDistance += distance;

                                    }

                                    txtDistance.setText(totalDistance+"km");

                                    walkingListArrayList = walkingLists;
                                    // 어댑터에 데이터 변경을 알립니다.
                                    adapter = new WalkingAdapter(getActivity(), walkingListArrayList);
                                    // 어댑터 연결
                                    recyclerView.setAdapter(adapter);
                                    // 리사이클러뷰 어레이 리스트의 크기의 -1 위치로 이동
                                    recyclerView.scrollToPosition(walkingListArrayList.size()-1);
                                    adapter.notifyDataSetChanged();




                                }
                            }

                            @Override
                            public void onFailure(Call<WalkingRes> call, Throwable t) {

                            }
                        });


                    } else {
                        return;
                    }

                } else {
                    Log.i("pet","펫 정보 불러오기 실패");
                }
            }

            @Override
            public void onFailure(Call<PetList> call, Throwable t) {
                Log.i("pet","펫 정보 불러오기 실패");
            }
        });



        // 캘린더 뷰에서 날짜 클릭하면 해당 년도와 월을 보여줌
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String month2;
                if (month < 10) {
                    month2 = "0" + month;
                } else {
                    month2 = "" + month;
                }

                String date = year + "." + (month2);
                txtDate.setText(date + " 산책일지");


            }
        });




        return rootView;
    }


}