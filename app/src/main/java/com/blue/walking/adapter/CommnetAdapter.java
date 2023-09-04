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
import com.blue.walking.model.Firebase;
import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CommnetAdapter extends RecyclerView.Adapter<CommnetAdapter.ViewHolder>{

    Context context;
    ArrayList<Firebase> firebaseArrayList;

    public CommnetAdapter(Context context, ArrayList<Firebase> firebaseArrayList) {
        this.context = context;
        this.firebaseArrayList = firebaseArrayList;
    }

    @NonNull
    @Override
    public CommnetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_row, parent, false);
        return new CommnetAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommnetAdapter.ViewHolder holder, int position) {
        Firebase firebase = firebaseArrayList.get(position);

        // 댓글 내용 설정
        holder.txtComment.setText(firebase.commentContent);

        // 댓글 작성자 정보 설정 (사용자 프로필 사진, 닉네임, 지역 등)
        Glide.with(context).load(firebase.userImgUrl).into(holder.imgUser);
        holder.txtUserName.setText(firebase.userNickname);
        holder.txtPlace.setText(firebase.userAddress);

        // 댓글 작성 시간 설정
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        holder.txtTime.setText(sdf.format(firebase.createdAt.toDate()));

    }

    @Override
    public int getItemCount() {
        return firebaseArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        /** 커뮤니티 코멘트(댓글) 화면뷰 */
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

            // 댓글 화면뷰 초기화
            imgUser = itemView.findViewById(R.id.imgUser);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            imgMy = itemView.findViewById(R.id.imgMy);
            txtMy = itemView.findViewById(R.id.txtMy);
            txtPlace = itemView.findViewById(R.id.txtPlace);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtComment = itemView.findViewById(R.id.txtComment);
            imgLike = itemView.findViewById(R.id.imgLike);
            txtLike = itemView.findViewById(R.id.txtLike);
            txtComment2 = itemView.findViewById(R.id.txtComment2);

        }
    }
}
