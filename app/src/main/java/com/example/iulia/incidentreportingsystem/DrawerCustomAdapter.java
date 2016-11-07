package com.example.iulia.incidentreportingsystem;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Iulia on 10/16/2016.
 */

public class DrawerCustomAdapter extends ArrayAdapter<String> {

    public DrawerCustomAdapter(Context context, String[] menuItems){

        super(context, R.layout.drawer_row, menuItems);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View createView = inflater.inflate(R.layout.drawer_row, parent, false);

        String anItem = getItem(position);

        TextView anItemText = (TextView) createView.findViewById(R.id.drawerTextItem);

        anItemText.setText(anItem);

        return createView;

    }
}
