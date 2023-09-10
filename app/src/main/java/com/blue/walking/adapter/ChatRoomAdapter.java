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
    public ChatRoomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_row, parent, false);
        return new ChatRoomAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatRoom chatRoom = chatRoomArrayList.get(position);

        Glide.with(context)
                .load(chatRoom.userImgUrl)
                .into(holder.imgUserUrl);
        holder.txtNickname.setText(chatRoom.userNickname);
        holder.txtTime.setText("");
        holder.txtMessage.setText(chatRoom.message);

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


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgUserUrl = itemView.findViewById(R.id.imgUserUrl);
            txtNickname = itemView.findViewById(R.id.txtNickname);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtTime = itemView.findViewById(R.id.txtTime);

        }
    }
}
