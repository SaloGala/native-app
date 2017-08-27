package com.inflexionlabs.goparken;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by odalysmarronsanchez on 25/07/17.
 */

public class ServicesFragment extends Fragment {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;

    private static final String TAG = "ServicesFragment";

    Button btnTalleres;
    Button btnGasolineras;
    Button btnAutolavados;
    ValuesUtilities valuesUtilities = ValuesUtilities.getInstance();
    boolean servicesLoadedFlag = false;
    public View mView;
    RequestQueue queue;

    TextView TVCarWashDistance;
    TextView TVGasStationDistance;
    TextView TVCarRepairDistance;

    TextView TVCarWashTitle;
    TextView TVGasStationTitle;
    TextView TVCarRepairTitle;

    TextView TVCarWashDescription;
    TextView TVGasStationDescription;
    TextView TVCarRepairDescription;

    TextView TVCarWashIndications;
    TextView TVGasStationIndications;
    TextView TVCarRepairIndications;

    List<HashMap<String, String>> nearbyCarWashesList = null;
    List<HashMap<String, String>> nearbyGasStationsList = null;
    List<HashMap<String, String>> nearbyCarRepairsList = null;

    ScrollView SVServicesLoaded;
    ScrollView SVServicesNotLoaded;

    MainActivity mMainActivity;
    private OnFragmentInteractionListener mListener;

