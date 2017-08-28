package com.inflexionlabs.goparken;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.FloatProperty;
import android.util.Log;
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

    ParkingUtilities parkingUtilities = ParkingUtilities.getInstance();
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

    JsonObjectRequest jsArrayRequest;
    String URL_GETCHECKININFO;

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
        txtSec.setText(before + "" + Double.toString(numseconds));

        before = "";
        if (numminutes < 10) {
            before = "0";
        }

        txtMin.setText(before + "" + Double.toString(numminutes));

        before = "";
        if (numhours < 10) {
            before = "0";
        }

        txtHoras.setText(before + "" + Double.toString(numhours));

        if (numhours < 1) {

            txtPrecio.setText(Double.toString(parkingUtilities.getCost_goparken_by_hour()));

        } else {

            priceH = parkingUtilities.getCost_goparken_by_hour() * numhours;

            /*Calculamos las fracciones*/
            double minutos = timerMinutes_;
            minutos = minutos / 15;
            minutos = Math.ceil(minutos);

            priceF = parkingUtilities.getCost_goparken_by_fraction() * minutos;

            txtPrecio.setText("$ "+Double.toString(priceH)+Double.toString(priceF));
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
                                JSONObject parking = content.getJSONObject("parking");

                                if(checkin != null){

                                    txtHoraEntrada.setText("HORA DE ENTRADA "+checkin.getString("in"));
                                    txtParkingName.setText(parking.getString("name"));
                                    txtAddress.setText(formatAddress(parking));
                                    txtCardMask.setText(getString(R.string.tu_metodo_msg)+" "+content.getString("mask_card"));

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
}
