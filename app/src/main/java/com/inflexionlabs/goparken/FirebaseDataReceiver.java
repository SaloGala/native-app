package com.inflexionlabs.goparken;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by odalysmarronsanchez on 11/08/17.
 *
 * This is called whenever app receives notification
 * in background/foreground state so you can
 * apply logic for background task, but still Firebase notification
 * will be shown in notification tray
 *
 */


public class FirebaseDataReceiver extends WakefulBroadcastReceiver {

    private final String TAG = "Firebase Data Receiver";

    public void onReceive(Context context, Intent intent){

        Log.d(TAG,"Firebase Data Receiver onReceive method");

        if(intent.getExtras()!=null){
            for(String key: intent.getExtras().keySet()){
                Object value = intent.getExtras().get(key);

                Log.e("FirebaseDataReceiver","Key: "+key+"Value: "+value);
            }
        }

    }
}
