package com.inflexionlabs.goparken;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ParkingPreviewActivity extends AppCompatActivity {

    ParkingUtilities parkingUtilities = ParkingUtilities.getInstance();

    private String availability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("NexaLight.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.activity_parking_preview);

        Button BTN1 = (Button) findViewById(R.id.BTN1);
        Button BTN2 = (Button) findViewById(R.id.BTN2);
        TextView txtCostoHora = (TextView) findViewById(R.id.txtCostoHora);
        TextView txtAddressParking = (TextView) findViewById(R.id.txtAddressParking);
        TextView txtCostoHoraGP = (TextView) findViewById(R.id.txtCostoHoraGP);
        TextView txtCostoHoraR = (TextView) findViewById(R.id.txtCostoHoraR);
        LinearLayout LLNoGoParken = (LinearLayout) findViewById(R.id.LLNOGoParken);
        LinearLayout LLYesGoParken = (LinearLayout) findViewById(R.id.LLYesGoParken);

        txtAddressParking.setText(formatAddress());

        if (parkingUtilities.acceptGoParken == 1) {

            LLNoGoParken.setVisibility(View.GONE);
            LLYesGoParken.setVisibility(View.VISIBLE);

            if (parkingUtilities.getTarifaPromo() == 1) {

                txtCostoHoraGP.setText("$ " + Double.toString(parkingUtilities.getCost_goparken_by_hour()) + "(PROMO: " + parkingUtilities.getHorasPromo() + "hrs X $" + Integer.toString(parkingUtilities.getTarifaPromo()));

            } else {

                txtCostoHoraGP.setText("$ " + Double.toString(parkingUtilities.getCost_goparken_by_hour()));
            }

            txtCostoHoraR.setText("$ " + Double.toString(parkingUtilities.getCost_public_by_hour()));

        } else {
            LLNoGoParken.setVisibility(View.VISIBLE);
            LLYesGoParken.setVisibility(View.GONE);

            txtCostoHora.setText("$ " + parkingUtilities.getCost_public_by_hour());
        }

        CardView CVDetail = (CardView) findViewById(R.id.CVDetail);

        CVDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToParkingDetailActivity(parkingUtilities.getAcceptGoParken());
            }
        });

        BTN1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return true;
            }
        });

        BTN2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return true;
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void goToParkingDetailActivity(int acceptGoParken) {

        Intent intent;

        if (acceptGoParken == 1) {
            Intent intentExtra = getIntent();
            availability = intentExtra.getStringExtra("availability");

            intent = new Intent(this, ParkingActivity.class);
            intent.putExtra("availability", availability);
        } else {
            intent = new Intent(this, NoGPParkingActivity.class);
        }

        startActivity(intent);
    }

    public String formatAddress() {
        String address = "";

        address = parkingUtilities.getAddress_street() + ", "
                + parkingUtilities.getAddress_number() + ", "
                + parkingUtilities.getAddress_colony() + ", C.P. "
                + parkingUtilities.getAddress_postal_code() + ", "
                + parkingUtilities.getAddress_delegation() + ", "
                + parkingUtilities.getAddress_state();

        return address;
    }
}
