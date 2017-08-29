package com.inflexionlabs.goparken;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
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

public class PerfilFragment extends Fragment {

    public View mView;

    static final String TAG = "ProfileActivity";

    TextView txtUserName;
    TextView txtUserEmail;
    ImageView imgUserPhoto;
    Button btnEditarPerfil;
    Button btnAddCard;
    Button btnAddAuto;
    Button btnLogOut;
    private DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;


    String photoUrl;

    UserUtilities userUtilities = UserUtilities.getInstance();

    public PerfilFragment() {

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
        btnLogOut = (Button) mView.findViewById(R.id.btnCerrarSesion);

        btnEditarPerfil.setOnClickListener(btnListener);
        btnAddAuto.setOnClickListener(btnListener);
        btnAddCard.setOnClickListener(btnListener);
        btnLogOut.setOnClickListener(btnListener);

        initializeUserInfo();
        return mView;
    }

    private void initializeUserInfo() {

        mFirebaseAuth = FirebaseAuth.getInstance();

        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        //Traer de la base
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference mUserDetail = mDatabaseReference.child("users").child(mFirebaseUser.getUid() + "/data");

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                userUtilities.setId(user.getId());
                userUtilities.setUid(user.getUid());
                userUtilities.setUserName(user.getUserName());
                userUtilities.setEmail(user.getEmail());
                userUtilities.setPassword(user.getPassword());
                userUtilities.setToken(user.getToken());
                userUtilities.setStatus(user.getStatus());
                userUtilities.setType(user.getType());
                userUtilities.setAccess_token(user.getAccess_token());
                userUtilities.setNickname(user.getNickname());
                userUtilities.setFull_name(user.getNickname());
                userUtilities.setAvatar(user.getAvatar());
                userUtilities.setDetails(user.getDetails());
                userUtilities.setSocial_id(user.getSocial());
                userUtilities.setSocial_type(user.getSocial_type());
                userUtilities.setSocial_id(user.getSocial_id());
                userUtilities.setSocial_json(user.getSocial_json());
                userUtilities.setSocial_email(user.getSocial_email());
                userUtilities.setLastname(user.getLastname());
                userUtilities.setPhone(user.getPhone());
                userUtilities.setPostalcode(user.getPostalcode());
                userUtilities.setState(user.getState());
                userUtilities.setCity(user.getCity());
                userUtilities.setOpenpay_id(user.getOpenpay_id());
                userUtilities.setRemember_token(user.getRemember_token());
                userUtilities.setAddress(user.getAddress());
                userUtilities.setFacebook_share(user.getFacebook_share());
                userUtilities.setProvider(user.getProvider());
                userUtilities.setPhotoUrl(user.getPhotoUrl());


                if (userUtilities.getProvider().equals("password")) {
                    photoUrl = "https://firebasestorage.googleapis.com/v0/b/goparkennativa-cfff1.appspot.com/o/perfil_imagen%402x.png?alt=media&token=0104417e-f8d8-4b1d-8712-ea90e18ecadd";

                } else {
                    photoUrl = userUtilities.getPhotoUrl().toString();
                }

                txtUserName.setText(userUtilities.getUserName() + " " + userUtilities.getLastname());
                txtUserEmail.setText(userUtilities.getEmail());

                Picasso.with(getContext()).load(photoUrl).noFade().fit().into(imgUserPhoto);
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
                    showCarsList();
                    break;

                case R.id.btnVerMetodosPago:
                    showCardsList();
                    break;
                case R.id.btnCerrarSesion:
                    logOut();
                    break;

            }

        }
    };

    public void logOut() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String providerId = "";

        for (UserInfo profile : user.getProviderData()) {
            providerId = profile.getProviderId();
        }

        mFirebaseAuth.signOut();

        if (providerId.equals("facebook.com")) {
            LoginManager.getInstance().logOut();
        }

        goLoginScreen();
    }

    private void goLoginScreen() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }


    private void editProfileForm() {

        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
        startActivity(intent);
    }

    private void showCarsList() {
        Intent intent = new Intent(getActivity(), AutosListActivity.class);
        startActivity(intent);
    }

    private void showCardsList() {
        Intent intent = new Intent(getActivity(), CardsListActivity.class);
        startActivity(intent);
    }

}
