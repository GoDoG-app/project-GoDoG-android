package com.blue.walking.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.blue.walking.CommuPostActivity;
import com.blue.walking.R;
import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.UserApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.Firebase;
import com.blue.walking.model.Post;
import com.blue.walking.model.UserInfo;
import com.blue.walking.model.UserList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyCommentAdapter extends RecyclerView.Adapter<MyCommentAdapter.ViewHolder>{

    Context context;
    ArrayList<Post> myPostArrayList;

    // 시간 로컬타임 변수들을 멤버변수로 뺌
    SimpleDateFormat sf;
    SimpleDateFormat df;

    String token;
    String user;

    List<String> commentList = new ArrayList<>(); // "comments" 서브컬렉션의 댓글을 저장할 리스트
    ArrayList<UserInfo> userInfoArrayList = new ArrayList<>();

    public MyCommentAdapter(Context context, ArrayList<Post> myPostArrayList) {
        this.context = context;
        this.myPostArrayList = myPostArrayList;

        // 여기에 넣으면 한번만 실행된대
        sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm");
        sf.setTimeZone(TimeZone.getTimeZone("UTC")); // UTC 글로벌 시간
        df.setTimeZone(TimeZone.getDefault());
    }

    @NonNull
    @Override
    public MyCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mycomment_row, parent, false);
        return new MyCommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCommentAdapter.ViewHolder holder, int position) {
        Post post = myPostArrayList.get(position);

        // Firebase Firestore 인스턴스 가져오기
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String postId = String.valueOf(post.post_id);



        SharedPreferences sp = context.getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString(Config.ACCESS_TOKEN, "");

        Retrofit retrofit = NetworkClient.getRetrofitClient(context);
        UserApi api = retrofit.create(UserApi.class);

        Log.i("user","내 정보 API 실행");

        Call<UserList> call = api.getUserInfo("Bearer " + token);
        call.enqueue(new Callback<UserList>() {
            @Override
            public void onResponse(Call<UserList> call, Response<UserList> response) {
                if (response.isSuccessful()){

                    userInfoArrayList.clear(); // 초기화
                    UserList userList = response.body();
                    userInfoArrayList.addAll(userList.info);
                    user = userInfoArrayList.get(0).userNickname;

                    // Todo: whereEqualTo 에 특정 유저의 닉네임을 넣는 방법...(유저 API 를 호출?)
                    // postId와 연관된 "comments" 하위 컬렉션의 레퍼런스 가져오기
                    db.collection("post_comments")
                            .document(postId)
                            .collection("comments")
                            .whereEqualTo("userNickname", user)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        Log.i("Call", "파이어베이스 실행 성공");
                                        Log.i("user", user);

                                        // 리스트 데이터 한 번 초기화
                                        commentList.clear();

                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.i("TAG", document.getId() + " => " + document.getData());

                                            Firebase firebase = document.toObject(Firebase.class);
                                            if (firebase != null) {
                                                String commentContent = firebase.commentContent;
                                                commentList.add(commentContent);  // 내가 쓴 댓글을 가져와서 리스트에 저장
                                                Log.i("aaa", "댓글 가져옴");

                                            } else {
                                                return; // 없으면 리턴
                                            }
                                        }

                                        if (commentList.size() != 0) {
                                            // 내가 쓴 댓글이 있다면 화면 세팅

                                            StringBuilder commentsBuilder = new StringBuilder();

                                            for (String comment : commentList) {
                                                Log.i("CommentList", comment);

                                                commentsBuilder.append(comment).append("\n");
                                                // 같은 게시글에 쓴 댓글들이 덮어씌워지지 않게 \n (줄바꿈)으로 구별

                                            }
                                            holder.txtComment.setText(commentsBuilder.toString());

                                            holder.txtCategory.setText(post.category);
                                            holder.txtPost.setText(post.postContent + " 에서 작성함");

                                            Log.i("bbb", "화면뷰적용");

                                            try {
                                                Date date = sf.parse(post.createdAt); // 자바가 이해하는 시간으로 바꾸기
                                                String localTime = df.format(date); // 자바가 이해한 시간을 사람이 이해할 수 있는 시간으로 바꾸기
                                                holder.txtTime.setText(localTime);

                                            } catch (ParseException e) {
                                                Log.i("walking", e.toString());
                                            }

                                        } else {
                                            // 내가 쓴 댓글이 없으면 화면에 띄우지 않기 (이게되네)
                                            holder.cardView.setVisibility(View.GONE);
                                            return;
                                        }
                                        Log.i("CommentList", "반복 끝");

                                    } else {
                                        Log.i("TAG", "Error getting documents: ", task.getException());
                                    }
                                }
                            });

                } else {
                    Log.i("user", "내 정보 불러오기 실패");
                }
            }

            @Override
            public void onFailure(Call<UserList> call, Throwable t) {
                Log.i("user", "내 정보 불러오기 실패");
            }
        });
    }

    @Override
    public int getItemCount() {
        return myPostArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView txtCategory;
        TextView txtTime;
        TextView txtComment;
        TextView txtPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtComment = itemView.findViewById(R.id.txtComment);
            txtPost = itemView.findViewById(R.id.txtPost);

            // 카드뷰를 눌렀을 때 -> 내용 상세보기로 이동
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 몇번째 카드뷰를 눌렀는지 확인
                    int index = getAdapterPosition();
                    Post post = myPostArrayList.get(index);

                    // 보내주기
                    Intent intent;
                    intent = new Intent(context, CommuPostActivity.class);
                    intent.putExtra("post", post);
                    context.startActivity(intent);

                    Log.i("cardView", post+"번 게시글");
                }
            });
        }
    }
}