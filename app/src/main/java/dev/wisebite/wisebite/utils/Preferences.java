package dev.wisebite.wisebite.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by albert on 14/04/17.
 * @author albert
 */
public class Preferences {

    private static final String PACKAGE_NAME = "dev.wisebite.wisebite";
    private static final String EMAIL_KEY = "user_email";

    private static SharedPreferences prefs;

    public static void init(Context context) {
        prefs = context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
    }

    public static String getCurrentUserEmail() {
        if (prefs == null){
            return "";
        }
        return prefs.getString(EMAIL_KEY, "");
    }

    public static void setCurrentUserEmail(String email) {
        if (prefs != null){
            prefs.edit().putString(EMAIL_KEY, email.split("\\.")[0]).apply();
        }
    }

}