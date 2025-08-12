package com.example.milfittracker.ui.more;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import java.util.List;
import java.util.Locale;
import com.example.milfittracker.helpers.AppExec;
import com.example.milfittracker.R;
import com.example.milfittracker.repo.ScoreRepo;
import com.example.milfittracker.room.MilFitDB;
import com.example.milfittracker.room.Scores;
import com.example.milfittracker.ui.onboarding.OnboardingActivity;

public class MoreFragment extends PreferenceFragmentCompat {

    private ScoreRepo scoreRepo;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.more, rootKey);

        Context ctx = requireContext().getApplicationContext();
        scoreRepo = new ScoreRepo(MilFitDB.getInstance(ctx));

        // Profile
        findPreference("edit_profile").setOnPreferenceClickListener(p -> {
            startActivity(new Intent(requireContext(), OnboardingActivity.class));
            return true;
        });

        // Units (nothing to do here yet—stored in SharedPreferences automatically)
        ListPreference units = findPreference("units_key");
        if (units != null) units.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());

        // Altitude mode
        ListPreference altitude = findPreference("altitude_key");
        if (altitude != null) altitude.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());

        // Theme
        ListPreference theme = findPreference("theme_key");
        if (theme != null) {
            theme.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
            theme.setOnPreferenceChangeListener((pref, newValue) -> {
                String v = String.valueOf(newValue);
                switch (v) {
                    case "light":  AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);  break;
                    case "dark":   AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); break;
                    default:       AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }
                return true;
            });
        }

        // Export CSV
        findPreference("export_csv").setOnPreferenceClickListener(p -> {
            exportCsv();
            return true;
        });

        // Reset scores only
        findPreference("reset_scores").setOnPreferenceClickListener(p -> {
            confirmAndRun("Delete all logs?",
                    "This will remove all PRTs and Runs. This cannot be undone.",
                    this::resetScores);
            return true;
        });

        // Reset everything (scores + goals + users if you want; here just scores for demo)
        findPreference("reset_all").setOnPreferenceClickListener(p -> {
            confirmAndRun("Reset all app data?",
                    "This will clear logs and local settings (not permissions).",
                    this::resetScores);
            return true;
        });

        // Permissions → app info page
        findPreference("permissions").setOnPreferenceClickListener(p -> {
            openAppSettings();
            return true;
        });

        // Privacy Policy (stub: open a link or local asset)
        findPreference("privacy").setOnPreferenceClickListener(p -> {
            openUrl("https://example.com/privacy");
            return true;
        });

        // About (simple toast stub—you can show a dialog)
        findPreference("about").setOnPreferenceClickListener(p -> {
            Toast.makeText(requireContext(),
                    "MilFit Tracker v0.1 • © 2025", Toast.LENGTH_LONG).show();
            return true;
        });
    }

    private void exportCsv() {
        // Fetch on background; share on main
        scoreRepo.getAll(list -> {
            if (list == null || list.isEmpty()) {
                Toast.makeText(requireContext(), "No data to export", Toast.LENGTH_SHORT).show();
                return;
            }
            String csv = buildCsv(list);
            shareText(csv, "milfit_export.csv", "text/csv");
        });
    }

    private String buildCsv(@NonNull List<Scores> data) {
        StringBuilder sb = new StringBuilder();
        sb.append("id,branch,event,gender,ageInt,eventValue,unit,date\n");
        for (Scores s : data) {
            sb.append(s.getId()).append(',')
                    .append(csv(s.getBranch())).append(',')
                    .append(csv(s.getEvent())).append(',')
                    .append(csv(s.getGender())).append(',')
                    .append(s.getAge()).append(',')
                    .append(s.getEventValue()).append(',')
                    .append(csv(s.getUnit())).append(',')
                    .append(csv(s.getDate()))
                    .append('\n');
        }
        return sb.toString();
    }

    private static String csv(String v){
        if (v == null) return "";
        String out = v.replace("\"","\"\"");
        return "\"" + out + "\"";
    }

    private void shareText(String content, String filename, String mime){
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType(mime);
        send.putExtra(Intent.EXTRA_SUBJECT, "MilFit Tracker Export");
        send.putExtra(Intent.EXTRA_TEXT, content);
        startActivity(Intent.createChooser(send, "Share CSV"));
    }

    private void resetScores() {
        scoreRepo.clearAll(() ->
                Toast.makeText(requireContext(), "Logs cleared", Toast.LENGTH_SHORT).show());
    }

    private void openAppSettings() {
        try {
            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.setData(Uri.fromParts("package", requireContext().getPackageName(), null));
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), "Unable to open settings", Toast.LENGTH_SHORT).show();
        }
    }

    private void openUrl(String url){
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e){
            Toast.makeText(requireContext(), "No browser found", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmAndRun(String title, String msg, Runnable yesAction){
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(msg)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", (d, w) -> yesAction.run())
                .show();
    }
}