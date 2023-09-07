package com.blue.walking;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blue.walking.adapter.CommnetAdapter;
import com.blue.walking.adapter.MyCommentAdapter;
import com.blue.walking.adapter.MyPostAdapter;
import com.blue.walking.model.Firebase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.remote.GrpcCallProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

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
    FirebaseFirestore db;
    int postId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_my_post_2, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // 파이어베이스 객체 생성
        db = FirebaseFirestore.getInstance();

        db.collection("post_comments")
                .document(String.valueOf(postId))
                .collection("comments")
                .orderBy("createdAt", Query.Direction.ASCENDING)// 시간 순으로 정렬
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        // onEvent 데이터베이스의 데이터가 변경되었을 때

                        if (e != null) {
                            Toast.makeText(getActivity(),
                                    "message Load Error: " + e,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // 리스트 초기화
                        myFirebaseArrayList.clear();
                        // 문서의 쿼리스냅샷 가져옴
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Firebase comment = document.toObject(Firebase.class);
//                            Log.i("테스트",firebase.imgUrl);
                            if (comment != null) {
                                // 리스트에 추가
                                myFirebaseArrayList.add(comment);
                            }
                        }

                        // 인덱스는 0부터 시작하기 때문에 마지막 값을 가져오기 위해 -1을 함
//                        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
//                        recyclerView.setAdapter(adapter);
                        // RecyclerView 업데이트
                        adapter.notifyDataSetChanged();

//                        // 댓글 수 업데이트
//                        txtComment.setText("댓글 " + firebaseArrayList.size());
                    }
                });

        adapter = new MyCommentAdapter(getActivity(), myFirebaseArrayList);
        recyclerView.setAdapter(adapter);

        return rootView;
    }
}