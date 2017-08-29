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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddVehicleActivity extends AppCompatActivity {

    private final String TAG = "AddVehicleActivity";

    UserUtilities userUtilities = UserUtilities.getInstance();

    DatabaseReference databaseReference;

    EditText editNombreAuto;
    EditText editPlaca;
    EditText editMarca;
    EditText editSubmarca;
    EditText editYear;

    Button btnAddCar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

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

        editNombreAuto = (EditText) findViewById(R.id.editNombreAuto);
        editPlaca = (EditText) findViewById(R.id.editPlaca);
        editMarca = (EditText) findViewById(R.id.editMarca);
        editSubmarca = (EditText) findViewById(R.id.editSubmarca);
        editYear = (EditText) findViewById(R.id.editNombreAuto);

        btnAddCar = (Button) findViewById(R.id.btnGuardarAuto);

        btnAddCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarAuto();
            }
        });

    }

    private boolean validateForm(){
        boolean valid = true;

        String nombreAuto = editNombreAuto.getText().toString();
        if(TextUtils.isEmpty(nombreAuto)){
            editPlaca.setText("");
        }

        String placa = editPlaca.getText().toString();
        if(TextUtils.isEmpty(placa)){
            editPlaca.setError("Campo requerido");
            valid = false;
        }else{
            editPlaca.setError(null);
        }

        String marca = editMarca.getText().toString();
        if(TextUtils.isEmpty(marca)){
            editMarca.setError("Campo requerido");
            valid = false;
        }else{
            editMarca.setError(null);
        }

        String submarca = editSubmarca.getText().toString();
        if(TextUtils.isEmpty(submarca)){
            editSubmarca.setError("Campo requerido");
            valid = false;
        }else{
            editSubmarca.setError(null);
        }

        String year = editYear.getText().toString();
        if(TextUtils.isEmpty(year)){
            editYear.setError("Campo requerido");
            valid = false;
        }else{
            editYear.setError(null);
        }

        return valid;
    }

    public void guardarAuto(){

        if(!validateForm()){
            return;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();

        String key = databaseReference.child("users_vehicles").push().getKey();

        Auto auto = new Auto(userUtilities.getUid(),
                editNombreAuto.getText().toString(),
                editPlaca.getText().toString(),
                editMarca.getText().toString(),
                editSubmarca.getText().toString(),
                editYear.getText().toString()
                );
        Map<String, Object> autoValues = auto.toMap();

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/users_vehicles/" + userUtilities.getUid() + "/" + key, autoValues);

        databaseReference.updateChildren(childUpdates);

        Toast.makeText(this,"Auto agregado con exito",Toast.LENGTH_LONG).show();
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
