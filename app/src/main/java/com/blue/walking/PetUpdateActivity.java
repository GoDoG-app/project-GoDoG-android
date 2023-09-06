package com.blue.walking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.PetApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.Pet;
import com.blue.walking.model.PetList;
import com.blue.walking.model.ResultRes;
import com.bumptech.glide.Glide;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PetUpdateActivity extends AppCompatActivity {

    ImageView imgBack;  // 뒤로가기 버튼
    ImageView imgPet;   // 강아지 사진
    TextView txtImg;    // 사진 선택
    EditText editPetName;   // 강아지 이름
    EditText editPetAge;   // 강아지 생일
    Button btnMale;    // 강아지 성별 남자
    Button btnFemale;  // 강아지 성별 여자
    EditText editComment;   // 한 줄 소개
    Button btnUpdate;   // 수정완료


    int petGender = 1;  // RequestBody 에 들어갈때는 +"" 를 붙여서 문자열로 넣음
    File photoFile; // 사진 들어있는 멤버변수 파일
    String token;
    ArrayList<Pet> petArrayList = new ArrayList<>();

    String petName;
    String petAge;
    String petComment;
    String photoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_update);

        txtImg = findViewById(R.id.txtImg);
        editPetName = findViewById(R.id.editPetName);
        editPetAge = findViewById(R.id.editPetAge);
        btnMale = findViewById(R.id.btnMale);
        btnFemale = findViewById(R.id.btnFemale);
        editComment = findViewById(R.id.editComment);
        btnUpdate = findViewById(R.id.btnUpdate);
        imgBack = findViewById(R.id.imgBack);
        imgPet = findViewById(R.id.imgPet);

        imgPet.setClipToOutline(true);  // 둥근 테두리 적용



        /** 내 펫 정보 API (수정 전 정보를 화면에 띄워주기 + 저장하기) */
        Retrofit retrofit = NetworkClient.getRetrofitClient(PetUpdateActivity.this);
        PetApi api = retrofit.create(PetApi.class);

        Log.i("pet","내 펫 정보 API 실행");

        // 유저 토큰
        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString(Config.ACCESS_TOKEN, "");

        Call<PetList> call = api.petInfo("Bearer " + token);
        call.enqueue(new Callback<PetList>() {
            @Override
            public void onResponse(Call<PetList> call, Response<PetList> response) {
                if (response.isSuccessful()) {

                    // 서버에서 받아온 데이터를 리스트에 넣음
                    PetList petList = response.body();
                    petArrayList.addAll(petList.items);

                    if (petArrayList.size() != 0) {
                        // 리스트의 사이즈가 0이 아닐때만 화면에 적용 실행

                        Log.i("pet", "펫 정보 불러오기 완료");

                        // 성별 확인
                        if (Integer.valueOf(petArrayList.get(0).petGender) == 0) {
                            btnFemale.setBackgroundColor(Color.parseColor("#262D33"));
                            btnFemale.setTextColor(Color.parseColor("#FFFFFF"));
                            btnMale.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            btnMale.setTextColor(Color.parseColor("#262D33"));
                            petGender = 0;
                        } else {
                            btnMale.setBackgroundColor(Color.parseColor("#262D33"));
                            btnMale.setTextColor(Color.parseColor("#FFFFFF"));
                            btnFemale.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            btnFemale.setTextColor(Color.parseColor("#262D33"));
                            petGender = 1;
                        }

                        // 수정하기 전, 서버를 통해 가져온 수정 전인 값을 멤버 변수에 넣음(저장)
                        // 이렇게하면 부분 수정 가능해짐
                        petName = petArrayList.get(0).petName;
                        petAge = petArrayList.get(0).petAge;
                        petComment = petArrayList.get(0).oneliner;
                        photoText = petArrayList.get(0).petProUrl;


                        // 수정 전인 값을 화면에 적용
                        editPetName.setText(petName);
                        editPetAge.setText(petAge);
                        editComment.setText(petComment);
                        Glide.with(PetUpdateActivity.this).load(petArrayList.get(0).petProUrl).into(imgPet);

                    } else {
                        return;
                    }

                } else {
                    Log.i("pet","펫 정보 불러오기 실패");
                }
            }

            @Override
            public void onFailure(Call<PetList> call, Throwable t) {
                Log.i("pet","펫 정보 불러오기 실패");
            }
        });


        // 성별 버튼 클릭
        btnMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnMale.setBackgroundColor(Color.parseColor("#262D33"));
                btnMale.setTextColor(Color.parseColor("#FFFFFF"));
                btnFemale.setBackgroundColor(Color.parseColor("#FFFFFF"));
                btnFemale.setTextColor(Color.parseColor("#262D33"));
            }
        });

        btnFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnFemale.setBackgroundColor(Color.parseColor("#262D33"));
                btnFemale.setTextColor(Color.parseColor("#FFFFFF"));
                btnMale.setBackgroundColor(Color.parseColor("#FFFFFF"));
                btnMale.setTextColor(Color.parseColor("#262D33"));
                petGender = 0;
            }
        });

        // 프로필 사진 선택하기
        txtImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("imgContent", "이미지 선택하기");
                showDialog();
            }
        });

        // 수정 완료 버튼을 눌렀을 때
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("pet", "수정버튼클릭함");

                // 이름 가져오기
                petName = editPetName.getText().toString().trim();

                // 나이 가져오기
                petAge = editPetAge.getText().toString().trim();

                // 한 줄 소개 가져오기
                petComment = editComment.getText().toString().trim();

                /** 펫 수정 API */
                Retrofit retrofit = NetworkClient.getRetrofitClient(PetUpdateActivity.this);
                PetApi api = retrofit.create(PetApi.class);

                Log.i("pet","펫 수정 API 실행");

                // 유저 토큰
                SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                token = sp.getString(Config.ACCESS_TOKEN, "");


                // Body 폼 데이터에 들어갈 데이터
                // 유저가 사진을 선택한 경우와 안한 경우
                MultipartBody.Part photo;
                if (photoFile == null){
                    // 선택 안했다면 아까 서버를 통해 가져온 수정 전인 값을 보내기
                    photo = MultipartBody.Part.createFormData("photo", photoText);

                } else {
                    // 선택했다면 photoFile 보내기
                    RequestBody fileBody = RequestBody.create(photoFile, MediaType.parse("image/jpg"));
                    photo = MultipartBody.Part.createFormData("photo", photoFile.getName(), fileBody);
                }

                RequestBody petNameBody = RequestBody.create(petName, MediaType.parse("text/plain"));
                RequestBody petAgeBody = RequestBody.create(petAge, MediaType.parse("text/plain"));
                RequestBody petGenderBody = RequestBody.create(petGender+"", MediaType.parse("text/plain"));
                RequestBody petCommentBody = RequestBody.create(petComment, MediaType.parse("text/plain"));


                // API 호출
                Call<ResultRes> call = api.petEdit("Bearer "+token, photo, petNameBody, petAgeBody, petGenderBody, petCommentBody);
                call.enqueue(new Callback<ResultRes>() {
                    @Override
                    public void onResponse(Call<ResultRes> call, Response<ResultRes> response) {
                        if (response.isSuccessful()){
                            Log.i("pet","펫 수정 완료");
                            Toast.makeText(PetUpdateActivity.this,
                                    "수정을 완료했습니다.",
                                    Toast.LENGTH_SHORT).show();
                            finish();

                        } else {
                            Log.i("pet","펫 수정 실패");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultRes> call, Throwable t) {
                        Log.i("pet","펫 수정 실패");
                    }
                });
            }
        });


        // 뒤로가기
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void showDialog() {
        Log.i("Dialog", "다이얼로그 실행");

        AlertDialog.Builder builder = new AlertDialog.Builder(PetUpdateActivity.this);
        builder.setTitle(R.string.alert_Title);
        builder.setItems(R.array.alert_photo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (i == 0) {
                    // 첫번째 항목을 눌렀을 때
                    // 카메라로 사진찍기 함수 실행
                    camera();

                } else if (i == 1) {
                    // 두번째 항목을 눌렀을 때
                    // 앨범에서 가져오기 함수 실행
                    album();

                }
            }
        });
        builder.show();

    }

    private void camera() {

        int permissionCheck = ContextCompat.checkSelfPermission(
                PetUpdateActivity.this, android.Manifest.permission.CAMERA);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            // 권한 수락이 되지 않았을 때
            ActivityCompat.requestPermissions(PetUpdateActivity.this,
                    new String[]{android.Manifest.permission.CAMERA} ,
                    1000);
            Toast.makeText(PetUpdateActivity.this, "카메라 권한 필요합니다.",
                    Toast.LENGTH_SHORT).show();
            return;

        } else {
            // 권한 수락이 되었을 때
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(i.resolveActivity(PetUpdateActivity.this.getPackageManager())  != null  ){
                // 카메라 앱이 있을 때
                // 사진의 파일명을 만들기
                String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                photoFile = getPhotoFile(fileName);

                Uri fileProvider = FileProvider.getUriForFile(PetUpdateActivity.this,
                        "com.blue.walking.fileprovider", photoFile); // AndroidManifest 의 authorities 와 같아야함!!
                i.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                startActivityForResult(i, 100);

            } else{
                // 카메라 앱이 없을 때
                Toast.makeText(PetUpdateActivity.this, "이폰에는 카메라 앱이 없습니다.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    // 카메라로 찍은 사진 저장
    private File getPhotoFile(String fileName) {
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try{
            return File.createTempFile(fileName, ".jpg", storageDirectory);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    // 눌렀을 때 권한 작동
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: { // 카메라로 사진찍기를 눌렀을 때
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(PetUpdateActivity.this, "권한 허가 되었음",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PetUpdateActivity.this, "아직 승인하지 않았음",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 500: { // 갤러리로 사진찍기를 눌렀을 때
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(PetUpdateActivity.this, "권한 허가 되었음",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PetUpdateActivity.this, "아직 승인하지 않았음",
                            Toast.LENGTH_SHORT).show();
                }

            }

        }
    }

    // 카메라로 사진을 찍었을 때
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 100 && resultCode == RESULT_OK){

            Bitmap photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

            ExifInterface exif = null;
            try {
                exif = new ExifInterface(photoFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            photo = rotateBitmap(photo, orientation);

            // 압축시킨다. 해상도 낮춰서 (용량 짱 크기 때문)
            // 네트워크 통신과 저장 공간을 수월하게 하기 위해..
            OutputStream os;
            try {
                os = new FileOutputStream(photoFile);
                photo.compress(Bitmap.CompressFormat.JPEG, 50, os);
                os.flush();
                os.close();
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
            }

            photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

            imgPet.setImageBitmap(photo);
            imgPet.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // 네트워크로 데이터 보낸다.
            // 필요할 때 작성하면 됨(지금은 필요없어서 비워둠)


        }else if(requestCode == 300 && resultCode == RESULT_OK && data != null &&
                data.getData() != null){

            Uri albumUri = data.getData( );
            String fileName = getFileName( albumUri );
            try {

                ParcelFileDescriptor parcelFileDescriptor = getContentResolver( ).openFileDescriptor( albumUri, "r" );
                if ( parcelFileDescriptor == null ) return;
                FileInputStream inputStream = new FileInputStream( parcelFileDescriptor.getFileDescriptor( ) );
                photoFile = new File( this.getCacheDir( ), fileName );
                FileOutputStream outputStream = new FileOutputStream( photoFile );
                IOUtils.copy( inputStream, outputStream );

//                //임시파일 생성
//                File file = createImgCacheFile( );
//                String cacheFilePath = file.getAbsolutePath( );


                // 압축시킨다. 해상도 낮춰서
                Bitmap photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                OutputStream os;
                try {
                    os = new FileOutputStream(photoFile);
                    photo.compress(Bitmap.CompressFormat.JPEG, 60, os);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
                }

                imgPet.setImageBitmap(photo);
                imgPet.setScaleType(ImageView.ScaleType.CENTER_CROP);

//                imageView.setImageBitmap( getBitmapAlbum( imageView, albumUri ) );

            } catch ( Exception e ) {
                e.printStackTrace( );
            }

            // 네트워크로 보낸다.
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 사진의 가로/세로 파악해서 맞게 회전시켜주는 함수
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    // 앨범에서 사진 가져올 때
    private void album(){
        if(checkPermission()){
            displayFileChoose();
        }else{
            requestPermission();
        }
    }

    // 앨범에서 사진 가져오기 권한주기
    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(PetUpdateActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_DENIED){
            return false;
        }else{
            return true;
        }
    }

    // 권한 요청 코드
    private void requestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(PetUpdateActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Log.i("DEBUGGING5", "true");
            Toast.makeText(PetUpdateActivity.this, "권한 수락이 필요합니다.",
                    Toast.LENGTH_SHORT).show();
        }else{
            Log.i("DEBUGGING6", "false");
            ActivityCompat.requestPermissions(PetUpdateActivity.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 500);
        }
    }

    // 앨범에서 파일 선택해옴
    private void displayFileChoose() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "SELECT IMAGE"), 300);
    }

    //앨범에서 선택한 사진이름 가져오기
    public String getFileName( Uri uri ) {
        Cursor cursor = getContentResolver( ).query( uri, null, null, null, null );
        try {
            if ( cursor == null ) return null;
            cursor.moveToFirst( );
            @SuppressLint("Range") String fileName = cursor.getString( cursor.getColumnIndex( OpenableColumns.DISPLAY_NAME ) );
            cursor.close( );
            return fileName;

        } catch ( Exception e ) {
            e.printStackTrace( );
            cursor.close( );
            return null;
        }
    }
}