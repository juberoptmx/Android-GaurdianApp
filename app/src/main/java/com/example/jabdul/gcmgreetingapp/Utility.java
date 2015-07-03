package com.example.jabdul.gcmgreetingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jabdul on 5/20/2015.
 */
public class Utility {
    private static Pattern pattern;
    private static Matcher matcher;
    //Email Pattern
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * Validate Email with regular expression
     *
     * @param email
     * @return true for Valid Email and false for Invalid Email
     */
    public static boolean validate(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void showShortToast(Context context, String message){
        Toast.makeText(context,message,60000).show();
    }

    public static SharedPreferences IntializeSharedPreference(Context context,String key){

        return  context.getSharedPreferences(key,Context.MODE_PRIVATE);

    }
    public static void setPreference(SharedPreferences pref,String key,boolean val){

        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, val);
        editor.commit();

    }

    public static boolean getPreferences(SharedPreferences pref, String key){

        return pref.getBoolean(key,false);
    }


}
