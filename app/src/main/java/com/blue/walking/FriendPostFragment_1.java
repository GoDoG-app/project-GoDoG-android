package com.blue.walking;

import static android.content.Context.MODE_PRIVATE;

import static java.util.Calendar.getInstance;

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

import com.blue.walking.adapter.FriendPostAdapter;
import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.PostApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.Post;
import com.blue.walking.model.PostList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendPostFragment_1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendPostFragment_1 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendPostFragment_1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendPostFragment_1.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendPostFragment_1 newInstance(String param1, String param2) {
        FriendPostFragment_1 fragment = new FriendPostFragment_1();
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

    FriendPostAdapter adapter;

    // 페이징 처리에 필요한 변수
    int offset = 0;
    int limit = 15;
    int count = 0;
    String token;
    int friendId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_friend_post_1, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // FriendPostActivity 에게 친구 아이디를 받아옴
        friendId = this.getArguments().getInt("friendId");

        Log.i("friendID", friendId+"");
        Log.i("recyclerView", "친구 작성글 카드뷰 띄우기 실행");

        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString(Config.ACCESS_TOKEN, "");

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();

                if (lastPosition+1 == totalCount){
                    // 데이터를 추가로 불러오기
                    // 카운트한게 리미트보다 작냐 => 작으면 데이터 불러올 필요 없음
                    if (count == limit){
                        addNetworkData();
                    }
                }
            }
        });

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        offset =0;
        count = 0;
        Log.i("ONRESUME","111");

        getNetworkData();
    }

    private void getNetworkData() {
        friendPostArrayList.clear();

        /** 특정 유저 게시물 가져오는 API */
        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());

        PostApi api = retrofit.create(PostApi.class);

        Call<PostList> call = api.getFriendPostList(friendId, offset, limit, "Bearer "+token);
        call.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                if (response.isSuccessful()){
                    Log.i("Call", "서버 실행 성공");

                    PostList postList = response.body();

                    // 페이징처리
                    count = postList.count;
                    offset = offset + count;

                    friendPostArrayList.addAll(postList.items);
                    adapter = new FriendPostAdapter(getActivity(), friendPostArrayList);

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

    }

    private void addNetworkData() {
        /** 특정 유저 게시물 가져오는 API */
        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());

        PostApi api = retrofit.create(PostApi.class);

        Call<PostList> call = api.getFriendPostList(friendId, offset, limit, "Bearer "+token);
        call.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                if (response.isSuccessful()){
                    Log.i("Call", "서버 실행 성공");

                    PostList postList = response.body();

                    // 페이징처리
                    count = postList.count;
                    offset = offset + count;

                    friendPostArrayList.addAll(postList.items);
                    adapter = new FriendPostAdapter(getActivity(), friendPostArrayList);

                } else {
                    Log.i("Call", "서버 실행 실패");
                }

            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {
                Log.i("Call", "서버 실행 실패");
            }
        });
    }
}