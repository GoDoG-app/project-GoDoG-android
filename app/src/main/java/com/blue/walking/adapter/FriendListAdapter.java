package com.blue.walking.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.blue.walking.CommuPostActivity;
import com.blue.walking.FriendActivity;
import com.blue.walking.FriendActivityFromFriendList;
import com.blue.walking.R;
import com.blue.walking.model.FriendsInfo;
import com.blue.walking.model.Post;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder>{

    Context context;
    ArrayList<FriendsInfo> friendsInfoArrayList;

    public FriendListAdapter(Context context, ArrayList<FriendsInfo> friendsInfoArrayList) {
        this.context = context;
        this.friendsInfoArrayList = friendsInfoArrayList;
    }

    @NonNull
    @Override
    public FriendListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list_row, parent,false);

        return new FriendListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        FriendsInfo friendsInfo = friendsInfoArrayList.get(position);

        Glide.with(context).load(friendsInfo.proImgUrl).into(holder.imgUser);


        holder.txtName.setText(friendsInfo.nickname);
        holder.txtOneliner.setText(friendsInfo.oneliner);



    }

    @Override
    public int getItemCount() {
        return friendsInfoArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgUser;
        TextView txtName;
        TextView txtOneliner;

        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgUser = itemView.findViewById(R.id.imgUser);
            txtName = itemView.findViewById(R.id.txtName);
            txtOneliner = itemView.findViewById(R.id.txtOneliner);
            cardView = itemView.findViewById(R.id.cardView);

            imgUser.setClipToOutline(true);  // 둥근 테두리 적용

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int index = getAdapterPosition();
                    FriendsInfo friendsInfo = friendsInfoArrayList.get(index);

                    // 보내주기
                    Intent intent;
                    intent = new Intent(context, FriendActivityFromFriendList.class);
                    intent.putExtra("friends", friendsInfo);
                    context.startActivity(intent);


                }
            });


        }
    }

}
