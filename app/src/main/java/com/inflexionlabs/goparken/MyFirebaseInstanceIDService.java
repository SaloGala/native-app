package com.inflexionlabs.goparken;

import android.app.Service;
import android.preference.Preference;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by odalysmarronsanchez on 11/08/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "Firebase ID Service";

    @Override
    public void onTokenRefresh(){
        //Get updated InstanceID token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,"Refreshed token: "+ refreshedToken);

        //PreferenceData.setStringPref(PreferenceData.KEY_GCM_ID,this,refreshedToken);

        //TODO:Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }


    /*
    * Persist token to third-party servers
    *
    * Modify this method to associate the user's FCM Instance ID token with any
    * maintained by your application
    *
    * @param token The new token*/
    private void sendRegistrationToServer(String token){
        //ADD custom implementation, as needed.
    }
}
