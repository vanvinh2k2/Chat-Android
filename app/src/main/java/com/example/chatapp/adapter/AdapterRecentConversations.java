package com.example.chatapp.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.databinding.ItemContainerReceiverConversionBinding;
import com.example.chatapp.listenner.ListenConversation;
import com.example.chatapp.models.ChatMessage;
import com.example.chatapp.models.User;

import java.util.List;

public class AdapterRecentConversations extends RecyclerView.Adapter<AdapterRecentConversations.ConversionViewHoder>{
    List<ChatMessage> messageList;
    ListenConversation conversation;

    public AdapterRecentConversations(List<ChatMessage> messageList, ListenConversation conversation) {
        this.messageList = messageList;
        this.conversation = conversation;
    }

    @NonNull
    @Override
    public ConversionViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_receiver_conversion, parent, false);
        return new ConversionViewHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHoder holder, int position) {
        holder.getData(messageList.get(position));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ConversionViewHoder extends RecyclerView.ViewHolder{
        //ItemContainerReceiverConversionBinding binding;
        ImageView imageUser;
        TextView textName,textRecent;
        public ConversionViewHoder(@NonNull View itemView) {
            super(itemView);
            imageUser = itemView.findViewById(R.id.imageUser);
            textName = itemView.findViewById(R.id.textName);
            textRecent = itemView.findViewById(R.id.textRecent);
        }
        private void getData(ChatMessage chat){
            imageUser.setImageBitmap(getConversionImage(chat.conversionImage));
            textName.setText(chat.conversionName);
            textRecent.setText(chat.message);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = new User();
                    user.id = chat.conversionId;
                    user.name = chat.conversionName;
                    user.image = chat.conversionImage;
                    conversation.onConversionClick(user);
                }
            });
        }
    }
    private Bitmap getConversionImage(String encodeImage){
        byte[] bytes = Base64.decode(encodeImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

}
