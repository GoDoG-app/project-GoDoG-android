package com.blue.walking;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blue.walking.adapter.CommnetAdapter;
import com.blue.walking.adapter.CommuAdapter;
import com.blue.walking.adapter.MyCommentAdapter;
import com.blue.walking.adapter.MyPostAdapter;
import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.PostApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.Firebase;
import com.blue.walking.model.Post;
import com.blue.walking.model.PostList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.remote.GrpcCallProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyPostFragment_2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPostFragment_2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyPostFragment_2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyPostFragment_2.
     */
    // TODO: Rename and change types and number of parameters
    public static MyPostFragment_2 newInstance(String param1, String param2) {
        MyPostFragment_2 fragment = new MyPostFragment_2();
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


    MyCommentAdapter adapter;
    ArrayList<Post> myPostArrayList = new ArrayList<>();

    int offset = 0;
    int limit = 15;
    String token;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_my_post_2, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Log.i("aaa", "댓글보기 화면");

        // 파이어베이스 객체 생성
        db = FirebaseFirestore.getInstance();

        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString(Config.ACCESS_TOKEN, "");

        /** 전체 게시물 가져오는 API */
        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());

        PostApi api = retrofit.create(PostApi.class);

        Call<PostList> call = api.getPostList(offset, limit, "Bearer "+token);

        call.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                if (response.isSuccessful()){
                    Log.i("Call", "서버 실행 성공");

                    // 데이터 한 번 초기화
                    myPostArrayList.clear();

                    PostList postList = response.body();

                    myPostArrayList.addAll(postList.items);
                    adapter = new MyCommentAdapter(getActivity(), myPostArrayList);

                    // 프레그먼트에서 리사이클러뷰를 만들면 layoutManager 를 사용해야함
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);

                    recyclerView.setAdapter(adapter);

                    Log.i("Call", "aaa");

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