package com.example.iulia.incidentreportingsystem;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;

/**
 * Created by Iulia on 11/27/2016.
 */

public class GetLocation extends Service implements LocationListener {

    private final IBinder locationBinder = new LocationBinder();
    private LocationManager mLocationManager;

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public class LocationBinder extends Binder{

        GetLocation getService(){
            return GetLocation.this;
        }

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return locationBinder;
    }

    public Location getGPSCoords(){
        Context mContext = this.getBaseContext();
        mLocationManager = (LocationManager)
                getSystemService(mContext.LOCATION_SERVICE);

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( mContext, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1,
                1, this);
        Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //System.out.println("In Serviciu: Latitudine: "+locationGPS.getLatitude()+" Longitudine: "+locationGPS.getLongitude());
        return locationGPS;

    }

}
