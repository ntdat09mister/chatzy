package com.example.chatzy.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatzy.databinding.ItemContainerReceivedImageBinding;
import com.example.chatzy.databinding.ItemContainerReceivedMessageBinding;
import com.example.chatzy.databinding.ItemContainerSentImageBinding;
import com.example.chatzy.databinding.ItemContainerSentMessageBinding;
import com.example.chatzy.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final List<ChatMessage> chatMessages;
    private Bitmap receivierProfileImage;
    private final String senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;
    public static final int VIEW_TYPE_SENT_IMAGE = 3;
    public static final int VIEW_TYPE_RECEIVED_IMAGE = 4;

    public void setReceivierProfileImage(Bitmap bitmap) {
        receivierProfileImage = bitmap;
    }

    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap receivierProfileImage, String senderId) {
        this.chatMessages = chatMessages;
        this.receivierProfileImage = receivierProfileImage;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
            );
        } else if (viewType == VIEW_TYPE_SENT_IMAGE) {
            return new SentImageViewHolder(
                    ItemContainerSentImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
            );
        } else if (viewType == VIEW_TYPE_RECEIVED_IMAGE) {
            return new ReceivedImageViewHolder(
                    ItemContainerReceivedImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
            );
        } else {
            return new ReceivedMessageViewHolder(
                    ItemContainerReceivedMessageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
            );
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
        } else if (getItemViewType(position) == VIEW_TYPE_SENT_IMAGE) {
            ((SentImageViewHolder) holder).setData(chatMessages.get(position));
        } else if (getItemViewType(position) == VIEW_TYPE_RECEIVED_IMAGE) {
            ((ReceivedImageViewHolder) holder).setData(chatMessages.get(position), receivierProfileImage);
        } else {
            ((ReceivedMessageViewHolder) holder).setData(chatMessages.get(position), receivierProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).senderId.equals(senderId)) {
            if (chatMessages.get(position).messafe.contains("/9j/")) {
                return VIEW_TYPE_SENT_IMAGE;
            } else {
                return VIEW_TYPE_SENT;
            }
        } else {
            if (chatMessages.get(position).messafe.contains("/9j/")) {
                return VIEW_TYPE_RECEIVED_IMAGE;
            } else {
                return VIEW_TYPE_RECEIVED;
            }
        }

    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.messafe);
            binding.textDateTime.setText(chatMessage.dataTime);

        }
    }


    static class SentImageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerSentImageBinding bindingImage;

        SentImageViewHolder(ItemContainerSentImageBinding itemContainerSentImageBinding) {
            super(itemContainerSentImageBinding.getRoot());
            bindingImage = itemContainerSentImageBinding;

        }

        void setData(ChatMessage chatMessage) {
            bindingImage.textMessage.setImageBitmap(getBitmapFromEncodedString(chatMessage.messafe));
            bindingImage.textDateTime.setText(chatMessage.dataTime);
        }

    }

    private static Bitmap getBitmapFromEncodedString(String encodedImage) {
        if (encodedImage != null) {
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {
            return null;
        }

    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerReceivedMessageBinding binding;

        ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        void setData(ChatMessage chatMessage, Bitmap receivierProfileImage) {
            binding.textMessage.setText(chatMessage.messafe);
            binding.textDateTime.setText(chatMessage.dataTime);
            if (receivierProfileImage != null) {
                binding.imageProfile.setImageBitmap(receivierProfileImage);
            }
        }
    }

    static class ReceivedImageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerReceivedImageBinding binding;

        ReceivedImageViewHolder(ItemContainerReceivedImageBinding itemContainerReceivedImageBinding) {
            super(itemContainerReceivedImageBinding.getRoot());
            binding = itemContainerReceivedImageBinding;
        }

        void setData(ChatMessage chatMessage, Bitmap receivierProfileImage) {
            binding.textMessage.setImageBitmap(getBitmapFromEncodedString(chatMessage.messafe));
            binding.textDateTime.setText(chatMessage.dataTime);
            if (receivierProfileImage != null) {
                binding.imageProfile.setImageBitmap(receivierProfileImage);
            }

        }
    }
}
