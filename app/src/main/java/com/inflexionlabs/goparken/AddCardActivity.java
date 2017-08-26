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

import mx.openpay.android.Openpay;
import mx.openpay.android.OperationCallBack;
import mx.openpay.android.OperationResult;
import mx.openpay.android.exceptions.OpenpayServiceException;
import mx.openpay.android.exceptions.ServiceUnavailableException;
import mx.openpay.android.model.Address;
import mx.openpay.android.model.Card;
import mx.openpay.android.model.Token;
import mx.openpay.android.validation.CardValidator;


public class AddCardActivity extends AppCompatActivity implements OperationCallBack {

    final private String TAG = "AddCardActivity";

    UserUtilities userUtilities = UserUtilities.getInstance();

    EditText editTxtDireccion;
    EditText editTxtCodPostal;
    EditText editTxtEstado;
    EditText editTxtCiudad;
    EditText editTxtNonbreT;
    EditText editTxtNoCard;
    EditText editTxtCVV;
    EditText editTxtYY;
    EditText editTxtMM;

    Button btnSaveCard;

    private final String MERCHANT_ID = "mtfur53iopbr7ceh01ro";
    private final String PRIVATE_API_KEY = "sk_99ab173dcfe944e28cc048f5534eb857";
    private final String PUBLIC_API_KEY = "pk_7226b0afacd546e0bb883e90945bdb0a";
    boolean productionMode = false;
    Openpay openpay;
    String deviceIdString;
    Token tokenResponse;
    Card cardResponse;

    String URL_BASE = "http://ec2-107-20-100-168.compute-1.amazonaws.com/api/v1/";
    String URL_COMPLEMENTO="OpenPay/Card";
    JSONObject dataRequest = new JSONObject();
    JsonObjectRequest jsArrayRequest;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

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

        editTxtDireccion = (EditText) findViewById(R.id.editDireccion);
        editTxtCodPostal = (EditText) findViewById(R.id.editPostal);
        editTxtEstado = (EditText) findViewById(R.id.editEdo);
        editTxtCiudad = (EditText) findViewById(R.id.editCd);

        editTxtNonbreT = (EditText) findViewById(R.id.editTitular);
        editTxtNoCard = (EditText) findViewById(R.id.editNoCard);
        editTxtCVV = (EditText) findViewById(R.id.editCVV);
        editTxtYY = (EditText) findViewById(R.id.editYY);
        editTxtMM = (EditText) findViewById(R.id.editMM);

        showInfoUser();

        btnSaveCard = (Button) findViewById(R.id.btnGuardarCard);

        btnSaveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCard();
            }
        });

        openpay = new Openpay(MERCHANT_ID,PRIVATE_API_KEY,productionMode);
        deviceIdString = openpay.getDeviceCollectorDefaultImpl().setup(this);

    }

    private void saveCard() {

        if(!validateForm()){
            return;
        }

        Card card = new Card();
        card.holderName(editTxtNonbreT.getText().toString());
        card.cardNumber(editTxtNoCard.getText().toString());
        card.cvv2(editTxtCVV.getText().toString());
        card.expirationMonth(Integer.parseInt(editTxtMM.getText().toString()));
        card.expirationYear(Integer.parseInt(editTxtYY.getText().toString()));

        Address address = new Address();
        address.line1(editTxtDireccion.getText().toString());
        address.postalCode(editTxtCodPostal.getText().toString());
        address.state(editTxtEstado.getText().toString());
        address.city(editTxtCiudad.getText().toString());
        address.line2("");
        address.line3("");
        address.countryCode("MX");

        card.address(address);

        //openpay.createCard(card,this);
        openpay.createToken(card,this);

    }

    public void showInfoUser(){

        editTxtDireccion.setText(userUtilities.getAddress());
        editTxtCodPostal.setText(userUtilities.getPostalcode());
        editTxtEstado.setText(userUtilities.getState());
        editTxtCiudad.setText(userUtilities.getCity());

    }

    private boolean validateForm(){
        boolean valid = true;

        String direccion = editTxtDireccion.getText().toString();
        if(TextUtils.isEmpty(direccion)){
            editTxtDireccion.setError("Campo requerido");
            valid = false;
        }else{
            editTxtDireccion.setError(null);
        }

        String codigoPostal = editTxtCodPostal.getText().toString();
        if(TextUtils.isEmpty(codigoPostal)){
            editTxtCodPostal.setError("Campo requerido");
            valid = false;
        }else{
            if(codigoPostal.length()!=5){
                editTxtCodPostal.setError("El código postal debe ser de 5 caracteres");
                valid = false;

            }
        }

        String estado = editTxtEstado.getText().toString();
        if(TextUtils.isEmpty(estado)){
            editTxtEstado.setError("Campo requerido");
            valid = false;
        }else{
            editTxtEstado.setError(null);
        }

        String ciudad = editTxtCiudad.getText().toString();
        if(TextUtils.isEmpty(ciudad)){
            editTxtCiudad.setError("Campo requerido");
            valid = false;
        }else{
            editTxtCiudad.setError(null);
        }

        String card = editTxtNoCard.getText().toString();
        if(TextUtils.isEmpty(card)){
            editTxtNoCard.setError("Campo requerido");
            valid = false;
        }else{
            if(!CardValidator.validateNumber(card)){
                editTxtNoCard.setError("La tarjeta no es válida");
                valid = false;
            }
        }

        String cvv = editTxtCVV.getText().toString();
        if(TextUtils.isEmpty(cvv)){
            editTxtCVV.setError("Campo requerido");
            valid = false;
        }else{
            if(!CardValidator.validateCVV(cvv,card)){
                editTxtCVV.setError("El CVV no es válido");
                valid = false;
            }
        }

        String year = editTxtYY.getText().toString();
        String month = editTxtMM.getText().toString();

        if(TextUtils.isEmpty(month)){
            editTxtMM.setError("Campo requerido");
            valid = false;
        }else{

            if(TextUtils.isEmpty(year)){
                editTxtYY.setError("Campo requerido");
                valid = false;
            }else{
                if(!CardValidator.validateExpiryDate(Integer.parseInt(month), Integer.parseInt(year))){
                    editTxtMM.setError("Mes no válido");
                    editTxtYY.setError("Año no válido");
                    valid = false;
                }
            }

        }


        return valid;
    }

    public void constructDataRequest(){

        try {

            dataRequest.put("USER_NAME", userUtilities.getUserName());
            dataRequest.put("USER_EMAIL", userUtilities.getEmail());

            if(userUtilities.getLastname().isEmpty()){

                dataRequest.put("USER_LASTNAME", "-");
            }else{

                dataRequest.put("USER_LASTNAME", userUtilities.getLastname());
            }

            if(userUtilities.getPhone().isEmpty()){

                dataRequest.put("USER_PHONE","-");
            }else{

                dataRequest.put("USER_PHONE",userUtilities.getPhone());
            }

            dataRequest.put("USER_ADDRESS",userUtilities.getAddress());
            dataRequest.put("USER_POSTALCODE",userUtilities.getPostalcode());
            dataRequest.put("USER_STATE",userUtilities.getState());
            dataRequest.put("USER_CITY",userUtilities.getCity());

            dataRequest.put("deviceSessionId", deviceIdString);

            dataRequest.put("OPENPAY_TOKEN", tokenResponse.getId());
            dataRequest.put("OPENPAY_CARD",cardResponse);
            dataRequest.put("OPENPAY_MASK",cardResponse.getCardNumber());

            dataRequest.put("token", userUtilities.getToken());

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void updateUserUtilities(){

        userUtilities.setAddress(editTxtDireccion.getText().toString());
        userUtilities.setPostalcode(editTxtCodPostal.getText().toString());
        userUtilities.setState(editTxtEstado.getText().toString());
        userUtilities.setCity(editTxtCiudad.getText().toString());
    }

    private void showMessge(String msg) {

        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    public void updateFireBaseUser(){

        Log.d(TAG,"updateFireBaseUser");

        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").child(userUtilities.getUid()).child("data").child("address").setValue(editTxtDireccion.getText().toString());
        databaseReference.child("users").child(userUtilities.getUid()).child("data").child("postalcode").setValue(editTxtCodPostal.getText().toString());
        databaseReference.child("users").child(userUtilities.getUid()).child("data").child("state").setValue(editTxtEstado.getText().toString());
        databaseReference.child("users").child(userUtilities.getUid()).child("data").child("city").setValue(editTxtCiudad.getText().toString());

        //method payment
        /*databaseReference.child("users").child(userUtilities.getUid()).child("cards").child(tokenResponse.getId()).child("openpay_token").setValue(tokenResponse.getId());
        databaseReference.child("users").child(userUtilities.getUid()).child("cards").child(tokenResponse.getId()).child("openpay_card_mask").setValue(cardResponse.getCardNumber());
        databaseReference.child("users").child(userUtilities.getUid()).child("cards").child(tokenResponse.getId()).child("response_from_openpay").setValue(cardResponse);
        databaseReference.child("users").child(userUtilities.getUid()).child("cards").child(tokenResponse.getId()).child("deviceSessionId").setValue(deviceIdString);
        databaseReference.child("users").child(userUtilities.getUid()).child("cards").child(tokenResponse.getId()).child("status").setValue("active");
        databaseReference.child("users").child(userUtilities.getUid()).child("cards").child(tokenResponse.getId()).child("default").setValue(1);*/

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onError(OpenpayServiceException error) {

        error.printStackTrace();
        Log.d(TAG,"onError: "+error.getErrorCode());

    }

    @Override
    public void onCommunicationError(ServiceUnavailableException error) {

        Log.d(TAG,"onCommunicationError: "+error.getMessage());
    }

    @Override
    public void onSuccess(OperationResult operationResult) {

        updateUserUtilities();

        tokenResponse  = (Token)operationResult.getResult();
        cardResponse = tokenResponse.getCard();

        constructDataRequest();

        jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL_BASE + URL_COMPLEMENTO,
                dataRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejo de la respuesta
                        Log.d(TAG, "Respuesta en JSON: " + response);

                        updateFireBaseUser();

                        showMessge("Datos guardados con èxito");

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
}
