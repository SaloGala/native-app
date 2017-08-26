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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import android.widget.LinearLayout.LayoutParams;


public class ParkingActivity extends AppCompatActivity {
    final private String TAG = "ParkingActivity";

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

    String URL_COMPLEMENTO = "OpenPay/GetCards?";

    String URL_PREDET = "OpenPay/MakeDefault";

    JsonObjectRequest jsArrayRequest;
    JsonObjectRequest jsArrayRequestP;
    JSONObject dataRequest = new JSONObject();

    ArrayList<Card> cards;
    Context context;

    LinearLayout lytInfoParking;

    TextView txtTarifaGP;
    TextView txtTarifaGPpesos;
    TextView txtTarifaRegular;
    TextView txtTarifaRegulapesos;

    LayoutParams layoutparams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        initializeComponents();
        getCardList();
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

        btnNav = (ImageButton) findViewById(R.id.btnNavigation);
        btnAddCardActivity = (Button) findViewById(R.id.btnAddCardActivity);

        parkingImage = (ImageView) findViewById(R.id.imgPaking);
        Picasso.with(getApplicationContext()).load(parkingUtilities.getImage_path()).fit().into(parkingImage);

        avaImage = (ImageView) findViewById(R.id.imgAva);
        spinnerCards = (Spinner) findViewById(R.id.spinnerCards);

        lytInfoParking = (LinearLayout) findViewById(R.id.lytInfoParking);

        txtTarifaGP = new TextView(this);
        txtTarifaGPpesos = new TextView(this);
        txtTarifaRegular = new TextView(this);
        txtTarifaRegulapesos = new TextView(this);

        btnNav.setOnClickListener(btnListener);
        btnAddCardActivity.setOnClickListener(btnListener);
        //

        Intent intent = getIntent();
        availability = intent.getStringExtra("availability");

        cards = new ArrayList<>();
        context = this;

        initializeViewComponents();


    }

    public void initializeViewComponents() {

        if (parkingUtilities.getAcceptGoParken() == 1) {

            if (availability.equals("full")) {
                avaImage.setImageResource(R.drawable.marca_roja);

            } else if (availability.equals("almost_full")) {
                avaImage.setImageResource(R.drawable.marca_amarilla);
            } else {
                avaImage.setImageResource(R.drawable.marca_verde);
            }

            if (parkingUtilities.getTarifaPromo() == 1) {

                txtTarifaGP.setText("TARIFA GOPARKEN");
                txtTarifaGPpesos.setText("$ " + Double.toString(parkingUtilities.getCost_goparken_by_hour()) + "(PROMO: " + parkingUtilities.getHorasPromo() + "hrs X $" + Integer.toString(parkingUtilities.getTarifaPromo()));

            } else {

                txtTarifaGP.setText("TARIFA GOPARKEN");
                txtTarifaGPpesos.setText("$ " + Double.toString(parkingUtilities.getCost_goparken_by_hour()));
            }

            lytInfoParking.addView(txtTarifaGP);
            lytInfoParking.addView(txtTarifaGPpesos);


        } else {

            avaImage.setImageResource(R.drawable.marca_gris);
        }

        txtTarifaRegular.setText("TARIFA REGULAR");
        txtTarifaRegulapesos.setText("$ " + Double.toString(parkingUtilities.getCost_public_by_hour()));

        lytInfoParking.addView(txtTarifaRegular);
        lytInfoParking.addView(txtTarifaRegulapesos);

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

        Intent intent = new Intent(this, AddCardActivity.class);
        startActivity(intent);
    }

    public void getCardList() {

        URL_COMPLEMENTO = URL_COMPLEMENTO + "token=" + userUtilities.getToken();

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

                            if (payable > 0) {

                                for (int i = 0; i < methods.length(); i++) {

                                    Log.d(TAG, "Method " + i + ": " + methods.getJSONObject(i));

                                    if (methods.getJSONObject(i).getString("status").equals("active")) {
                                        cards.add(new Card(methods.getJSONObject(i).getInt("id"),
                                                methods.getJSONObject(i).getString("openpay_card_mask"),
                                                methods.getJSONObject(i).getString("status"),
                                                methods.getJSONObject(i).getInt("default")
                                        ));
                                    }

                                }

                                spinnerAdapter = new SpinnerItemAdapter(cards, getApplicationContext());
                                spinnerCards.setAdapter(spinnerAdapter);

                                spinnerCards.setVisibility(View.VISIBLE);

                                spinnerCards.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                        predetCard(Integer.toString(((Card) parent.getItemAtPosition(position)).getId()));
                                        Toast.makeText(getApplicationContext(), Integer.toString(((Card) parent.getItemAtPosition(position)).getId()), Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });

                            } else {
                                btnAddCardActivity.setVisibility(View.VISIBLE);
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

    public void predetCard(String method_id) {

        try {

            dataRequest.put("method_id", method_id);
            dataRequest.put("token", userUtilities.getToken());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsArrayRequestP = new JsonObjectRequest(
                Request.Method.POST,
                URL_BASE + URL_PREDET,
                dataRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejo de la respuesta
                        Log.d(TAG, "Respuesta en JSON: " + response);

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
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsArrayRequestP);

    }

    public void navegarWazeMaps() {

        String uri = "geo:0,0" + "?q=" + parkingUtilities.getLatitude() + "," + parkingUtilities.getLongitude();
        startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(uri)));
    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
