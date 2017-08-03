package com.inflexionlabs.goparken;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by odalysmarronsanchez on 25/07/17.
 */

public class ServicesFragment extends Fragment {

    private static final String TAG = "ServicesFragment";

    Button btnTalleres;
    Button btnGasolineras;
    Button btnAutolavados;
    public View mView;

    MainActivity mMainActivity;
    private OnFragmentInteractionListener mListener;


    public ServicesFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState){
        mView = inflater.inflate(R.layout.services_fragment,container,false);

        initializeComponents();
        initializeVariables();

        return mView;
    }

    private void initializeVariables() {
        mMainActivity = (MainActivity) getActivity();
    }

    private void initializeComponents() {
        btnTalleres = (Button) mView.findViewById(R.id.btnTaller) ;
        btnGasolineras = (Button) mView.findViewById(R.id.btnGasolineria);
        btnAutolavados = (Button) mView.findViewById(R.id.btnAutolavado);

        btnTalleres.setOnClickListener(btnListener);
        btnGasolineras.setOnClickListener(btnListener);
        btnAutolavados.setOnClickListener(btnListener);
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {

        public void onClick(View v)
        {

            Intent intent = new Intent(mMainActivity, ServiceActivity.class);

            switch (v.getId()){
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

    };



    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
