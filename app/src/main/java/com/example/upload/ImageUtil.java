package com.example.upload;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileInputStream;


public class ImageUtil {

    // 从文件路径加载图像
    public static Bitmap loadImage(String imagePath) {
        try {
            FileInputStream inputStream = new FileInputStream(imagePath);
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 保存图像
    public static void saveImage(Bitmap bitmap, String savePath) {
        try (FileOutputStream out = new FileOutputStream(savePath)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

