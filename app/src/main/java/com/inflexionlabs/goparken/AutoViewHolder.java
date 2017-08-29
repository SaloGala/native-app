package com.inflexionlabs.goparken;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by odalysmarronsanchez on 29/08/17.
 */

public class AutoViewHolder extends ViewHolder{

    public TextView txtPlaca;
    public TextView txtSubmarca;
    public Button btnEliminar;

    public AutoViewHolder(View itemView) {
        super(itemView);

        txtPlaca= (TextView) itemView.findViewById(R.id.auto_placa);
        txtSubmarca = (TextView) itemView.findViewById(R.id.auto_submarca);
        btnEliminar = (Button) itemView.findViewById(R.id.btnEliminarAuto);
    }

    public void bindToAuto(Auto auto, View.OnClickListener btnClickListener) {
        txtPlaca.setText(auto.placa);
        txtSubmarca.setText(auto.submarca);


        btnEliminar.setOnClickListener(btnClickListener);
    }
}
