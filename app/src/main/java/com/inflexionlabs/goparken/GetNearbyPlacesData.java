package com.inflexionlabs.goparken;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

/**
 * Created by odalysmarronsanchez on 02/08/17.
 */

public class GetNearbyPlacesData extends AsyncTask<Object,String, String > {


    String googlePlacesData;
    GoogleMap mMap;
    String url;

    Bitmap iconMarker;

    @Override
    protected String doInBackground(Object... objects) {

        try{
            Log.d("GetNearbyPlacesData","doInBackground entered");
            mMap = (GoogleMap) objects[0];
            url = (String) objects[1];
            iconMarker = (Bitmap) objects[2];
            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlacesData = downloadUrl.readUrl(url);
            Log.d("GooglePlacesReadTask", "doInBackground Exit");

        }catch (Exception e){
            Log.d("GooglePlacesReadTask", e.toString());
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result){

        Log.d("GooglePlacesReadTask", "onPostExecute Entered");

        List<HashMap<String , String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList = dataParser.parse(result);

        showNearbyPlaces(nearbyPlacesList);

        Log.d("GooglePlacesReadTask", "onPostExecute Exit");

    }

    private void showNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList){

        for(int i =0; i<nearbyPlacesList.size(); i++){
            Log.d("onPostExecute","Entered into showing locations");

            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String,String> googlePlace = nearbyPlacesList.get(i);
            Double lat = Double.parseDouble(googlePlace.get("lat"));
            Double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            LatLng latlng = new LatLng(lat,lng);
            markerOptions.position(latlng);
            markerOptions.title(placeName + " : "+ vicinity);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconMarker));
            mMap.addMarker(markerOptions);

            //move map camera
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

        }

    }

}
