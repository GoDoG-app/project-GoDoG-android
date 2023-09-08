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
import com.blue.walking.model.Chat;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = chatArrayList.get(position);

        Glide.with(context)
                .load(chat.userImgUrl)
                .into(holder.imgUserUrl);

        holder.txtMessage.setText(chat.lastMessage);
        holder.txtNickname.setText(chat.userNickname);
        holder.txtTime.setText(chat.lastCreatedAt);

    }

    @Override
    public int getItemCount() {
        return chatArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgUserUrl;
        TextView txtNickname;
        TextView txtTime;
        TextView txtMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgUserUrl = itemView.findViewById(R.id.imgUserUrl);
            txtNickname = itemView.findViewById(R.id.txtNickname);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtTime = itemView.findViewById(R.id.txtTime);

        }
    }
}
