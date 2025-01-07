package com.example.upload;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    @GET("api/messages")
    Call<List<ChatMessage>> getMessages();
}

