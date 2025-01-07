package com.example.upload;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.upload.ChatMessage;
import com.example.upload.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> chatMessages;
    private static final int TYPE_TEXT = 0;
    private static final int TYPE_IMAGE = 1;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        return chatMessage.isImage() ? TYPE_IMAGE : TYPE_TEXT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_IMAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_message, parent, false);
            return new ImageMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text_message, parent, false);
            return new TextMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);

        if (getItemViewType(position) == TYPE_IMAGE) {
            ((ImageMessageViewHolder) holder).bind(chatMessage);
        } else {
            ((TextMessageViewHolder) holder).bind(chatMessage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    // 图片消息的 ViewHolder
    public static class ImageMessageViewHolder extends RecyclerView.ViewHolder {
        private ImageView messageImageView;

        public ImageMessageViewHolder(View itemView) {
            super(itemView);
            messageImageView = itemView.findViewById(R.id.messageImageView);
        }

        public void bind(ChatMessage chatMessage) {
            Glide.with(messageImageView.getContext())
                    .load(chatMessage.getContent()) // 获取图片路径
                    .into(messageImageView);
        }
    }

    // 文字消息的 ViewHolder
    public static class TextMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageTextView;

        public TextMessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }

        public void bind(ChatMessage chatMessage) {
            messageTextView.setText(chatMessage.getContent());
        }
    }
}
