package com.example.jabdul.gcmgreetingapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.SmsManager;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.androidquery.AQuery;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Method;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.concurrent.atomic.AtomicInteger;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.Display;
//import android.content.DialogInterface.OnClickListener;

import android.widget.Button;
import android.widget.Switch;
import android.util.Log;
import android.os.AsyncTask;
import android.widget.ToggleButton;
import android.widget.CompoundButton;
import android.view.View.OnClickListener;

import java.text.NumberFormat;
import java.text.ParsePosition;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import android.graphics.Bitmap;
/**
 * Created by jabdul on 5/20/2015.
 */

public class GreetingActivity extends Activity {
    TextView emailET;
    private AQuery aq;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private TextView batteryPercent;
    static int battery_level;
    EditText user_battery_level;

   static ProgressDialog pushDialog;
    String msgPush = "Battery Low";
    static RequestParams params = new RequestParams();
    static Context applicationContext ;
    Switch airplaneSwitch;
    SharedPreferences trigPref;
    Switch locationSwitch;
    Switch wifiDataSwitch;
    Button qrBtnView;
    Button qrBtnScan;


    static String regIdPush = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);

        applicationContext = this;
        trigPref = Utility.IntializeSharedPreference(this,ApplicationConstants.mTriggerPrefName);

        GreetingActivity.this.findViewById(R.id.greetimg).setVisibility(View.INVISIBLE);

        batteryPercent = (TextView) this.findViewById(R.id.txtview_battery_status);

        aq = new AQuery(this);
        String json = getIntent().getStringExtra("greetjson");
        SharedPreferences prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        emailET = (TextView) findViewById(R.id.greetingmsg);

        // Check if Google Play Service is installed in Device
        // Play services is needed to handle GCM stuffs
       if (!checkPlayServices()) {
            Toast.makeText(
                    getApplicationContext(),
                    "This device doesn't support Play services, App will not work normally",
                    Toast.LENGTH_LONG).show();
        }

        // When json is not null
        if (json != null) {
            try {
                JSONObject jsonObj = new JSONObject(json);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("greetimgurl", jsonObj.getString("greetImgURL"));
                editor.putString("greetmsg", jsonObj.getString("greetMsg"));
                editor.commit();

                emailET.setText(prefs.getString("greetmsg", ""));
                // Render Image read from Image URL using aquery 'image' method
                aq.id(R.id.greetimg)
                        .progress(R.id.progress)
                        .image(prefs.getString("greetimgurl", ""), true, true,
                                0, 0, null, AQuery.FADE_IN);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        // When Json is null
        else if (!"".equals(prefs.getString("greetimgurl", ""))    && !"".equals(prefs.getString("greetmsg", "") != null)) {
            emailET.setText(prefs.getString("greetmsg", ""));
            aq.id(R.id.greetimg)
                    .progress(R.id.progress)
                    .image(prefs.getString("greetimgurl", ""), true, true, 0,
                            0, null, AQuery.FADE_IN);
        }

        // Push messgage from device
        Button btnSendMessage = (Button) findViewById(R.id.push_button);

        btnSendMessage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                user_battery_level = (EditText) GreetingActivity.this.findViewById(R.id.edtTxt_battery_level);

                if (!(user_battery_level == null)) {
                    try {
                        battery_level = Integer.parseInt(user_battery_level.getText().toString());
                        if ((battery_level >= 1) && (battery_level <= 99)) {
                            Intent intent = new Intent(GreetingActivity.this,BatteryService.class);
                            startService(intent);
                            Utility.showShortToast(getApplicationContext(), "Battery service started");

                        } else {
                            Toast.makeText(getApplicationContext(), "Please enter number (1-99)", Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Please do not enter random string.", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Trigger level field can not be empty", Toast.LENGTH_SHORT).show();

                }

            }
        });


        // Location Switch
        locationSwitch = (Switch)  findViewById(R.id.switch_location);
        locationSwitch.setChecked(Utility.getPreferences(trigPref,ApplicationConstants.mTriggerLocation));
        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Utility.setPreference(trigPref,ApplicationConstants.mTriggerLocation,isChecked);
                Intent intent = new Intent(GreetingActivity.this,LocationService.class);

                if(isChecked){
                    startService(intent);
                }else{
                    stopService(intent);
                }

            }

        });

        // Airplane Switch
        airplaneSwitch = (Switch) findViewById(R.id.switch_airplane_mode);
        boolean airplanePrefState = trigPref.getBoolean(ApplicationConstants.mTriggerAirplane, false);

        airplaneSwitch.setChecked(Utility.getPreferences(trigPref, ApplicationConstants.mTriggerAirplane));

        airplaneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Utility.setPreference(trigPref, ApplicationConstants.mTriggerAirplane, isChecked);

            }

        });

        // WifiData Switch
        wifiDataSwitch = (Switch)  findViewById(R.id.switch_wifi_data);

        wifiDataSwitch.setChecked(Utility.getPreferences(trigPref, ApplicationConstants.mTriggerWifidata));
        wifiDataSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Utility.setPreference(trigPref, ApplicationConstants.mTriggerWifidata, isChecked);
            }
        });


        qrBtnView = (Button) findViewById(R.id.btn_qrcode_view);
        qrBtnView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // ????? here I want to give the path of the page to be opened...
                if (v.getId() == R.id.btn_qrcode_view) {
                    Intent qrpage = new Intent(getApplicationContext(), QRCodeViewer.class);

                    startActivity(qrpage);

                }
            }
        });

        qrBtnScan = (Button) findViewById(R.id.btn_qrcode_scan);
        qrBtnScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // ????? here I want to give the path of the page to be opened...
                if(v.getId()==R.id.btn_qrcode_scan)
                {
                    Intent qrscan = new Intent(getApplicationContext(), QRCodeScanner.class);
                    startActivity(qrscan);

                }
            }
        });

    }

    // When Application is resumed, check for Play services support to make sure
    // app will be running normally
    @Override
    protected void onResume() {
        super.onResume();
       // checkPlayServices();
    }

    // Check if Google Playservices is installed in Device or not
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        // When Play services not found in device
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                // Show Error dialog to install Play services
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
              /*  Toast.makeText(
                        getApplicationContext(),
                        "This device doesn't support Play services, App will not work normally",
                        Toast.LENGTH_LONG).show();*/
                finish();
            }
            return false;
        } else {
           /* Toast.makeText(
                    getApplicationContext(),
                    "This device supports Play services, App will work normally",
                    Toast.LENGTH_LONG).show();*/
        }
        return true;
    }

    // Push notification from android device
    // Share RegID and Email ID with GCM Server Application (Php)
     public static void pushNotificationServer(String regIdPush2, String msgPush) {
//       pushDialog.show();
      params.put("message", msgPush);
        params.put("gcmId", regIdPush);
        System.out.println("Message = " + msgPush + " Reg Id = " + regIdPush);
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(ApplicationConstants.APP_SERVER_PUSH_URL, params,
                new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {

                        Toast.makeText(applicationContext,
                                "Push Notification Sent Successfully.",
                                Toast.LENGTH_LONG).show();

                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {

                        // When Http response code is '404'
                        if (statusCode == 404) {
                            Toast.makeText(applicationContext,
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(applicationContext,
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    applicationContext,
                                    "Unexpected Error occcured! [Most common Error: Device might "
                                            + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }

    /* Battery Percentage */
  /*  private void firstBatteryPercentage() {
        Intent intent = new Intent(GreetingActivity.this,BatteryService.class);
        startService(intent);
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int level = -1;
                if (currentLevel >= 0 && scale > 0) {
                    level = (currentLevel * 100) / scale;
                        batteryPercent.setText("Battery level " + level + "% <");

                }

            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }*/

   /* private void getBatteryPercentage(final int chk_level) {
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int level = -1;
                if (currentLevel >= 0 && scale > 0) {
                    level = (currentLevel * 100) / scale;
                    if ( level < chk_level){

                        msgPush = "Battery Low " + level + "%";
                     //   batteryPercent.setText("Battery level " + level + "% <");
                        pushDialog = new ProgressDialog(GreetingActivity.this);
                        // Set Progress Dialog Text
                        pushDialog.setMessage("Please wait...");
                        // Set Cancelable as False
                        pushDialog.setCancelable(false);

                        pushNotificationServer(regIdPush, msgPush);

                    }
                }

            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }
*/
    /* Mobile Data Check */
    public  boolean isDataEnabled(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(connManager.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            if((Boolean)method.invoke(connManager)){
                Toast.makeText(getApplicationContext(), "Mobile Data Enabled", Toast.LENGTH_SHORT).show();
                return true;
            }
            else{
                Toast.makeText(getApplicationContext(), "Mobile Data Disabled", Toast.LENGTH_SHORT).show();
                return false;
            }

        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }
    return false;
    }



}

