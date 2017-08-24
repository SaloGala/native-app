package com.inflexionlabs.goparken;

/**
 * Created by odalysmarronsanchez on 22/08/17.
 */

class UserUtilities {
    private static final UserUtilities ourInstance = new UserUtilities();

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

    static UserUtilities getInstance() {
        return ourInstance;
    }

    private UserUtilities() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
