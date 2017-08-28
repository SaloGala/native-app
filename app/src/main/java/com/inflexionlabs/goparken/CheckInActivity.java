package com.inflexionlabs.goparken;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.UserHandle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class CheckInActivity extends AppCompatActivity {

    private final String TAG = "CheckInActivity";

    String promo;
    ImageButton btnCallme;

    String URL_BASE = "http://ec2-107-20-100-168.compute-1.amazonaws.com/api/v1/";
    String URL_PHONE="Help/Info";
    String URL_ENTRYCODE = "Parking/EntryCode?";
    String URL_ADDCHECKIN ="Checkin";
    JsonObjectRequest jsArrayRequest;
    JsonObjectRequest jsArrayRequest2;
    JSONObject dataRequest = new JSONObject();

    UserUtilities userUtilities = UserUtilities.getInstance();
    ParkingUtilities parkingUtilities = ParkingUtilities.getInstance();

    EditText editTxtCode1;
    EditText editTxtCode2;
    EditText editTxtCode3;
    EditText editTxtCode4;

    Button btnValidar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

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

        Intent intent = getIntent();
        promo = intent.getStringExtra("promo");

        btnCallme = (ImageButton) findViewById(R.id.btnCallme);

        editTxtCode1 = (EditText) findViewById(R.id.editTxtCode_1);
        editTxtCode2 = (EditText) findViewById(R.id.editTxtCode_2);
        editTxtCode3 = (EditText) findViewById(R.id.editTxtCode_3);
        editTxtCode4 = (EditText) findViewById(R.id.editTxtCode_4);

        btnValidar = (Button) findViewById(R.id.btnValidar);

        editTxtCode1.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                if(editTxtCode1.getText().toString().length() == 1){
                    editTxtCode2.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        editTxtCode2.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                if(editTxtCode2.getText().toString().length() == 1){
                    editTxtCode3.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        editTxtCode3.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                if(editTxtCode3.getText().toString().length() == 1){
                    editTxtCode4.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        btnCallme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callme();
            }
        });

        btnValidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validar();
            }
        });

    }

    private void validar() {

        if(!validateForm()){
            return;
        }

        if(promo.equals("true")){
            checkInPromo();
        }else{
            checkInNormal();
        }

        //Log.e("accInternet",   Boolean.toString(isOnlineNet()));
    }

    private void checkInNormal(){

        String codigo = editTxtCode1.getText().toString()+editTxtCode2.getText().toString()+editTxtCode3.getText().toString()+editTxtCode4.getText().toString();

        URL_ENTRYCODE = URL_ENTRYCODE+"token="+userUtilities.getToken()+"&parking_id="+Integer.toString(parkingUtilities.getId())+
                "&entry_code="+codigo;

        jsArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL_BASE + URL_ENTRYCODE,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejo de la respuesta
                        Log.d(TAG, "Respuesta en JSON: " + response);

                        try {
                            dataRequest.put("marker_id",parkingUtilities.getId_marker());
                            dataRequest.put("token",userUtilities.getToken());

                            jsArrayRequest2 = new JsonObjectRequest(
                                    Request.Method.POST,
                                    URL_BASE + URL_ADDCHECKIN,
                                    dataRequest,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            // Manejo de la respuesta
                                            Log.d(TAG, "Respuesta en JSON 2: " + response);
                                            goToTimerActivity();


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
                            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsArrayRequest2);

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

    private void checkInPromo() {



    }

    public void goToTimerActivity(){
        Intent intent = new Intent(this, TimerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("promo",promo);
        startActivity(intent);
        finish();
    }

    public Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    private boolean validateForm(){
        boolean valid = true;


        if(TextUtils.isEmpty(editTxtCode1.getText().toString())){
            editTxtCode1.setError("Campo requerido");
            valid = false;
        }else{
            editTxtCode1.setError(null);
        }


        if(TextUtils.isEmpty(editTxtCode2.getText().toString())){
            editTxtCode2.setError("Campo requerido");
            valid = false;
        }else{
            editTxtCode2.setError(null);
        }

        if(TextUtils.isEmpty(editTxtCode3.getText().toString())){
            editTxtCode3.setError("Campo requerido");
            valid = false;
        }else{
            editTxtCode3.setError(null);
        }

        if(TextUtils.isEmpty(editTxtCode4.getText().toString())){
            editTxtCode4.setError("Campo requerido");
            valid = false;
        }else{
            editTxtCode4.setError(null);
        }




        return valid;
    }

    private void callme() {

        jsArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL_BASE + URL_PHONE,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejo de la respuesta
                        Log.d(TAG, "Respuesta en JSON: " + response);
                        try {
                            JSONObject content = response.getJSONObject("content");
                            String phone = content.getString("phone");

                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:"+phone));
                            startActivity(intent);

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
