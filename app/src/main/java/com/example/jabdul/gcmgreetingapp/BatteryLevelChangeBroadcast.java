package com.example.jabdul.gcmgreetingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by jabdul on 5/20/2015.
 */
public class BatteryLevelChangeBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        SharedPreferences pref = Utility.IntializeSharedPreference(context,ApplicationConstants.mTriggerPrefName);
        int level = intent.getIntExtra("level", 0);

        Intent startActivityIntent = new Intent(context, GreetingActivity.class);
        startActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startActivityIntent);
        Utility.showShortToast(context,"batterylevel: "+level);

    }
}
