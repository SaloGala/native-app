package com.inflexionlabs.goparken;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

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
    GeoLocation oldAbsoluteCenter;

    Boolean initializeUpdateFlag;

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
        mValuesUtilities.setInitializeUpdateFlag(false);
        initializeUpdateFlag = mValuesUtilities.getInitializeUpdateFlag();

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
                mValuesUtilities.setGoogleMap(mGoogleMap);



                try {
                    mGoogleMap.clear();
                } catch (Exception e) {
                }

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

                mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener(){

                    @Override
                    public void onCameraIdle() {

                        CameraPosition cameraPosition = mGoogleMap.getCameraPosition();

                        if(cameraPosition.zoom >=10){
                            mAbsoluteCenter = new GeoLocation(cameraPosition.target.latitude, cameraPosition.target.longitude);

                            if(!mValuesUtilities.getInitializeUpdateFlag()){

                                mMainActivity.searchParkings(mAbsoluteCenter);
                                mValuesUtilities.setInitializeUpdateFlag(true);
                                oldAbsoluteCenter = mAbsoluteCenter;
                            }else{

                                float[] results = new float[1];

                                Location.distanceBetween(mAbsoluteCenter.latitude,mAbsoluteCenter.longitude,oldAbsoluteCenter.latitude,oldAbsoluteCenter.longitude,results);

                                if (results[0]>10000) {
                                    mMainActivity.updateParkingsSearch(mAbsoluteCenter);
                                    oldAbsoluteCenter = mAbsoluteCenter;
                                }
                            }
                        }
                    }
                });

                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){

                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference mParkingDetail = mDatabaseReference.child("parkings").child(mValuesUtilities.getParkingsMarkers().inverse().get(marker)+"/data");

                        ValueEventListener parkingListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {


                                Map<String, String> value = (Map<String, String>) dataSnapshot.getValue();
                                JSONObject mJSONObject = new JSONObject(value);

                                //Log.d(TAG, "mJSONObject: "+mJSONObject);

                                RequestQueue mRequestQueue;
                                mRequestQueue = Volley.newRequestQueue(mValuesUtilities.getMainContext());

                                // Nueva petición JSONObject
                                String URL_BASE = "http://ec2-107-20-100-168.compute-1.amazonaws.com/api/v1/MarkerAv?parking_id=23&marker_id=23";
                                //String URL_JSON = "";

                                JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                                        Request.Method.GET,
                                        URL_BASE ,
                                        null,
                                        new Response.Listener<JSONObject>() {

                                            @Override
                                            public void onResponse(JSONObject response) {
                                                Log.d(TAG, "Respuesta en JSON: " + response);
                                            }
                                        },

                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.d(TAG, "Error Respuesta en JSON: " + error.getMessage());

                                            }
                                        }
                                );
                                // Add request to de queue
                                mRequestQueue.add(jsArrayRequest);


                                /*try {
                                    Log.d(TAG, "mJSONObject: " + mJSONObject.get("name").toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }*/


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                                Toast.makeText(getActivity(), "Failed to loas parking", Toast.LENGTH_SHORT).show();
                            }
                        };

                        mParkingDetail.addValueEventListener(parkingListener);



                        return true;
                    }
                });

            }
        });

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


}
