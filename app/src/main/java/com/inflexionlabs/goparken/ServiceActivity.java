package com.inflexionlabs.goparken;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ServiceActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mGoogleMap;
    double lt;
    double lng;
    private int RADIUS = 10000;
    Marker mCurrentLocationMarker;

    String type;
    String appBarTitle;

    Bitmap iconMarker;
    int height = 40;
    int width = 40;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        //Check if Google Play Services Available or not
        if (!checkGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();
        } else {
            Log.d("onCreate", "Google Play Services available");
        }

        initializeComponents();
    }

    private boolean checkGooglePlayServices() {
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        int result = googleApi.isGooglePlayServicesAvailable(this);

        if (result != ConnectionResult.SUCCESS) {
            if (googleApi.isUserResolvableError(result)) {
                googleApi.getErrorDialog(this, result, 0).show();
            }
            return false;
        }
        return true;
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

        if (type.equals("car_repair")) {
            appBarTitle = getString(R.string.btn_taller);

            BitmapDrawable parkingBitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.car_repair_icon);
            Bitmap bitmapGraff = parkingBitmapDrawable.getBitmap();
            iconMarker = Bitmap.createScaledBitmap(bitmapGraff, width, height, false);

        } else if (type.equals("gas_station")) {
            appBarTitle = getString(R.string.btn_gas);


            BitmapDrawable parkingBitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.gas_station_icon);
            Bitmap bitmapGraff = parkingBitmapDrawable.getBitmap();
            iconMarker = Bitmap.createScaledBitmap(bitmapGraff, width, height, false);


        } else {
            appBarTitle = getString(R.string.btn_auto);

            BitmapDrawable parkingBitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.car_wash_icon);
            Bitmap bitmapGraff = parkingBitmapDrawable.getBitmap();
            iconMarker = Bitmap.createScaledBitmap(bitmapGraff, width, height, false);


        }

        getSupportActionBar().setTitle(appBarTitle);

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

        Intent intent = getIntent();

        lt = intent.getDoubleExtra("my-lat", 0);
        lng = intent.getDoubleExtra("my-lng", 0);

        if (mCurrentLocationMarker != null) {
            mCurrentLocationMarker.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(lt, lng));
        markerOptions.title("Current Position");

        mCurrentLocationMarker = mGoogleMap.addMarker(markerOptions);

        //move map Camera

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lt, lng), 14));


        //mGoogleMap.clear();

        String url = getUrl(lt, lng, type);
        Object[] dataTransfer = new Object[3];

        dataTransfer[0] = mGoogleMap;
        dataTransfer[1] = url;
        dataTransfer[2] = iconMarker;

        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        getNearbyPlacesData.execute(dataTransfer);


        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            public boolean onMarkerClick(Marker marker) {

                Toast.makeText(ServiceActivity.this, marker.getTitle(), Toast.LENGTH_LONG).show();

                return true;
            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private String getUrl(double lat, double lng, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");

        googlePlacesUrl.append("location=" + lat + "," + lng);
        googlePlacesUrl.append("&radius=" + RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyC-EQ7WdjOHIwnSI0Hh6MSUKD4d5OmYv2Y");

        Log.d("getUrl", googlePlacesUrl.toString());

        return (googlePlacesUrl.toString());
    }

}
