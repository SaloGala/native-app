package com.inflexionlabs.goparken;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ActionBarOverlayLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.widget.LinearLayout.LayoutParams;

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

    PlaceAutocompleteFragment autocompleteFragment;

    Context mContext;

    String availability;
    String key;

    ParkingUtilities parkingUtilities = ParkingUtilities.getInstance();

    DatabaseReference mDatabaseReference;
    DatabaseReference mParkingDetail;

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

        //autocompleteFragment = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);



        /*autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                //TODO: GET info about the selected place
                //Toast.makeText(getActivity(),"Place: "+place.getName(),Toast.LENGTH_SHORT).show();

                CameraPosition cameraPosition = new CameraPosition.Builder().target(place.getLatLng()).zoom(16).build();

                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                if (mMainActivity.searchLocationMarker != null) {
                    mMainActivity.searchLocationMarker.remove();
                }

                mMainActivity.searchLocationMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(place.getLatLng())
                );
            }

            @Override
            public void onError(Status status) {
                //TODO: Handle the error
                Toast.makeText(getActivity(),"An error occurred: "+status,Toast.LENGTH_SHORT).show();
            }
        });*/


        mContext = getContext();

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

                        //Toast.makeText(mMainActivity, "KEY: "+ mValuesUtilities.getParkingsMarkers().inverse().get(marker), Toast.LENGTH_SHORT).show();

                        availability = (String) marker.getTag();
                        key = mValuesUtilities.getParkingsMarkers().inverse().get(marker);

                        //Toast.makeText(mMainActivity, "Availability: "+ availability+" Key: "+ key, Toast.LENGTH_SHORT).show();

                        getParkingDetail(key);


                        return true;
                    }
                });

            }
        });

    }

    private void getParkingDetail(String key) {

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mParkingDetail = mDatabaseReference.child("parkings").child(key + "/data");

        final ValueEventListener parkingListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> value = (Map<String, String>) dataSnapshot.getValue();
                final JSONObject mJSONObject = new JSONObject(value);

                try {
                    parkingUtilities.setAcceptGoParken(mJSONObject.getInt("acceptGoParken"));

                    parkingUtilities.setAddress_colony(mJSONObject.getString("address_colony"));
                    parkingUtilities.setAddress_country(mJSONObject.getString("address_country"));
                    parkingUtilities.setAddress_delegation(mJSONObject.getString("address_delegation"));
                    parkingUtilities.setAddress_number(mJSONObject.getString("address_number"));
                    parkingUtilities.setAddress_postal_code(mJSONObject.getString("address_postal_code"));
                    parkingUtilities.setAddress_state(mJSONObject.getString("address_state"));
                    parkingUtilities.setAddress_street(mJSONObject.getString("address_street"));
                    parkingUtilities.setAddress_street_between_one(mJSONObject.getString("address_street_between_one"));
                    parkingUtilities.setAddress_street_between_two(mJSONObject.getString("address_street_between_two"));
                    parkingUtilities.setComFija(mJSONObject.getDouble("comFija"));
                    parkingUtilities.setComVar(mJSONObject.getDouble("comVar"));
                    parkingUtilities.setComision(mJSONObject.getInt("comision"));
                    parkingUtilities.setExit_code(mJSONObject.getString("exit_code"));
                    parkingUtilities.setDescription(mJSONObject.getString("description"));
                    parkingUtilities.setId(mJSONObject.getInt("id"));
                    parkingUtilities.setId_marker(mJSONObject.getInt("id_marker"));
                    parkingUtilities.setImage_name(mJSONObject.getString("image_name"));
                    parkingUtilities.setImage_path(mJSONObject.getString("image_path"));
                    parkingUtilities.setIva(mJSONObject.getDouble("iva"));
                    parkingUtilities.setMonto(mJSONObject.getDouble("monto"));
                    parkingUtilities.setName(mJSONObject.getString("name"));
                    parkingUtilities.setPrecioPromo(mJSONObject.getDouble("precioPromo"));
                    parkingUtilities.setSchedule_finish_friday(mJSONObject.getString("schedule_finish_friday"));
                    parkingUtilities.setSchedule_finish_monday(mJSONObject.getString("schedule_finish_monday"));
                    parkingUtilities.setSchedule_finish_saturday(mJSONObject.getString("schedule_finish_saturday"));
                    parkingUtilities.setSchedule_finish_sunday(mJSONObject.getString("schedule_finish_sunday"));
                    parkingUtilities.setSchedule_finish_thursday(mJSONObject.getString("schedule_finish_thursday"));
                    parkingUtilities.setSchedule_finish_tuesday(mJSONObject.getString("schedule_finish_tuesday"));
                    parkingUtilities.setSchedule_finish_wednesday(mJSONObject.getString("schedule_finish_wednesday"));
                    parkingUtilities.setSchedule_start_friday(mJSONObject.getString("schedule_start_friday"));
                    parkingUtilities.setSchedule_start_monday(mJSONObject.getString("schedule_start_monday"));
                    parkingUtilities.setSchedule_start_saturday(mJSONObject.getString("schedule_start_saturday"));
                    parkingUtilities.setSchedule_start_sunday(mJSONObject.getString("schedule_start_sunday"));
                    parkingUtilities.setSchedule_start_thursday(mJSONObject.getString("schedule_start_thursday"));
                    parkingUtilities.setSchedule_start_tuesday(mJSONObject.getString("schedule_start_tuesday"));
                    parkingUtilities.setSchedule_start_wednesday(mJSONObject.getString("schedule_start_wednesday"));
                    parkingUtilities.setSize(mJSONObject.getInt("size"));
                    parkingUtilities.setSize_warning(mJSONObject.getInt("size_warning"));
                    parkingUtilities.setStatus(mJSONObject.getString("status"));
                    parkingUtilities.setTarifaPromo(mJSONObject.getInt("tarifaPromo"));
                    parkingUtilities.setType(mJSONObject.getString("type"));


                    if(parkingUtilities.getAcceptGoParken() == 0){

                        parkingUtilities.setCost_goparken_by_fraction(mJSONObject.getDouble("cost_goparken_by_fraction"));
                        parkingUtilities.setCost_goparken_by_hour(mJSONObject.getDouble("cost_goparken_by_hour"));
                        parkingUtilities.setCost_public_by_fraction(mJSONObject.getDouble("cost_public_by_fraction"));
                        parkingUtilities.setCost_public_by_hour(mJSONObject.getDouble("cost_public_by_hour"));
                        parkingUtilities.setEntry_code(" ");
                        parkingUtilities.setHorasPromo(" ");
                        parkingUtilities.setId_form(mJSONObject.getString("id_form"));
                        parkingUtilities.setLatitude(mJSONObject.getString("latitude"));
                        parkingUtilities.setLongitude(mJSONObject.getString("longitude"));

                    } else if(parkingUtilities.getAcceptGoParken() == 1){

                        parkingUtilities.setCost_goparken_by_fraction(mJSONObject.getDouble("cost_goparken_by_fraction"));
                        parkingUtilities.setCost_goparken_by_hour(mJSONObject.getDouble("cost_goparken_by_hour"));
                        parkingUtilities.setCost_public_by_fraction(mJSONObject.getDouble("cost_public_by_fraction"));
                        parkingUtilities.setCost_public_by_hour(mJSONObject.getDouble("cost_public_by_hour"));
                        parkingUtilities.setEntry_code(mJSONObject.getString("entry_code"));
                        parkingUtilities.setHorasPromo(mJSONObject.getString("horasPromo"));
                        parkingUtilities.setId_form(mJSONObject.getString("id_form"));
                        parkingUtilities.setLatitude(mJSONObject.getString("latitude"));
                        parkingUtilities.setLongitude(mJSONObject.getString("longitude"));

                    } else if (parkingUtilities.getAcceptGoParken() == 2){

                        /*parkingUtilities.setCost_goparken_by_fraction(Double.parseDouble(mJSONObject.getString("cost_goparken_by_fraction")));
                        parkingUtilities.setCost_goparken_by_hour(Double.parseDouble(mJSONObject.getString("cost_goparken_by_hour")));
                        parkingUtilities.setCost_public_by_fraction(Double.parseDouble(mJSONObject.getString("cost_public_by_fraction")));
                        parkingUtilities.setCost_public_by_hour(Double.parseDouble(mJSONObject.getString("cost_public_by_hour")));*/

                        parkingUtilities.setCost_goparken_by_fraction(0);
                        parkingUtilities.setCost_goparken_by_hour(0);
                        parkingUtilities.setCost_public_by_fraction(0);
                        parkingUtilities.setCost_public_by_hour(0);

                        parkingUtilities.setEntry_code(mJSONObject.getString("entry_code"));
                        parkingUtilities.setHorasPromo(mJSONObject.getString("horasPromo"));
                        parkingUtilities.setId_form(Long.toString((Long)mJSONObject.get("id_form")));
                        parkingUtilities.setLatitude(Double.toString((Double) mJSONObject.get("latitude")));
                        parkingUtilities.setLongitude(Double.toString((Double) mJSONObject.get("longitude")));


                    }

                    goToParkingDetailActivity();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d(TAG,"dataSnapshot: "+mJSONObject);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mParkingDetail.addListenerForSingleValueEvent(parkingListener);

    }

    public void goToParkingDetailActivity() {

        Log.d(TAG,"goToParkingDetailActivity");

        Intent intent = new Intent(mMainActivity, ParkingActivity.class);
        startActivity(intent);
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }



}
