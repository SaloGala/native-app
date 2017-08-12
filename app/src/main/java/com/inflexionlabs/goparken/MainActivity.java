package com.inflexionlabs.goparken;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.collect.HashBiMap;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.provider.Settings.Global.getString;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, MapFragment.OnFragmentInteractionListener, LocationListener  {

    private static final String TAG= "MainActivity";

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 3;

    public GoogleApiClient mGoogleApiClient;
    LocationManager locationManager;
    LocationRequest mLocationRequest;

    Marker searchLocationMarker;

    public ViewPager mViewPager;
    private Menu menu;
    private FirebaseAuth mFirebaseAuth;

    ValuesUtilities mValuesUtilities = ValuesUtilities.getInstance();;

    GooglePlayServicesLocationFromActivity googlePlayServicesLocationFromActivityCallback;
    private Boolean MapInitializedFlag = false;
    private Boolean RequestingLocationUpdatesFlag = false;

    private HashBiMap<String, Marker> parkingsMarkers = HashBiMap.create();
    GeoQuery geoQuery;
    Bitmap grayMarker;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mValuesUtilities.setMainContext(this);
        mValuesUtilities.setMainActivity(this);
        setContentView(R.layout.activity_main);

        Log.d(TAG,"onCreate Starting");

        mFirebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if(mFirebaseUser!=null){


            writeNewUser();
            initializeComponents();
            initializeApiComponents();
            initializeGraphicComponents();
            createLocationManager();


        }else{
            goLoginScreen();
        }

        //Obtain the FirebaseAnalytics instance
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


    }

    private void initializeGraphicComponents() {

        int height = 120;
        int width = 100;

        BitmapDrawable parkingBitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.pin_gris);
        Bitmap bitmapParking = parkingBitmapDrawable.getBitmap();
        grayMarker = Bitmap.createScaledBitmap(bitmapParking, width, height, false);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            logOut();
        }  else if (id == R.id.action_search_place) {
            openAutocompleteActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(this);

            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);

        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Log.e("Message", message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }


    private void initializeComponents(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        SectionsPageAdapter mSectionsPagerAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
    }


    private void initializeApiComponents(){
        FacebookSdk.sdkInitialize(getApplicationContext());

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();


    }


    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    public void createLocationManager() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mValuesUtilities.setLocationManager(locationManager);
    }

    private void goLoginScreen(){
        Intent intent = new Intent(this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public boolean isCheckLocationPermission() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                showMessageOKCancelLocation(getString(R.string.msg_localization));
                return false;
            }

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            return false;
        }

        return true;

    }

    private void showMessageOKCancelLocation(String message) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton(R.string.accept, listenerLocation)
                .setNegativeButton(R.string.cancel, listenerLocation)
                .create()
                .show();
    }

    DialogInterface.OnClickListener listenerLocation = new DialogInterface.OnClickListener() {

        final int BUTTON_NEGATIVE = -2;
        final int BUTTON_POSITIVE = -1;

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case BUTTON_NEGATIVE:
                    // int which = -2
                    dialog.dismiss();
                    break;

                case BUTTON_POSITIVE:
                    // int which = -1
                    ActivityCompat.requestPermissions(
                            MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    dialog.dismiss();
                    break;
            }
        }
    };

    public void logOut(){

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

    private void writeNewUser(){
        final DatabaseReference dataBaseRef = FirebaseDatabase.getInstance().getReference();
        final User currentUser = new User();

        dataBaseRef.child("users").child(currentUser.getUid()).child("userName").setValue(currentUser.getUserName());
        dataBaseRef.child("users").child(currentUser.getUid()).child("email").setValue(currentUser.getEmail());
        dataBaseRef.child("users").child(currentUser.getUid()).child("photoUrl").setValue(currentUser.getPhotoUrl());
        dataBaseRef.child("users").child(currentUser.getUid()).child("provider").setValue(currentUser.getProvider());

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(500);
        mLocationRequest.setFastestInterval(300);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    public void subscribeToGooglePlayServicesLocation() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates lss = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, mValuesUtilities.REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    protected void startLocationUpdates() {

        RequestingLocationUpdatesFlag = true;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        try {
            googlePlayServicesLocationFromActivityCallback = (GooglePlayServicesLocationFromActivity) fragment;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!MapInitializedFlag) {
            googlePlayServicesLocationFromActivityCallback.onLocationAcquired(null);
            MapInitializedFlag = true;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        createLocationRequest();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            googlePlayServicesLocationFromActivityCallback.onLocationAcquired(null);
            MapInitializedFlag = true;

            return;
        }

        startLocationUpdates();

        if (!MapInitializedFlag) {
            Location userLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            googlePlayServicesLocationFromActivityCallback.onLocationAcquired(userLocation);
            MapInitializedFlag = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {

                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];

                    if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {

                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                            startLocationUpdates();

                            if (!MapInitializedFlag) {
                                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                    return;
                                }
                                Location userLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                                googlePlayServicesLocationFromActivityCallback.onLocationAcquired(userLocation);
                                MapInitializedFlag = true;
                            }

                        }

                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
                            if (!showRationale) {

                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

                                builder.setTitle(R.string.msg_location_user);

                                builder.setMessage("Esto te permitirá ver los estacionamientos que hay cerca de ti.\n\n" +
                                        "Para activarlo, haz click en \"Configuración de la App\" abajo y activa Ubicación en el menú permisos.")

                                        .setPositiveButton("Configuración de la App", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                Intent intent = new Intent();
                                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                                intent.setData(Uri.parse("package:" + getBaseContext().getPackageName()));
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                                getBaseContext().startActivity(intent);

                                            }
                                        })

                                        .setNegativeButton("Ahora no", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });

                                android.support.v7.app.AlertDialog locationFinalDialog = builder.create();
                                locationFinalDialog.show();

                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if (requestCode == mValuesUtilities.REQUEST_CHECK_SETTINGS) {

            if (resultCode == Activity.RESULT_OK) {
            } else if (resultCode == Activity.RESULT_CANCELED) {
            }

        } else if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {

            if (resultCode == RESULT_OK) {

                GoogleMap tempMap = mValuesUtilities.getGoogleMap();

                if (tempMap != null) {

                    Place place = PlaceAutocomplete.getPlace(this, data);

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(place.getLatLng()).zoom(16).build();

                    tempMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    if( searchLocationMarker !=null){
                        searchLocationMarker.remove();
                    }

                    searchLocationMarker = tempMap.addMarker(new MarkerOptions()
                        .position(place.getLatLng())
                    );

                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {

            } else if (resultCode == RESULT_CANCELED) {

            }

        }
    }

    private void updateUserLocation(Location location) {

        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        if (mValuesUtilities.getUserLocation() == null) {

            CameraPosition cameraPosition = new CameraPosition.Builder().target(userLocation).zoom(19).build();
            mValuesUtilities.getGoogleMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }

        mValuesUtilities.setUserLocation(new LatLng(location.getLatitude(), location.getLongitude()));
    }


    public void searchParkings(GeoLocation searchArea){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("markers");

        GeoQueryEventListener parkingsEventListener = new GeoQueryEventListener() {

            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                LatLng markerLatLng = new LatLng(location.latitude,location.longitude);

                Marker newMarker = mValuesUtilities.getGoogleMap().addMarker(new MarkerOptions()
                        .position(markerLatLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(grayMarker))
                );

                parkingsMarkers.put(key,newMarker);
                mValuesUtilities.setParkingsMarkers(parkingsMarkers);

            }

            @Override
            public void onKeyExited(String key) {

                parkingsMarkers.get(key).remove();
                parkingsMarkers.remove(key);
                mValuesUtilities.setParkingsMarkers(parkingsMarkers);
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        };

        GeoFire geoFire = new GeoFire(ref);
        geoQuery = geoFire.queryAtLocation(searchArea,10);
        geoQuery.addGeoQueryEventListener(parkingsEventListener);

    }

    public void updateParkingsSearch(GeoLocation newGeoLocationCriteria){

        geoQuery.setCenter(newGeoLocationCriteria);
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        RequestingLocationUpdatesFlag = false;
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !RequestingLocationUpdatesFlag) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onLocationChanged(Location location) {
        updateUserLocation(location);
    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        public static Fragment newInstance(int sectionNumber) {
            Fragment fragment = null;

            switch (sectionNumber) {
                case 1:
                    fragment = new MapFragment();
                    break;
                case 2:
                    fragment = new ServicesFragment();
                    break;
                case 3:
                    fragment = new PaymentFragment();
                    break;
                case 4:
                    fragment = new PerfilFragment();
                    break;
                case 5:
                    fragment = new PromoFragment();

            }

            return fragment;
        }
    }

    public class SectionsPageAdapter extends FragmentPagerAdapter {

        public SectionsPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return getString(R.string.map);
                case 1:
                    return getString(R.string.service);

            }

            return null;
        }
    }

}

interface GooglePlayServicesLocationFromActivity {
    public void onLocationAcquired(Location location);
}
