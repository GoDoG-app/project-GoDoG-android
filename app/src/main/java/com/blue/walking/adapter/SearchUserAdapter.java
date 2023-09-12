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

import com.blue.walking.FriendActivity;
import com.blue.walking.FriendActivityFromFriendList;
import com.blue.walking.FriendActivityFromSearch;
import com.blue.walking.R;
import com.blue.walking.model.FriendsInfo;
import com.blue.walking.model.SearchUserItems;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.ViewHolder>{

    Context context;

    ArrayList<SearchUserItems> searchUserItemsArrayList;

    public SearchUserAdapter(Context context, ArrayList<SearchUserItems> searchUserItemsArrayList) {
        this.context = context;
        this.searchUserItemsArrayList = searchUserItemsArrayList;
    }

    @NonNull
    @Override
    public SearchUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list_row, parent,false);

        return new SearchUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchUserAdapter.ViewHolder holder, int position) {

        SearchUserItems searchUserItems = searchUserItemsArrayList.get(position);

        Glide.with(context).load(searchUserItems.proImgUrl).into(holder.imgUser);


        holder.txtName.setText(searchUserItems.nickname);
        holder.txtOneliner.setText(searchUserItems.oneliner);

    }

    @Override
    public int getItemCount() {
        return searchUserItemsArrayList.size();
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
                    SearchUserItems searchUserItems = searchUserItemsArrayList.get(index);

                    // 보내주기
                    Intent intent;
                    intent = new Intent(context, FriendActivityFromSearch.class);
                    intent.putExtra("search", searchUserItems);
                    context.startActivity(intent);


                }
            });
        }
    }
}
