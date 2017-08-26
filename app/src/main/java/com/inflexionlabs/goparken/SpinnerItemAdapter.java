package com.inflexionlabs.goparken;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by odalysmarronsanchez on 25/08/17.
 */

public class SpinnerItemAdapter extends BaseAdapter {

    private final String TAG="SpinnerItemAdapter ";

    private ArrayList<Card> arrayList;
    private Context context;
    private LayoutInflater layoutInflater;


    public SpinnerItemAdapter(ArrayList<Card> arrayList, Context context) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.spinner_item, null);

        TextView txtCardMask = (TextView) view.findViewById(R.id.txtMask);

        txtCardMask.setText(arrayList.get(position).getOpenpayMask());


        return view;
    }
}
