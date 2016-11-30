package com.example.iulia.incidentreportingsystem;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Iulia on 11/30/2016.
 */

public class InitialScreen extends Fragment implements View.OnClickListener{

    private Context myContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View InitialScreenView = inflater.inflate(R.layout.initial_screen, container, false);

        //adaugam evenimente onclick pentru butoane
        TextView mapViewLink = (TextView) InitialScreenView.findViewById(R.id.mapViewLink);
        TextView addEventLink = (TextView) InitialScreenView.findViewById(R.id.addEventLink);

        mapViewLink.setOnClickListener(this);
        addEventLink.setOnClickListener(this);
        return InitialScreenView;
    }

    @Override
    public void onAttach(Context context) {
        myContext= context;
        super.onAttach(context);
    }

    @Override
    public void onClick(View v) {
        Fragment desiredFragment;
        FragmentManager mFragmentManager = getFragmentManager();
        switch (v.getId()){

            case R.id.mapViewLink:

                desiredFragment = new mapViewFragment();
                //aici inlocuiesc layoutul meu cu fragmentul
                mFragmentManager.beginTransaction().replace(R.id.user_screen, desiredFragment).commit();
                break;

            case R.id.addEventLink:
                desiredFragment = new addIncidentFragment();
                //aici inlocuiesc layoutul meu cu fragmentul
                mFragmentManager.beginTransaction().replace(R.id.user_screen, desiredFragment).commit();
                break;

            default:
                desiredFragment = new addIncidentFragment();
                //aici inlocuiesc layoutul meu cu fragmentul
                mFragmentManager.beginTransaction().replace(R.id.user_screen, desiredFragment).commit();
                break;
        }
    }
}
