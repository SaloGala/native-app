package com.inflexionlabs.goparken;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG="EmailPasword";

    private EditText emailField;
    private EditText emailFieldR;

    private EditText passwordField;
    private EditText passwordFieldR;

    private EditText nombreField;
    private EditText apellidosField;

    private FirebaseAuth mAuth;

    String URL_BASE = "http://ec2-107-20-100-168.compute-1.amazonaws.com/api/v1/";

    String URL_COMPLEMENTO="User/AddFromEmail";

    User objectUser = new User();

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("NexaLight.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setContentView(R.layout.activity_register);

        emailField = (EditText) findViewById(R.id.editTxtCorreo);
        emailFieldR = (EditText) findViewById(R.id.editTxtCorreoR);

        passwordField = (EditText) findViewById(R.id.editTxtPass);
        passwordFieldR = (EditText) findViewById(R.id.editTxtPassR);

        nombreField = (EditText) findViewById(R.id.editTxtNombre);
        apellidosField = (EditText) findViewById(R.id.editTxtApellidos);

        mAuth = FirebaseAuth.getInstance();

        progress = new ProgressDialog(this);

        initializeComponents();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initializeComponents() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

        String password = passwordField.getText().toString();
        if(TextUtils.isEmpty(password)){
            passwordField.setError("Campo requerido");
            valid = false;
        }else{
            if(password.length()<6){
                passwordField.setError("Ingresa una contraseña de por lo menos 6 caracteres");
                valid = false;
            }
        }

        String passwordR = passwordFieldR.getText().toString();
        if(TextUtils.isEmpty(passwordR)){
            passwordFieldR.setError("Campo requerido");
            valid = false;
        }else{
            if(passwordR.length()<6){
                passwordFieldR.setError("Ingresa una contraseña de por lo menos 6 caracteres");
                valid = false;
            }
        }

        if (!password.equals(passwordR)) {
            passwordField.setError("Las contraseñas deben coincidir");
            valid = false;
        }


        return valid;
    }

    public final static boolean validEmail(CharSequence email) {

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

    public void addFromEmail(String nombre, String apellidos, final String email, final String password){

        Log.d(TAG,"addFromEmail:"+email);

        if(!validateForm()){
            return;
        }

        // Mapeo de los pares clave-valor
        HashMap<String, String> data = new HashMap();
        data.put("USER_NAME", nombre);
        data.put("USER_LASTNAME", apellidos);
        data.put("USER_PHONE","0000000000");
        data.put("USER_EMAIL", email);
        data.put("USER_PASS",password);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL_BASE + URL_COMPLEMENTO,
                new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejo de la respuesta
                        Log.d(TAG, "Respuesta en JSON: " + response);


                        try {
                            JSONObject content = response.getJSONObject("content");
                            JSONObject user = content.getJSONObject("user");

                            objectUser.setId(user.getInt("id"));
                            objectUser.setUserName(user.getString("name"));
                            objectUser.setEmail(user.getString("email"));
                            objectUser.setPassword(password);
                            objectUser.setToken(user.getString("token"));
                            objectUser.setStatus("");
                            objectUser.setType(user.getString("type"));
                            objectUser.setAccess_token("");
                            objectUser.setNickname(user.getString("nickname"));
                            objectUser.setFull_name(user.getString("full_name"));
                            objectUser.setAvatar("");
                            objectUser.setDetails("");
                            objectUser.setSocial(user.getString("social"));
                            objectUser.setSocial_type("");
                            objectUser.setSocial_id("0");
                            objectUser.setSocial_json(user.getString("social_json"));
                            objectUser.setSocial_email(user.getString("social_email"));
                            objectUser.setLastname(user.getString("lastname"));
                            objectUser.setPhone(user.getString("phone"));
                            objectUser.setPostalcode("");
                            objectUser.setState("");
                            objectUser.setCity("");
                            objectUser.setOpenpay_id("");
                            objectUser.setRemember_token("");
                            objectUser.setAddress("");
                            objectUser.setFacebook_share("");
                            objectUser.setProvider("emailAndPassword");
                            objectUser.setPhotoUrl("https://firebasestorage.googleapis.com/v0/b/goparkennativa-cfff1.appspot.com/o/perfil_imagen%402x.png?alt=media&token=0104417e-f8d8-4b1d-8712-ea90e18ecadd");

                            createAccount(email,password);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejo de errores
                        Log.d(TAG, "Error Respuesta en JSON: " + error.getMessage());
                    }
                });

        // Add request to de queue
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsArrayRequest);

    }

    private void createAccount(String email, String password){
        Log.d(TAG,"createAccount:"+email);
        if(!validateForm()){
            return;
        }

        progress.setMessage("Guardando...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            objectUser.setUid(user.getUid());
                            writeNewUser(objectUser);
                            sendEmailVerification();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed."+ task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        // [END create_user_with_email]

    }

    private void sendEmailVerification() {

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button

                        if (task.isSuccessful()) {
                            progress.dismiss();

                            Toast.makeText(RegisterActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            progress.dismiss();

                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(RegisterActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    private void writeNewUser(User newUser){

        final DatabaseReference dataBaseRef = FirebaseDatabase.getInstance().getReference();

        dataBaseRef.child("users").child(newUser.getUid()).child("data").setValue(newUser);

    }

    public void send(View view) {
        // Do something in response to button
        Log.d(TAG, "send method");

        addFromEmail(nombreField.getText().toString(),apellidosField.getText().toString(),emailField.getText().toString(),passwordField.getText().toString());

    }

}
