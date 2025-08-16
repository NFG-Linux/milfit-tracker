package com.example.milfittracker.helpers;


import android.content.Context;
import android.content.SharedPreferences;
public class LaunchOrder {
    private static final String FILE = "milfit_prefs";
    private static final String KEY_WELCOME = "seen_welcome";
    private static final String KEY_ONBOARDED = "onboarded";

    private static SharedPreferences sp(Context c) {
        return c.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public static boolean seenWelcome(Context c) {
        return sp(c).getBoolean(KEY_WELCOME, false);
    }
    public static void setSeenWelcome(Context c, boolean v) {
        sp(c).edit().putBoolean(KEY_WELCOME, v).apply();
    }

    public static boolean onboarded(Context c) {
        return sp(c).getBoolean(KEY_ONBOARDED, false);
    }
    public static void setOnboarded(Context c, boolean v) {
        sp(c).edit().putBoolean(KEY_ONBOARDED, v).apply();
    }
}
