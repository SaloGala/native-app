package com.inflexionlabs.goparken;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class EditCardActivity extends AppCompatActivity {

    private final String TAG = "EditCardActivity";

    UserUtilities userUtilities = UserUtilities.getInstance();

    TextView txtNombreUser;
    TextView txtEmailUser;
    TextView txtCardNumber;

    Button btnDeleteCard;
    Button btnPredetCard;

    int method_id;
    String card_mask="";

    String URL_BASE = "http://ec2-107-20-100-168.compute-1.amazonaws.com/api/v1/";
    String URL_DELETE="OpenPay/Delete";
    String URL_PREDET="OpenPay/MakeDefault";
    JSONObject dataRequest = new JSONObject();
    JsonObjectRequest jsArrayRequest;

    ImageView imgUserPhoto;
    String photoUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);

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

        txtNombreUser = (TextView) findViewById(R.id.txtNombreUsuario);
        txtEmailUser = (TextView) findViewById(R.id.txtEmailUsuario);
        txtCardNumber = (TextView) findViewById(R.id.txtNumTarjeta);

        btnDeleteCard = (Button) findViewById(R.id.btnDeleteCard);
        btnPredetCard = (Button) findViewById(R.id.btnPredetCard);

        imgUserPhoto = (ImageView) findViewById(R.id.imgUserFoto);

        btnDeleteCard.setOnClickListener(btnListener);
        btnPredetCard.setOnClickListener(btnListener);

        Intent intent = getIntent();
        method_id = Integer.parseInt(intent.getStringExtra("method_id"));
        card_mask = intent.getStringExtra("card_mask");



        initializeViewComponents();


    }

    public void initializeViewComponents(){

        txtNombreUser.setText(userUtilities.getUserName());
        txtEmailUser.setText(userUtilities.getEmail());
        txtCardNumber.setText(card_mask);

        if (userUtilities.getProvider().equals("password")) {
            photoUrl = "https://firebasestorage.googleapis.com/v0/b/goparkennativa-cfff1.appspot.com/o/perfil_imagen%402x.png?alt=media&token=0104417e-f8d8-4b1d-8712-ea90e18ecadd";

        } else {
            photoUrl = userUtilities.getPhotoUrl().toString();
        }
        Picasso.with(this).load(photoUrl).noFade().fit().into(imgUserPhoto);
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.btnDeleteCard:
                    deleteCard();
                    break;

                case R.id.btnPredetCard:
                    predetCard();
                    break;

            }

        }
    };

    public void constructDataRequest(){

        try {

            dataRequest.put("method_id", method_id);
            dataRequest.put("token", userUtilities.getToken());


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    private void showMessge(String msg) {

        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    public void deleteCard(){


        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.alert_dialog);
        dialog.setTitle("Aviso");

        // set the custom dialog components - text, image and button
        TextView txtTexto = (TextView) dialog.findViewById(R.id.txtTexto);
        Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
        Button btnCancelar = (Button) dialog.findViewById(R.id.btnCancelar);

        txtTexto.setText("¿Seguro que quieres eliminar esta tarjeta?");

        btnAceptar.setText("Aceptar");
        btnCancelar.setText("Cancelar");

        // if button is clicked, close the custom dialog
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                constructDataRequest();

                jsArrayRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        URL_BASE + URL_DELETE,
                        dataRequest,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Manejo de la respuesta
                                Log.d(TAG, "Respuesta en JSON: " + response);



                                showMessge("Tarjeta eliminada con éxito");

                                finish();

                            }
                        },
                        new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Manejo de errores

                                showMessge("Ocurrio un error, por favor intente nuevamente");
                                Log.d(TAG, "Error: " + error.getMessage());
                            }
                        });

                // Add request to de queue
                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsArrayRequest);

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

    public void predetCard(){

        constructDataRequest();

        jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL_BASE + URL_PREDET,
                dataRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejo de la respuesta
                        Log.d(TAG, "Respuesta en JSON: " + response);

                        showMessge("Tarjeta predeterminada con éxito");


                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejo de errores

                        showMessge("Ocurrio un error, por favor intente nuevamente");
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
}
