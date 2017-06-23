package com.inflexionlabs.goparken;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // --> hide bar from Activity
        setContentView(R.layout.activity_main);

    }
}
