package com.inflexionlabs.goparken;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.widget.LinearLayout.LayoutParams;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class ParkingActivity extends AppCompatActivity {
    final private String TAG = "ParkingActivity";

    ParkingUtilities parkingUtilities = ParkingUtilities.getInstance();
    UserUtilities userUtilities = UserUtilities.getInstance();

    ImageButton btnNav;
    ImageView parkingImage;
    ImageView avaImage;

    String availability;
    int payable = 0;

    String URL_BASE = "http://ec2-107-20-100-168.compute-1.amazonaws.com/api/v1/";

    String URL_COMPLEMENTO = "OpenPay/GetCards?";

    String URL_PREDET = "OpenPay/MakeDefault";

    JsonObjectRequest jsArrayRequest;
    JsonObjectRequest jsArrayRequestP;
    JSONObject dataRequest = new JSONObject();

    ArrayList<Card> cards;
    Context context;

    TextView txtTarifaGP;
    TextView txtTarifaRegular;
    TextView txtAddress;
    TextView txtDiaHora;
    TextView txtDescripcion;

    LinearLayout lytInfoParking;

    LayoutParams layoutparams;

    Boolean isOpen = true;
    Boolean isAvailable = true;

    CheckInUtilities checkInUtilities = CheckInUtilities.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("NexaLight.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setContentView(R.layout.activity_parking);

        initializeComponents();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
        //btnAddCardActivity = (Button) findViewById(R.id.btnAddCardActivity);

        parkingImage = (ImageView) findViewById(R.id.imgPaking);
        Picasso.with(this).load(parkingUtilities.getImage_path()).fit().into(parkingImage);

        avaImage = (ImageView) findViewById(R.id.imgAva);
        //spinnerCards = (Spinner) findViewById(R.id.spinnerCards);
        txtTarifaGP = (TextView) findViewById(R.id.txtCostoHoraGP);
        txtTarifaRegular = (TextView) findViewById(R.id.txtCostoHoraR);
        txtAddress = (TextView) findViewById(R.id.txtParkingAddress);
        txtDiaHora = (TextView) findViewById(R.id.txtDiaHora);
        txtDescripcion = (TextView) findViewById(R.id.txtDescripcion);

        lytInfoParking = (LinearLayout) findViewById(R.id.lytInfoParking);

        btnNav.setOnClickListener(btnListener);
        //btnAddCardActivity.setOnClickListener(btnListener);

        Intent intent = getIntent();
        availability = intent.getStringExtra("availability");

        cards = new ArrayList<>();
        context = this;

        //getCardList();

        initializeViewComponents();


    }

    public void initializeViewComponents() {

        if (availability.equals("full")) {

            avaImage.setImageResource(R.drawable.marca_roja);
            isAvailable = false;

        } else if (availability.equals("almost_full")) {

            avaImage.setImageResource(R.drawable.marca_amarilla);

        } else {

            avaImage.setImageResource(R.drawable.marca_verde);
        }

        if (parkingUtilities.getTarifaPromo() == 1) {

            txtTarifaGP.setText("$ " + Double.toString(parkingUtilities.getCost_goparken_by_hour()) + "(PROMO: " + parkingUtilities.getHorasPromo() + "hrs X $" + Integer.toString(parkingUtilities.getTarifaPromo()));

        } else {

            txtTarifaGP.setText("$ " + Double.toString(parkingUtilities.getCost_goparken_by_hour()));
        }


        txtTarifaRegular.setText("$ " + Double.toString(parkingUtilities.getCost_public_by_hour()));
        txtAddress.setText(formatAddress());
        txtDiaHora.setText(calculateSchedule());
        txtDescripcion.setText(parkingUtilities.getDescription());

        if (isOpen) {

            if (isAvailable) {
                getCardList();
            } else {
                TextView txtNoPlace = new TextView(this);

                txtNoPlace.setText(getString(R.string.no_place));
                txtNoPlace.setTextSize(30);
                txtNoPlace.setLayoutParams(new LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                ));

                lytInfoParking.addView(txtNoPlace);
            }

        } else {

            TextView txtParkingClosed = new TextView(this);

            txtParkingClosed.setText(getString(R.string.parking_closed));
            txtParkingClosed.setTextSize(30);
            txtParkingClosed.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
            ));

            Typeface type = Typeface.createFromAsset(getApplication().getAssets(), "NexaLight.ttf");
            txtParkingClosed.setTypeface(type);

            txtParkingClosed.setTextColor(Color.RED);

            txtParkingClosed.setGravity(Gravity.CENTER);

            lytInfoParking.addView(txtParkingClosed);
        }


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

    public String calculateSchedule() {
        String DiaHora = "";

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        String start = "";
        String finish = "";

        switch (dayOfTheWeek) {
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

        if (start.equals("Cerrado")) {
            //mostar "ESTACIONAMIENTO CERRADO";
            isOpen = false;
        }

        if (start.equals("24 horas") || start.equals("Cerrado")) {
            DiaHora = dayOfTheWeek + " " + start;
        } else {
            DiaHora = dayOfTheWeek + " De " + start + " hrs a " + finish + " hrs";
        }

        if (start.equals("")) {
            DiaHora = dayOfTheWeek;
        }

        return DiaHora;
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.btnNavigation:
                    navegarWazeMaps();

                    break;

                /*case R.id.btnAddCardActivity:
                    goToAddCardScreen();
                    break;*/
            }

        }
    };

    private void goToAddCardScreen() {

        Intent intent = new Intent(this, AddCardActivity.class);
        startActivity(intent);
    }

    private void goToAddCheckInScreen() {

        Intent intent = new Intent(this, CheckInActivity.class);
        startActivity(intent);
    }

    private void verificarTarifa() {

        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.aviso_dialog);
        dialog.setTitle("Aviso");
        // set the custom dialog components - text, image and button
        Button btnOK = (Button) dialog.findViewById(R.id.btnOK);

        TextView txtTarifa = (TextView) dialog.findViewById(R.id.txtTarifa);
        txtTarifa.setText("En este estacionamiento se cobrará una comisión de $" + Double.toString(parkingUtilities.getComFija()) + " + " + Double.toString(parkingUtilities.getComVar()) + "%  + I.V.A ");

        // if button is clicked, close the custom dialog
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                goToAddCheckInScreen();

            }
        });

        dialog.show();

    }

    private void verifcarPromo() {

        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.promo_dialog);
        dialog.setTitle("Información");
        // set the custom dialog components - text, image and button
        Button btnPromo = (Button) dialog.findViewById(R.id.btnPromo);
        Button btnCheckInNormal = (Button) dialog.findViewById(R.id.btnCheckInNormal);

        btnPromo.setText("Promo " + parkingUtilities.getHorasPromo() + " hrs X $" + Double.toString(parkingUtilities.getPrecioPromo()));
        btnCheckInNormal.setText("Cada hora X $" + Double.toString(parkingUtilities.getCost_goparken_by_hour()));

        // if button is clicked, close the custom dialog
        btnPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                checkInUtilities.setPromo(true);
                if (parkingUtilities.getComision() == 1) {
                    verificarTarifa();
                } else {

                    goToAddCheckInScreen();
                }

            }
        });

        btnCheckInNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                checkInUtilities.setPromo(false);
                if (parkingUtilities.getComision() == 1) {
                    verificarTarifa();
                } else {

                    goToAddCheckInScreen();
                }

            }
        });

        dialog.show();

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

                                ImageView imgCard = new ImageView(context);

                                imgCard.setImageResource(R.drawable.icono_tarjeta);
                                imgCard.setLayoutParams(new LayoutParams(
                                        LayoutParams.MATCH_PARENT,
                                        LayoutParams.WRAP_CONTENT
                                ));

                                TextView txtTusMetodos = new TextView(context);
                                Typeface type = Typeface.createFromAsset(getApplication().getAssets(), "NexaBold.ttf");
                                txtTusMetodos.setTypeface(type);
                                txtTusMetodos.setText(getString(R.string.tu_metodo_msg));
                                txtTusMetodos.setTextSize(18);
                                txtTusMetodos.setLayoutParams(new LayoutParams(
                                        LayoutParams.MATCH_PARENT,
                                        LayoutParams.WRAP_CONTENT
                                ));

                                txtTusMetodos.setGravity(Gravity.CENTER);

                                txtTusMetodos.setTextColor(getResources().getColor(R.color.colorPrimary));

                                Spinner spinnerCards = new Spinner(context);
                                spinnerCards.setLayoutParams(new LayoutParams(
                                        LayoutParams.MATCH_PARENT,
                                        LayoutParams.WRAP_CONTENT
                                ));

                                //spinnerCards.setBackgroundColor(Color.GRAY);

                                SpinnerItemAdapter spinnerAdapter = new SpinnerItemAdapter(cards, context);
                                spinnerCards.setAdapter(spinnerAdapter);

                                spinnerCards.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                        predetCard(Integer.toString(((Card) parent.getItemAtPosition(position)).getId()));
                                        //Toast.makeText(getApplicationContext(), Integer.toString(((Card) parent.getItemAtPosition(position)).getId()), Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });

                                Button btnCheckin = new Button(context);

                                btnCheckin.setText(getString(R.string.btn_checkin));
                                btnCheckin.setLayoutParams(new LayoutParams(
                                        LayoutParams.MATCH_PARENT,
                                        LayoutParams.WRAP_CONTENT
                                ));

                                btnCheckin.setBackground(getResources().getDrawable(R.drawable.round_checkin_button));

                                btnCheckin.setTextSize(24);

                                btnCheckin.setTextColor(Color.WHITE);

                                btnCheckin.setTypeface(type);

                                btnCheckin.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {


                                        if (parkingUtilities.getTarifaPromo() == 1) {
                                            verifcarPromo();
                                        } else {
                                            checkInUtilities.setPromo(false);
                                            if (parkingUtilities.getComision() == 1) {
                                                verificarTarifa();
                                            } else {
                                                goToAddCheckInScreen();
                                            }

                                        }

                                    }
                                });

                                Space space1 = new Space(context);
                                Space space2 = new Space(context);
                                Space space3 = new Space(context);

                                final float scale = context.getResources().getDisplayMetrics().density;
                                int pixels = (int) (15 * scale + 0.5f);

                                space1.setLayoutParams(new LayoutParams(
                                        LayoutParams.MATCH_PARENT,
                                        pixels
                                ));

                                space2.setLayoutParams(new LayoutParams(
                                        LayoutParams.MATCH_PARENT,
                                        pixels
                                ));

                                space3.setLayoutParams(new LayoutParams(
                                        LayoutParams.MATCH_PARENT,
                                        pixels
                                ));

                                lytInfoParking.addView(imgCard);
                                lytInfoParking.addView(space1);
                                lytInfoParking.addView(txtTusMetodos);
                                lytInfoParking.addView(space2);
                                lytInfoParking.addView(spinnerCards);
                                lytInfoParking.addView(space3);
                                lytInfoParking.addView(btnCheckin);

                            } else {
                                Button btnAddCardActivity = new Button(context);

                                btnAddCardActivity.setText(getString(R.string.bnt_registrar_pago));
                                btnAddCardActivity.setLayoutParams(new LayoutParams(
                                        LayoutParams.MATCH_PARENT,
                                        LayoutParams.WRAP_CONTENT
                                ));

                                Typeface type = Typeface.createFromAsset(getApplication().getAssets(), "NexaLight.ttf");
                                btnAddCardActivity.setTypeface(type);

                                btnAddCardActivity.setTextColor(Color.WHITE);

                                btnAddCardActivity.setTextSize(24);

                                btnAddCardActivity.setBackground(getResources().getDrawable(R.drawable.round_send_button));

                                btnAddCardActivity.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        goToAddCardScreen();
                                    }
                                });

                                lytInfoParking.addView(btnAddCardActivity);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
