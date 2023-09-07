package com.blue.walking.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.blue.walking.R;
import com.blue.walking.model.Firebase;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class MyCommentAdapter extends RecyclerView.Adapter<MyCommentAdapter.ViewHolder>{

    Context context;
    ArrayList<Firebase> myFirebaseArrayList;

    SimpleDateFormat sf;
    SimpleDateFormat df;

    public MyCommentAdapter(Context context, ArrayList<Firebase> myFirebaseArrayList) {
        this.context = context;
        this.myFirebaseArrayList = myFirebaseArrayList;

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
        Firebase firebase = myFirebaseArrayList.get(position);

        // 댓글 내용 설정
        holder.txtComment.setText(firebase.commentContent);

        Date date = firebase.createdAt; // 자바가 이해하는 시간으로 바꾸기
        String localTime = df.format(date); // 자바가 이해한 시간을 사람이 이해할 수 있는 시간으로 바꾸기
        holder.txtTime.setText(localTime);

    }

    @Override
    public int getItemCount() {
        return myFirebaseArrayList.size();
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

        }
    }
}
