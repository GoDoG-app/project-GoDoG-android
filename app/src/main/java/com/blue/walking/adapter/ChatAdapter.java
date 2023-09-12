package com.blue.walking.adapter;

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

import com.blue.walking.ChatRoomActivity;
import com.blue.walking.R;
import com.blue.walking.model.Chat;
import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    // 채팅목록
    Context context;
    ArrayList<Chat> chatArrayList;

    public ChatAdapter(Context context, ArrayList<Chat> chatArrayList) {
        this.context = context;
        this.chatArrayList = chatArrayList;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_row, parent, false);
        return new ChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = chatArrayList.get(position);



        // 시간 변환
        String timestampString = chat.lastCreatedAt;

        // "("와 ")" 문자를 제거
        timestampString = timestampString.replaceAll("[()]", "");

        // Timestamp 문자열에서 seconds와 nanoseconds 추출
        String[] parts = timestampString.split(",");
        String secondsPart = parts[0].substring(parts[0].indexOf('=') + 1).trim();
        String nanosecondsPart = parts[1].substring(parts[1].indexOf('=') + 1).trim();

        // seconds와 nanoseconds를 long으로 변환
        long seconds = Long.parseLong(secondsPart);
        long nanoseconds = Long.parseLong(nanosecondsPart);

        // Firebase의 Timestamp 객체 생성
        Timestamp timestamp = new Timestamp(seconds, (int) (nanoseconds / 1000)); // nanoseconds를 마이크로초로 변환

        // Timestamp를 Date 객체로 변환
        Date date = timestamp.toDate();

        // Date 객체를 원하는 형식의 문자열로 변환
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String formattedTime = sdf.format(date);


        Glide.with(context)
                .load(chat.userImgUrl)
                .into(holder.imgUser);

        holder.txtMessage.setText(chat.lastMessage);
        holder.txtUserName.setText(chat.userNickname);
        holder.txtTime.setText(formattedTime);


    }

    @Override
    public long getItemId(int position) {
        return chatArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return chatArrayList.size();
    }

    @Override
    public int getItemCount() {
        return chatArrayList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgUser;
        TextView txtUserName;
        TextView txtTime;
        TextView txtMessage;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgUser = itemView.findViewById(R.id.imgUser);
            imgUser.setClipToOutline(true);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtTime = itemView.findViewById(R.id.txtTime);
            cardView = itemView.findViewById(R.id.cardView);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = getAdapterPosition();

                    Chat chat = chatArrayList.get(index);

                    // 클릭한 채팅 정보를 ChatRoomActivity로 전달하기 위해 Intent를 생성
                    Intent intent = new Intent(context, ChatRoomActivity.class);
                    intent.putExtra("chat", chat);

                    // ChatRoomActivity 시작
                    context.startActivity(intent);
                }
            });

        }
    }
}
