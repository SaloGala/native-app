package com.inflexionlabs.goparken;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    static final String TAG = "ProfileActivity";

    TextView txtUserName;
    TextView txtUserEmail;
    ImageView imgUserPhoto;

    private DatabaseReference mDatabaseReference;

    FirebaseUser currentUser;

    String provider;
    String photoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeComponents();
    }

    private void initializeComponents() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtUserName = (TextView) findViewById(R.id.txtUserName);
        txtUserEmail = (TextView) findViewById(R.id.txtUserEmail);
        imgUserPhoto = (ImageView) findViewById(R.id.imgUserPhoto);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        showInfoUser();
        getInfoUser();


    }

    private void showInfoUser(){

        for (UserInfo profile: currentUser.getProviderData()){
            provider = profile.getProviderId();
        }

        if(provider.equals("password")){
            photoUrl = "https://firebasestorage.googleapis.com/v0/b/goparkennativa-cfff1.appspot.com/o/perfil_imagen%402x.png?alt=media&token=0104417e-f8d8-4b1d-8712-ea90e18ecadd";

        }else{
            photoUrl = currentUser.getPhotoUrl().toString();
        }

        txtUserName.setText(currentUser.getDisplayName());
        txtUserEmail.setText(currentUser.getEmail());
        Picasso.with(getApplicationContext()).load(photoUrl).fit().into(imgUserPhoto);
    }

    private void getInfoUser(){


        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference mUserDetail = mDatabaseReference.child("users").child(currentUser.getUid()+"/data");

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                /*txtUserName.setText(user.getUserName());
                txtUserEmail.setText(user.getEmail());
                Picasso.with(getApplicationContext()).load(user.getPhotoUrl()).fit().into(imgUserPhoto);*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }


        };

        mUserDetail.addListenerForSingleValueEvent(userListener);



    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEditarPerfil:
                editProfileForm();
                break;



        }
    }

    private void editProfileForm() {

        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }


    public void onBackPressed() {
        finish();
    }


}
