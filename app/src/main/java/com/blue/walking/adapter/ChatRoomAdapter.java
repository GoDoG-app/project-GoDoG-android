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
import com.blue.walking.model.ChatRoom;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatRoom chatRoom = chatRoomArrayList.get(position);

        Glide.with(context)
                .load(chatRoom.userImgUrl)
                .into(holder.imgUser);
        holder.txtUserName.setText(chatRoom.userNickname);
        holder.txtTime.setText(chatRoom.CreatedAt);
        holder.txtMessage.setText(chatRoom.Message);

    }

    @Override
    public int getItemCount() {
        return chatRoomArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgUser;
        TextView txtUserName;
        TextView txtMessage;
        TextView txtTime;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgUser = itemView.findViewById(R.id.imgUser);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtTime = itemView.findViewById(R.id.txtTime);

        }
    }
}
