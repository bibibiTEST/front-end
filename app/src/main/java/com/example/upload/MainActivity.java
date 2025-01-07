package com.example.upload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_UPLOAD = 1; // 定义请求码
    private static final String BASE_URL = "172.20.10.6:5000/upload"; // 后端的基础 URL
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

        recyclerView = findViewById(R.id.recyclerView);
        inputMessage = findViewById(R.id.inputMessage);
        sendButton = findViewById(R.id.sendButton);
        myImageView = findViewById(R.id.myImageView);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        // 每次进入页面时请求消息记录
        fetchMessagesFromServer();

        // 发送文字消息
        sendButton.setOnClickListener(v -> {
            String message = inputMessage.getText().toString();
            if (!message.isEmpty()) {
                chatMessages.add(new ChatMessage(message, true, false)); // 储存文字消息
                chatAdapter.notifyDataSetChanged();
                inputMessage.setText("");
            }
        });

        // 点击图片上传
        myImageView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UploadActivity.class);
            startActivityForResult(intent, REQUEST_CODE_UPLOAD);
        });
    }

    // 从后端获取消息记录
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
                        chatAdapter.notifyDataSetChanged();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_UPLOAD && resultCode == RESULT_OK) {
            // 处理从 UploadActivity 返回的数据
            String imagePath = data.getStringExtra("imagePath");
            if (imagePath != null) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    myImageView.setImageBitmap(bitmap);
                    Toast.makeText(this, "图片上传成功并显示", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "图片文件不存在", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "未接收到图片路径", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
