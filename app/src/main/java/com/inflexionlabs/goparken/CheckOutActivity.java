package com.inflexionlabs.goparken;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import mx.openpay.android.Openpay;

public class CheckOutActivity extends AppCompatActivity {

    private final String MERCHANT_ID = "mtfur53iopbr7ceh01ro";
    private final String PRIVATE_API_KEY = "sk_99ab173dcfe944e28cc048f5534eb857";
    private final String PUBLIC_API_KEY = "pk_7226b0afacd546e0bb883e90945bdb0a";
    boolean productionMode = false;

    private final String TAG = "CheckOutActivity";

    ImageButton btnCallmeE;

    String URL_BASE = "http://ec2-107-20-100-168.compute-1.amazonaws.com/api/v1/";
    String URL_PHONE="Help/Info";
    String URL_EXITCODE = "Parking/ExitCode?";
    String URL_CHEKOUT ="Checkout";
    JsonObjectRequest jsArrayRequest;
    JsonObjectRequest jsArrayRequest2;
    JSONObject dataRequest = new JSONObject();

    UserUtilities userUtilities = UserUtilities.getInstance();
    CheckInUtilities checkInUtilities = CheckInUtilities.getInstance();

    EditText editTxtECode1;
    EditText editTxtECode2;
    EditText editTxtECode3;
    EditText editTxtECode4;

    Button btnValidarE;

    TextView txtExitCode;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

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

        btnCallmeE = (ImageButton) findViewById(R.id.btnCallmeE);

        editTxtECode1 = (EditText) findViewById(R.id.editTxtECode_1);
        editTxtECode2 = (EditText) findViewById(R.id.editTxtECode_2);
        editTxtECode3 = (EditText) findViewById(R.id.editTxtECode_3);
        editTxtECode4 = (EditText) findViewById(R.id.editTxtECode_4);

        btnValidarE = (Button) findViewById(R.id.btnValidarE);

        txtExitCode = (TextView) findViewById(R.id.txtExitCode);
        txtExitCode.setText(checkInUtilities.getExit_code());

        editTxtECode1.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                if(editTxtECode1.getText().toString().length() == 1){
                    editTxtECode2.requestFocus();
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

        editTxtECode2.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                if(editTxtECode2.getText().toString().length() == 1){
                    editTxtECode3.requestFocus();
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

        editTxtECode3.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                if(editTxtECode3.getText().toString().length() == 1){
                    editTxtECode4.requestFocus();
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

        btnCallmeE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callme();
            }
        });

        btnValidarE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validar();
            }
        });



    }

    private boolean validateForm(){
        boolean valid = true;


        if(TextUtils.isEmpty(editTxtECode1.getText().toString())){
            editTxtECode1.setError("Campo requerido");
            valid = false;
        }else{
            editTxtECode1.setError(null);
        }


        if(TextUtils.isEmpty(editTxtECode2.getText().toString())){
            editTxtECode2.setError("Campo requerido");
            valid = false;
        }else{
            editTxtECode2.setError(null);
        }

        if(TextUtils.isEmpty(editTxtECode3.getText().toString())){
            editTxtECode3.setError("Campo requerido");
            valid = false;
        }else{
            editTxtECode3.setError(null);
        }

        if(TextUtils.isEmpty(editTxtECode4.getText().toString())){
            editTxtECode4.setError("Campo requerido");
            valid = false;
        }else{
            editTxtECode4.setError(null);
        }




        return valid;
    }

    public void validar(){

        if(!validateForm()){
            return;
        }

        String codigo = editTxtECode1.getText().toString()+editTxtECode2.getText().toString()+editTxtECode3.getText().toString()+editTxtECode4.getText().toString();

        URL_EXITCODE = URL_EXITCODE+"token="+userUtilities.getToken()+"&parking_id="+Integer.toString(checkInUtilities.getParking_id())+
                "&exit_code="+codigo+
                "&checkin_id="+Integer.toString(checkInUtilities.getId());

        jsArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL_BASE + URL_EXITCODE,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejo de la respuesta
                        Log.d(TAG, "Respuesta en JSON: " + response);
                        checkOutNormal();

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

    public void checkOutNormal(){

        Openpay openpay = new Openpay(MERCHANT_ID,PRIVATE_API_KEY,productionMode);

        String deviceSessionId;

        try {
            dataRequest.put("checkin_id",checkInUtilities.getId());
            dataRequest.put("deviceSessionId",openpay.getDeviceCollectorDefaultImpl().setup(this));
            dataRequest.put("comision",checkInUtilities.getComision());
            dataRequest.put("token",userUtilities.getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsArrayRequest2 = new JsonObjectRequest(
                Request.Method.POST,
                URL_BASE + URL_CHEKOUT,
                dataRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejo de la respuesta
                        Log.d(TAG, "Respuesta en JSON: " + response);
                        goToGoodbayScreen();


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
    }

    public void goToTimerActivity(){
        Intent intent = new Intent(this, TimerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("promo","false");
        startActivity(intent);
        finish();
    }

    public void goToGoodbayScreen(){
        Intent intent = new Intent(this, GoodbyeActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        goToTimerActivity();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
