package com.inflexionlabs.goparken;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by odalysmarronsanchez on 24/08/17.
 */

public class CardViewAdapter extends BaseAdapter{

    private final String TAG="CardViewAdapter";

    private ArrayList<Card> arrayList;
    private Context context;
    private LayoutInflater layoutInflater;


    public CardViewAdapter(ArrayList<Card> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.card_list, null);

        TextView txtCardMask = (TextView) view.findViewById(R.id.txtCarMask);
        TextView txtDefault = (TextView) view.findViewById(R.id.txtDefault);
        Button btnEditarCard = (Button) view.findViewById(R.id.btnEditarCard);

        txtCardMask.setText(arrayList.get(position).getOpenpayMask());
        int d = arrayList.get(position).getDef();
        if(d == 1){
            txtDefault.setText("Predeterminada");
        }else{
            txtDefault.setText("");
        }

        btnEditarCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, EditCardActivity.class);
                intent.putExtra("method_id", Integer.toString(arrayList.get(position).getId()));
                intent.putExtra("card_mask", arrayList.get(position).getOpenpayMask());
                context.startActivity(intent);

                Log.d(TAG,"Click editar tarjeta ID: "+arrayList.get(position).getId());
            }
        });

        return view;
    }
}
