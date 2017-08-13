package com.inflexionlabs.goparken;

import android.content.Context;
import android.media.midi.MidiDevice;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by odalysmarronsanchez on 13/08/17.
 */

public class MySingleton {

    private static MySingleton singleton;
    private RequestQueue requestQueue;
    private static Context context;

    private MySingleton(Context context){
        MySingleton.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized MySingleton getInstance(Context context){

        if(singleton == null){
            singleton = new MySingleton(context);
        }

        return singleton;

    }

    RequestQueue getRequestQueue() {

        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }

        return requestQueue;
    }

    public void addToRequestQueue(Request req){
        getRequestQueue().add(req);
    }

}
