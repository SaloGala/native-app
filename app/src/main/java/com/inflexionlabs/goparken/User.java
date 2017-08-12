package com.inflexionlabs.goparken;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

/**
 * Created by odalysmarronsanchez on 19/07/17.
 */

public class User {
    private String uid;
    private String userName;
    private String email;
    private String provider;
    private String photoUrl;

    public User(){
        FirebaseUser currentUser =FirebaseAuth.getInstance().getCurrentUser();
        this.uid = currentUser.getUid();
        this.userName = currentUser.getDisplayName();
        this.email = currentUser.getEmail();

        for (UserInfo profile: currentUser.getProviderData()){
            this.provider = profile.getProviderId();
        }

        if(this.provider.equals("password")){
            this.photoUrl = "https://firebasestorage.googleapis.com/v0/b/goparkennativa-cfff1.appspot.com/o/perfil_imagen%402x.png?alt=media&token=0104417e-f8d8-4b1d-8712-ea90e18ecadd";
            this.provider = "emailAndPassword";
        }else{
            this.photoUrl = currentUser.getPhotoUrl().toString();
        }

    }

    public void setUid(String uid){
        this.uid = uid;
    }

    public String getUid(){
        return uid;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getUserName(){
        return userName;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail(){
        return email;
    }

    public void setProvider(String provider){
        this.provider = provider;
    }

    public String getProvider(){
        return provider;
    }

    public void setPhotoUrl(String photoUrl){
        this.photoUrl = photoUrl;
    }

    public String getPhotoUrl(){
        return photoUrl;
    }

}
