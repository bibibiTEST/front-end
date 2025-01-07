package com.example.upload;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import java.util.List;

public interface ApiService {

    // 获取消息列表
    @GET("api/messages")
    Call<List<ChatMessage>> getMessages();

    // 上传加密后的图片，并发送用户 ID
    @Multipart
    @POST("api/upload")
    Call<ResponseBody> sendMessage(
            @Part MultipartBody.Part image,      // 图片文件
            @Part("userId") RequestBody userId  // 用户 ID
    );
}
