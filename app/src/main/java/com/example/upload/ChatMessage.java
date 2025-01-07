package com.example.upload;

public class ChatMessage {
    private String message;
    private boolean isSentByMe;

    public ChatMessage(String message, boolean isSentByMe) {
        this.message = message;
        this.isSentByMe = isSentByMe;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSentByMe() {
        return isSentByMe;
    }
}