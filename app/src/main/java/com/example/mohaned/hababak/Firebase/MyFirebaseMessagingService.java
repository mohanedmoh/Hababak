package com.example.mohaned.hababak.Firebase;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.mohaned.hababak.R;
import com.example.mohaned.hababak.current_req_status;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import static com.example.mohaned.hababak.Firebase.MyNotificationManager.ID_SMALL_NOTIFICATION;

/**
 * Created by mohaned on 3/13/2017.
 */
@SuppressLint("Registered")
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    NotificationManager notificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (!(remoteMessage.getNotification().getTitle().isEmpty())) {
            System.out.println("FFFFFFFFFFFFFFFFFf"+remoteMessage+"ffffff");
            try {
                createNotificationChannel();
                sendPushNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }
    private void sendPushNotification(String title, String body) {
        createNotificationChannel();
        System.out.println("()()()()()()");

        //optionally we can display the json into log
        try {
            //getting the json data
            Intent intent = new Intent(getApplicationContext(), current_req_status.class);

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            getApplicationContext(),
                            ID_SMALL_NOTIFICATION,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            //parsing json data
           /* String title = data.getString("title");
            String message = data.getString("message");
            String imageUrl = data.getString("image");*/

            NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setChannelId("Default")
                    .setContentIntent(resultPendingIntent);
            //creating MyNotificationManager object
            System.out.println("()()(3333333333)()()()");


// notificationId is a unique int for each notification that you must define
            notificationManager.notify(1, notification.build());
                //displaying small notification
                //mNotificationManager.showSmallNotification(title, message, intent);

        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.notification);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Default", name, importance);

            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
             notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
