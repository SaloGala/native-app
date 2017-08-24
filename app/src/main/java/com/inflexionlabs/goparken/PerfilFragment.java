package com.inflexionlabs.goparken;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

    String photoUrl;

    UserUtilities userUtilities = UserUtilities.getInstance();

    public PerfilFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

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
        showInfoUser();

        return mView;
    }

    private void showInfoUser(){

        if(userUtilities.getProvider().equals("password")){
            photoUrl = "https://firebasestorage.googleapis.com/v0/b/goparkennativa-cfff1.appspot.com/o/perfil_imagen%402x.png?alt=media&token=0104417e-f8d8-4b1d-8712-ea90e18ecadd";

        }else{
            photoUrl = userUtilities.getPhotoUrl().toString();
        }

        txtUserName.setText(userUtilities.getUserName());
        txtUserEmail.setText(userUtilities.getEmail());
        Picasso.with(getContext()).load(photoUrl).fit().into(imgUserPhoto);
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
                    showCardsList();
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

    private void showCardsList(){
        Intent intent = new Intent(getActivity(),CardsListActivity.class);
        startActivity(intent);
    }

}
