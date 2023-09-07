package com.blue.walking.adapter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
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
import com.blue.walking.model.Post;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.ViewHolder>{

    Context context;
    ArrayList<Post> myPostArrayList;

    // 시간 로컬타임 변수들을 멤버변수로 뺌
    SimpleDateFormat sf;
    SimpleDateFormat df;

    public MyPostAdapter(Context context, ArrayList<Post> postArrayList) {
        this.context = context;
        this.myPostArrayList = postArrayList;

        // 여기에 넣으면 한번만 실행된대
        sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm");
        sf.setTimeZone(TimeZone.getTimeZone("UTC")); // UTC 글로벌 시간
        df.setTimeZone(TimeZone.getDefault());
    }

    @NonNull
    @Override
    public MyPostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mypost_row, parent, false);
        return new MyPostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPostAdapter.ViewHolder holder, int position) {
        Post post = myPostArrayList.get(position);

        holder.txtContent.setText(post.postContent);
        holder.txtCategory.setText(post.category);

        // 이미지
        Glide.with(context).load(post.postImgUrl).into(holder.imgContent);

        try {
            Date date = sf.parse(post.createdAt); // 자바가 이해하는 시간으로 바꾸기
            String localTime = df.format(date); // 자바가 이해한 시간을 사람이 이해할 수 있는 시간으로 바꾸기
            holder.txtTime.setText(localTime);

        } catch (ParseException e) {
            Log.i("post", e.toString());
        }

        // Firebase Firestore 인스턴스 가져오기
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String postId = String.valueOf(post.post_id);

        // postId와 연관된 "comments" 하위 컬렉션의 레퍼런스 가져오기
        CollectionReference commentsRef = db.collection("post_comments").document(postId).collection("comments");

        // "comments" 컬렉션의 문서 수(댓글 수) 가져오기
        commentsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int commentCount = task.getResult().size();
                    // 댓글 수를 commentCount 변수에 저장하고 사용할 수 있습니다.

                    holder.txtComment.setText("댓글 " + commentCount);

                    // commentCount를 UI에 업데이트하거나 다른 작업을 수행할 수 있습니다.
                    Log.d(TAG, "댓글 수: " + commentCount);
                } else {
                    Log.w(TAG, "댓글 수를 가져오는 데 실패했습니다.", task.getException());
                }
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
        TextView txtContent;
        TextView txtComment;
        ImageView imgContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtContent = itemView.findViewById(R.id.txtContent);
            txtComment = itemView.findViewById(R.id.txtComment);
            imgContent = itemView.findViewById(R.id.imgContent);

            imgContent.setClipToOutline(true);  // 둥근 테두리 적용

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
