package com.inflexionlabs.goparken;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapActivity extends AppCompatActivity {

    private TextView txtUid;
    private TextView txtNombre;
    private TextView txtEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        txtUid = (TextView) findViewById(R.id.txtUid);
        txtNombre = (TextView) findViewById(R.id.txtNombre);
        txtEmail = (TextView) findViewById(R.id.txtEmail);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null){


            writeNewUser();

            String uid = user.getUid();
            String nombre = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            txtUid.setText(uid);
            txtNombre.setText(nombre);
            txtEmail.setText(email);

        }else{
            goLoginScreen();
        }



    }

    private void goLoginScreen(){
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void logOut(View view){
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        goLoginScreen();
    }

    private void writeNewUser(){
        final DatabaseReference dataBaseRef = FirebaseDatabase.getInstance().getReference();
        final User currentUser = new User();

        dataBaseRef.child("users").child(currentUser.getUid()).child("userName").setValue(currentUser.getUserName());
        dataBaseRef.child("users").child(currentUser.getUid()).child("email").setValue(currentUser.getEmail());
        dataBaseRef.child("users").child(currentUser.getUid()).child("photoUrl").setValue(currentUser.getPhotoUrl());
        dataBaseRef.child("users").child(currentUser.getUid()).child("provider").setValue(currentUser.getProvider());

    }


}
