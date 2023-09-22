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
import com.blue.walking.model.Promise;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PromiseListAdapter extends RecyclerView.Adapter<PromiseListAdapter.ViewHolder>{

    Context context;
    ArrayList<Promise> promiseArrayList = new ArrayList<>();

    public PromiseListAdapter(Context context, ArrayList<Promise> promiseArrayList) {
        this.context = context;
        this.promiseArrayList = promiseArrayList;
    }

    @NonNull
    @Override
    public PromiseListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.promise_list_row, parent,false);

        return new PromiseListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromiseListAdapter.ViewHolder holder, int position) {
        Promise promise = promiseArrayList.get(position);

        holder.txtDay.setText(promise.meetingDay);
        holder.txtTime.setText(promise.meetingTime);
        holder.txtPlase.setText(promise.meetingPlace);
        Glide.with(context).load(promise.friendproImgUrl).into(holder.imgFriend);
    }

    @Override
    public int getItemCount() {
        return promiseArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgFriend;
        TextView txtDay;
        TextView txtTime;
        TextView txtPlase;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgFriend = itemView.findViewById(R.id.imgFriend);
            txtDay = itemView.findViewById(R.id.txtDay);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtPlase = itemView.findViewById(R.id.txtPlase);

            imgFriend.setClipToOutline(true);  // 둥근 테두리 적용
        }
    }
}
