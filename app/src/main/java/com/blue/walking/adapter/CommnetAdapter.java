package com.blue.walking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blue.walking.R;

import org.w3c.dom.Text;

public class CommnetAdapter extends RecyclerView.Adapter<CommnetAdapter.ViewHolder>{

    Context context;


    @NonNull
    @Override
    public CommnetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_row, parent, false);
        return new CommnetAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommnetAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgUser;  // 유저 프로필 사진
        TextView txtUserName;  // 유저 이름
        ImageView imgMy;  // 작성자 아이콘
        TextView txtMy;   // 작성자
        TextView txtPlace; // 유저 지역
        TextView txtTime;  // 작성 시간
        TextView txtComment;  // 댓글 내용
        ImageView imgLike;  // 좋아요 아이콘
        TextView txtLike;  // 좋아요 수
        TextView txtComment2;  // 답글 달기

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
