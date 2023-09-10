package com.blue.walking;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blue.walking.adapter.ChatAdapter;
import com.blue.walking.adapter.ChatRoomAdapter;
import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.UserApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.Chat;
import com.blue.walking.model.ChatRoom;
import com.blue.walking.model.RandomFriend;
import com.blue.walking.model.UserInfo;
import com.blue.walking.model.UserList;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
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

    String token;
    RecyclerView recyclerView;
    ChatAdapter adapter;
    ArrayList<Chat> chatArrayList = new ArrayList<>();
    int id;
    String userImgUrl;
    String userNickname;
    String chatMessage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_chat, container, false);

        // 토큰 받아오기(레트로핏 위해서)
        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME,getActivity().MODE_PRIVATE);
        token = sp.getString(Config.ACCESS_TOKEN, "");

        // 리사이클러뷰 초기화
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        UserApi api = retrofit.create(UserApi.class);

        Call<UserList> call = api.getUserInfo(token);

        call.enqueue(new Callback<UserList>() {
            @Override
            public void onResponse(Call<UserList> call, Response<UserList> response) {
                if (response.isSuccessful()){
                    UserList userList= response.body();
                    // 유저 정보 가져오기
                    ArrayList<UserInfo> userInfoArrayList = userList.info;
                    if (!userInfoArrayList.isEmpty()) {
                        UserInfo userInfo = userInfoArrayList.get(0);
                        userImgUrl = userInfo.userImgUrl;
                        userNickname = userInfo.userNickname;
                        id = userInfo.id;
                    }
                }
            }

            @Override
            public void onFailure(Call<UserList> call, Throwable t) {

            }
        });


        return rootView;
    }

}