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
    ArrayList<Firebase> myFirebaseArrayList = new ArrayList<>();
//
//    int offset = 0;
//    int limit = 15;
//    String token;
//    ArrayList<Post> postArrayList = new ArrayList<>();
//    int postId;
//    String user;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_my_post_2, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Log.i("aaa", "댓글보기 화면");

        adapter = new MyCommentAdapter(getActivity(), myFirebaseArrayList);
        recyclerView.setAdapter(adapter);

        // 파이어스토어
        db = FirebaseFirestore.getInstance();

        Log.i("aaa", "파이어스토어 실행");

        // 리스트 초기화
        myFirebaseArrayList.clear();

        return rootView;

    }


    // "post_comments" 컬렉션의 4개의 문서를 가져온 후, 각 문서의 ID를 사용하여 fetchSubCollectionData 함수를 호출하고
    public void fetchDataFromFirestore() {
        CollectionReference postCommentsCollectionRef = db.collection("post_comments");

        postCommentsCollectionRef.limit(4).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot postCommentDocument : task.getResult().getDocuments()) {
                                fetchSubCollectionData(postCommentDocument.getId());

                                Log.i("TAG", "fetchSubCollectionData 함수 호출");
                            }
                        } else {
                            Log.i("TAG", "post_comments 컬렉션에서 데이터 가져오기 실패");
                        }
                    }
                });
    }

    // 해당 문서의 "comments" 서브컬렉션 데이터를 가져옵니다.
    private void fetchSubCollectionData(String postId) {
        CollectionReference commentsCollectionRef = db.collection("post_comments")
                .document(postId)
                .collection("comments");

        commentsCollectionRef.whereEqualTo("userNickname", "hora").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            Log.i("TAG", "fetchSubCollectionData 함수 실행");

                            for (DocumentSnapshot commentDocument : task.getResult().getDocuments()) {
                                String commentData = commentDocument.getString("comment_field_name");
                                // commentData를 사용하여 작업 수행

                                //todo : 로직 만들 예정..

                            }
                        } else {
                            // "comments" 서브컬렉션에서 데이터 가져오기 실패
                            Log.i("TAG", "comments 서브컬렉션에서 데이터 가져오기 실패");
                        }
                    }
                });
            }
        }



//        // "post_comments" 컬렉션을 참조
//        CollectionReference parentCollectionRef = db.collection("post_comments");
//
//        parentCollectionRef.limit(4).get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//
//                    for (QueryDocumentSnapshot postCommentDocument  : task.getResult()) {
//                        // 각 부모 문서에서 서브컬렉션을 참조
////                        CollectionReference commentsCollectionRef = postCommentDocument .getReference().collection("comments");
//
//                        // 각 "post_comments" 문서에서 데이터를 가져옵니다.
//                        String documentData = postCommentDocument.getString("GGjI8RO5k6zdPJchNPg0"); // 필드 이름을 수정하세요
//
//                        Log.i("필드", "각 \"post_comments\" 문서에서 데이터를 가져옵니다. : " + documentData);
//
//                        // 이곳에서 데이터를 처리하거나, 서브컬렉션 쿼리를 실행합니다.
//                        // 예를 들어, 서브컬렉션 "comments"에서 데이터를 가져오는 쿼리를 실행할 수 있습니다.
//                        CollectionReference commentsCollectionRef = postCommentDocument.getReference().collection("comments");
//                        commentsCollectionRef.whereEqualTo("userNickname", "hora").get()
//                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                        if (task.isSuccessful()) {
//                                            // "comments" 서브컬렉션에서 데이터를 가져옵니다.
//                                            for (QueryDocumentSnapshot commentDocument : task.getResult()) {
//                                                String commentData = commentDocument.getString("userNickname"); // 필드 이름을 수정하세요
//
//                                                // commentData를 사용하여 작업 수행
//
//                                                Log.i("TAG", "comments 데이터 가져오기");
//                                                Log.i("TAG", commentDocument.getId() + " => " + commentDocument.getData());
//
//                                                Firebase firebase = commentDocument.toObject(Firebase.class);
//                                                if (firebase != null) {
//                                                    // 리스트에 추가
//                                                    myFirebaseArrayList.add(firebase);
//                                                }
//                                            }
//                                            adapter.notifyDataSetChanged();
//                                            Log.i("aaa", "어뎁터 업데이트");
//                                        } else {
//                                            // "comments" 서브컬렉션에서 데이터 가져오기 실패
//                                        }
//                                    }
//                                });
//                    }
//                } else {
//                    Log.i("TAG", "부모 컬렉션 가져오기 실패");
//                }
//            }
//        });


//        db.collection("post_comments")
//                .document()
//                .collection("comments")
//                .whereEqualTo("userNickname", "hora")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()){
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.i("TAG", document.getId() + " => " + document.getData());
//
//                                // 리스트 초기화
//                                myFirebaseArrayList.clear();
//
//                                // 문서의 쿼리스냅샷 가져옴
//                                for (DocumentSnapshot document2 : task.getResult()) {
//                                    Firebase comment = document2.toObject(Firebase.class);
//                                    if (comment != null) {
//                                        // 리스트에 추가
//                                        myFirebaseArrayList.add(comment);
//                                    }
//                                }
//                                adapter.notifyDataSetChanged();
//                                Log.i("aaa", "어뎁터 업데이트");
//                            }
//                        }else {
//                            Log.i("TAG", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });


//        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
//        token = sp.getString(Config.ACCESS_TOKEN, "");
//
//        /** 전체 게시물 가져오는 API */
//        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
//
//        PostApi api = retrofit.create(PostApi.class);
//
//        Call<PostList> call = api.getPostList(offset, limit, "Bearer "+token);
//
//        call.enqueue(new Callback<PostList>() {
//            @Override
//            public void onResponse(Call<PostList> call, Response<PostList> response) {
//                if (response.isSuccessful()){
//                    PostList postList = response.body();
//
//                    Log.i("Call", "서버 실행 성공");
//
//                    postArrayList.addAll(postList.items);
//
//                    postId = postArrayList.get(2).post_id;
//                    user = postArrayList.get(2).user_nickname;
//
//                    Log.i("Call", String.valueOf(postId));
//                    Log.i("Call", user);
//
//                } else {
//                    Log.i("Call", "서버 실행 실패");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<PostList> call, Throwable t) {
//                Log.i("Call", "서버 실행 실패");
//            }
//        });



