package com.zoomdemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;

import com.facebook.react.bridge.ReactContext;

import java.util.ArrayList;

/**
 * Created by Priyesh Sharma on 5/9/20.
 */
public class NotificationUtil {
    private NotificationManagerCompat mNotificationManagerCompat;
    private NotificationCompat.Builder notificationBuilder;
    private String channelId = "messaging_channel";
    private String channelName = "Messaging Notification";
    private static final int REQUEST_CODE = 269;
    private ReactContext mReactContext;

    public NotificationUtil(ReactContext reactContext) {
        mNotificationManagerCompat = NotificationManagerCompat.from(reactContext);
        mReactContext = reactContext;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) reactContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        createNotification();
    }

    private void createNotification() {
        notificationBuilder = new NotificationCompat
                .Builder(mReactContext, channelId)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setCategory(Notification.CATEGORY_MESSAGE);
        }
    }

    public void notifyMessaging(int id, NotificationCompat.MessagingStyle messagingStyle) {
        notificationBuilder.setStyle(messagingStyle);
        mNotificationManagerCompat.notify(id, notificationBuilder.build());
    }

    public void setLargeIcon(Bitmap bitmap) {
        createNotification();
        notificationBuilder.setLargeIcon(bitmap);
    }

    public void cancelNotification(int id) {
        mNotificationManagerCompat.cancel(id);
    }

    public void cancelAllNotifications() {
        mNotificationManagerCompat.cancelAll();
    }

    public NotificationCompat.MessagingStyle createMessagingNotification(Person adminPerson, ArrayList<NotificationCompat.MessagingStyle.Message> messageArrayList, String conversationTitle, boolean isGroupNotification, Bundle extras) {
        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(adminPerson)
                .setGroupConversation(isGroupNotification);
        if (!TextUtils.isEmpty(conversationTitle)) {
            messagingStyle.setConversationTitle(conversationTitle);
        }

        for (NotificationCompat.MessagingStyle.Message message : messageArrayList) {
            messagingStyle.addMessage(message);
        }
        notificationBuilder.setStyle(messagingStyle);
        Intent intent = new Intent(mReactContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extras != null) {
            intent.putExtra("extraData", extras);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(mReactContext, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);
        return messagingStyle;
    }
}
