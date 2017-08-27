package com.inflexionlabs.goparken;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class NoGPParkingActivity extends AppCompatActivity {
    final private String TAG = "NoGPParkingActivity";

    ParkingUtilities parkingUtilities = ParkingUtilities.getInstance();

    ImageButton btnNav;
    ImageView parkingImage;
    TextView txtCostoHora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_gpparking);

        initializeComponents();
        //initializeViewComponents();
    }

    private void initializeComponents() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnNav = (ImageButton) findViewById(R.id.btnNavigationNGP);

        parkingImage = (ImageView) findViewById(R.id.imgPakingNGP);

        Picasso.with(getApplicationContext()).load(parkingUtilities.getImage_path()).fit().into(parkingImage);

        txtCostoHora = (TextView) findViewById(R.id.txtCostoHora);

        txtCostoHora.setText("$ "+ parkingUtilities.getCost_public_by_hour());


        btnNav.setOnClickListener(btnListener);
        //initializeViewComponents();


    }

    public void initializeViewComponents() {

    }

    private View.OnClickListener btnListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.btnNavigationNGP:
                    navegarWazeMaps();

                    break;
            }

        }
    };

    public void navegarWazeMaps() {

        String uri = "geo:0,0" + "?q=" + parkingUtilities.getLatitude() + "," + parkingUtilities.getLongitude();
        startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(uri)));
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
