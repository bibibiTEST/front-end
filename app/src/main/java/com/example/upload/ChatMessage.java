package com.example.upload;

public class ChatMessage {
    private String content; // 文字内容或者图片路径
    private boolean isSentByUser; // 判断消息是否由用户发送
    private boolean isImage; // 判断消息是否是图片消息

    // 构造函数，用于创建消息
    public ChatMessage(String content, boolean isSentByUser, boolean isImage) {
        this.content = content;
        this.isSentByUser = isSentByUser;
        this.isImage = isImage;
    }

    public String getContent() {
        return content;
    }

    public boolean isSentByUser() {
        return isSentByUser;
    }

    public boolean isImage() {
        return isImage;
    }
}
