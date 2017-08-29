package com.inflexionlabs.goparken;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AutosListActivity extends AppCompatActivity {

    private final String TAG = "AutosListActivity";

    private DatabaseReference mDataBaseReference;

    private FirebaseRecyclerAdapter<Auto,AutoViewHolder> mAdapter;

    ListView lsyAutosList;

    TextView txtUsuario;
    ImageView imgUserPhoto;
    String photoUrl;

    Button btnAddCar;

    UserUtilities userUtilities = UserUtilities.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autos_list);

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

        mDataBaseReference = FirebaseDatabase.getInstance().getReference();

        lsyAutosList = (ListView)findViewById(R.id.lstCars);

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



    }

    private void goToAddCarScreen() {

        Intent intent = new Intent(this,AddVehicleActivity.class);
        startActivity(intent);
    }

    public void setupView(){

        Query autosQuery = getQuery(mDataBaseReference);

        mAdapter = new FirebaseRecyclerAdapter<Auto, AutoViewHolder>(Auto.class,R.layout.auto_list,AutoViewHolder.class,autosQuery) {
            @Override
            protected void populateViewHolder(AutoViewHolder viewHolder, Auto model, int position) {
                final DatabaseReference autoRef = getRef(position);

                final String autoKey = autoRef.getKey();

                viewHolder.bindToAuto(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(),"Borrar auto",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        lsyAutosList.setAdapter((ListAdapter) mAdapter);

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

    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child("user-posts")
                .child(userUtilities.getUid());
    }
}
