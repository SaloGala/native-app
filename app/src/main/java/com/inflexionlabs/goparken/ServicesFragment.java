package com.inflexionlabs.goparken;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by odalysmarronsanchez on 25/07/17.
 */

public class ServicesFragment extends Fragment {

    private static final String TAG = "ServicesFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.services_fragment,container,false);

        return view;
    }
}
