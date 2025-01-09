package com.example.upload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_UPLOAD = 1; // 定义请求码
    private static final String BASE_URL = "http://172.20.10.6:5000/"; // 后端的基础 URL
    private static final String UPLOAD_URL = "http://172.20.10.6:5000/api/upload";
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private EditText inputMessage;
    private Button sendButton;
    private ImageView myImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main); // 主页面布局
        Button sendButton = findViewById(R.id.sendButton); // 确保布局中存在此按钮


        recyclerView = findViewById(R.id.recyclerView);
        inputMessage = findViewById(R.id.inputMessage);
        sendButton = findViewById(R.id.sendButton);
        myImageView = findViewById(R.id.myImageView);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        // 每次进入页面时请求消息记录
        fetchMessagesFromServer();

        // 发送文字消息

        sendButton.setOnClickListener(v -> {
            // 获取文本框中的消息
            String message = inputMessage.getText().toString();
            if (!message.isEmpty()) {
                // 将消息保存到 chatMessages 列表
                String key=message;//密钥保存
                chatAdapter.notifyDataSetChanged(); // 刷新适配器
                inputMessage.setText(""); // 清空输入框
            } else {
                Toast.makeText(MainActivity.this, "密钥不能为空", Toast.LENGTH_SHORT).show();
                return; // 如果消息为空，不继续执行
            }

            // 如果已选中图片，则发送消息
            if (selectedImageFile != null) {
                uploadImageToServer(selectedImageFile, userId); // 发送用户 ID 和图片
            } else {
                Toast.makeText(MainActivity.this, "请先选择图片", Toast.LENGTH_SHORT).show();
            }
        });

        // 点击图片上传
        myImageView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UploadActivity.class);
            startActivityForResult(intent, REQUEST_CODE_UPLOAD);
        });

    }

    // 从后端获取消息记录
//    private void fetchMessagesFromServer() {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        ApiService apiService = retrofit.create(ApiService.class);
//
//        // 获取之前的消息记录
//        Call<List<ChatMessage>> call = apiService.getMessages();
//
//        call.enqueue(new Callback<List<ChatMessage>>() {
//                         @Override
//                         public void onResponse(Call<List<ChatMessage>> call, Response<List<ChatMessage>> response) {
//                             if (response.isSuccessful()) {
//                                 List<ChatMessage> messages = response.body();
//                                 if (messages != null) {
//                                     chatMessages.clear();
//                                     chatMessages.addAll(messages); // 加载获取的消息
//                                     // 遍历消息，加载图片
//                                     for (ChatMessage message : messages) {
//                                         loadImage(message.getContent());  // 加载图片
//                                     }
//                                     chatAdapter.notifyDataSetChanged();
//                                     recyclerView.scrollToPosition(chatMessages.size() - 1);
//
//                                 }
//                             } else {
//                                 Toast.makeText(MainActivity.this, "获取消息失败", Toast.LENGTH_SHORT).show();
//                             }
//                         }
//                         @Override
//                         public void onFailure(Call<List<ChatMessage>> call, Throwable t) {
//                             Toast.makeText(MainActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
//                         }
//                     }
//        );
//    }

    // 使用 Glide 加载图片
    // 使用 Glide 加载图片
//    private void loadImage(String imageUrl) {
//        ImageView displayImageView = findViewById(R.id.displayImageView); // 获取新的 ImageView
//        Glide.with(MainActivity.this)
//                .load(BASE_URL + imageUrl) // 图片的完整 URL
//                .into(displayImageView);  // 将图片加载到 displayImageView
//    }


    private File selectedImageFile; // 用于存储选中的图片文件

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_UPLOAD && resultCode == RESULT_OK) {
            // 处理从 UploadActivity 返回的数据
            String imagePath = data.getStringExtra("imagePath");
            if (imagePath != null) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    selectedImageFile = imageFile; // 保存图片文件以便稍后发送
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    myImageView.setImageBitmap(bitmap); // 显示选中的图片
                    Toast.makeText(this, "图片选择成功，点击按钮发送", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "图片文件不存在", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "未接收到图片路径", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void uploadImageToServer(File file, int userId) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

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
                okhttp3.Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        // 上传成功后手动添加消息到列表
                        String message = "Message with image"; // 示例文本消息
                        ChatMessage newMessage = new ChatMessage(); // 假设服务器返回图片URL
                        chatMessages.add(newMessage);
                        chatAdapter.notifyDataSetChanged(); // 刷新 RecyclerView
                        recyclerView.scrollToPosition(chatMessages.size() - 1); // 滚动到最新消息
                        Toast.makeText(MainActivity.this, "消息发送成功", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "上传失败：" + response.message(), Toast.LENGTH_LONG).show());
                }
            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "请求执行过程出错: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void fetchMessagesFromServer() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // 获取之前的消息记录
        Call<List<ChatMessage>> call = apiService.getMessages();

        call.enqueue(new Callback<List<ChatMessage>>() {
            @Override
            public void onResponse(Call<List<ChatMessage>> call, Response<List<ChatMessage>> response) {
                if (response.isSuccessful()) {
                    List<ChatMessage> messages = response.body();
                    if (messages != null) {
                        chatMessages.clear();
                        chatMessages.addAll(messages); // 加载获取的消息
                        chatAdapter.notifyDataSetChanged(); // 刷新适配器
                        recyclerView.scrollToPosition(chatMessages.size() - 1); // 滚动到最新消息

                        // 遍历消息，加载图片
                        for (ChatMessage message : messages) {
                            loadImage(message.getContent()); // 加载图片
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "获取消息失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ChatMessage>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 使用 Glide 加载图片
    private void loadImage(String imageUrl) {
        // 确保imageUrl是完整的URL
        if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            imageUrl = BASE_URL + imageUrl;  // 拼接完整的URL
        }

        ImageView displayImageView = findViewById(R.id.displayImageView); // 获取新的 ImageView
        Glide.with(MainActivity.this)
                .load(imageUrl) // 图片的完整 URL
                .into(displayImageView);  // 将图片加载到 displayImageView
    }


    // 发送消息的功能
    int userId = 1;
    private void sendMessage(File imageFile, int userId) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestBody);

        RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);

        Call<ResponseBody> call = apiService.sendMessage(imagePart, userIdBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "消息发送成功", Toast.LENGTH_SHORT).show();
                    fetchMessagesFromServer(); // 刷新消息列表
                } else {
                    // 打印请求的详细信息
                    try {
                        Log.d("Retrofit", "Request URL: " + call.request().url());
                        Log.d("Retrofit", "Request Method: " + call.request().method());
                        Log.d("Retrofit", "Request Headers: " + call.request().headers());
                        Log.d("Retrofit", "Request Body: " + call.request().body());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
            }

        });

    }
}

