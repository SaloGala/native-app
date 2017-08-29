package com.inflexionlabs.goparken;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CardsListActivity extends AppCompatActivity {

    final private String TAG="CardsListActivity";

    UserUtilities userUtilities = UserUtilities.getInstance();

    String URL_BASE = "http://ec2-107-20-100-168.compute-1.amazonaws.com/api/v1/";

    String URL_COMPLEMENTO="OpenPay/GetCards?";

    JsonObjectRequest jsArrayRequest;

    Button btnAddCard;

    ListView lstCards;
    ArrayList<Card> cards;
    CardViewAdapter cardViewAdapter = null;
    Context context;

    TextView txtUsuario;
    ImageView imgUserPhoto;
    String photoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards_list);

        initializeComponents();
        getCardList();
    }


    private void initializeComponents() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnAddCard = (Button) findViewById(R.id.btnAddCard);

        btnAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddCardScreen();
            }
        });

        lstCards = (ListView) findViewById(R.id.lstCards);
        cards = new ArrayList<>();

        context = this;

        txtUsuario = (TextView) findViewById(R.id.txtUsuario);
        txtUsuario.setText(userUtilities.getUserName());

        imgUserPhoto = (ImageView) findViewById(R.id.imgUserPicture);

        if (userUtilities.getProvider().equals("password")) {
            photoUrl = "https://firebasestorage.googleapis.com/v0/b/goparkennativa-cfff1.appspot.com/o/perfil_imagen%402x.png?alt=media&token=0104417e-f8d8-4b1d-8712-ea90e18ecadd";

        } else {
            photoUrl = userUtilities.getPhotoUrl().toString();
        }
        Picasso.with(this).load(photoUrl).noFade().fit().into(imgUserPhoto);



    }

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

                            cardViewAdapter = new CardViewAdapter(cards,context);
                            lstCards.setAdapter(cardViewAdapter);

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