    public ServicesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.services_fragment, container, false);

        initializeComponents();
        initializeVariables();

        initializeServices();
        return mView;
    }

    private void initializeServices() {
        if (!servicesLoadedFlag) {

            FusedLocationProviderClient mFusedLocationClient;
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

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

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            if (location != null) {

                                StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");

                                googlePlacesUrl.append("location=" + location.getLatitude() + "," + location.getLongitude());
                                googlePlacesUrl.append("&radius=" + 10000);
                                googlePlacesUrl.append("&sensor=true");
                                googlePlacesUrl.append("&key=" + "AIzaSyC-EQ7WdjOHIwnSI0Hh6MSUKD4d5OmYv2Y");

                                String carRepairURL = googlePlacesUrl.toString() + "&type=car_repair";
                                String gasStationURL = googlePlacesUrl.toString() + "&type=gas_station";
                                String carWashURL = googlePlacesUrl.toString() + "&type=car_wash";

                                doRequest(carRepairURL, "car_repair", location);
                                doRequest(gasStationURL, "gas_station", location);
                                doRequest(carWashURL, "car_wash", location);


                            }
                        }
                    });
        }
    }

    private void doRequest(String url, final String type, final Location location) {

        if (queue == null) {
            queue = Volley.newRequestQueue(getActivity());
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        List<HashMap<String, String>> nearbyPlacesList = null;
                        DataParser dataParser = new DataParser();
                        nearbyPlacesList = dataParser.parse(response);

                        for (Iterator<HashMap<String, String>> i = nearbyPlacesList.iterator(); i.hasNext(); ) {
                            HashMap<String, String> item = i.next();

                            float[] results = new float[1];

                            Double lat = Double.parseDouble(item.get("lat"));
                            Double lng = Double.parseDouble(item.get("lng"));

                            Location.distanceBetween(lat, lng, location.getLatitude(), location.getLongitude(), results);

                            item.put("distance", results[0] + "");
                        }

                        Collections.sort(nearbyPlacesList, new Comparator<HashMap<String, String>>() {
                            @Override
                            public int compare(HashMap<String, String> h1, HashMap<String, String> h2) {
                                return Double.compare(Double.parseDouble(h1.get("distance")), Double.parseDouble(h2.get("distance")));
                            }
                        });


                        /*for (Iterator<HashMap<String, String>> i = nearbyPlacesList.iterator(); i.hasNext(); ) {
                            HashMap<String, String> item = i.next();

                            System.out.println("Distance: " + item.get("distance") + " meters -" + "Type: " + type + item);

                        }*/

                        System.out.println("Type: " + type + " Nearest: " + nearbyPlacesList.get(0));
                        int finalDistance = Math.round(Float.parseFloat(nearbyPlacesList.get(0).get("distance")));
                        String finalTitle = nearbyPlacesList.get(0).get("place_name");
                        String finalDescription = nearbyPlacesList.get(0).get("vicinity");

                        if (type.equals("car_wash")) {

                            TVCarWashDistance.setText(finalDistance + "");
                            TVCarWashTitle.setText(finalTitle);
                            TVCarWashDescription.setText(finalDescription);

                            nearbyCarWashesList = nearbyPlacesList;

                        } else if (type.equals("gas_station")) {
                            TVGasStationDistance.setText(finalDistance + "");
                            TVGasStationTitle.setText(finalTitle);
                            TVGasStationDescription.setText(finalDescription);

                            nearbyGasStationsList = nearbyPlacesList;

                        } else {
                            TVCarRepairDistance.setText(finalDistance + "");
                            TVCarRepairTitle.setText(finalTitle);
                            TVCarRepairDescription.setText(finalDescription);

                            nearbyCarRepairsList = nearbyPlacesList;
                        }

                        SVServicesLoaded.setVisibility(View.VISIBLE);
                        SVServicesNotLoaded.setVisibility(View.GONE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Show an error
            }
        });

        queue.add(stringRequest);
    }

    private void initializeVariables() {
        mMainActivity = (MainActivity) getActivity();
    }

    private void initializeComponents() {
        btnTalleres = (Button) mView.findViewById(R.id.btnTaller);
        btnGasolineras = (Button) mView.findViewById(R.id.btnGasolineria);
        btnAutolavados = (Button) mView.findViewById(R.id.btnAutolavado);

        btnTalleres.setOnClickListener(btnListener);
        btnGasolineras.setOnClickListener(btnListener);
        btnAutolavados.setOnClickListener(btnListener);

        TextView TVCarWash = (TextView) mView.findViewById(R.id.TVCarWash);
        TextView TVGasStation = (TextView) mView.findViewById(R.id.TVGasStation);
        TextView TVCarRepair = (TextView) mView.findViewById(R.id.TVCarRepair);


        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "NexaBold.ttf");

        TVCarWash.setTypeface(type);
        TVGasStation.setTypeface(type);
        TVCarRepair.setTypeface(type);

        TVCarWashDistance = (TextView) mView.findViewById(R.id.TVCarWashDistance);
        TVGasStationDistance = (TextView) mView.findViewById(R.id.TVGasStationDistance);
        TVCarRepairDistance = (TextView) mView.findViewById(R.id.TVCarRepairDistance);

        TVCarWashTitle = (TextView) mView.findViewById(R.id.TVCarWashTitle);
        TVGasStationTitle = (TextView) mView.findViewById(R.id.TVGasStationTitle);
        TVCarRepairTitle = (TextView) mView.findViewById(R.id.TVCarRepairTitle);

        TVCarWashDescription = (TextView) mView.findViewById(R.id.TVCarWashDescription);
        TVGasStationDescription = (TextView) mView.findViewById(R.id.TVGasStationDescription);
        TVCarRepairDescription = (TextView) mView.findViewById(R.id.TVCarRepairDescription);

        TVCarWashIndications = (TextView) mView.findViewById(R.id.TVCarWashIndications);
        TVGasStationIndications = (TextView) mView.findViewById(R.id.TVGasStationIndications);
        TVCarRepairIndications = (TextView) mView.findViewById(R.id.TVCarRepairIndications);

        TVCarWashTitle.setTypeface(type);
        TVGasStationTitle.setTypeface(type);
        TVCarRepairTitle.setTypeface(type);

        SVServicesLoaded = (ScrollView) mView.findViewById(R.id.SVServicesLoaded);
        SVServicesNotLoaded = (ScrollView) mView.findViewById(R.id.SVServicesNotLoaded);

        TVCarWashIndications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo("car_wash");
            }
        });

        TVGasStationIndications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo("gas_station");
            }
        });

        TVCarRepairIndications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo("car_repair");
            }
        });

        Button BTNRefreshServices = (Button) mView.findViewById(R.id.BTNRefreshServices);

        BTNRefreshServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeServices();
            }
        });
    }

    private void goTo(String type) {
        HashMap<String, String> placeToGo;

        if (type.equals("car_wash")) {
            placeToGo = nearbyCarWashesList.get(0);
        } else if (type.equals("gas_station")) {
            placeToGo = nearbyGasStationsList.get(0);
        } else {
            placeToGo = nearbyCarRepairsList.get(0);
        }

        String uri = "geo:0,0" + "?q=" + placeToGo.get("lat") + "," + placeToGo.get("lng");
        startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(uri)));
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {

        public void onClick(View v) {

            Intent intent = new Intent(mMainActivity, ServiceActivity.class);

            Location actualLocation = new Location(LocationManager.GPS_PROVIDER);

            try {
                actualLocation.setLatitude(valuesUtilities.getUserLocation().latitude);
                actualLocation.setLongitude(valuesUtilities.getUserLocation().longitude);

                intent.putExtra("my-lat", actualLocation.getLatitude());
                intent.putExtra("my-lng", actualLocation.getLongitude());
            } catch (Exception e) {
                actualLocation = null;
            }

            if (actualLocation == null) {

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkLocationPermissions();
                    //isCheckLocationPermission();
                }

                mMainActivity.subscribeToGooglePlayServicesLocation();

            } else {

                switch (v.getId()) {
                    case R.id.btnTaller:

                        intent.putExtra("my-string", "car_repair");
                        startActivity(intent);

                    /*MapFragment nextFrag= new MapFragment();
                    mMainActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, nextFrag)
                            .addToBackStack(null)
                            .commit();*/
                        break;
                    case R.id.btnGasolineria:

                        intent.putExtra("my-string", "gas_station");
                        startActivity(intent);

                        break;

                    case R.id.btnAutolavado:

                        intent.putExtra("my-string", "car_wash");
                        startActivity(intent);

                        break;
                }
            }
        }

    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {

                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];

                    if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {

                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                        }

                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
                            if (!showRationale) {

                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());

                                builder.setTitle("¿Permitir a Go Parken utilizar la ubicación de tu dispositivo?");

                                builder.setMessage("Esto te permitirá ver los servicios que hay cerca de ti, como autolavados, gasolinerías y talleres.\n\n" +
                                        "Para activarlo, haz click en \"Configuración de la App\" abajo y activa Ubicación en el menú permisos.")

                                        .setPositiveButton("Configuración de la App", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                Intent intent = new Intent();
                                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                                intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                                getActivity().startActivity(intent);

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

    private boolean checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            } else {
                //No explanation needed, we can request the permission
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public boolean isCheckLocationPermission() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                showMessageOKCancelLocation("Debes permitir el acceso a tu ubicación");
                return false;
            }

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            return false;
        }

        return true;

    }

    private void showMessageOKCancelLocation(String message) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("Aceptar", listenerLocation)
                .setNegativeButton("Cancelar", listenerLocation)
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
                            getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    dialog.dismiss();
                    break;
            }
        }
    };
}
