package com.blue.walking;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blue.walking.adapter.CommuAdapter;
import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.PostApi;
import com.blue.walking.model.Post;
import com.blue.walking.model.PostList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommuFragment_1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommuFragment_1 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CommuFragment_1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommuFragment_1.
     */
    // TODO: Rename and change types and number of parameters
    public static CommuFragment_1 newInstance(String param1, String param2) {
        CommuFragment_1 fragment = new CommuFragment_1();
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

    CommuAdapter adapter;
    ArrayList<Post> postArrayList = new ArrayList<>();

    // 페이징 처리에 필요한 변수
    int offset = 0;
    int limit = 15;
    int count = 0;
    String token;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup)  inflater.inflate(R.layout.fragment_commu_1, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // 데이터 추가로 불러오기 위해 추가된 리사이클러뷰 스크롤
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount(); // PostAdapter에서 가져옴

                if (lastPosition+1 == totalCount){
                    // 데이터를 추가로 불러오기
                    // 카운트한게 리미트보다 작냐 => 작으면 데이터 불러올 필요 없음
                    if (count == limit){
                        addNetworkData();
                    }
                }
            }
        });
        getNetworkData();

        return rootView;
    }


    private void addNetworkData() {
        // 레트로핏 변수 생성
        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        // API 만들기
        PostApi api = retrofit.create(PostApi.class);
        // API 실행
        Call<PostList> call = api.getPostList(offset, limit, "Bearer "+token);

        call.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                if (response.isSuccessful()){
                    PostList postList = response.body();

                    // 페이징 처리
                    count = postList.count;
                    offset = offset + count;

                    // postList 안에 있는 items 리스트를 전부 가져오고
                    // 업데이트 하기
                    postArrayList.addAll(postList.items);
                    adapter.notifyDataSetChanged();

                } else{

                }
            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {
            }
        });

    }

    private void getNetworkData() {
        // 리스트 초기화
        postArrayList.clear();
        // 레트로핏 변수 생성
        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        // API 만들기
        PostApi api = retrofit.create(PostApi.class);
        // API 실행
        Call<PostList> call = api.getPostList(offset, limit, "Bearer "+token);

        call.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                if (response.isSuccessful()){
                    PostList postList = response.body();

                    // 페이징 처리
                    count = postList.count;
                    offset = offset + count;

                    // postList 안에 있는 items 리스트를 전부 가져오고
                    // 업데이트 하기
                    postArrayList.addAll(postList.items);
                    adapter.notifyDataSetChanged();

                } else{

                }
            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {
            }
        });
    }


}