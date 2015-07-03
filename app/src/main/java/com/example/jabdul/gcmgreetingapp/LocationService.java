package com.example.jabdul.gcmgreetingapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Html;
import android.widget.Toast;

/**
 * Created by optadmin on 28/06/15.
 */
public class LocationService extends Service implements LocationListener{

    LocationManager locationManager ;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful

        Utility.showShortToast(this,"Location service started");
        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria,true);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 10000, 0, this);
       // LocationManager.GPS_PROVIDER, 10000, 0, this);

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

        SharedPreferences pref = Utility.IntializeSharedPreference(getApplicationContext(),ApplicationConstants.mTriggerPrefName);

        if(pref.getBoolean(ApplicationConstants.mTriggerLocation,false)) {
            if(!(GreetingActivity.regIdPush == "")) {
                    Utility.showShortToast(getApplicationContext(), "http://maps.google.com/maps?z=12&t=m&q=loc:" + location.getLatitude() + "+" + location.getLongitude());
                    GreetingActivity.pushNotificationServer(GreetingActivity.regIdPush, "http://maps.google.com/maps?z=12&t=m&q=loc:" + location.getLatitude() + "+" + location.getLongitude());
            }

            else{
                Utility.showShortToast(this,"Location:Guardian not set.");
            }

        }

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
}

