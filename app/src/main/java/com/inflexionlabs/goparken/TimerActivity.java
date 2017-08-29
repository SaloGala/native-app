package com.inflexionlabs.goparken;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.FloatProperty;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TimerActivity extends AppCompatActivity {

    private final String TAG = "TimerActivity";

    CheckInUtilities checkInUtilities = CheckInUtilities.getInstance();

    UserUtilities userUtilities = UserUtilities.getInstance();

    double globalSeconds = 0;
    double timerSeconds_ = 0;
    double timerMinutes_ = 0;
    double timerHours_ = 0;
    Boolean counting = false;

    TextView txtSec;
    TextView txtMin;
    TextView txtHoras;
    TextView txtPrecio;
    TextView txtParkingName;
    TextView txtHoraEntrada;
    TextView txtAddress;
    TextView txtCardMask;

    double priceH = 0;
    double priceF = 0;
    double priceT = 0;

    JsonObjectRequest jsArrayRequest;
    JsonObjectRequest jsArrayRequest1;
    String URL_BASE = "http://ec2-107-20-100-168.compute-1.amazonaws.com/api/v1/";
    JSONObject dataRequest = new JSONObject();

    String URL_GETCHECKININFO;
    JSONObject parking;

    Button btnCheckOut;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        initializeComponents();

        loadTimer();

    }

    public void initializeComponents(){

        txtSec = (TextView) findViewById(R.id.txtSec);
        txtMin = (TextView) findViewById(R.id.txtMin);
        txtHoras = (TextView) findViewById(R.id.txtHoras);
        txtPrecio = (TextView) findViewById(R.id.txtPrecio);
        txtParkingName = (TextView) findViewById(R.id.txtParkingName);
        txtHoraEntrada = (TextView) findViewById(R.id.txtHoraEntrada);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtCardMask = (TextView) findViewById(R.id.txtMetodoPago);

        progress = new ProgressDialog(this);

        btnCheckOut = (Button) findViewById(R.id.btnCheckOut);

        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOut();
            }
        });
    }

    public void incremental(){
        globalSeconds++;
        calculateTimer();

        if(counting){
            new CountDownTimer(1000, 1000) {

                public void onTick(long millisUntilFinished) {
                    Log.d(TAG,"seconds remaining: " + Long.toString(millisUntilFinished / 1000));
                }

                public void onFinish() {
                    Log.d(TAG,"call incremental()");
                    incremental();
                }
            }.start();
        }


    }

    public void validar(){

    }

    public void calculateTimer(){

        double seconds = globalSeconds;
        double numdays = Math.floor(seconds / 86400);
        double numhours = Math.floor((seconds % 86400) / 3600);
        numhours += numdays * 24;
        double numminutes = Math.floor(((seconds % 86400) % 3600) / 60);
        double numseconds = ((seconds % 86400) % 3600) % 60;

        timerSeconds_ = numseconds;
        timerMinutes_ = numminutes;
        timerHours_ = numhours;

        String before = "";

        if (numseconds < 10) {
            before = "0";
        }
        txtSec.setText(before + "" + Double.toString(numseconds)+"s");

        before = "";
        if (numminutes < 10) {
            before = "0";
        }

        txtMin.setText(before + "" + Double.toString(numminutes)+":m:");

        before = "";
        if (numhours < 10) {
            before = "0";
        }

        txtHoras.setText(before + "" + Double.toString(numhours)+"h:");

        if(checkInUtilities.isPromo()){

            try{
                txtPrecio.setText(Double.toString(parking.getDouble("precioPromo")));

                if(numhours > Double.parseDouble(parking.getString("horasPromo"))){

                    globalSeconds = 0;
                    timerHours_ = 0;
                    timerMinutes_ = 0;
                    timerSeconds_ = 0;

                    txtHoras.setText("00");
                    txtMin.setText("00");
                    txtSec.setText("00");

                    txtPrecio.setText(Double.toString(parking.getDouble("cost_goparken_by_hour")));

                    checkInUtilities.setPromo(false);

                    try {
                        dataRequest.put("checkin_id",checkInUtilities.getId());
                        dataRequest.put("token",userUtilities.getToken());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    jsArrayRequest1 = new JsonObjectRequest(
                            Request.Method.POST,
                            URL_BASE + "UpdateCheckin",
                            dataRequest,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // Manejo de la respuesta
                                    Log.d(TAG, "Respuesta en JSON 2 timer: " + response);

                                    }
                            },
                            new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // Manejo de errores
                                    ///progress.dismiss();
                                    //showMessge("Ocurrio un error por favor intenrte mas tarde");
                                    Log.d(TAG, "Error: " + error.getMessage());
                                }
                            });

                    // Add request to de queue
                    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsArrayRequest1);

                }

            }catch (JSONException e) {
                e.printStackTrace();
            }

        }else{

            if (numhours < 1) {

                try{
                    txtPrecio.setText(Double.toString(parking.getDouble("cost_goparken_by_hour")));
                }catch (JSONException e) {
                    e.printStackTrace();
                }



            } else {

                try{
                    priceH =  parking.getDouble("cost_goparken_by_hour") * numhours;

                /*Calculamos las fracciones*/
                    double minutos = timerMinutes_;
                    minutos = minutos / 15;
                    minutos = Math.ceil(minutos);

                    priceF = parking.getDouble("cost_goparken_by_fraction") * minutos;

                    priceT = priceH +priceF;

                    txtPrecio.setText("$ "+Double.toString(priceT));
                }catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }



    }

    public String formatAddress(JSONObject parking){
        String address="";

        try {
            address = parking.getString("address_street")+", "+
                    parking.getString("address_number")+", "+
                    parking.getString("address_colony")+", C.P. "+
                    parking.getString("address_postal_code")+", "+
                    parking.getString("address_delegation")+", "+
                    parking.getString("address_state");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*address = parkingUtilities.getAddress_street()+", "
                +parkingUtilities.getAddress_number()+", "
                +parkingUtilities.getAddress_colony()+", C.P. "
                +parkingUtilities.getAddress_postal_code()+", "
                +parkingUtilities.getAddress_delegation()+", "
                +parkingUtilities.getAddress_state();*/

        return address;
    }

    public void loadTimer(){

        hideProgress();

        progress.setMessage("Cargando temporizador ...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();


        URL_GETCHECKININFO = "http://ec2-107-20-100-168.compute-1.amazonaws.com/api/v1/CheckinByUserId?token="+
                userUtilities.getToken();

        jsArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL_GETCHECKININFO,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejo de la respuesta
                        Log.d(TAG, "Respuesta en JSON: " + response);
                        if(response != null){

                            try {
                                JSONObject content = response.getJSONObject("content");
                                JSONObject checkin = content.getJSONObject("checkin");
                                parking = content.getJSONObject("parking");

                                if(checkin != null){

                                    checkInUtilities.setId(checkin.getInt("id"));
                                    checkInUtilities.setParking_id(checkin.getInt("parking_id"));
                                    checkInUtilities.setMarker_id(checkin.getInt("marker_id"));
                                    checkInUtilities.setIn(checkin.getString("in"));
                                    checkInUtilities.setOut(checkin.getString("out"));
                                    checkInUtilities.setComision(parking.getInt("comision"));
                                    checkInUtilities.setExit_code(checkin.getString("exit_code"));

                                    txtHoraEntrada.setText("HORA DE ENTRADA: "+checkin.getString("in"));
                                    txtParkingName.setText(parking.getString("name"));
                                    txtAddress.setText(formatAddress(parking));
                                    txtCardMask.setText(getString(R.string.tu_metodo_msg)+": "+content.getString("mask_card"));

                                    globalSeconds = content.getDouble("secondsSinceCheckin");
                                    counting = true;

                                    new CountDownTimer(1000, 1000) {

                                        public void onTick(long millisUntilFinished) {
                                            Log.d(TAG,"seconds remaining: " + Long.toString(millisUntilFinished / 1000));
                                        }

                                        public void onFinish() {
                                            Log.d(TAG,"call incremental()");
                                            incremental();
                                        }
                                    }.start();

                                    hideProgress();


                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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

    public void load(){
        globalSeconds = 0;
        timerSeconds_ = 0;
        timerMinutes_ = 0;
        timerHours_ = 0;
        txtSec.setText("00");
        txtMin.setText("00");
        txtHoras.setText("00");
        counting = false;
    }

    public void checkOut(){

        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.alert_dialog);
        dialog.setTitle("Aviso");

        // set the custom dialog components - text, image and button
        TextView txtTexto = (TextView) dialog.findViewById(R.id.txtTexto);
        Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
        Button btnCancelar = (Button) dialog.findViewById(R.id.btnCancelar);

        txtTexto.setText("¿Seguro que quieres terminar la sesión?");

        btnAceptar.setText("Aceptar");
        btnCancelar.setText("Cancelar");

        // if button is clicked, close the custom dialog
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                killLoad();
                goToCheckOutAcitvity();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        dialog.show();
    }

    public void killLoad(){
        load();
    }

    public void goToCheckOutAcitvity(){

        Intent intent = new Intent(this, CheckOutActivity.class);

        startActivity(intent);

        finish();

    }

    public void hideProgress(){

        new CountDownTimer(1000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.d(TAG,"hideProgress seconds remaining: " + Long.toString(millisUntilFinished / 1000));
            }

            public void onFinish() {
                Log.d(TAG,"hideProgress");
                progress.dismiss();
            }
        }.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        /*killLoad();

        progress.setMessage("Resumiendo ...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        counting = false;

        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.d(TAG,"hideProgress seconds remaining: " + Long.toString(millisUntilFinished / 1000));
            }

            public void onFinish() {
                Log.d(TAG,"loadTimer()");
                loadTimer();
            }
        }.start();*/

    }
}
