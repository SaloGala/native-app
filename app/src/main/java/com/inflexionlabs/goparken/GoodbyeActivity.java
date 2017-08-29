package com.inflexionlabs.goparken;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class GoodbyeActivity extends AppCompatActivity {

    private final String TAG = "GoodbyeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goodbye);

        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.d(TAG,"seconds remaining: " + Long.toString(millisUntilFinished / 1000));
            }

            public void onFinish() {
                Log.d(TAG,"call incremental()");

                Intent intent = new Intent(GoodbyeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }
}
