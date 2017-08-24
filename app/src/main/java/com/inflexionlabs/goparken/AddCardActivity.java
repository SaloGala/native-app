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

import mx.openpay.android.Openpay;
import mx.openpay.android.OperationCallBack;
import mx.openpay.android.OperationResult;
import mx.openpay.android.exceptions.OpenpayServiceException;
import mx.openpay.android.exceptions.ServiceUnavailableException;
import mx.openpay.android.model.Card;
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


        openpay.createCard(card,this);
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

        Log.d(TAG,"onSucces: "+ operationResult.getResult());

    }
}
