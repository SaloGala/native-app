package com.inflexionlabs.goparken;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;
import android.widget.Toast;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG="EditProfile";

    private EditText nombreField;
    private EditText apellidosField;

    private EditText emailField;
    private EditText emailFieldR;

    Button btnGuardar;

    JsonObjectRequest jsArrayRequest;

    String URL_BASE = "http://ec2-107-20-100-168.compute-1.amazonaws.com/api/v1/";

    String URL_COMPLEMENTO="User/Update";

    UserUtilities userUtilities = UserUtilities.getInstance();

    JSONObject dataRequest = new JSONObject();

    DatabaseReference databaseReference;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

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

        emailField = (EditText) findViewById(R.id.editEmail);
        emailFieldR = (EditText) findViewById(R.id.editEmail2);

        nombreField = (EditText) findViewById(R.id.editNombre);
        apellidosField = (EditText) findViewById(R.id.editApellidos);

        progress = new ProgressDialog(this);

        btnGuardar = (Button) findViewById(R.id.btnSavePerfil);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    guardarBackend();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        showInfoUser();


    }

    private boolean validateForm(){
        boolean valid = true;

        String nombre = nombreField.getText().toString();
        if(TextUtils.isEmpty(nombre)){
            nombreField.setError("Campo requerido");
            valid = false;
        }else{
            nombreField.setError(null);
        }

        String apellidos = apellidosField.getText().toString();
        if(TextUtils.isEmpty(apellidos)){
            apellidosField.setError("Campo requerido");
            valid = false;
        }else{
            apellidosField.setError(null);
        }

        String email = emailField.getText().toString();
        if(TextUtils.isEmpty(email)){
            emailField.setError("Campo requerido");
            valid = false;
        }else{
            if(!validEmail(email)){
                emailField.setError("Email no válido");
                valid = false;
            }
        }

        String emailR = emailFieldR.getText().toString();
        if(TextUtils.isEmpty(emailR)){
            emailFieldR.setError("Campo requerido");
            valid = false;
        }else{
            if(!validEmail(emailR)){
                emailFieldR.setError("Email no válido");
                valid = false;
            }
        }

        if (!email.equals(emailR)){
            emailField.setError("Los email deben coincidir");
            valid = false;
        }



        return valid;
    }

    public final static boolean validEmail(CharSequence email) {

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

    private void showInfoUser(){

        nombreField.setText(userUtilities.getUserName());
        apellidosField.setText(userUtilities.getLastname());
        emailField.setText(userUtilities.getEmail());
        emailFieldR.setText(userUtilities.getEmail());


    }

    public void constructDataRequest(){

        try {
            dataRequest.put("USER_ID", userUtilities.getId());
            dataRequest.put("USER_NAME", nombreField.getText());
            dataRequest.put("USER_EMAIL", emailField.getText());
            dataRequest.put("USER_PICTURE",null);
            dataRequest.put("USER_SOCIALID",userUtilities.getSocial_id());
            dataRequest.put("USER_TOKEN",userUtilities.getToken());
            dataRequest.put("USER_SOCIAL",userUtilities.getSocial());
            dataRequest.put("USER_LASTNAME", apellidosField.getText());
            dataRequest.put("FACEBOOK_SHARE", userUtilities.getFacebook_share());
            dataRequest.put("USER_EMAILTWO",emailFieldR.getText());
            dataRequest.put("token", userUtilities.getToken());

            if(userUtilities.getPhone().isEmpty()){

                dataRequest.put("USER_PHONE","-");
            }else{

                dataRequest.put("USER_PHONE",userUtilities.getPhone());
            }


            if(userUtilities.getAddress().isEmpty()){

                dataRequest.put("USER_ADDRESS", "-");

            }else{

                dataRequest.put("USER_ADDRESS", userUtilities.getAddress());
            }


            if(userUtilities.getPostalcode().isEmpty()){

                dataRequest.put("USER_POSTALCODE", "-");

            }else{

                dataRequest.put("USER_POSTALCODE", userUtilities.getPostalcode());
            }


            if(userUtilities.getState().isEmpty()){

                dataRequest.put("USER_STATE", "-");

            }else{

                dataRequest.put("USER_STATE", userUtilities.getState());
            }


            if(userUtilities.getCity().isEmpty()){

                dataRequest.put("USER_CITY", "-");

            }else{

                dataRequest.put("USER_CITY",userUtilities.getCity());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void guardarBackend() throws JSONException {


        if(!validateForm()){
            return;
        }

        progress.setMessage("Actualizando datos...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        constructDataRequest();

        jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL_BASE + URL_COMPLEMENTO,
                dataRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejo de la respuesta
                        Log.d(TAG, "Respuesta en JSON editar perfil: " + response);

                        updateUserUtilities();

                        updateFireBaseUser();

                        progress.dismiss();
                        showMessge("Datos guardados con èxito");

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejo de errores
                        progress.dismiss();
                        showMessge("Ocurrio un error, por favor intente nuevamente");
                        Log.d(TAG, "Error en la respuesta editar perfil: " + error.getMessage());
                    }
                });

        // Add request to de queue
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsArrayRequest);

    }

    private void showMessge(String msg) {

        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    public void updateFireBaseUser(){

        Log.d(TAG,"updateFireBaseUser");

        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").child(userUtilities.getUid()).child("data").child("userName").setValue(nombreField.getText().toString());
        databaseReference.child("users").child(userUtilities.getUid()).child("data").child("email").setValue(emailField.getText().toString());
        databaseReference.child("users").child(userUtilities.getUid()).child("data").child("lastname").setValue(apellidosField.getText().toString());

    }

    public void updateUserUtilities(){

        userUtilities.setUserName(nombreField.getText().toString());
        userUtilities.setEmail(emailField.getText().toString());
        userUtilities.setLastname(apellidosField.getText().toString());
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
