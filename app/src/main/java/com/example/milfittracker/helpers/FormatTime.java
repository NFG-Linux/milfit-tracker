package com.example.milfittracker.helpers;

import java.util.Locale;
public class FormatTime {
    public static String formatSeconds(int secs) {
        int m = secs / 60;
        int s = secs % 60;
        return String.format(Locale.US, "%d:%02d", m, s);
    }
}
