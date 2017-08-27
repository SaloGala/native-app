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

public class PaymentFragment extends Fragment {

    private static final String TAG = "PaymentsFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.tarifa_dialog,container,false);

        return view;
    }
}
