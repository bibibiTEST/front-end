package com.example.upload;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("/api/messages") // 后端返回所有消息记录的接口
    Call<List<ChatMessage>> getMessages();
}


