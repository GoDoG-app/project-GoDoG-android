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
import com.blue.walking.model.RandomFriend;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RandomAdapter extends RecyclerView.Adapter<RandomAdapter.ViewHolder>{

    Context context;
    ArrayList<RandomFriend> randomFriendArrayList;

    public RandomAdapter(Context context, ArrayList<RandomFriend> randomFriendArrayList) {
        this.context = context;
        this.randomFriendArrayList = randomFriendArrayList;
    }

    @NonNull
    @Override
    public RandomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.random_friend_row, parent, false);
        return new RandomAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RandomFriend randomFriend = randomFriendArrayList.get(position);

        holder.txtRandomFriend.setText(randomFriend.nickname);

        Glide.with(context)
                .load(randomFriend.proImgUrl)
                .into(holder.imgRandomFriend);

    }

    @Override
    public int getItemCount() {
        return randomFriendArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRandomFriend;
        TextView txtRandomFriend;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgRandomFriend = itemView.findViewById(R.id.imgRandomFriend);
            imgRandomFriend.setClipToOutline(true); // 둥근테두리 적용
            txtRandomFriend = itemView.findViewById(R.id.txtRandomFriend);


        }
    }
}
