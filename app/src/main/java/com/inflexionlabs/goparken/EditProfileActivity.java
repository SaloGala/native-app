package com.inflexionlabs.goparken;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG="EditProfile";

    private EditText nombreField;
    private EditText apellidosField;

    private EditText emailField;
    private EditText emailFieldR;

    private DatabaseReference mDatabaseReference;

    FirebaseUser currentUser;

    JsonObjectRequest jsArrayRequest;

    String URL_BASE = "http://ec2-107-20-100-168.compute-1.amazonaws.com/api/v1/";

    String URL_COMPLEMENTO="User/Update";

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
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryActionBar), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emailField = (EditText) findViewById(R.id.editEmail);
        emailFieldR = (EditText) findViewById(R.id.editEmail2);

        nombreField = (EditText) findViewById(R.id.editNombre);
        apellidosField = (EditText) findViewById(R.id.editApellidos);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

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

    private void getInfoUser(){

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference mUserDetail = mDatabaseReference.child("users").child(currentUser.getUid()+"/data");

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                Log.d(TAG,"getInfoUser: "+user.getUid()+user.getEmail());

                guardarBackend(user);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }


        };

        mUserDetail.addListenerForSingleValueEvent(userListener);



    }

    public void guardarBackend(User user){

        Log.d(TAG,"guardarBackend:"+user.getEmail());

        if(!validateForm()){
            return;
        }

        // Mapeo de los pares clave-valor
        HashMap<String, String> data = new HashMap();

        data.put("USER_EMAIL", user.getEmail());
        data.put("USER_NAME", user.getUserName());
        data.put("USER_LASTNAME", user.getLastname());
        data.put("USER_PHONE",user.getPhone());
        data.put("USER_ADDRESS", user.getAddress());
        data.put("USER_POSTALCODE", user.getPostalcode());
        data.put("USER_STATE", user.getPostalcode());
        data.put("USER_CITY", user.getCity());
        //data.put("token", user.getToken());

        jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL_BASE + URL_COMPLEMENTO,
                new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejo de la respuesta
                        Log.d(TAG, "Respuesta en JSON editar perfil: " + response.);

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejo de errores
                        Log.d(TAG, "Respuesta en JSON editar perfil: " + error.getMessage());
                    }
                });

        // Add request to de queue
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsArrayRequest);

    }

    public void Guardar(View view){
        getInfoUser();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
