package com.blue.walking.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.blue.walking.CommuPostActivity;
import com.blue.walking.R;
import com.blue.walking.UserUpdateActivity;
import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.PostApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.Post;
import com.blue.walking.model.ResultRes;
import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CommuAdapter extends RecyclerView.Adapter<CommuAdapter.ViewHolder>{

    Context context;
    ArrayList<Post> postArrayList;

    // 시간 로컬타임 변수들을 멤버변수로 뺌
    SimpleDateFormat sf;
    SimpleDateFormat df;

    public CommuAdapter(Context context, ArrayList<Post> postArrayList) {
        this.context = context;
        this.postArrayList = postArrayList;

        // 여기에 넣으면 한번만 실행된대
        sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df = new SimpleDateFormat("yyyy년MM월dd일 HH:mm");
        sf.setTimeZone(TimeZone.getTimeZone("UTC")); // UTC 글로벌 시간
        df.setTimeZone(TimeZone.getDefault());
    }

    @NonNull
    @Override
    public CommuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.commu_row, parent, false);
        return new CommuAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommuAdapter.ViewHolder holder, int position) {
        Post post = postArrayList.get(position);

        holder.txtContent.setText(post.postContent);
        holder.txtUserName.setText(post.user_nickname);
        holder.txtCategory.setText(post.category);
        holder.txtPlace.setText(post.user_region);
        holder.txtLike.setText(String.valueOf(post.isLike));
        holder.txtComment.setText(String.valueOf(post.post_likes_count));

        // 이미지
        Glide.with(context).load(post.postImgUrl).into(holder.imgContent);
        Glide.with(context).load(post.user_profile_image).into(holder.imgUser);

        try {
            Date date = sf.parse(post.createdAt); // 자바가 이해하는 시간으로 바꾸기
            String localTime = df.format(date); // 자바가 이해한 시간을 사람이 이해할 수 있는 시간으로 바꾸기
            holder.txtTime.setText(localTime);

        } catch (ParseException e) {
            Log.i("walking", e.toString());
        }

        // 좋아요인 isLike 는 0 또는 1로 나타남
        if(post.isLike == 0){
            // 좋아요가 0 이면 빈 하트 사진으로
            holder.imgLike.setImageResource(R.drawable.baseline_favorite_border_24);

        } else if(post.isLike == 1){
            // 좋아요가 1이면 채워진 하트 사진으로
            holder.imgLike.setImageResource(R.drawable.baseline_favorite_24);
        }
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        /** 화면뷰 */
        CardView cardView;  // 카드뷰
        ImageView imgUser;   // 프로필 사진
        TextView txtUserName;  // 이름
        TextView txtCategory; // 카테고리
        TextView txtPlace;  // 유저의 지역
        TextView txtTime;   // 업로드 시간
        TextView txtContent;  // 포스팅 내용
        ImageView imgContent;  // 포스팅 이미지

        ImageView imgLike;  // 좋아요 이미지
        TextView txtLike;   // 좋아요 수
        TextView txtComment; // 댓글 수


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            imgUser = itemView.findViewById(R.id.imgUser);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtPlace = itemView.findViewById(R.id.txtPlace);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtContent = itemView.findViewById(R.id.txtContent);
            imgContent = itemView.findViewById(R.id.imgContent);
            imgLike = itemView.findViewById(R.id.imgLike);
            txtLike = itemView.findViewById(R.id.txtLike);
            txtComment = itemView.findViewById(R.id.txtComment);

            imgContent.setClipToOutline(true);  // 둥근 테두리 적용
            imgUser.setClipToOutline(true);  // 둥근 테두리 적용


            // 카드뷰를 눌렀을 때 -> 내용 상세보기로 이동
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 몇번째 카드뷰를 눌렀는지 확인
                    int index = getAdapterPosition();
                    Post post = postArrayList.get(index);

                    // 보내주기
                    Intent intent;
                    intent = new Intent(context, CommuPostActivity.class);
                    intent.putExtra("post", post);
                    context.startActivity(intent);

                    Log.i("cardView", post+"번 게시글");
                    }
             });


            // 게시물 좋아요 눌렀을 때 API
            imgLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("imgLike", "좋아요 누름");

                    // 유저가 누른 포스트의 좋아요 유무 확인해서, API 호출
                    int index = getAdapterPosition();
                    Post post = postArrayList.get(index);

                    Retrofit retrofit = NetworkClient.getRetrofitClient(context);
                    PostApi api = retrofit.create(PostApi.class);

                    SharedPreferences sp = context.getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
                    String token = sp.getString(Config.ACCESS_TOKEN, "");

                    if (post.isLike ==0){ // 좋아요가 안눌러져 있는 상태니깐 좋아요 API 사용
                        Call<ResultRes> call = api.setPostLike(post.post_id, "Bearer "+token);
                        call.enqueue(new Callback<ResultRes>() {
                            @Override
                            public void onResponse(Call<ResultRes> call, Response<ResultRes> response) {
                                if (response.isSuccessful()){
                                    Log.i("Call", "서버 실행 성공");

                                    post.isLike = 1; // 좋아요 값을 1로 변경
                                    notifyDataSetChanged(); // 어뎁터 업데이트

                                } else {
                                    Log.i("Call", "서버 실행 실패");
                                }
                            }

                            @Override
                            public void onFailure(Call<ResultRes> call, Throwable t) {
                                Log.i("Call", "서버 실행 실패");
                            }
                        });
                    } else { // 좋아요가 눌러져 있는 상태니깐 좋아요 해제 API 사용
                        Call<ResultRes> call = api.deletePostLike(post.post_id, "Bearer "+token);
                        call.enqueue(new Callback<ResultRes>() {
                            @Override
                            public void onResponse(Call<ResultRes> call, Response<ResultRes> response) {
                                if (response.isSuccessful()){
                                    Log.i("Call", "서버 실행 성공");

                                    post.isLike = 0; // 좋아요 값을 0으로 변경
                                    notifyDataSetChanged(); // 어뎁터 업데이트

                                } else {
                                    Log.i("Call", "서버 실행 실패");
                                }
                            }

                            @Override
                            public void onFailure(Call<ResultRes> call, Throwable t) {
                                Log.i("Call", "서버 실행 실패");
                            }
                        });
                    }
                }
            });
        }
    }
}
