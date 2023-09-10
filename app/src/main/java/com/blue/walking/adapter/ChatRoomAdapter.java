package com.blue.walking.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.blue.walking.R;
import com.blue.walking.model.ChatRoom;
import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder>{
    // 1:1채팅방
    Context context;
    ArrayList<ChatRoom> chatRoomArrayList;


    public ChatRoomAdapter(Context context, ArrayList<ChatRoom> chatRoomArrayList) {
        this.context = context;
        this.chatRoomArrayList = chatRoomArrayList;

    }

    @NonNull
    @Override
    public ChatRoomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_row, parent, false);
        return new ChatRoomAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatRoom chatRoom = chatRoomArrayList.get(position);

        // 시간 변환
        Timestamp timestamp = (Timestamp) chatRoom.createdAt;
        Date timestampDate =  timestamp.toDate();
        Date date = new Date(String.valueOf(timestampDate));
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String getTime = sdf.format(date);

        Log.i("adapter", date+"");

        Glide.with(context)
                .load(chatRoom.userImgUrl)
                .into(holder.imgUserUrl);
        holder.txtNickname.setText(chatRoom.userNickname);
        holder.txtTime.setText(getTime);
        holder.txtMessage.setText(chatRoom.message);
        Log.i("adapter", chatRoom.createdAt+"");

        // 내가 쓴 메세지면 오른쪽 정렬
        Log.i("adapter", chatRoomArrayList.get(0).id+"하이"+chatRoom.id+"");
        if (chatRoomArrayList.get(0).id!=chatRoom.id){
            holder.imgUserUrl.setVisibility(View.GONE);
            holder.txtNickname.setGravity(Gravity.RIGHT);

            holder.linearLayout1.setGravity(Gravity.RIGHT);

            // params로 지정
            LinearLayout.LayoutParams paramsMessage = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            LinearLayout.LayoutParams paramsTime = (LinearLayout.LayoutParams) holder.txtTime.getLayoutParams();

            // txtMessage와 txtTime의 위치를 바꿈
            holder.linearLayout1.removeView(holder.txtMessage);
            holder.linearLayout1.removeView(holder.txtTime);

            // 순서를 변경한 후 다시 추가
            holder.linearLayout1.addView(holder.txtTime, paramsTime);
            holder.linearLayout1.addView(holder.txtMessage, paramsMessage);

            // 메세지 배경색 바꾸기
            holder.txtMessage.setBackgroundColor(Color.parseColor("#FFF6C5"));


//            holder.linearLayout.addView(holder.txtTime);    // 먼저 txtTime을 추가
//            holder.linearLayout.addView(holder.txtMessage); // 그 다음 txtMessage를 추가
//
//            holder.txtMessage.setGravity(Gravity.RIGHT); // 텍스트 내용을 오른쪽으로 정렬
//            holder.txtTime.setGravity(Gravity.RIGHT);
        }

    }

    @Override
    public int getItemCount() {
        return chatRoomArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgUserUrl;
        TextView txtNickname;
        TextView txtMessage;
        TextView txtTime;
        LinearLayout linearLayout1;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgUserUrl = itemView.findViewById(R.id.imgUserUrl);
            txtNickname = itemView.findViewById(R.id.txtNickname);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtTime = itemView.findViewById(R.id.txtTime);
            linearLayout1 = itemView.findViewById(R.id.linearLayout1);

        }
    }
}
