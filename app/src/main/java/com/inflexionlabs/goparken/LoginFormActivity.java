package com.inflexionlabs.goparken;

import android.content.Intent;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class LoginFormActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";

    private EditText emailField;
    private EditText passwordField;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_login_form);



        //Intent intent = getIntent();
    }
}
