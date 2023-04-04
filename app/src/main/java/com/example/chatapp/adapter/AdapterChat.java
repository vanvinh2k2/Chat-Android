package com.example.chatapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatapp.R;
import com.example.chatapp.models.ChatMessage;

import java.util.List;

public class AdapterChat extends BaseAdapter {
    Context context;
    List<ChatMessage> chatMessageList;
    Bitmap bitmap;
    String senderId;
    static final int VIEW_TYPE_SEND = 1;
    static final int VIEW_TYPE_RECEIVED = 0;
    public AdapterChat(Context context, List<ChatMessage> chatMessageList, Bitmap bitmap, String senderId) {
        this.context = context;
        this.chatMessageList = chatMessageList;
        this.bitmap = bitmap;
        this.senderId = senderId;
    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }
    private class Hoder1{
        TextView messagetxt,timetxt;
    }
    private class Hoder2{
        TextView messagetxt,timetxt;
        ImageView userimg;
    }
    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMessageList.get(position).senderId.equals(senderId)){
            return VIEW_TYPE_SEND;
        }else return VIEW_TYPE_RECEIVED;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(getItemViewType(position)==VIEW_TYPE_SEND){
            Hoder1 hoder1;
            if(convertView==null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_container_sent_message,null);
                hoder1 = new Hoder1();
                hoder1.messagetxt = convertView.findViewById(R.id.textMessage);
                hoder1.timetxt = convertView.findViewById(R.id.textTime);
                convertView.setTag(hoder1);
            }else hoder1 = (Hoder1) convertView.getTag();
            ChatMessage message = chatMessageList.get(position);
            hoder1.timetxt.setText(message.dateTime);
            hoder1.messagetxt.setText(message.message);
        }
        else{
            Hoder2 hoder2;
            if(convertView==null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_container_received_message,null);
                hoder2 = new Hoder2();
                hoder2.messagetxt = convertView.findViewById(R.id.textMessage);
                hoder2.timetxt = convertView.findViewById(R.id.textTime);
                hoder2.userimg = convertView.findViewById(R.id.imageUser);
                convertView.setTag(hoder2);
            }else hoder2 = (Hoder2) convertView.getTag();
            ChatMessage message = chatMessageList.get(position);
            hoder2.userimg.setImageBitmap(bitmap);
            hoder2.timetxt.setText(message.dateTime);
            hoder2.messagetxt.setText(message.message);
        }
        return convertView;
    }
}
