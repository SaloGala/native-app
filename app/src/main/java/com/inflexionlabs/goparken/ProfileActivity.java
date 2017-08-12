package com.inflexionlabs.goparken;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    TextView txtUserName;
    TextView txtUserEmail;
    ImageView imgUserPhoto;

    User currentUser;

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
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryActionBar), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtUserName = (TextView) findViewById(R.id.txtUserName);
        txtUserEmail = (TextView) findViewById(R.id.txtUserEmail);
        imgUserPhoto = (ImageView) findViewById(R.id.imgUserPhoto);

        showInfoUser();


    }

    private void showInfoUser(){
        currentUser = new User();

        txtUserName.setText(currentUser.getUserName());
        txtUserEmail.setText(currentUser.getEmail());
        Picasso.with(getApplicationContext()).load(currentUser.getPhotoUrl()).fit().into(imgUserPhoto);

    }




    public void onBackPressed() {
        finish();
    }


}
