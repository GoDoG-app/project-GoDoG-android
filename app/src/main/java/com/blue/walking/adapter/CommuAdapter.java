package com.blue.walking.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.blue.walking.model.Post;
import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

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
//                    Intent intent = new Intent(context, CommuPostActivity.class);
//                    intent.putExtra("post", (Serializable) post);
//                    context.startActivity(intent);

                    Intent intent;
                    intent = new Intent(context, CommuPostActivity.class);
                    intent.putExtra("post", post.toString());
                    context.startActivity(intent);

                    Log.i("cardView", post+"번 게시글");
                    }
             });


            // 게시물 좋아요 눌렀을 때 API
            imgLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.i("imgLike", "좋아요 누름");

                }
            });
        }
    }
}
