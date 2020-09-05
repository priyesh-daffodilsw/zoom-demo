package com.zoomdemo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;

import com.facebook.react.bridge.ReactContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Priyesh Sharma on 5/9/20.
 */
public class NotificationUtil {
    private Map<String, Person> mPersonList = new HashMap<>();
    private NotificationCompat.MessagingStyle mMessagingStyle;
    private ReactContext mReactContext;
    private NotificationManagerCompat mNotificationManagerCompat;
    private NotificationCompat.Builder notificationBuilder;
    private String channelId = "messaging_channel";
    private String channelName = "Messaging Notification";

    public NotificationUtil(ReactContext reactContext) {
        mReactContext = reactContext;
        mNotificationManagerCompat = NotificationManagerCompat.from(mReactContext);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) reactContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationBuilder = new NotificationCompat.Builder(reactContext, channelId).setSmallIcon(R.mipmap.ic_launcher);
    }

    public Person createPerson(String key, String name, String icon) {
        if (!mPersonList.containsKey(key)) {
            Person person = new Person.Builder()
                    .setName(name)
                    .setKey(key)
                    .setIcon(IconCompat.createWithResource(mReactContext, R.mipmap.ic_launcher))
                    .build();
            mPersonList.put(key, person);
            return person;
        } else {
            //TODO if name icon changes
            return mPersonList.get(key);
        }
    }

    public Person getPerson(String key) {
        return mPersonList.get(key);
    }

    public void initializeMessagingNotificationInstance(Person person) {
        mMessagingStyle = new NotificationCompat.MessagingStyle(person);
    }

    public NotificationCompat.MessagingStyle getMessagingStyle(long id) {
        return mMessagingStyle;
    }

    public void notifyMessaging(int id, NotificationCompat.MessagingStyle messagingStyle) {
        notificationBuilder.setStyle(messagingStyle);
        mNotificationManagerCompat.notify(id, notificationBuilder.build());
    }

    public NotificationCompat.MessagingStyle createMessagingNotification(Person adminPerson, ArrayList<NotificationCompat.MessagingStyle.Message> messageArrayList, String conversationTitle, boolean isGroupNotification) {
        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(adminPerson)
                .setGroupConversation(isGroupNotification);
        if (!TextUtils.isEmpty(conversationTitle)) {
            messagingStyle.setConversationTitle(conversationTitle);
        }

        for (NotificationCompat.MessagingStyle.Message message : messageArrayList) {
            messagingStyle.addMessage(message);
        }
        notificationBuilder.setStyle(messagingStyle);
        return messagingStyle;
    }
}
