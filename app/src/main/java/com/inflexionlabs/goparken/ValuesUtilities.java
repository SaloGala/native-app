package com.inflexionlabs.goparken;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by odalysmarronsanchez on 28/07/17.
 */

class ValuesUtilities {
    private static ValuesUtilities ourInstance = new ValuesUtilities();

    private LatLng userLocation;
    private Location lastKnownLocation;
    private LocationManager locationManager;
    private GoogleApiClient mGoogleApiClient;
    private MainActivity mainActivity;
    private GoogleMap googleMap;
    private Context mainContext;

    public final int REQUEST_CHECK_SETTINGS = 2;

    public static ValuesUtilities getInstance() {
        return ourInstance;
    }

    private ValuesUtilities() {
    }

    public void setMainContext(Context context) {
        this.mainContext = context;
    }

    public Context getMainContext() {
        return this.mainContext;
    }

    public void setUserLocation(LatLng userLocation) {
        this.userLocation = userLocation;
    }

    public LatLng getUserLocation() {
        return this.userLocation;
    }

    public void setLastKnownLocation(Location lastKnownLocation) {
        this.lastKnownLocation = lastKnownLocation;
    }

    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    public LocationManager getLocationManager() {
        return this.locationManager;
    }

    public MainActivity getMainActivity() {
        return this.mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public GoogleMap getGoogleMap() {
        return this.googleMap;
    }
}
