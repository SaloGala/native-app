package com.inflexionlabs.goparken;

import android.*;
import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ServiceActivity extends AppCompatActivity implements OnMapReadyCallback ,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    MapView mMapView;
    private GoogleMap mGoogleMap;
    double lt;
    double lng;
    private int RADIUS = 10000;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrentLocationMarker;
    LocationRequest mLocationRequest;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    String type;
    String appBarTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkLocationPermission();
        }

        //Check if Google Play Services Available or not
        if(!checkGooglePlayServices()){
            Log.d("onCreate","Finishing test case since Google Play Services are not available");
            finish();
        }else{
            Log.d("onCreate","Google Play Services available");
        }

        initializeComponents();
    }

    private boolean checkGooglePlayServices() {
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        int result = googleApi.isGooglePlayServicesAvailable(this);

        if(result != ConnectionResult.SUCCESS){
            if(googleApi.isUserResolvableError(result)){
                googleApi.getErrorDialog(this,result,0).show();
            }
            return false;
        }
        return true;
    }

    private boolean checkLocationPermission() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

            //Asking user if explanation is needed
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_LOCATION);
            }else{
                //No explanation needed, we can request the permission
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_LOCATION);
            }return false;
        }else{
            return true;
        }
    }

    private void initializeComponents() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryActionBar), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        type = intent.getStringExtra("my-string");

        if (type.equals("car_repair")){
            appBarTitle = getString(R.string.btn_taller);
        }else if (type.equals("gas_station")){
            appBarTitle = getString(R.string.btn_gas);
        }else{
            appBarTitle = getString(R.string.btn_auto);
        }

        getSupportActionBar().setTitle(appBarTitle);

        //Toast.makeText(ServiceActivity.this,type,Toast.LENGTH_LONG).show();


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            }
            
        }else{
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }


    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d("onLocationChanged","entered");

        mLastLocation = location;
        if(mCurrentLocationMarker!=null){
            mCurrentLocationMarker.remove();
        }

        //Place current location marker
        lt = location.getLatitude();
        lng = location.getLongitude();

        //Toast.makeText(ServiceActivity.this,"onLocationChanged lt: "+ lt+"lng: "+ lng, Toast.LENGTH_LONG).show();

        LatLng latlng = new LatLng(location.getLatitude(),location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latlng);
        markerOptions.title("Current Position");

        mCurrentLocationMarker = mGoogleMap.addMarker(markerOptions);

        //move map Camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        mGoogleMap.clear();

        String url = getUrl(lt,lng,type);
        Object[] dataTransfer = new Object[2];

        dataTransfer[0] = mGoogleMap;
        dataTransfer[1] = url;

        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        getNearbyPlacesData.execute(dataTransfer);


        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            public boolean onMarkerClick(Marker marker) {

                Toast.makeText(ServiceActivity.this, marker.getTitle(), Toast.LENGTH_LONG).show();

                return true;
            }
        });


        //stop location update
        if(mGoogleApiClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }
        Log.d("onLocationChanged", "Exit");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){

        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_LOCATION:{

                //if request is cancelled, the result arrays are empty.
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permissions was granted. Do the
                    //contacts-related task ypu need to do
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        if(mGoogleApiClient == null){
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }
                }else{
                    //permission denied, disable the functionality that depends in this permission
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }

                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }

    }

    private String getUrl (double lat, double lng, String nearbyPlace){

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");

        googlePlacesUrl.append("location="+lat+","+lng);
        googlePlacesUrl.append("&radius="+RADIUS);
        googlePlacesUrl.append("&type="+nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key="+"AIzaSyC-EQ7WdjOHIwnSI0Hh6MSUKD4d5OmYv2Y");

        Log.d("getUrl", googlePlacesUrl.toString());

        return (googlePlacesUrl.toString());
    }

}
