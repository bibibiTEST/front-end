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

    // 获取消息内容，文字或者图片URL
    public String getContent() {
        return content;
    }

    // 判断是否是发送者
    public boolean isSentByUser() {
        return isSentByUser;
    }

    // 判断消息是否是图片
    public boolean isImage() {
        return isImage;
    }

    // 设置内容
    public void setContent(String content) {
        this.content = content;
    }

    // 设置是否是图片
    public void setImage(boolean isImage) {
        this.isImage = isImage;
    }

    // 设置是否由用户发送
    public void setSentByUser(boolean isSentByUser) {
        this.isSentByUser = isSentByUser;
    }
}
