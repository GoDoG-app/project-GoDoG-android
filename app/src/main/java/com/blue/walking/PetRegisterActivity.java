package com.blue.walking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.PetApi;
import com.blue.walking.api.UserApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.Pet;
import com.blue.walking.model.ResultRes;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.transform.Result;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PetRegisterActivity extends AppCompatActivity {

    ImageView imgBack;  // 뒤로가기 버튼

    ImageView imgPet;  // 강아지 이미지
    TextView txtImg;   // 강아지 사진 선택
    EditText editPetName;  // 강아지 이름
    EditText editPetAge;  // 강아지 나이
    Button btnMale;  // 강아지 성별 남성
    Button btnFemale;  // 강아지 성별 여성
    EditText editComment;  // 한 줄 소개
    Button btnRegister;  // 등록 완료

    int petGender = 1;  // RequestBody 에 들어갈때는 +"" 를 붙여서 문자열로 넣음
    File photoFile; // 사진 들어있는 멤버변수 파일
    String token;  // 토큰


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_register);

        imgBack = findViewById(R.id.imgBack);
        imgPet = findViewById(R.id.imgPet);
        txtImg = findViewById(R.id.txtImg);
        editPetName = findViewById(R.id.editPetName);
        editPetAge = findViewById(R.id.editPetAge);
        btnMale = findViewById(R.id.btnMale);
        btnFemale = findViewById(R.id.btnFemale);
        editComment = findViewById(R.id.editComment);
        btnRegister = findViewById(R.id.btnRegister);

        imgPet.setClipToOutline(true);  // 둥근 테두리 적용

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

        // 반려가족 등록 버튼 클릭할 때
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이름 가져오기
                String petName = editPetName.getText().toString().trim();

                // 나이 가져오기
                String petAge = editPetAge.getText().toString().trim();

                // 한 줄 소개 가져오기
                String petComment = editComment.getText().toString().trim();

                if (petName.isEmpty() || petAge.isEmpty()) {
                    Snackbar.make(btnRegister,
                            "이름 또는 나이를 입력해주세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                /** 펫 등록 API */
                Retrofit retrofit = NetworkClient.getRetrofitClient(PetRegisterActivity.this);
                PetApi api = retrofit.create(PetApi.class);

                Log.i("pet","펫 등록 API 실행");

                // 유저 토큰
                SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                token = sp.getString(Config.ACCESS_TOKEN, "");

                // Body 폼 데이터에 들어갈 데이터
                RequestBody fileBody = RequestBody.create(photoFile, MediaType.parse("image/jpg"));
                MultipartBody.Part photo = MultipartBody.Part.createFormData("photo", photoFile.getName(), fileBody);
                RequestBody petNameBody = RequestBody.create(petName, MediaType.parse("text/plain"));
                RequestBody petAgeBody = RequestBody.create(petAge, MediaType.parse("text/plain"));
                RequestBody petGenderBody = RequestBody.create(petGender+"", MediaType.parse("text/plain"));
                RequestBody petCommentBody = RequestBody.create(petComment, MediaType.parse("text/plain"));

                // API 호출
                Call<ResultRes> call = api.petRegister("Bearer "+token, photo, petNameBody, petAgeBody, petGenderBody, petCommentBody);
                call.enqueue(new Callback<ResultRes>() {
                    @Override
                    public void onResponse(Call<ResultRes> call, Response<ResultRes> response) {
                        if (response.isSuccessful()){
                            Log.i("pet","펫 등록 완료");
                            Toast.makeText(PetRegisterActivity.this,
                                    "등록을 완료했습니다.",
                                    Toast.LENGTH_SHORT).show();
                            finish();

                        } else {
                            Log.i("pet","펫 등록 실패");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultRes> call, Throwable t) {
                        Log.i("pet","펫 등록 실패");
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

        AlertDialog.Builder builder = new AlertDialog.Builder(PetRegisterActivity.this);
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
                PetRegisterActivity.this, android.Manifest.permission.CAMERA);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            // 권한 수락이 되지 않았을 때
            ActivityCompat.requestPermissions(PetRegisterActivity.this,
                    new String[]{android.Manifest.permission.CAMERA} ,
                    1000);
            Toast.makeText(PetRegisterActivity.this, "카메라 권한 필요합니다.",
                    Toast.LENGTH_SHORT).show();
            return;

        } else {
            // 권한 수락이 되었을 때
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(i.resolveActivity(PetRegisterActivity.this.getPackageManager())  != null  ){
                // 카메라 앱이 있을 때
                // 사진의 파일명을 만들기
                String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                photoFile = getPhotoFile(fileName);

                Uri fileProvider = FileProvider.getUriForFile(PetRegisterActivity.this,
                        "com.blue.walking.fileprovider", photoFile); // AndroidManifest 의 authorities 와 같아야함!!
                i.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                startActivityForResult(i, 100);

            } else{
                // 카메라 앱이 없을 때
                Toast.makeText(PetRegisterActivity.this, "이폰에는 카메라 앱이 없습니다.",
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
                    Toast.makeText(PetRegisterActivity.this, "권한 허가 되었음",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PetRegisterActivity.this, "아직 승인하지 않았음",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 500: { // 갤러리로 사진찍기를 눌렀을 때
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(PetRegisterActivity.this, "권한 허가 되었음",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PetRegisterActivity.this, "아직 승인하지 않았음",
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
                imgPet.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

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
        int result = ContextCompat.checkSelfPermission(PetRegisterActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_DENIED){
            return false;
        }else{
            return true;
        }
    }

    // 권한 요청 코드
    private void requestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(PetRegisterActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Log.i("DEBUGGING5", "true");
            Toast.makeText(PetRegisterActivity.this, "권한 수락이 필요합니다.",
                    Toast.LENGTH_SHORT).show();
        }else{
            Log.i("DEBUGGING6", "false");
            ActivityCompat.requestPermissions(PetRegisterActivity.this,
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
