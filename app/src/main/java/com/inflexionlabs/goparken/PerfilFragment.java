package com.inflexionlabs.goparken;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * Created by odalysmarronsanchez on 23/08/17.
 */

public class PerfilFragment extends Fragment{

    public View mView;

    static final String TAG = "ProfileActivity";

    TextView txtUserName;
    TextView txtUserEmail;
    ImageView imgUserPhoto;
    Button btnEditarPerfil;
    Button btnAddCard;
    Button btnAddAuto;

    private DatabaseReference mDatabaseReference;

    FirebaseUser currentUser;

    String provider;
    String photoUrl;

    UserUtilities userUtilities = UserUtilities.getInstance();

    public PerfilFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.perfil_fragment, container, false);

        txtUserName = (TextView) mView.findViewById(R.id.txtUserName);
        txtUserEmail = (TextView) mView.findViewById(R.id.txtUserEmail);
        imgUserPhoto = (ImageView) mView.findViewById(R.id.imgUserPhoto);

        btnEditarPerfil = (Button) mView.findViewById(R.id.btnEditarPerfil);
        btnAddAuto = (Button) mView.findViewById(R.id.btnVerVehiculos);
        btnAddCard = (Button) mView.findViewById(R.id.btnVerMetodosPago);

        btnEditarPerfil.setOnClickListener(btnListener);
        btnAddAuto.setOnClickListener(btnListener);
        btnAddCard.setOnClickListener(btnListener);
        //showInfoUser();
        getInfoUser();

        return mView;
    }

    private void showInfoUser(){

        for (UserInfo profile: currentUser.getProviderData()){
            provider = profile.getProviderId();
        }

        if(provider.equals("password")){
            photoUrl = "https://firebasestorage.googleapis.com/v0/b/goparkennativa-cfff1.appspot.com/o/perfil_imagen%402x.png?alt=media&token=0104417e-f8d8-4b1d-8712-ea90e18ecadd";

        }else{
            photoUrl = currentUser.getPhotoUrl().toString();
        }

        txtUserName.setText(currentUser.getDisplayName());
        txtUserEmail.setText(currentUser.getEmail());
        Picasso.with(getContext()).load(photoUrl).fit().into(imgUserPhoto);
    }

    private void getInfoUser(){


        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference mUserDetail = mDatabaseReference.child("users").child(currentUser.getUid()+"/data");

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                txtUserName.setText(user.getUserName());
                txtUserEmail.setText(user.getEmail());
                Picasso.with(getContext()).load(user.getPhotoUrl()).fit().into(imgUserPhoto);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }


        };

        mUserDetail.addListenerForSingleValueEvent(userListener);

    }

    private View.OnClickListener btnListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.btnEditarPerfil:
                    editProfileForm();
                    break;

                case R.id.btnVerVehiculos:
                    addAuto();
                    break;

                case R.id.btnVerMetodosPago:
                    addCard();
                    break;

            }

        }
    };


    private void editProfileForm() {

        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
        startActivity(intent);
    }

    private void addAuto(){
        Intent intent = new Intent(getActivity(),AddVehicleActivity.class);
        startActivity(intent);
    }

    private void addCard(){
        Intent intent = new Intent(getActivity(),AddCardActivity.class);
        startActivity(intent);
    }

}
