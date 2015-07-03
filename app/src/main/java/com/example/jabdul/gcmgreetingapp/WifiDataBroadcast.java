package com.example.jabdul.gcmgreetingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.lang.reflect.Method;
import android.net.wifi.WifiManager;


/**
 * Created by optadmin on 29/06/15.
 */
public class WifiDataBroadcast extends BroadcastReceiver{
       @Override
        public void onReceive(Context context, Intent intent) {

        SharedPreferences pref = Utility.IntializeSharedPreference(context, ApplicationConstants.mTriggerPrefName);

           if(pref.getBoolean(ApplicationConstants.mTriggerWifidata, false)) {
               if(! (GreetingActivity.regIdPush == "")) {

                   if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                       NetworkInfo networkInfo =
                               intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                       if (networkInfo.isConnected()) {
                           // Wifi is connected

                       }
                   } else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                       NetworkInfo networkInfo =
                               intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                       if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI &&
                               !networkInfo.isConnected()) {
                           Utility.showShortToast(context, "Wifi Disconnected");
                           // Wifi is disconnected
                           if (!isDataEnabled(context)) {
                               Utility.showShortToast(context, "Data Disconnected");
                               SmsManager smsManager = SmsManager.getDefault();
                               smsManager.sendTextMessage("+919739081991", null, "Both Wifi and Data is OFF.", null, null);
                           }

                       }
                   }
               }
               else{
                   Utility.showShortToast(context, "Wifi:Guardian not set.");

               }
           }

        }

    /* Mobile Data Check */
    public  boolean isDataEnabled(Context cont){
        ConnectivityManager connManager = (ConnectivityManager) cont.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(connManager.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            if((Boolean)method.invoke(connManager)){

                return true;
            }
            else{

                return false;
            }

        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }
        return false;
    }
}

