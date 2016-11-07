package com.example.iulia.incidentreportingsystem;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by Iulia on 11/5/2016.
 */

public class addIncidentFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View FinalView = inflater.inflate(R.layout.addincident, container, false);

        Button submit = (Button) FinalView.findViewById(R.id.submit);
        submit.setOnClickListener(this);

        return FinalView;
    }

    @Override
    public void onClick(View v) {

        String name = v.findViewById(R.id.name).toString();
        String description = v.findViewById(R.id.description).toString();
        Spinner incidentTypeHelper = (Spinner) v.findViewById(R.id.incident_type);
        String incidentType = incidentTypeHelper.getSelectedItem().toString();

        //si acum le scriem in baza de date

    }
}
