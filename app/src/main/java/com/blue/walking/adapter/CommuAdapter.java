package com.blue.walking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.blue.walking.R;
import com.blue.walking.model.Post;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CommuAdapter extends RecyclerView.Adapter<CommuAdapter.ViewHolder>{

    Context context;
    ArrayList<Post> postArrayList;

    public CommuAdapter(Context context, ArrayList<Post> postArrayList) {
        this.context = context;
        this.postArrayList = postArrayList;
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
        holder.txtLike.setText(post.isLike);

        // 카테고리 항목
        String category = post.category;
        if (category.equals(0)){
            holder.txtCategory.setText("일상");

        } else if (category.equals(1)) {
            holder.txtCategory.setText("정보");

        } else if (category.equals(2)){
            holder.txtCategory.setText("산책");

        }

        // 이미지
        Glide.with(context).load(post.postImgUrl).into(holder.imgContent);
        Glide.with(context).load(post.user_profile_image).into(holder.imgUser);

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
            imgLike = itemView.findViewById(R.id.imgLike);
            txtLike = itemView.findViewById(R.id.txtLike);
            txtComment = itemView.findViewById(R.id.txtComment);

        }
    }
}
