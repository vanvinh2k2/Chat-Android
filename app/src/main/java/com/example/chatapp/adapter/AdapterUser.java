package com.example.chatapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatapp.R;
import com.example.chatapp.listenner.ListenerUser;
import com.example.chatapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class AdapterUser extends BaseAdapter {
    Context context;
    int layout;
    List<User> list;
    private final ListenerUser listenerUser;
    public AdapterUser(Context context, int layout, List<User> list, ListenerUser listenerUser) {
        this.context = context;
        this.layout = layout;
        this.list = list;
        this.listenerUser = listenerUser;
    }

    private Bitmap getUserImage(String encodeImage){
        byte[] bytes = Base64.decode(encodeImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    class Hoder{
        TextView nametxt,emailtxt;
        ImageView userimg;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Hoder hoder;
        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout,null);
            hoder = new Hoder();
            hoder.emailtxt = convertView.findViewById(R.id.textEmail);
            hoder.nametxt = convertView.findViewById(R.id.textName);
            hoder.userimg = convertView.findViewById(R.id.imageUser);
            convertView.setTag(hoder);
        }else hoder = (Hoder) convertView.getTag();
        User user = list.get(position);
        hoder.emailtxt.setText(user.email);
        hoder.nametxt.setText(user.name);
        hoder.userimg.setImageBitmap(getUserImage(user.image));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenerUser.onUserClicked(user);
            }
        });
        return convertView;
    }
}
