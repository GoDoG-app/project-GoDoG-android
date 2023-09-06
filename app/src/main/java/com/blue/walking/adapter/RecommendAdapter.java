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
import com.blue.walking.model.Park;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.ViewHolder>{
    Context context;
    ArrayList<Park> parkArrayList;
    public RecommendAdapter(Context context, ArrayList<Park> ParkArrayList) {
        this.context = context;
        this.parkArrayList = parkArrayList;
    }

    @NonNull
    @Override
    public RecommendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_row, parent, false);
        return new RecommendAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Park park =parkArrayList.get(position);

        // 공원 이름, 주소 설정
        holder.txtPlaceName1.setText(park.getName());
        holder.txtPlace1.setText(park.getAddress());

        // 공원 이미지
//        Glide.with(context).load(park.parkImgUrl).into(holder.imgPlace1);

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        /** 산책로 추천 화면뷰 */
        ImageView imgPlace1;
        TextView txtPlaceName1;
        TextView txtPlace1;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPlace1 = itemView.findViewById(R.id.imgPlace1);
            txtPlaceName1 = itemView.findViewById(R.id.txtPlaceName1);
            txtPlace1 = itemView.findViewById(R.id.txtPlace1);


        }
    }
}
