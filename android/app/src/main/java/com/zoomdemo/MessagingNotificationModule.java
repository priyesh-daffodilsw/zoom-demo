package com.zoomdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.FutureTarget;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

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

    private Person parsePerson(ReadableMap readableMap) throws Exception {
        String key = readableMap.getString("key");
        String name = readableMap.getString("name");
        String icon = readableMap.hasKey("icon") ? readableMap.getString("icon") : null;
        Person.Builder personBuilder = new Person.Builder();
        personBuilder.setKey(key);
        personBuilder.setName(name);
        if (icon != null) {
            if ((icon.startsWith("http") || icon.startsWith("https"))) {
                FutureTarget<Bitmap> bitmapFutureTask = Glide.with(getReactApplicationContext())
                        .asBitmap()
                        .transform(new RoundedCorners(300))
                        .sizeMultiplier(0.3f)
                        .load(icon)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .submit();
                try {
                    Bitmap bitmap = bitmapFutureTask.get();
                    if (bitmap != null) {
                        personBuilder.setIcon(IconCompat.createWithBitmap(bitmap));
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (icon.startsWith("content://")) {
                personBuilder.setIcon(IconCompat.createWithContentUri(icon));
            }
        }

        return personBuilder.build();
    }

    private NotificationCompat.MessagingStyle.Message parseMessage(ReadableMap readableMap) {
        long timestamp = (long) readableMap.getDouble("timestamp");
        String text = readableMap.getString("text");
        ReadableMap personObj = readableMap.getMap("person");
        if (personObj != null) {

            try {
                Person person = parsePerson(personObj);
                return new NotificationCompat.MessagingStyle.Message(text, timestamp, person);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private NotificationCompat.MessagingStyle parseMessagingNotification(ReadableMap messagingNotification) throws Exception {
        //sure
        boolean isGroupNotification = messagingNotification.getBoolean("isGroupNotification");
        ReadableArray messageArray = messagingNotification.getArray("messages");
        if (messageArray != null && messageArray.size() > 0) {
            ArrayList<NotificationCompat.MessagingStyle.Message> messageArrayList = new ArrayList<>();
            for (int i = 0; i < messageArray.size(); i++) {
                if (messageArray.getType(i) == ReadableType.Map) {
                    ReadableMap messageObj = messageArray.getMap(i);
                    if (messageObj != null) {
                        NotificationCompat.MessagingStyle.Message message = parseMessage(messageObj);
                        if (message != null) {
                            messageArrayList.add(message);
                        }
                    }
                }
            }
            //not sure
            String conversationTitle = messagingNotification.hasKey("conversationTitle") ?
                    messagingNotification.getString("conversationTitle") : null;
            ReadableMap admin = messagingNotification.hasKey("admin") ? messagingNotification.getMap("admin") : null;
            if (admin == null) {
                throw new Exception("Person object passed in Messaging style constructor is null or invalid");
            }
            Person adminPerson = parsePerson(admin);
            return mNotificationUtil.createMessagingNotification(adminPerson, messageArrayList, conversationTitle, isGroupNotification);
        } else {
            throw new Exception("message array is null or empty");
        }

    }

    @ReactMethod
    public void createAndNotify(int id, ReadableMap messagingNotification, Promise promise) {
        try {
            NotificationCompat.MessagingStyle messagingStyle = parseMessagingNotification(messagingNotification);
            mNotificationUtil.notifyMessaging(id, messagingStyle);
            promise.resolve(true);
        } catch (Exception e) {
            promise.reject(e);
            e.printStackTrace();
        }

    }

    @ReactMethod
    public void cancelNotification(int id) {
        mNotificationUtil.cancelNotification(id);
    }

    @ReactMethod
    public void getIntentData(Promise promise) {
        if (getCurrentActivity() != null) {
            Intent intent = getCurrentActivity().getIntent();
            Bundle bundle = intent.getBundleExtra("extraData");
            if (bundle == null) {
                promise.reject(new Exception("No Data Available"));
                return;
            }
            ReadableMap readableMap = Arguments.fromBundle(bundle);
            promise.resolve(readableMap);
        } else {
            promise.reject(new Exception("current activity is null"));
        }
    }

    @ReactMethod
    public void cancelAllNotifications(int id) {
        mNotificationUtil.cancelAllNotifications();
    }

    @NonNull
    @Override
    public String getName() {
        return NAME;
    }
}
