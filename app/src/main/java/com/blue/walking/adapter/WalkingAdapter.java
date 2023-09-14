package com.blue.walking.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blue.walking.R;
import com.blue.walking.model.Chat;
import com.blue.walking.model.WalkingList;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WalkingAdapter extends RecyclerView.Adapter<WalkingAdapter.ViewHolder>{

    Context context;
    ArrayList<WalkingList> walkingListArrayList;

    public WalkingAdapter(Context context, ArrayList<WalkingList> walkingListArrayList) {
        this.context = context;
        this.walkingListArrayList = walkingListArrayList;
    }

    @NonNull
    @Override
    public WalkingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.walking_row, parent, false);
        return new WalkingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WalkingList walkingList = walkingListArrayList.get(position);
        // 데이 데이터 가공
        // 한국시간으로
        Locale koreanLocale = new Locale("ko", "KR");

        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", koreanLocale);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd", koreanLocale);

        try {
            Date date = inputDateFormat.parse(walkingList.createdAt);
            String formattedDateStr = outputDateFormat.format(date);
            Log.i("test", formattedDateStr+"");

            holder.txtDay.setText(formattedDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            holder.txtDay.setText("날짜 변환 오류");
        }

        // 거리데이터 가공
        // 패턴을 정의 #은 숫자가 있을 때만 표시
        DecimalFormat decimalFormat = new DecimalFormat("#.###");

        // 숫자를 형식화된 문자열로 변환
        String formattedNumber = decimalFormat.format(walkingList.distance);
        holder.txtDistance.setText(formattedNumber+"km");
        Log.i("test", walkingList.distance+"");

        // 시간 데이터 가공(hh:mm:ss)
        double hours = walkingList.time / 3600;  // 1 시간은 3600 초
        double minutes = (walkingList.time % 3600) / 60;  // 1 분은 60 초
        double seconds = walkingList.time % 60;
        String timeFormatted = String.format("%02d:%02d:%02d", (int)hours, (int)minutes, (int)seconds);
        holder.txtTime.setText(timeFormatted);
        Log.i("test", walkingList.time+"");

    }

    @Override
    public int getItemCount() {
        return walkingListArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtDay;
        TextView txtDistance;
        TextView txtTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtDay = itemView.findViewById(R.id.txtDay);
            txtDistance = itemView.findViewById(R.id.txtDistance);
            txtTime = itemView.findViewById(R.id.txtTime);

        }
    }
}
