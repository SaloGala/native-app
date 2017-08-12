package com.inflexionlabs.goparken;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

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
    public View mView;


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

        return mView;
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
