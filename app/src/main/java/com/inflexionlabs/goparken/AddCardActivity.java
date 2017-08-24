package com.inflexionlabs.goparken;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import mx.openpay.android.Openpay;
import mx.openpay.android.validation.CardValidator;


public class AddCardActivity extends AppCompatActivity {

    final private String TAG = "AddCardActivity";

    UserUtilities userUtilities = UserUtilities.getInstance();

    EditText editTxtDireccion;
    EditText editTxtCodPostal;
    EditText editTxtEstado;
    EditText editTxtCiudad;
    EditText editTxtNonbreT;
    EditText editTxtNoCard;
    EditText editTxtCVV;
    EditText editTxtFechaE;

    Button btnSaveCard;

    private final String MERCHANT_ID = "mtfur53iopbr7ceh01ro";
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
        editTxtFechaE = (EditText) findViewById(R.id.editFecha);

        showInfoUser();

        btnSaveCard = (Button) findViewById(R.id.btnGuardarCard);

        btnSaveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCard();
            }
        });

        openpay = new Openpay(MERCHANT_ID,PUBLIC_API_KEY,productionMode);

    }

    private void saveCard() {

        Toast.makeText(this,"Guardar tarjeta...",Toast.LENGTH_SHORT).show();
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
                
            }
        }



        return valid;
    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
