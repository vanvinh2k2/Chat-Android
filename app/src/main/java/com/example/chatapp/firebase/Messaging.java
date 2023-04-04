package com.example.chatapp.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.chatapp.R;
import com.example.chatapp.models.User;
import com.example.chatapp.unitilitise.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class Messaging extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("TAG","Token: "+token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        //Log.d("TAG","Message: "+ message.getNotification().getBody());
        User user = new User();
        user.id = message.getData().get(Constants.KEY_USERS_ID);
        user.name = message.getData().get(Constants.KEY_NAME);
        user.token = message.getData().get(Constants.KEY_FCM_TOKEN);

        int notificationId = new Random().nextInt();
        String channelId = "channel";

        Intent intent =new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.KEY_USER,user);
        PendingIntent pendingintent = PendingIntent.getActivity(getApplicationContext(),1,intent,0);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this,channelId);
        notification.setContentTitle(user.name);
        notification.setContentIntent(pendingintent);
        notification.setSmallIcon(R.drawable.ic_notifications);
        notification.setAutoCancel(true);
        notification.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notification.setContentText(message.getData().get(Constants.KEY_MESSAGE));
        notification.setStyle(new NotificationCompat.BigTextStyle().bigText(message.getData().get(Constants.KEY_MESSAGE)));
        notification.build();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(channelId,"Channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel to announce the conversation");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(notificationId,notification.build());
    }
}
