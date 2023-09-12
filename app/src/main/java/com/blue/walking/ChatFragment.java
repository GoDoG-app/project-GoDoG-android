package com.blue.walking;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.blue.walking.adapter.ChatAdapter;
import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.UserApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.Chat;
import com.blue.walking.model.UserInfo;
import com.blue.walking.model.UserList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    FirebaseFirestore db;
    String lastMessage;
    Timestamp lastCreatedAt;
    int friendId;
    ArrayList<UserInfo> userInfoArrayList= new ArrayList<>();

    int documentsProcessed = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_chat, container, false);


        // 토큰 받아오기(레트로핏 위해서)
        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, getActivity().MODE_PRIVATE);
        token = sp.getString(Config.ACCESS_TOKEN, "");

        // 내 아이디 가져오기
        loadUserDataAndChat();

        // 리사이클러뷰 초기화
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }

    public void loadUserDataAndChat() {
        // 내 아이디 가져오기
        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        UserApi api = retrofit.create(UserApi.class);

        Call<UserList> call = api.getUserInfo("Bearer " + token);

        call.enqueue(new Callback<UserList>() {
            @Override
            public void onResponse(Call<UserList> call, Response<UserList> response) {
                if (response.isSuccessful()) {
                    UserList userList = response.body();
                    // 유저 정보 가져오기
                    ArrayList<UserInfo> userInfoArrayList = userList.info;
                    if (!userInfoArrayList.isEmpty()) {
                        UserInfo userInfo = userInfoArrayList.get(0);
                        id = userInfo.id;
                    }

                    // 유저 정보를 가져왔으면 이제 채팅 데이터를 로드할 수 있습니다.
                    loadMyMessage();
                }
            }

            @Override
            public void onFailure(Call<UserList> call, Throwable t) {
                // 실패 처리
            }
        });
    }
    public void loadMyMessage() {
        showProgress();
        // Firestore 인스턴스 초기화
        db = FirebaseFirestore.getInstance();
        chatArrayList.clear(); // 데이터 초기화
        // "chat" 컬렉션에서 쿼리 수행
        db.collection("chat")
                .whereArrayContains("user", id) // 현재 사용자 ID가 포함된 도큐먼트만 가져옴
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int totalDocuments = task.getResult().size();
                            documentsProcessed = 0; // 문서 처리 횟수 초기화

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // "chatMessage" 서브컬렉션 참조를 가져옵니다.
                                CollectionReference chatMessageRef = db.collection("chat")
                                        .document(document.getId())
                                        .collection("chatMessage");

                                // "createdAt" 필드를 기준으로 내림차순 정렬하여 가장 최근 메시지 가져오기
                                Query query = chatMessageRef.orderBy("createdAt", Query.Direction.DESCENDING).limit(1);

                                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(Task<QuerySnapshot> subCollectionTask) {
                                        if (subCollectionTask.isSuccessful()) {
                                            QuerySnapshot querySnapshot = subCollectionTask.getResult();
                                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                                // 마지막 타임의 도큐먼트만 처리
                                                QueryDocumentSnapshot subDocument = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);
                                                lastMessage = subDocument.getString("message");
                                                lastCreatedAt = subDocument.getTimestamp("createdAt");

                                                // "user" 배열에서 현재 사용자 ID를 제외한 다른 사용자 ID 가져오기
                                                List<Long> users = (List<Long>) document.get("user");

                                                if (users != null) {
                                                    // 다른 사용자의 ID를 저장할 리스트
                                                    List<Long> otherUserIds = new ArrayList<>();

                                                    for (Long userId : users) {
                                                        if (!userId.equals(id)) {
                                                            // 현재 사용자의 ID가 아닌 경우에만 다른 사용자의 ID로 처리
                                                            otherUserIds.add(userId);
                                                        }
                                                    }

                                                    // 다른 사용자의 ID를 출력 또는 처리할 수 있습니다.
                                                    for (Long otherUserId : otherUserIds) {
                                                        int otherUserIntId = otherUserId.intValue();
                                                        if (otherUserIntId != id) {
                                                            int currentFriendId = otherUserIntId; // 현재 사용자의 friendId를 저장합니다.

                                                            Retrofit retrofit1 = NetworkClient.getRetrofitClient(getActivity());
                                                            UserApi api1 = retrofit1.create(UserApi.class);

                                                            Call<UserList> call1 = api1.getFriendInfo(currentFriendId);

                                                            call1.enqueue(new Callback<UserList>() {
                                                                @Override
                                                                public void onResponse(Call<UserList> call, Response<UserList> response) {
                                                                    if (response.isSuccessful()) {
                                                                        userInfoArrayList.clear();

                                                                        UserList userList = response.body();
                                                                        userInfoArrayList.addAll(0, userList.info);
                                                                        userImgUrl = userInfoArrayList.get(0).userImgUrl;
                                                                        userNickname = userInfoArrayList.get(0).userNickname;

                                                                        lastMessage = null;
                                                                        lastCreatedAt = null;

                                                                        if (lastMessage == null || lastCreatedAt == null ||
                                                                                !lastMessage.equals(subDocument.getString("message")) ||
                                                                                !lastCreatedAt.equals(subDocument.getTimestamp("createdAt"))) {
                                                                            lastMessage = subDocument.getString("message");
                                                                            lastCreatedAt = subDocument.getTimestamp("createdAt");
                                                                        }

                                                                        Chat chat = new Chat(currentFriendId, userNickname, userImgUrl, lastMessage, lastCreatedAt + "");
                                                                        chatArrayList.add(chat);
                                                                        Log.i("hihi", chat.lastCreatedAt + chat.lastMessage + chat.id);

                                                                        documentsProcessed++;
                                                                        if (documentsProcessed == totalDocuments) {
                                                                            updateUI();
                                                                            dismissProgress();
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onFailure(Call<UserList> call, Throwable t) {

                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            Log.i("RecentMessage", "쿼리 실패");
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.i("FoundDocument", "쿼리 실패");
                        }
                    }
                });
    }

    // UI 업데이트를 수행하는 메서드
    private void updateUI() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // RecyclerView 어댑터를 업데이트하거나 TextView 등의 UI 요소를 변경
                adapter = new ChatAdapter(getActivity(), chatArrayList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    Dialog dialog;

    void showProgress(){
        dialog = new Dialog(getActivity());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(new ProgressBar(getActivity()));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    // 다이얼로그를 사라지게 하는 함수
    void dismissProgress(){
        dialog.dismiss();
    }


}

