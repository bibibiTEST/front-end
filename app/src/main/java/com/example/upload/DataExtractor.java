package com.example.upload;

import android.graphics.Bitmap;
import android.util.Log;

public class DataExtractor {

    // 从图像中提取数据
    public static String extractData(Bitmap bitmap, int blockSize, int[][] keySet) {
        StringBuilder extractedData = new StringBuilder();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // 遍历每个像素块
        for (int row = 0; row < height; row += blockSize) {
            for (int col = 0; col < width; col += blockSize) {
                int blockValue = 0;
                for (int i = 0; i < blockSize; i++) {
                    for (int j = 0; j < blockSize; j++) {
                        int pixel = bitmap.getPixel(col + j, row + i);
                        // 提取某些特定的位
                        blockValue ^= (pixel & 0xFF);  // XOR 运算（举例）
                    }
                }
                extractedData.append(Integer.toBinaryString(blockValue));
            }
        }

        return extractedData.toString();
    }

    // 将二进制字符串转换为字符
    public static String binaryToString(String binaryStr) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < binaryStr.length(); i += 8) {
            String byteStr = binaryStr.substring(i, i + 8);
            char c = (char) Integer.parseInt(byteStr, 2);
            result.append(c);
        }
        return result.toString();
    }
}
