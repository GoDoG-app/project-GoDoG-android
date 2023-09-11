package com.blue.walking.adapter;

import android.content.Context;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class CommnetAdapter extends RecyclerView.Adapter<CommnetAdapter.ViewHolder>{

    Context context;
    ArrayList<Firebase> firebaseArrayList;

    SimpleDateFormat sf;
    SimpleDateFormat df;

    int postId;

    public CommnetAdapter(Context context, ArrayList<Firebase> firebaseArrayList, int postId) {
        this.context = context;
        this.firebaseArrayList = firebaseArrayList;
        this.postId = postId;

        // 여기에 넣으면 한번만 실행된대
        sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm");
        sf.setTimeZone(TimeZone.getTimeZone("UTC")); // UTC 글로벌 시간
        df.setTimeZone(TimeZone.getDefault());

    }

    @NonNull
    @Override
    public CommnetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_row, parent, false);
        return new CommnetAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Firebase firebase = firebaseArrayList.get(position);

        // 댓글 내용 설정
        holder.txtComment.setText(firebase.commentContent);

        // 댓글 작성자 정보 설정 (사용자 프로필 사진, 닉네임, 지역 등)
        Glide.with(context).load(firebase.userImgUrl).into(holder.imgUser);
        holder.txtUserName.setText(firebase.userNickname);
        holder.txtPlace.setText(firebase.userAddress);

        holder.txtLike.setText(String.valueOf(firebase.likeCount));

        // 댓글 작성 시간 설정
        Date date = firebase.createdAt; // 자바가 이해하는 시간으로 바꾸기
        String localTime = df.format(firebase.createdAt); // 자바가 이해한 시간을 사람이 이해할 수 있는 시간으로 바꾸기

        // 업로드 시간 가공
        long curTime = System.currentTimeMillis();  // 현재 시간
        long diffTime = (curTime - date.getTime()) / 1000;  // (현재시간 - 계산할 업로드시간)/1000
        String msg = null;
        if (diffTime < 60){
            msg = "방금 전";
            holder.txtTime.setText(msg);

        } else if ((diffTime /= 60)< 60) {
            msg = diffTime + "분 전";
            holder.txtTime.setText(msg);

        } else if ((diffTime /= 60)< 24) {
            msg = diffTime + "시간 전";
            holder.txtTime.setText(msg);

        } else if ((diffTime /= 24)< 30) {
            msg = diffTime + "일 전";
            holder.txtTime.setText(msg);

        } else {
            holder.txtTime.setText(localTime.substring(6)); // 년 제외 몇월 몇일 몇시
        }

        holder.imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 좋아요 이미지를 클릭시
                // 파이어베이스에 성공적으로 업데이트 되었다면 이미지 변경

                // 좋아요 이미지 누르면 isLiked가 true,false 반복

                // 파이어베이스에 isLiked가 ture면 색칠된 이미지 false면 색칠 안되어있는 이미지

                // 파이어베이스에 likeCount는 색칠 안되어있을때(isLiked == fasle) 내가 누르면 +1
                // 색칠 되어있을때(isLiked == true) 내가 누르면 -1

                if (firebase.isLiked == false){

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference commentRef = db.collection("post_comments")
                            .document(String.valueOf(postId)) // 업데이트하려는 댓글이 있는 포스트의 ID
                            .collection("comments")
                            .document(firebase.getDocumentId()); // 업데이트하려는 댓글의 ID
                    // 업데이트할 데이터 생성
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("isLiked", true); // 새로운 좋아요 상태를 true로 설정
                    updateData.put("likeCount", FieldValue.increment(1));
                    // Firestore 문서 업데이트
                    commentRef.update(updateData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // 업데이트 성공
                                    // 여기에서 UI 업데이트 또는 다른 작업을 수행할 수 있습니다.


//                                    updateData.put("likeCount", FieldValue.increment(+1));
                                    commentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                // 'likeCount' 필드가 존재할 경우 값을 가져와서 txtLike에 설정
                                                Long likeCount = documentSnapshot.getLong("likeCount");
                                                if (likeCount != null) {
                                                    holder.txtLike.setText(String.valueOf(likeCount));
                                                }
                                            }
                                        }
                                    });

                                    holder.imgLike.setImageResource(R.drawable.baseline_favorite_24);


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // 업데이트 실패
                                    Log.e("FirestoreUpdate", "Failed to update isLiked: " + e.getMessage());
                                }
                            });
                }else{
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference commentRef = db.collection("post_comments")
                            .document(String.valueOf(postId)) // 업데이트하려는 댓글이 있는 포스트의 ID
                            .collection("comments")
                            .document(firebase.getDocumentId()); // 업데이트하려는 댓글의 ID
                    // 업데이트할 데이터 생성
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("isLiked", false); // 새로운 좋아요 상태를 true로 설정
                    updateData.put("likeCount", FieldValue.increment(-1));
                    // Firestore 문서 업데이트
                    commentRef.update(updateData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // 업데이트 성공
                                    // 여기에서 UI 업데이트 또는 다른 작업을 수행할 수 있습니다.
                                    commentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                // 'likeCount' 필드가 존재할 경우 값을 가져와서 txtLike에 설정
                                                Long likeCount = documentSnapshot.getLong("likeCount");
                                                if (likeCount != null) {
                                                    holder.txtLike.setText(String.valueOf(likeCount));
                                                }
                                            }
                                        }
                                    });

                                    holder.imgLike.setImageResource(R.drawable.baseline_favorite_border_24);


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // 업데이트 실패
                                    Log.e("FirestoreUpdate", "Failed to update isLiked: " + e.getMessage());
                                }
                            });

                }

            }
        });
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

            imgUser.setClipToOutline(true);
        }
    }

    public void addComment(Firebase comment) {
        firebaseArrayList.add(comment);
        notifyItemInserted(firebaseArrayList.size() - 1); // 마지막 항목 갱신
    }
}
