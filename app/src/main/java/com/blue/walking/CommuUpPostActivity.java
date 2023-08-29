package com.blue.walking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blue.walking.api.NetworkClient;
import com.blue.walking.api.PostApi;
import com.blue.walking.config.Config;
import com.blue.walking.model.Post;
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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CommuUpPostActivity extends AppCompatActivity {

    /** 화면 뷰 */
    ImageView imgBack;  // 뒤로가기
    Spinner spinner;  // 카테고리 목록
    EditText editContent;  // 업로드할 내용 입력
    ImageView imgContent;  // 업로드할 사진 선택
    Button btnUpload;   // 게시글 업로드

    String[] items = {"일상","정보","산책"};
    String category;
    String content;
    File photoFile; // 사진 들어있는 멤버변수 파일

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commu_up_post);

        editContent = findViewById(R.id.editContent);
        imgContent = findViewById(R.id.imgContent);
        btnUpload = findViewById(R.id.btnUpload);
        imgBack = findViewById(R.id.imgBack);
        spinner = findViewById(R.id.spinner);


        // 카테고리 목록 띄우기
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // 카테고리 선택하면 실행할 부분
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // 이 오버라이드 메소드에서 i 는 몇번째 값이 클릭됬는지 알 수 있음
                // 0:일상, 1:정보, 2:산책 (전체는 이 세개를 전부 가져오기)
                category = i+"";  // 문자열로 담음(isEmpty() 때문)
                Log.i("category","카테고리 "+category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        // 사진 가져오기
        imgContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("imgContent", "이미지 선택하기");
                showDialog();
            }
        });



        /** 커뮤니티 게시물 작성 API (아직 안됨) */
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 작성한 내용 가져오기
                content = editContent.getText().toString().trim();

                // 내용, 사진, 카테고리 선택되었는지 확인
                if (content.isEmpty() || category.isEmpty() || photoFile == null){
                    Snackbar.make(btnUpload,
                            "항목을 입력 또는 선택해주세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                // 확인했다면 서버로 데이터 보내기
                Retrofit retrofit = NetworkClient.getRetrofitClient(CommuUpPostActivity.this);
                PostApi api = retrofit.create(PostApi.class);

                // 헤더에 세팅할 토큰 가져오기
                SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                String token = sp.getString(Config.ACCESS_TOKEN, "");

                // 큰 파일을 한번에 못 보내니깐 잘게 쪼개서 보낼 수 있도록 하는 코드
                RequestBody fileBody = RequestBody.create(photoFile, MediaType.parse("image/jpg"));
                MultipartBody.Part photo = MultipartBody.Part.createFormData("Photo", photoFile.getName(), fileBody);

                RequestBody textBody = RequestBody.create(content,  MediaType.parse("text/plain"));

                Call<ResultRes> call = api.upPost("Bearer "+token,photo,textBody); //todo: 카테고리는 어떻게보낼지..
                call.enqueue(new Callback<ResultRes>() {
                    @Override
                    public void onResponse(Call<ResultRes> call, Response<ResultRes> response) {
                        dismissProgress();
                        if (response.isSuccessful()){
                            finish();

                        }else {

                        }
                    }

                    @Override
                    public void onFailure(Call<ResultRes> call, Throwable t) {

                    }
                });
                finish();
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

    private void dismissProgress() {
    }

    // 사진 가져오는 알러트 다이얼로그
    private void showDialog() {
        Log.i("Dialog", "다이얼로그 실행");

        AlertDialog.Builder builder = new AlertDialog.Builder(CommuUpPostActivity.this);
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

    private void camera(){

        int permissionCheck = ContextCompat.checkSelfPermission(
                CommuUpPostActivity.this, android.Manifest.permission.CAMERA);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            // 권한 수락이 되지 않았을 때
            ActivityCompat.requestPermissions(CommuUpPostActivity.this,
                    new String[]{android.Manifest.permission.CAMERA} ,
                    1000);
            Toast.makeText(CommuUpPostActivity.this, "카메라 권한 필요합니다.",
                    Toast.LENGTH_SHORT).show();
            return;

        } else {
            // 권한 수락이 되었을 때
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(i.resolveActivity(CommuUpPostActivity.this.getPackageManager())  != null  ){
                // 카메라 앱이 있을 때
                // 사진의 파일명을 만들기
                String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                photoFile = getPhotoFile(fileName);

                Uri fileProvider = FileProvider.getUriForFile(CommuUpPostActivity.this,
                        "com.blue.postapp.fileprovider", photoFile); // AndroidManifest 의 authorities 와 같아야함!!
                i.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                startActivityForResult(i, 100);

            } else{
                // 카메라 앱이 없을 때
                Toast.makeText(CommuUpPostActivity.this, "이폰에는 카메라 앱이 없습니다.",
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
                    Toast.makeText(CommuUpPostActivity.this, "권한 허가 되었음",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CommuUpPostActivity.this, "아직 승인하지 않았음",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 500: { // 갤러리로 사진찍기를 눌렀을 때
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(CommuUpPostActivity.this, "권한 허가 되었음",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CommuUpPostActivity.this, "아직 승인하지 않았음",
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

            imgContent.setImageBitmap(photo);
            imgContent.setScaleType(ImageView.ScaleType.CENTER_CROP);

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

                imgContent.setImageBitmap(photo);
                imgContent.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

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
        int result = ContextCompat.checkSelfPermission(CommuUpPostActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_DENIED){
            return false;
        }else{
            return true;
        }
    }

    // 권한 요청 코드
    private void requestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(CommuUpPostActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Log.i("DEBUGGING5", "true");
            Toast.makeText(CommuUpPostActivity.this, "권한 수락이 필요합니다.",
                    Toast.LENGTH_SHORT).show();
        }else{
            Log.i("DEBUGGING6", "false");
            ActivityCompat.requestPermissions(CommuUpPostActivity.this,
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