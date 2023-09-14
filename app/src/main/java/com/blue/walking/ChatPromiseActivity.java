package com.blue.walking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.blue.walking.api.NetworkStatus;
import com.blue.walking.model.ChatRoom;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatPromiseActivity extends AppCompatActivity {

    /** 화면뷰 */
    ImageView imgBack;  // 뒤로가기
    Button btnDate;   // 날짜 선택
    Button btnTime;   // 시간 선택
    TextView txtPlace;  // 장소 선택
    Button btnPromise;  // 약속 잡기

    String date = "";
    String time = "";

    String address;
    double latitude;
    double longitude;
    String chatMessage;
    ChatRoom chatRoom;
    FirebaseFirestore db;
    int id;
    int receiverId;
    String userImgUrl;
    String userNickname;
    String roomName;

    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_promise);

//        intent.putExtra("id",id);
//        intent.putExtra("userImgUrl",userImgUrl);
//        intent.putExtra("userNickname",userNickname);
//        intent.putExtra("roomName",roomName);
//        intent.putExtra("receiverId",receiverId);
        id = getIntent().getIntExtra("id",0);
        userImgUrl = getIntent().getStringExtra("userImgUrl");
        userNickname = getIntent().getStringExtra("userNickname");
        roomName = getIntent().getStringExtra("roomName");
        receiverId = getIntent().getIntExtra("receiverId",0);



        imgBack = findViewById(R.id.imgBack);
        btnDate = findViewById(R.id.btnDate);
        btnTime = findViewById(R.id.btnTime);
        txtPlace = findViewById(R.id.txtPlace);
        btnPromise = findViewById(R.id.btnPromise);

        // 뒤로가기
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 날짜선택 버튼 눌렀을때
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar current = Calendar.getInstance();

                // 현재의 년, 월, 일 가져오는 코드.
//                current.get(Calendar.YEAR);
//                current.get(Calendar.MONTH);
//                current.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ChatPromiseActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // 유저가 선택한 년,월(0 부터 시작),일을 띄운다

                                String strMonth;
                                if(month < 10){
                                    strMonth = "0" + (month+1);
                                }else {
                                    strMonth = "" + (month+1);
                                }

                                String strDay;
                                if(dayOfMonth < 10){
                                    strDay = "0" + dayOfMonth;
                                }else {
                                    strDay = "" + dayOfMonth;
                                }

                                date = year + "-" + strMonth + "-" + strDay;
                                btnDate.setText(date);

                            }
                        },
                        current.get(Calendar.YEAR),
                        current.get(Calendar.MONTH),
                        current.get(Calendar.DAY_OF_MONTH)
                );
                dialog.show();
            }
        });

        // 시간선택 버튼 눌렀을때
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar current = Calendar.getInstance();


                TimePickerDialog dialog = new TimePickerDialog(
                        ChatPromiseActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


                                String strHour;
                                if(hourOfDay < 10){
                                    strHour = "0" + hourOfDay;
                                }else {
                                    strHour = "" + hourOfDay;
                                }

                                String strMinute;
                                if(minute < 10){
                                    strMinute = "0" + minute;
                                }else {
                                    strMinute = "" + minute;
                                }

                                time = strHour + ":" + strMinute;
                                btnTime.setText(time);



                            }
                        },
                        current.get(Calendar.HOUR_OF_DAY),
                        current.get(Calendar.MINUTE),
                        true

                );
                dialog.show();

            }
        });

        // 장소선택 버튼 눌렀을때
        txtPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("주소설정페이지", "주소입력창 클릭");
                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if(status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {

                    Log.i("주소설정페이지", "주소입력창 클릭");
                    Intent intent = new Intent(getApplicationContext(), RegionActivity.class);
                    // 화면전환 애니메이션 없애기
                    overridePendingTransition(0, 0);
                    // 주소결과
                    startActivityForResult(intent, SEARCH_ADDRESS_ACTIVITY);

                }else {
                    Toast.makeText(getApplicationContext(),
                            "인터넷 연결을 확인해주세요.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

        // 약속잡기 버튼 눌렀을때
        btnPromise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMessage();

            }
        });


    }

    public void sendMessage(){


        // 입력한 메세지 가져오기
        chatMessage = "약속을 만들었어요." + "\n" + "날짜 : " +date + "\n" +"시간 : " + time + "\n" + "장소 : " + address;
        Log.i("chatMessage","" +chatMessage);
        if (chatMessage.isEmpty()){
            Snackbar.make(btnPromise,
                    "필수항목을 확인하세요.",
                    Snackbar.LENGTH_SHORT).show();
            return;
        } else {

            Timestamp timestamp = Timestamp.now();

            Log.i("test", id+userNickname+userImgUrl+chatMessage+timestamp.toDate());

            // 대화 상대의 user id를 배열로 저장
            List<Integer> user = Arrays.asList(id, receiverId);

            db = FirebaseFirestore.getInstance();

            // Firestore에 저장할 데이터 생성
            Map<String, Object> chatRoomData = new HashMap<>();
            chatRoomData.put("user", user);
            // Firestore에 데이터를 쓸 때 서버 타임스탬프를 할당
            chatRoom = new ChatRoom(id, userNickname, userImgUrl, chatMessage, timestamp);

            DocumentReference chatDocRef = db.collection("chat").document(roomName);
            Log.i("roomName2", roomName+"");
            // 문서 참조를 사용하여 데이터 설정
            chatDocRef.set(chatRoomData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // 채팅 방 문서 생성 성공
                            // chatMessage 컬렉션에 저장할 채팅 메시지 데이터 생성
                            ChatRoom chatRoom = new ChatRoom(id, userNickname, userImgUrl, chatMessage, timestamp);

                            // chatMessage 컬렉션에 새로운 문서 추가
                            chatDocRef.collection("chatMessage")
                                    .add(chatRoom)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference messageDocRef) {
                                            Log.i("test", "메시지 문서 생성 성공");

                                            // 마지막 초기화
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.i("test", "메시지 문서 생성 실패: " + e.getMessage());
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // 채팅 방 문서 생성 실패
                        }
                    });

        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.i("test", "onActivityResult");

        switch (requestCode) {
            case SEARCH_ADDRESS_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    // 주소 데이터 추출
                    String sido = intent.getStringExtra("sido");
                    String sigungu = intent.getStringExtra("sigungu");
                    String hname = intent.getStringExtra("hname");
                    String fullAddr = intent.getStringExtra("fullAddr");

                    if (sido != null && sigungu != null && hname != null && fullAddr != null) {
                        // 주소 데이터 로그찍기
                        Log.i("test", "시/도:" + sido);
                        Log.i("test", "시/군/구:" + sigungu);
                        Log.i("test", "행정동 이름:" + hname);
                        Log.i("test", "전체 주소:" + fullAddr);

                        // 시군구동 합쳐서 보여주기
                        address = sido+" "+sigungu+" "+hname;
                        txtPlace.setText(fullAddr);

                        // 위도 경도 구하기
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocationName(fullAddr, 1);
                            if (!addresses.isEmpty()) {
                                latitude = addresses.get(0).getLatitude();
                                longitude = addresses.get(0).getLongitude();

                                // 위도 경도 로그 표시
                                Log.i("test", "위도: " + latitude + ", 경도: " + longitude);

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
                break;
        }
    }

}