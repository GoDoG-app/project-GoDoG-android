package com.blue.walking;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blue.walking.adapter.FriendCommentAdapter;
import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.PostApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.Post;
import com.blue.walking.model.PostList;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendPostFragment_2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendPostFragment_2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendPostFragment_2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendPostFragment_2.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendPostFragment_2 newInstance(String param1, String param2) {
        FriendPostFragment_2 fragment = new FriendPostFragment_2();
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
    RecyclerView recyclerView;

    ArrayList<Post> friendPostArrayList= new ArrayList<>();

    FriendCommentAdapter adapter;

    String token;
    int friendId;
    int offset = 0;
    int limit = 15;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_friend_post_2, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // FriendPostActivity 에게 친구 아이디를 받아옴
        friendId = this.getArguments().getInt("friendId");

        Log.i("friendID", friendId+"");
        Log.i("recyclerView", "친구 댓글 카드뷰 띄우기 실행");


        // 파이어베이스 객체 생성
        db = FirebaseFirestore.getInstance();

        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString(Config.ACCESS_TOKEN, "");

        /** 특정 유저 게시물 가져오는 API */
        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());

        PostApi api = retrofit.create(PostApi.class);

        Call<PostList> call = api.getFriendPostList(friendId, offset, limit, "Bearer "+token);
        call.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                if (response.isSuccessful()){
                    Log.i("Call", "서버 실행 성공");

                    friendPostArrayList.clear();

                    PostList postList = response.body();

                    friendPostArrayList.addAll(postList.items);
                    adapter = new FriendCommentAdapter(getActivity(), friendPostArrayList, friendId);

                    // 프레그먼트에서 리사이클러뷰를 만들면 layoutManager 를 사용해야함
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);

                    recyclerView.setAdapter(adapter);

                } else {
                    Log.i("Call", "서버 실행 실패");
                }

            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {
                Log.i("Call", "서버 실행 실패");
            }
        });

        return rootView;
    }
}