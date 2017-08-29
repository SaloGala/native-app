package com.inflexionlabs.goparken;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

public class AutosListActivity extends AppCompatActivity {

    private final String TAG = "AutosListActivity";

    private DatabaseReference mDatabaseReference;
    private DatabaseReference userVehiclesReference;

    private FirebaseListAdapter<Auto> mAdapter;

    ListView lstAutosList;

    TextView txtUsuario;
    ImageView imgUserPhoto;
    String photoUrl;

    Button btnAddCar;

    UserUtilities userUtilities = UserUtilities.getInstance();
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autos_list);

        initializeComponents();
        setupView();
    }

    private void initializeComponents() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        lstAutosList = (ListView)findViewById(R.id.lstCars);

        btnAddCar = (Button) findViewById(R.id.btnAddCar);

        btnAddCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddCarScreen();
            }
        });


        txtUsuario = (TextView) findViewById(R.id.txtUsuarioN);
        txtUsuario.setText(userUtilities.getUserName());

        imgUserPhoto = (ImageView) findViewById(R.id.imgPicture);

        if (userUtilities.getProvider().equals("password")) {
            photoUrl = "https://firebasestorage.googleapis.com/v0/b/goparkennativa-cfff1.appspot.com/o/perfil_imagen%402x.png?alt=media&token=0104417e-f8d8-4b1d-8712-ea90e18ecadd";

        } else {
            photoUrl = userUtilities.getPhotoUrl().toString();
        }
        Picasso.with(this).load(photoUrl).noFade().fit().into(imgUserPhoto);

        context = this;


    }

    private void goToAddCarScreen() {

        Intent intent = new Intent(this,AddVehicleActivity.class);
        startActivity(intent);
    }

    public void setupView(){

        userVehiclesReference = mDatabaseReference.child("users_vehicles").child(userUtilities.getUid());

        mAdapter = new FirebaseListAdapter<Auto>(this, Auto.class, R.layout.auto_list, userVehiclesReference) {

            @Override
            protected void populateView(final View view, Auto model, final int position) {
                final String key = mAdapter.getRef(position).getKey();

                userVehiclesReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        View currentView = lstAutosList.getChildAt(position - lstAutosList.getFirstVisiblePosition());

                        String placa = dataSnapshot.child("placa").getValue().toString();
                        String submarca = dataSnapshot.child("submarca").getValue().toString();

                        if (placa != null && placa.length() > 0) {
                            ((TextView) view.findViewById(R.id.auto_placa)).setText(placa);
                        } else {
                            ((TextView) view.findViewById(R.id.auto_placa)).setText("SIN PLACA");
                        }

                        if (submarca != null && submarca.length() > 0) {
                            ((TextView) view.findViewById(R.id.auto_submarca)).setText(submarca);
                        } else {
                            ((TextView) view.findViewById(R.id.auto_submarca)).setText("SIM SUBMARCA");
                        }

                        Button btnEliminar = (Button) view.findViewById(R.id.btnEliminarAuto);

                        btnEliminar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                final Dialog dialog = new Dialog(context);
                                dialog.setContentView(R.layout.alert_dialog);
                                dialog.setTitle("Aviso");


                                TextView txtTexto = (TextView) dialog.findViewById(R.id.txtTexto);
                                Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
                                Button btnCancelar = (Button) dialog.findViewById(R.id.btnCancelar);

                                txtTexto.setText("¿Seguro que quieres eliminar esta auto?");

                                btnAceptar.setText("Aceptar");
                                btnCancelar.setText("Cancelar");


                                btnAceptar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        userVehiclesReference.child(key).removeValue();
                                        Toast.makeText(context,"Auto eliminado con éxito",Toast.LENGTH_SHORT).show();
                                    }
                                });

                                btnCancelar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();

                                    }
                                });

                                dialog.show();

                                /*userVehiclesReference.child(key).removeValue();
                                Toast.makeText(getApplicationContext(),"Auto eliminado con éxito",Toast.LENGTH_SHORT).show();*/
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        lstAutosList.setAdapter(mAdapter);

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
