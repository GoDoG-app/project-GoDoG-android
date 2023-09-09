package com.blue.walking;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blue.walking.adapter.ChatRoomAdapter;
import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.UserApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.ChatRoom;
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
    ChatRoomAdapter adapter;

    ArrayList<ChatRoom> chatRoomArrayList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_chat, container, false);

        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME,getActivity().MODE_PRIVATE);
        token = sp.getString(Config.ACCESS_TOKEN, "");

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
                    ArrayList<UserInfo> userInfoArrayList = new ArrayList<>();
                    userInfoArrayList.addAll(0, userList.info);

//                    ChatRoom chatRoom = new ChatRoom(id, userNickname, userImgUrl, chatMessage, serverTimestamp());
//                    chatRoom.id = userInfoArrayList.get(0).id;
//                    chatRoom.userImgUrl = userInfoArrayList.get(0).userImgUrl;
//                    chatRoom.userNickname = userInfoArrayList.get(0).userNickname;
//                    chatRoomArrayList.add(chatRoom);

                }
            }

            @Override
            public void onFailure(Call<UserList> call, Throwable t) {

            }
        });

        // Firestore 인스턴스 생성
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Firestore에서 데이터 읽기
        db.collection("chat")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        chatRoomArrayList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Firestore 문서에서 필요한 데이터 추출


//                            // ChatRoom 객체를 chatroomarraylist에 추가
//                            chatRoomArrayList.add(chatRoom);

                        }

                    } else {
                        // Firestore에서 데이터를 읽어오지 못한 경우에 대한 처리
                        Exception e = task.getException();
                        if (e != null) {
                            // 오류 처리
                        }
                    }
                });

        return rootView;
    }

}