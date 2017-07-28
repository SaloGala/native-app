package com.inflexionlabs.goparken;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by odalysmarronsanchez on 25/07/17.
 */

public class MapFragment extends Fragment implements GooglePlayServicesLocationFromActivity {

    private static final String TAG = "MapFragment";

    public MapView mMapView;
    private GoogleMap mGoogleMap;
    public View mView;

    MainActivity mMainActivity;
    private ValuesUtilities mValuesUtilities;
    GeoLocation mAbsoluteCenter;

    private OnFragmentInteractionListener mListener;


    public MapFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mMainActivity = (MainActivity) getActivity();
        mValuesUtilities = ValuesUtilities.getInstance();


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mValuesUtilities = ValuesUtilities.getInstance();

        mView = inflater.inflate(R.layout.map_fragment, container, false);


        mMapView = (MapView) (mView.findViewById(R.id.mapView));
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mView;

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    public void updatePosition() {
    } /* TODO: Rename method, update argument and hook method into UI event*/

    public void onButtonPressed(Uri uri) {
        if (mListener != null) mListener.onFragmentInteraction(uri);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
            mListener = (OnFragmentInteractionListener) context;
        else
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onLocationAcquired(final Location location) {

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {


                mGoogleMap = mMap;
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mGoogleMap.setMyLocationEnabled(true);

                try {
                    mGoogleMap.clear();
                } catch (Exception e) {
                }

                mValuesUtilities.setGoogleMap(mGoogleMap);

                if (location != null) {

                    mValuesUtilities.setUserLocation(new LatLng(location.getLatitude(), location.getLongitude()));

                    mValuesUtilities.setLastKnownLocation(location);

                    mAbsoluteCenter = new GeoLocation(location.getLatitude(), location.getLongitude());

                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    final LatLng userLatLng = new LatLng(latitude, longitude);


                    CameraPosition cameraPosition = new CameraPosition.Builder().target(userLatLng).zoom(19).build();

                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            if (!mValuesUtilities.getLocationManager().isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                mMainActivity.subscribeToGooglePlayServicesLocation();
                            }
                        }

                        @Override
                        public void onCancel() {

                        }
                    });


                } else {
                    mMainActivity.subscribeToGooglePlayServicesLocation();
                    mValuesUtilities.setUserLocation(null);
                }

            }
        });

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


}
