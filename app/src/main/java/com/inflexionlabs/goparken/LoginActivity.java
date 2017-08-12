package com.inflexionlabs.goparken;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "FacebookLogIn";

    private LoginButton facebookLoginButton;
    private CallbackManager callbackManager;

    private Button BTNfacebookLogin;

    private FirebaseAuth firebaseAuth;

    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        //getSupportActionBar().hide(); // --> hide bar from Activity
        //icons4android.com
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton = (LoginButton) findViewById(R.id.btnSigninWithFacebook);
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        BTNfacebookLogin = (Button) findViewById(R.id.BTNSigninWithFacebook);

        FacebookSdk.sdkInitialize(getApplicationContext());

        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this,R.string.cancel_login_facebook,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this,R.string.error_login_facebook,Toast.LENGTH_SHORT).show();

            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credencial = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credencial).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Log.w(TAG, "Facebook log in error", task.getException());
                    Toast.makeText(LoginActivity.this,R.string.error_login_facebook,Toast.LENGTH_SHORT).show();
                }else{
                    //goMapScreen();
                    registerEventFirebaseAnalitics("facebook_loggin","El usuario inicio sesion a traves de Facebook");

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    public void loginForm() {

        // Do something in response to button
        Intent intent = new Intent(this, LoginFormActivity.class);
        startActivity(intent);
    }

    public void register() {

        // Do something in response to button
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void registerEventFirebaseAnalitics(String name , String description){

        Bundle params = new Bundle();
        params.putString("nombre",name);
        params.putString("descripcion",description);

        mFirebaseAnalytics.logEvent("inicio_sesion",params);

    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnSignin:
                loginForm();
                break;

            case R.id.btnRegister:


                register();
                break;

            case R.id.BTNSigninWithFacebook:


                facebookLoginButton.performClick();
                break;

        }
    }

}
