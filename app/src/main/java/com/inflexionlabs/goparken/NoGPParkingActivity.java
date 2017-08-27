package com.inflexionlabs.goparken;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoGPParkingActivity extends AppCompatActivity {
    final private String TAG = "NoGPParkingActivity";

    ParkingUtilities parkingUtilities = ParkingUtilities.getInstance();

    ImageButton btnNav;
    ImageView parkingImage;
    TextView txtCostoHora;
    TextView txtAddressParking;
    TextView txtDiaHora;
    TextView txtDescripcion;

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
        txtCostoHora = (TextView) findViewById(R.id.txtCostoHora);
        txtAddressParking = (TextView) findViewById(R.id.txtParkingAddressNGP);
        txtDiaHora = (TextView) findViewById(R.id.txtDiaHoraNGP);
        txtDescripcion = (TextView) findViewById(R.id.txtDescripcionNGP);


        Picasso.with(this).load(parkingUtilities.getImage_path()).fit().into(parkingImage);
        txtCostoHora.setText("$ "+ parkingUtilities.getCost_public_by_hour());
        txtAddressParking.setText(formatAddress());
        txtDiaHora.setText(calculateSchedule());
        txtDescripcion.setText(parkingUtilities.getDescription());

        btnNav.setOnClickListener(btnListener);

    }


    public String formatAddress(){
        String address="";

        address = parkingUtilities.getAddress_street()+", "
                +parkingUtilities.getAddress_number()+", "
                +parkingUtilities.getAddress_colony()+", C.P. "
                +parkingUtilities.getAddress_postal_code()+", "
                +parkingUtilities.getAddress_delegation()+", "
                +parkingUtilities.getAddress_state();

        return address;
    }

    public String calculateSchedule(){
        String DiaHora="";

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        String start = "";
        String finish = "";

        switch (dayOfTheWeek){
            case "Lunes":

                start = parkingUtilities.getSchedule_start_monday();
                finish = parkingUtilities.getSchedule_finish_monday();

                break;
            case "Martes":

                start = parkingUtilities.getSchedule_start_tuesday();
                finish = parkingUtilities.getSchedule_finish_tuesday();

                break;
            case "Miércoles":

                start = parkingUtilities.getSchedule_start_wednesday();
                finish = parkingUtilities.getSchedule_finish_wednesday();

                break;
            case "Jueves":

                start = parkingUtilities.getSchedule_start_thursday();
                finish = parkingUtilities.getSchedule_finish_thursday();

                break;
            case "Viernes":

                start = parkingUtilities.getSchedule_start_friday();
                finish = parkingUtilities.getSchedule_finish_friday();

                break;
            case "Sábado":

                start = parkingUtilities.getSchedule_start_saturday();
                finish = parkingUtilities.getSchedule_finish_saturday();

                break;
            case "Domingo":

                start = parkingUtilities.getSchedule_start_sunday();
                finish = parkingUtilities.getSchedule_finish_sunday();

                break;
        }

        if(start.equals("Cerrado")){
            //mostar "ESTACIONAMIENTO CERRADO";
        }

        if(start.equals("24 horas") || start.equals("Cerrado")){
            DiaHora = dayOfTheWeek+" "+start;
        }else{
            DiaHora = dayOfTheWeek+" De "+start+" hrs a "+finish+" hrs";
        }

        if(start.equals("")){
            DiaHora = dayOfTheWeek;
        }

        return DiaHora;
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
