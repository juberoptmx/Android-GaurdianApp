package com.example.jabdul.gcmgreetingapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * Created by jabdul on 5/20/2015.
 */
public class AirplaneBroadcast extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences pref = Utility.IntializeSharedPreference(context,ApplicationConstants.mTriggerPrefName);

        if(pref.getBoolean(ApplicationConstants.mTriggerAirplane,false)) {

            if(! (GreetingActivity.regIdPush == "")) {
                Toast.makeText(context, "Message sent via SMS application.", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(context, "Airplane:Guardian not set.", Toast.LENGTH_SHORT).show();

            }
        }

    }
}
