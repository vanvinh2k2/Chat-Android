package com.example.chatapp.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.databinding.ItemContainerReceivedMessageBinding;
import com.example.chatapp.databinding.ItemContainerSentMessageBinding;
import com.example.chatapp.models.ChatMessage;

import org.w3c.dom.Text;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<ChatMessage> chatMessages;
    private Bitmap receiver;
    String senderId;

    static final int VIEW_TYPE_SEND = 1;
    static final int VIEW_TYPE_RECEIVED = 0;
    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap receiver, String senderId) {
        this.chatMessages = chatMessages;
        this.receiver = receiver;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_SEND) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_sent_message,parent,false);
            return new SentmessageViewHoder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_received_message,parent,false);
            return new ReceivermessageViewHoder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==VIEW_TYPE_SEND) ((SentmessageViewHoder) holder).setData(chatMessages.get(position));
        else ((ReceivermessageViewHoder) holder).setData(chatMessages.get(position),receiver);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMessages.get(position).senderId.equals(senderId)) return VIEW_TYPE_SEND;
        else return VIEW_TYPE_RECEIVED;
    }

    static class SentmessageViewHoder extends RecyclerView.ViewHolder{
        TextView textMessage,textTime;
        public SentmessageViewHoder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
            textTime = itemView.findViewById(R.id.textTime);
        }
        void setData(ChatMessage chat){
            textMessage.setText(chat.message);
            textTime.setText(chat.dateTime);
        }
    }
    static class ReceivermessageViewHoder extends RecyclerView.ViewHolder{
        TextView textMessage,textTime;
        ImageView imageUser;
        public ReceivermessageViewHoder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
            textTime = itemView.findViewById(R.id.textTime);
            imageUser = itemView.findViewById(R.id.imageUser);
        }
        void setData(ChatMessage chat,Bitmap map){
            textMessage.setText(chat.message);
            textTime.setText(chat.dateTime);
            imageUser.setImageBitmap(map);
        }
    }
}
