package com.example.upload;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

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
        // 判断该消息是文字还是图片
        ChatMessage chatMessage = chatMessages.get(position);
        return chatMessage.isImage() ? TYPE_IMAGE : TYPE_TEXT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 根据消息类型创建对应的 ViewHolder
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
        // 获取消息并绑定到对应的 ViewHolder
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
            messageImageView = itemView.findViewById(R.id.messageImageView); // 在布局中找到ImageView
        }

        public void bind(ChatMessage chatMessage) {
            // 使用 Glide 加载图片，content 为图片路径
            Glide.with(messageImageView.getContext())
                    .load(chatMessage.getContent()) // 获取图片路径
                    .into(messageImageView); // 设置图片到ImageView
        }
    }

    // 文字消息的 ViewHolder
    public static class TextMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageTextView;

        public TextMessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView); // 在布局中找到TextView
        }

        public void bind(ChatMessage chatMessage) {
            // 设置消息的文本内容
            messageTextView.setText(chatMessage.getContent());
        }
    }
}
