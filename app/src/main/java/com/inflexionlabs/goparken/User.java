package com.inflexionlabs.goparken;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

/**
 * Created by odalysmarronsanchez on 19/07/17.
 */

public class User {

    private int id;
    private String uid;
    private String userName;
    private String email;
    private String password;
    private String token;
    private String status;
    private String type;
    private String access_token;
    private String nickname;
    private String full_name;
    private String avatar;
    private String details;
    private String social;
    private String social_type;
    private String social_id;
    private String social_json;
    private String social_email;
    private String lastname;
    private String phone;
    private String postalcode;
    private String state;
    private String city;
    private String openpay_id;
    private String remember_token;
    private String address;
    private String facebook_share;
    private String provider;
    private String photoUrl;

    public User(){

    }

    public User(int id, String uid, String userName, String email, String password, String token, String status, String type, String access_token, String nickname, String full_name, String avatar, String details, String social, String social_type, String social_id, String social_json, String social_email, String lastname, String phone, String postalcode, String state, String city, String openpay_id, String remember_token, String address, String facebook_share, String provider, String photoUrl) {
        this.id = id;
        this.uid = uid;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.token = token;
        this.status = status;
        this.type = type;
        this.access_token = access_token;
        this.nickname = nickname;
        this.full_name = full_name;
        this.avatar = avatar;
        this.details = details;
        this.social = social;
        this.social_type = social_type;
        this.social_id = social_id;
        this.social_json = social_json;
        this.social_email = social_email;
        this.lastname = lastname;
        this.phone = phone;
        this.postalcode = postalcode;
        this.state = state;
        this.city = city;
        this.openpay_id = openpay_id;
        this.remember_token = remember_token;
        this.address = address;
        this.facebook_share = facebook_share;
        this.provider = provider;
        this.photoUrl = photoUrl;
    }

    /*public User(){
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

    }*/

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
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

    public void setPassword(String password){
        this.password = password;
    }

    public String getPassword(){
        return password;
    }

    public void setToken(String token){
        this.token = token;
    }

    public String getToken(){
        return token;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getSocial() {
        return social;
    }

    public void setSocial(String social) {
        this.social = social;
    }

    public String getSocial_type() {
        return social_type;
    }

    public void setSocial_type(String social_type) {
        this.social_type = social_type;
    }

    public String getSocial_id() {
        return social_id;
    }

    public void setSocial_id(String social_id) {
        this.social_id = social_id;
    }

    public String getSocial_json() {
        return social_json;
    }

    public void setSocial_json(String social_json) {
        this.social_json = social_json;
    }

    public String getSocial_email() {
        return social_email;
    }

    public void setSocial_email(String social_email) {
        this.social_email = social_email;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOpenpay_id() {
        return openpay_id;
    }

    public void setOpenpay_id(String openpay_id) {
        this.openpay_id = openpay_id;
    }

    public String getRemember_token() {
        return remember_token;
    }

    public void setRemember_token(String remember_token) {
        this.remember_token = remember_token;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFacebook_share() {
        return facebook_share;
    }

    public void setFacebook_share(String facebook_share) {
        this.facebook_share = facebook_share;
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
