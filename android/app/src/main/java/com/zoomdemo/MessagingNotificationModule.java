package com.zoomdemo;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;

import java.util.ArrayList;

/**
 * Created by Priyesh Sharma on 5/9/20.
 */
public class MessagingNotificationModule extends ReactContextBaseJavaModule {
    public static final String NAME = "MessagingNotification";
    private NotificationUtil mNotificationUtil;

    public MessagingNotificationModule(@NonNull ReactApplicationContext reactContext) {
        super(reactContext);
        mNotificationUtil = new NotificationUtil(reactContext);
    }

    @ReactMethod
    public void createPerson(String key, String name, String icon) {
        mNotificationUtil.createPerson(key, name, icon);
    }

    @ReactMethod
    public void initializeMessagingNotificationInstance(double id, ReadableMap person) {
//        mNotificationUtil.initializeMessagingNotificationInstance((long) id, ensurePerson(person));
    }

    private Person ensurePerson(ReadableMap person) {
        Arguments.toBundle(person);
        String key = person.getString("key");
        Person existingPerson = mNotificationUtil.getPerson(key);
        if (existingPerson == null) {
            String name = person.getString("name");
            String icon = person.hasKey("icon") ? person.getString("name") : null;
            existingPerson = mNotificationUtil.createPerson(key, name, icon);
        }

        return existingPerson;
    }

    @ReactMethod
    public void addMessage(double id, String text, double timestamp, ReadableMap person) {
        NotificationCompat.MessagingStyle messagingStyle = mNotificationUtil.getMessagingStyle((long) id);
        messagingStyle.addMessage(text, (long) timestamp, ensurePerson(person));
    }

    private Person parsePerson(ReadableMap readableMap) {
        String key = readableMap.getString("key");
        String name = readableMap.getString("name");
        String icon = readableMap.hasKey("icon") ? readableMap.getString("icon") : null;
        Person.Builder personBuilder = new Person.Builder();
        personBuilder.setKey(key);
        personBuilder.setName(name);
        personBuilder.setIcon(IconCompat.createWithResource(getReactApplicationContext(), R.mipmap.ic_launcher));
        return personBuilder.build();
    }

    private NotificationCompat.MessagingStyle.Message parseMessage(ReadableMap readableMap) {
        long timestamp = (long) readableMap.getDouble("timestamp");
        String text = readableMap.getString("text");
        Person person = parsePerson(readableMap.getMap("person"));
        return new NotificationCompat.MessagingStyle.Message(text, timestamp, person);
    }


    private NotificationCompat.MessagingStyle parseMessagingNotification(ReadableMap messagingNotification) {
        //sure

        boolean isGroupNotification = messagingNotification.getBoolean("isGroupNotification");
        ReadableArray messageArray = messagingNotification.getArray("messages");
        ArrayList<NotificationCompat.MessagingStyle.Message> messageArrayList = new ArrayList<>();
        for (int i = 0; i < messageArray.size(); i++) {
            if (messageArray.getType(i) == ReadableType.Map) {
                NotificationCompat.MessagingStyle.Message message = parseMessage(messageArray.getMap(i));
                messageArrayList.add(message);
            }
        }
        //not sure
        String conversationTitle = messagingNotification.hasKey("conversationTitle") ?
                messagingNotification.getString("conversationTitle") : null;
        ReadableMap admin = messagingNotification.hasKey("admin") ? messagingNotification.getMap("admin") : null;
        Person adminPerson = parsePerson(admin);
        return mNotificationUtil.createMessagingNotification(adminPerson, messageArrayList, conversationTitle, isGroupNotification);
    }

    @ReactMethod
    public void setConversationTitle(double id, String title) {
        NotificationCompat.MessagingStyle messagingStyle = mNotificationUtil.getMessagingStyle((long) id);
        messagingStyle.setConversationTitle(title);
    }

    @ReactMethod
    public void setGroupConversation(double id, boolean isGroupConvo) {
        NotificationCompat.MessagingStyle messagingStyle = mNotificationUtil.getMessagingStyle((long) id);
        messagingStyle.setGroupConversation(isGroupConvo);
    }

    @ReactMethod
    public void createAndNotify(int id, ReadableMap messagingNotification) {
        NotificationCompat.MessagingStyle messagingStyle = parseMessagingNotification(messagingNotification);
        mNotificationUtil.notifyMessaging(id, messagingStyle);
    }


    @ReactMethod
    public void notify(double notificationid, double messagingInstanceId) {
        NotificationCompat.MessagingStyle messagingStyle = mNotificationUtil.getMessagingStyle((long) messagingInstanceId);
        Log.d("TAG", "notifyId: " + (long) messagingInstanceId);
        mNotificationUtil.notifyMessaging((int) notificationid, messagingStyle);
    }

    @NonNull
    @Override
    public String getName() {
        return NAME;
    }
}
