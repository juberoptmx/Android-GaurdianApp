package com.example.jabdul.gcmgreetingapp;

/**
 * Created by jabdul on 5/20/2015.
 */
public class ApplicationConstants {
    // Php Application URL to store Reg ID created
    static final String APP_SERVER_URL = "http://172.18.100.29/gcmwebapp/insertuser.php";

    // Php Application URL to store Reg ID created
   static final String APP_SERVER_PUSH_URL = "http://172.18.100.29/gcmwebapp/processmessagecopy.php";

    // Google Project Number
    static final String GOOGLE_PROJ_ID = "761112518466";
    static final String MSG_KEY = "m";

    // all trigger preferences
    public static String mTriggerPrefName = "TriggerDetails";

    public static String mTriggerAirplane = "pref_airplane_mode";
    public static String mTriggerWifidata = "pref_wifidata_mode";
    public static String mTriggerLocation = "pref_location_mode";

    // all trigger messages
    public static String mAirplaneMsg = "Airplane mode is ON";
    public static String mWifiDataMsg = "WifiData is ON";
    public static String mLocationMsg = "Location changed";




}
