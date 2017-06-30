package com.unipd.fabio.agorun;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.SharedPreferences.Editor;

public class MySharedPreferencesHandler {

    public static class MyPreferencesKeys {
        public static final String userData = "UserData";
        public static final String username = "username";
        public static final String password = "password";
        public static final String joinedActivityHour = "joinedActivityHour";
        public static final String joinActivityDate = "joinedActivityDate";
    }

    public static void putSharedPreferencesInt(Context context, String key, int value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void putSharedPreferencesString(Context context, String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static int getMySharedPreferencesInt(Context context, String key, int value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, value);
    }

    public static String getMySharedPreferencesString(Context context, String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, value);
    }

}
