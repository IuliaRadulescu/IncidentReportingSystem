package com.example.iulia.incidentreportingsystem;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private DrawerCustomAdapter mDrawerCustomAdapter;
    private String[] MenuOptions;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        String[] menuOptions = new String[]{"Map View", "List View", "Add Event"};
        mDrawerCustomAdapter = new DrawerCustomAdapter(this, menuOptions);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.layout_drawer);
        mDrawerListView = (ListView) findViewById(R.id.user_drawer);
        mDrawerListView.setAdapter(mDrawerCustomAdapter);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.open_menu,
                R.string.close_menu
        );
        mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        mActionBarDrawerToggle.setHomeAsUpIndicator(R.mipmap.meniu);
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);

        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
             @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){

                 Fragment desiredFragment;

                 switch (position){

                     case 0:
                         desiredFragment = new mapViewFragment();
                         break;
                     case 1:
                         desiredFragment = new addIncidentFragment();
                         break;
                     default:
                         desiredFragment = new mapViewFragment();
                         break;
                 }

                FragmentManager mFragmentManager = getSupportFragmentManager();
                //aici inlocuiesc layoutul meu cu fragmentul
                 mFragmentManager.beginTransaction().replace(R.id.user_screen, desiredFragment).commit();

             }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
    }
}
