package com.inflexionlabs.goparken;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG="EmailPasword";

    private EditText emailField;
    private EditText emailFieldR;

    private EditText passwordField;
    private EditText passwordFieldR;

    private EditText nombreField;
    private EditText apellidosField;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailField = (EditText) findViewById(R.id.editTxtCorreo);
        emailFieldR = (EditText) findViewById(R.id.editTxtCorreoR);

        passwordField = (EditText) findViewById(R.id.editTxtPass);
        passwordFieldR = (EditText) findViewById(R.id.editTxtPassR);

        nombreField = (EditText) findViewById(R.id.editTxtNombre);
        apellidosField = (EditText) findViewById(R.id.editTxtApellidos);

        mAuth = FirebaseAuth.getInstance();

    }

    private boolean validateForm(){
        boolean valid = true;

        String nombre = nombreField.getText().toString();
        if(TextUtils.isEmpty(nombre)){
            nombreField.setError("Required.");
            valid = false;
        }else{
            nombreField.setError(null);
        }

        String apellidos = apellidosField.getText().toString();
        if(TextUtils.isEmpty(apellidos)){
            apellidosField.setError("Required.");
            valid = false;
        }else{
            apellidosField.setError(null);
        }

        String email = emailField.getText().toString();
        if(TextUtils.isEmpty(email)){
            emailField.setError("Required.");
            valid = false;
        }else{
            if(!validEmail(email)){
                emailField.setError("Email no válido");
                valid = false;
            }
        }

        String emailR = emailFieldR.getText().toString();
        if(TextUtils.isEmpty(emailR)){
            emailFieldR.setError("Required.");
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
            passwordField.setError("Required.");
            valid = false;
        }else{
            if(password.length()<6){
                passwordField.setError("Ingresa una contraseña de por lo menos 6 caracteres");
                valid = false;
            }
        }

        String passwordR = passwordFieldR.getText().toString();
        if(TextUtils.isEmpty(passwordR)){
            passwordFieldR.setError("Required.");
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

    private void createAccount(String email, String password){
        Log.d(TAG,"createAccount:"+email);
        if(!validateForm()){
            return;
        }

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            sendEmailVerification();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
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
                            Toast.makeText(RegisterActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
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

    public void send(View view) {
        // Do something in response to button
        createAccount(emailField.getText().toString(),passwordField.getText().toString());

    }

}
