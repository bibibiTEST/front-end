package com.example.upload.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RC4 {
    private static final int MOD = 256;
    private int[] S;

    // 密钥填充至256位
    public RC4(String key) {
        int keylen = key.length();
        int[] key0 = new int[keylen];
        for (int i = 0; i < keylen; i++) {
            key0[i] = key.charAt(i); // 将字符转换为整数
        }
        this.S = KSA(key0);
    }

    // RC4 密钥调度算法（KSA）
    private int[] KSA(int[] key) {
        int[] S = new int[MOD];
        for (int i = 0; i < MOD; i++) {
            S[i] = i;
        }
        int j = 0;
        for (int i = 0; i < MOD; i++) {
            j = (j + S[i] + key[i % key.length]) % MOD;
            int temp = S[i];
            S[i] = S[j];
            S[j] = temp;
        }
        return S;
    }

    // RC4 伪随机生成算法（PRGA）
    public List<Integer> PRGA(int size) {
        List<Integer> randStream = new ArrayList<>();
        int i = 0, j = 0;
        for (int l = 0; l < size; l++) {
            i = (i + 1) % 256;
            j = (j + S[i]) % 256;
            int temp = S[i];
            S[i] = S[j];
            S[j] = temp;
            int k = S[(S[i] + S[j]) % 256];
            randStream.add(k);
        }
        return randStream;
    }

    // 使用 RC4 加密图像
    public Bitmap RC4_img(Bitmap bitmap, List<Integer> randStream) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int channels = 3; // 假设图像为 RGB 三通道

        Bitmap encryptedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        int i = 0;

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int pixel = bitmap.getPixel(col, row);
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                // 使用随机流对每个像素进行加密
                r ^= randStream.get(i);
                g ^= randStream.get(i + 1);
                b ^= randStream.get(i + 2);

                int encryptedPixel = (r << 16) | (g << 8) | b;
                encryptedBitmap.setPixel(col, row, encryptedPixel);

                i += 3;
            }
        }
        return encryptedBitmap;
    }

    // 保存 Bitmap 到文件
    public static void saveBitmapToFile(Bitmap bitmap, String filePath) throws IOException {
        File file = new File(filePath);
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        out.flush();
        out.close();
    }

    // 加密方法，接收密钥和图像路径，返回加密后的图像
    public static Bitmap encryptImage(String key, String inputImagePath) throws IOException {
        // 使用 RC4 密钥创建实例
        RC4 rc4 = new RC4(key);

        // 加载原始图像并获取随机流
        Bitmap originalBitmap = BitmapFactory.decodeFile(inputImagePath);
        List<Integer> randStream = rc4.PRGA(originalBitmap.getWidth() * originalBitmap.getHeight() * 3);

        // 加密图像并返回
        return rc4.RC4_img(originalBitmap, randStream);
    }

    // 主函数示例 (仅测试)
    public static void main(String[] args) {
        try {
            // 从命令行获取密钥和图像路径（假设传入了正确的参数）
            String key = "84612565";  // 替换为实际的密钥
            String inputImagePath = "RawImage/5.jpg";  // 替换为实际的图像路径

            RC4 rc4 = new RC4(key);

            // 调用 encryptImage 方法加密图像
            Bitmap encryptedBitmap = rc4.encryptImage(key, inputImagePath);

            // 保存加密后的图像
            rc4.saveBitmapToFile(encryptedBitmap, "Encrypted_Image.png");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
