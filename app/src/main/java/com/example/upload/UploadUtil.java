package com.example.upload;

import androidx.compose.foundation.ExperimentalFoundationApi;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.MediaType.Companion;

import okhttp3.*;
import java.io.File;


@ExperimentalFoundationApi
public class UploadUtil {

    private static final String UPLOAD_URL = "http://172.20.10.6:5000/api/upload"; // 替换为你的服务器地址

//    private static final MediaType MEDIA_TYPE_JPEG = "image/jpeg".toMediaTypeOrNull();


    public static void uploadImage(File imageFile, Callback callback) {
        // 创建 OkHttpClient 实例
        OkHttpClient client = new OkHttpClient();

        // 创建 RequestBody，用于上传文件
//        RequestBody fileBody = RequestBody.create(imageFile, MEDIA_TYPE_JPEG);

        // 使用 MultipartBody 封装文件和其他参数
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
//                .addFormDataPart("image", imageFile.getName(), fileBody) // "image" 为服务器端字段名
                .build();

        // 构建请求
        Request request = new Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .build();

        // 异步请求上传
        client.newCall(request).enqueue(callback);
    }
}
