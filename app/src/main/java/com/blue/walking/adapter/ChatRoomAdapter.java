package com.blue.walking.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.blue.walking.ChatRoomActivity;
import com.blue.walking.R;
import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.UserApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.ChatRoom;
import com.blue.walking.model.UserInfo;
import com.blue.walking.model.UserList;
import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder>{
    // 1:1채팅방
    Context context;
    ArrayList<ChatRoom> chatRoomArrayList;
    String token;
    int id;



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


        // 시간 변환
        Timestamp timestamp = (Timestamp) chatRoom.createdAt;
        Date timestampDate = timestamp.toDate();
        Date date = new Date(String.valueOf(timestampDate));
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String getTime = sdf.format(date);

        Log.i("adapter", date + "");

        Glide.with(context)
                .load(chatRoom.userImgUrl)
                .into(holder.imgUserUrl);
        holder.txtNickname.setText(chatRoom.userNickname);
        holder.txtTime.setText(getTime);
        holder.txtMessage.setText(chatRoom.message);
        Log.i("adapter", chatRoom.createdAt + "");

        // 메시지를 작성한 사용자의 ID와 현재 사용자의 ID를 비교하여 정렬
        Retrofit retrofit = NetworkClient.getRetrofitClient(context);
        UserApi api = retrofit.create(UserApi.class);

        SharedPreferences sp = context.getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString(Config.ACCESS_TOKEN, "");

        Call<UserList> call = api.getUserInfo("Bearer " + token);

        call.enqueue(new Callback<UserList>() {
            @Override
            public void onResponse(Call<UserList> call, Response<UserList> response) {
                if (response.isSuccessful()) {
                    UserList userList = response.body();
                    ArrayList<UserInfo> userInfoArrayList = userList.info;
                    if (!userInfoArrayList.isEmpty()) {
                        UserInfo userInfo = userInfoArrayList.get(0);

                        id = userInfo.id;

                        int currentUserId = id;

                        if (chatRoom.id == currentUserId) {
                            // 메시지를 작성한 사용자가 현재 사용자인 경우 오른쪽 정렬
                            setRightAlignment(holder);
                        } else {
                            // 메시지를 작성한 사용자가 다른 사용자인 경우 왼쪽 정렬
                            setLeftAlignment(holder);
                        }
                    } else{

                    }
                }

            }

            @Override
            public void onFailure(Call<UserList> call, Throwable t) {

            }
        });

    }

            // 오른쪽 정렬을 설정하는 메서드
    private void setRightAlignment(ViewHolder holder) {
        holder.imgUserUrl.setVisibility(View.GONE);
        holder.txtNickname.setGravity(Gravity.RIGHT);

        holder.linearLayout1.setGravity(Gravity.RIGHT);

        // params로 지정
        LinearLayout.LayoutParams paramsMessage = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
        LinearLayout.LayoutParams paramsTime = (LinearLayout.LayoutParams) holder.txtTime.getLayoutParams();

        // txtMessage와 txtTime의 위치를 바꿈
        holder.linearLayout1.removeView(holder.txtMessage);
        holder.linearLayout1.removeView(holder.txtTime);

        // 순서를 변경한 후 다시 추가
        holder.linearLayout1.addView(holder.txtTime, paramsTime);
        holder.linearLayout1.addView(holder.txtMessage, paramsMessage);

        // 메시지 배경색 바꾸기
        holder.txtMessage.setBackgroundColor(Color.parseColor("#FFF6C5"));
    }

    // 왼쪽 정렬을 설정하는 메서드
    private void setLeftAlignment(ViewHolder holder) {
        holder.imgUserUrl.setVisibility(View.VISIBLE);
        holder.txtNickname.setGravity(Gravity.LEFT);

        holder.linearLayout1.setGravity(Gravity.LEFT);

        // params로 지정
        LinearLayout.LayoutParams paramsMessage = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
        LinearLayout.LayoutParams paramsTime = (LinearLayout.LayoutParams) holder.txtTime.getLayoutParams();

        // txtMessage와 txtTime의 위치를 바꿈
        holder.linearLayout1.removeView(holder.txtMessage);
        holder.linearLayout1.removeView(holder.txtTime);

        // 순서를 변경한 후 다시 추가
        holder.linearLayout1.addView(holder.txtMessage, paramsMessage);
        holder.linearLayout1.addView(holder.txtTime, paramsTime);

        // 메시지 배경색 초기화
        holder.txtMessage.setBackgroundColor(Color.parseColor("#E1E1E1"));
    }
    @Override
    public int getItemCount() {
        return chatRoomArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return chatRoomArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return chatRoomArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgUserUrl;
        TextView txtNickname;
        TextView txtMessage;
        TextView txtTime;
        LinearLayout linearLayout1;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgUserUrl = itemView.findViewById(R.id.imgUserUrl);
            txtNickname = itemView.findViewById(R.id.txtNickname);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtTime = itemView.findViewById(R.id.txtTime);
            linearLayout1 = itemView.findViewById(R.id.linearLayout1);

        }
    }
}

