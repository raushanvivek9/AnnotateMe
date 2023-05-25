package com.example.anno_tool.Project_Work;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.anno_tool.MainActivity;
import com.example.anno_tool.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyNotification extends FirebaseMessagingService {
//    String channelId="AnnotageMe";

//    @Override
//    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//        // Handle the incoming message
//        if (remoteMessage.getNotification() != null) {
//            // Notification message payload
//            String title = remoteMessage.getNotification().getTitle();
//            String body = remoteMessage.getNotification().getBody();
//
//            // Process the notification or display it in the system tray
//            showNotification(title, body);
//        }
//
//        // Handle custom data payload
//        if (remoteMessage.getData().size() > 0) {
//            // Custom data payload
//            // Extract the data and perform any required actions
//            for (String key : remoteMessage.getData().keySet()) {
//                String value = remoteMessage.getData().get(key);
//                // Process the custom data
//            }
//        }
//
//
//    }

//    private void showNotification(String title, String body) {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
//                .setSmallIcon(R.drawable.ic_baseline_call_24)
//                .setContentTitle(title)
//                .setContentText(body)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setAutoCancel(true);
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        notificationManager.notify(0, builder.build());
//    }
//    @Override
//    public void onNewToken(String token) {
//        super.onNewToken(token);
//    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        try {
            String title=remoteMessage.getNotification().getTitle();
            String text= remoteMessage.getNotification().getBody();
            final String CHANNEL_ID="AnnotateMe";
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID,"AnnotateMe",NotificationManager.IMPORTANCE_HIGH);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Notification.Builder notification=new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setSmallIcon(R.drawable.anno_logo)
                    .setLargeIcon(BitmapFactory. decodeResource (getResources() , R.drawable.anno_logo ))
                    .setStyle(new Notification.BigPictureStyle())
//                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.anno_logo)))
                    .setColor(this.getColor(R.color.white))
                    .setAutoCancel(true);
            NotificationManagerCompat.from(this).notify(1,notification.build());
        }catch (Exception e){

        }

        super.onMessageReceived(remoteMessage);

    }
}
