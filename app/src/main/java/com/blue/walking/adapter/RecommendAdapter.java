package com.blue.walking.adapter;

import android.annotation.SuppressLint;
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
import com.blue.walking.R;
import com.blue.walking.Walking3;
import com.blue.walking.model.Park;
import com.blue.walking.model.ParkImage;
import com.blue.walking.model.UserInfo;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.Random;

public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.ViewHolder>{

    Context context;
    ArrayList<Park> parkArrayList; // 공원 정보가 들어 있는 ArrayList
    ArrayList<UserInfo> userInfoArrayList;

    public RecommendAdapter(Context context, ArrayList<Park> parkArrayList, ArrayList<UserInfo> userInfoArrayList) {
        this.context = context;
        this.parkArrayList = parkArrayList;
        this.userInfoArrayList = userInfoArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recommend_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Park park = parkArrayList.get(position);

        // 공원 이름, 주소 설정
        holder.txtPlaceName1.setText(park.getName());
        holder.txtPlace1.setText(park.getAddress());

        ArrayList<ParkImage> parkImageArrayList = new ArrayList<>();
        for (int i = 1; i < 20; ++i){
            String imageUrl = "https://project4-walking-app.s3.amazonaws.com/park" + i + ".jpg";
            ParkImage parkImage = new ParkImage(imageUrl);
            parkImageArrayList.add(parkImage);
        }
        // 랜덤 객체 생성
        Random random = new Random();
        // parkImageArrayList의 크기를 가져옴
        int arrayListSize = parkImageArrayList.size();
        // 랜덤 인덱스 생성
        int randomIndex = random.nextInt(arrayListSize);
        // 랜덤 이미지 URL을 가져옴
        String randomImageUrl = parkImageArrayList.get(randomIndex).parkUrl;
        // 공원 이미지
        Glide.with(context).load(randomImageUrl).override(200,150).into(holder.imgPlace1);


    }

    @Override
    public int getItemCount() {
        return parkArrayList.size(); // 보여줄 공원 화면 갯수
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        /** 산책로 추천 화면뷰 */
        ImageView imgPlace1;
        TextView txtPlaceName1;
        TextView txtPlace1;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPlace1 = itemView.findViewById(R.id.imgPlace1);
            txtPlaceName1 = itemView.findViewById(R.id.txtPlaceName1);
            txtPlace1 = itemView.findViewById(R.id.txtPlace1);
            cardView = itemView.findViewById(R.id.cardView);

            cardView.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceType")
                @Override
                public void onClick(View view) {
                    // 유저가 클릭한 카드뷰 데이터의 인덱스
                    int index = getAdapterPosition();
                    Park park = parkArrayList.get(index);
                    UserInfo userInfo = userInfoArrayList.get(0);

                    // 유저가 클릭한 카드뷰의 데이터를 다음 액티비티로 전달하고 화면도 해당 액티비티로 이동
                    Intent intent = new Intent(context, Walking3.class);
                    // intent에 담을 데이터 (데이터가 있는 클래스가 Serializable을 구현해야함 )
                    intent.putExtra("walkingRoute", park);
                    intent.putExtra("userInfo", userInfo);
                    context.startActivity(intent);

//                    // 프래그먼트 간의 데이터 전달 인텐트 권장하지 않는다함
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("walkingRoute", park); // 번들에 담을 데이터 (put.string int 등등 있음)
//                    Walking_3 fragment = new Walking_3(); // 데이터를 받을 프래그먼트
//                    fragment.setArguments(bundle); // 프래그먼트에 담을 번들(데이터)
//
//                    /** Fragment 끼리 이동하기 위해서는 Activity 내 Fragment manager 를 통해 이동하게 되며,
//                     * Activity 위에서 이동하기 때문에 intent 가 아닌 메소드를 통해서 이동하게 된다. */
//                    // 프래그먼트 트랜잭션 시작
//                    FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
//                    // 프래그먼트를 트랜잭션에 추가 또는 교체하고 컨테이너 뷰 ID를 지정
//                    transaction.replace(R.id.fragment_container, fragment);
//                    // 백 스택에 프래그먼트 추가 (선택 사항)
//                    transaction.addToBackStack(null);
//                    // 트랜잭션 커밋
//                    transaction.commit();

                }
            });
        }
    }
}
