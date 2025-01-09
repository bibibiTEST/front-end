package com.example.upload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadActivity extends AppCompatActivity {
    private static final String UPLOAD_URL = "http://172.20.10.6:5000/api/upload";
    private ImageView imageView;
    private Button btnSelectImage, btnUploadImage;
    private Uri selectedImageUri;
    private File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        imageView = findViewById(R.id.imageView);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);

        btnSelectImage.setOnClickListener(v -> openImagePicker());
        btnUploadImage.setOnClickListener(v -> {
            if (imageFile != null) {
                uploadImageToServer(imageFile);
            } else {
                Toast.makeText(UploadActivity.this, "请先选择一张图片", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        displayImage(selectedImageUri);
                        imageFile = saveUriToFile(selectedImageUri);
                    }
                }
            }
    );

    private void displayImage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "图片显示失败", Toast.LENGTH_SHORT).show();
        }
    }


    private int imageCounter = 0; // 定义全局变量，用于计数

    private File saveUriToFile(Uri uri) {
        try {
            // 生成带自增数字的文件名
            String fileName = "upload_image_" + (++imageCounter) + ".jpg";
            File tempFile = new File(getCacheDir(), fileName);

            InputStream inputStream = getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "文件保存失败", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void uploadImageToServer(File file) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
int userId=1;//暂时将userid假设为1
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getName(),
                            RequestBody.create(MediaType.parse("image/jpeg"), file))
                    .addFormDataPart("userId", String.valueOf(userId))
                    .build();

            Request request = new Request.Builder()
                    .url(UPLOAD_URL)
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("imagePath", file.getAbsolutePath());
                        setResult(RESULT_OK, resultIntent);
                        finish(); // 返回主界面
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(UploadActivity.this, "上传失败：" + response.message(), Toast.LENGTH_LONG).show());
                }
            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(UploadActivity.this, "请求执行过程出错: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
