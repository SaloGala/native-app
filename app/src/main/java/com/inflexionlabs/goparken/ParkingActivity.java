package com.inflexionlabs.goparken;

import android.content.Context;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParkingActivity extends AppCompatActivity {
    final private String TAG="ParkingActivity";

    ParkingUtilities parkingUtilities = ParkingUtilities.getInstance();
    UserUtilities userUtilities = UserUtilities.getInstance();

    ImageButton btnNav;
    ImageView parkingImage;
    ImageView avaImage;
    Button btnAddCardActivity;


    String availability;
    int payable = 0;

    Spinner spinnerCards;
    SpinnerItemAdapter spinnerAdapter = null;

    String URL_BASE = "http://ec2-107-20-100-168.compute-1.amazonaws.com/api/v1/";

    String URL_COMPLEMENTO="OpenPay/GetCards?";

    JsonObjectRequest jsArrayRequest;
    ArrayList<Card> cards;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        initializeComponents();
        getCardList();
        initializeViewComponents();
    }

    private void initializeComponents() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnNav = (ImageButton) findViewById(R.id.btnNavigation);
        btnAddCardActivity = (Button) findViewById(R.id.btnAddCardActivity);

        parkingImage=(ImageView) findViewById(R.id.imgPaking);
        Picasso.with(getApplicationContext()).load(parkingUtilities.getImage_path()).fit().into(parkingImage);

        avaImage = (ImageView) findViewById(R.id.imgAva);
        spinnerCards = (Spinner) findViewById(R.id.spinnerCards);


        btnNav.setOnClickListener(btnListener);
        btnAddCardActivity.setOnClickListener(btnListener);
        //

        Intent intent = getIntent();
        availability = intent.getStringExtra("availability");

        cards = new ArrayList<>();
        context = this;




    }

    public void initializeViewComponents(){

        if(parkingUtilities.getAcceptGoParken()==1){
            if (availability.equals("full")) {
                avaImage.setImageResource(R.drawable.marca_roja);

            } else if (availability.equals("almost_full")) {
                avaImage.setImageResource(R.drawable.marca_amarilla);
            } else {
                avaImage.setImageResource(R.drawable.marca_verde);
            }
        }else {
            avaImage.setImageResource(R.drawable.marca_gris);
        }


        if(payable>0){
            spinnerCards.setVisibility(View.VISIBLE);
        }else{
            btnAddCardActivity.setVisibility(View.VISIBLE);
        }



    }

    private View.OnClickListener btnListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.btnNavigation:
                    navegarWazeMaps();

                    break;

                case R.id.btnAddCardActivity:
                    goToAddCardScreen();
                    break;
            }

        }
    };

    private void goToAddCardScreen() {

        Intent intent = new Intent(this,AddCardActivity.class);
        startActivity(intent);
    }

    public void getCardList(){

        URL_COMPLEMENTO = URL_COMPLEMENTO+"token="+userUtilities.getToken();

        jsArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL_BASE + URL_COMPLEMENTO,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejo de la respuesta

                        Log.d(TAG, "Response : " + response);

                        try {
                            JSONObject content = response.getJSONObject("content");
                            JSONArray methods = content.getJSONArray("methods");

                            Log.d(TAG, "Methods : " + methods);

                            payable = methods.length();

                            if(payable>0){

                                for (int i=0; i<methods.length(); i++){

                                    Log.d(TAG, "Method " +i+": "+ methods.getJSONObject(i));

                                    if(methods.getJSONObject(i).getString("status").equals("active")){
                                        cards.add(new Card(methods.getJSONObject(i).getInt("id"),
                                                methods.getJSONObject(i).getString("openpay_card_mask"),
                                                methods.getJSONObject(i).getString("status"),
                                                methods.getJSONObject(i).getInt("default")
                                        ));
                                    }

                                }

                                spinnerAdapter = new SpinnerItemAdapter(cards,getApplicationContext());
                                spinnerCards.setAdapter(spinnerAdapter);
                            }





                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejo de errores
                        Log.d(TAG, "Error: " + error.getMessage());
                    }
                });

        // Add request to de queue
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsArrayRequest);

    }

    public void navegarWazeMaps(){

        String uri = "geo:0,0"+"?q="+parkingUtilities.getLatitude() + "," + parkingUtilities.getLongitude();
        startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(uri)));
    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
