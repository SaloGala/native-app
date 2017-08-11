package com.inflexionlabs.goparken;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

/**
 * Created by odalysmarronsanchez on 11/08/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TAG = "FCM Service";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated

        String title="";
        String message="";
        
        if(remoteMessage.getNotification().getTitle() !=null ){
            title = remoteMessage.getNotification().getTitle();
        }
        
        if(remoteMessage.getNotification().getBody() !=null){
            message = remoteMessage.getNotification().getBody();
        }

        Log.d(TAG,"From: "+ remoteMessage.getFrom());
        Log.d(TAG,"Notification Message Body: "+message);
        
        sendNotification(title,message);
        

    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */

    private void sendNotification(String title, String message) {

        Intent intent = new Intent(this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0 ,intent ,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                    R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(getRecuestCode(),notificationBuilder.build());

    }

    private static int getRecuestCode() {

        Random rnd = new Random();

        return 100 + rnd.nextInt();
    }

}
