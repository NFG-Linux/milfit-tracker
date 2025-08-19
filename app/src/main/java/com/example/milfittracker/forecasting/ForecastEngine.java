package com.example.milfittracker.forecasting;

import android.util.Log;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.example.milfittracker.helpers.ScoreProjection;
import com.example.milfittracker.room.Scores;
import com.example.milfittracker.room.User;

public class ForecastEngine {
    private static final String TAG = "ForecastEngine";

    public ForecastResult forecast(List<Scores> history) {
        return forecastWithContext(history, null);
    }

    public ForecastResult forecastWithContext(List<Scores> history, User user) {
        if (history == null || history.isEmpty()) {
            return new ForecastResult("No data available");
        }

        List<Scores> sorted = new ArrayList<>(history);
        Collections.sort(sorted, Comparator.comparing(Scores::getDate));
        int n = sorted.size();

        List<Integer> vals = new ArrayList<>();
        for (Scores s : sorted) {
            vals.add(s.getEventValue());
        }

        double avg = vals.stream().mapToInt(Integer::intValue).average().orElse(0);
        int latest = vals.get(vals.size() - 1);

        double slope = (n > 1)
                ? (vals.get(n - 1) - vals.get(0)) / (double)(n - 1)
                : 0;

        String branch = user != null ? user.getBranch() : "Unspecified";
        String prtDateStr = user != null ? user.getPRT() : null;
        long daysToPrt = -1;
        if (prtDateStr != null) {
            try {
                LocalDate prt = LocalDate.parse(prtDateStr);
                daysToPrt = ChronoUnit.DAYS.between(LocalDate.now(), prt);
            } catch (Exception e) {
                Log.w(TAG, "Invalid PRT date: " + prtDateStr);
            }
        }

        int projected = (int) Math.round(latest + slope * Math.max(0, daysToPrt / 7.0));
        ScoreProjection proj = new ScoreProjection("General", projected, "reps", LocalDate.now());

        String readiness = assessReadiness(branch, projected);

        ForecastResult result = new ForecastResult("Forecast generated");
        result.addProjection("General", proj);
        result.setBranch(branch);

        if (daysToPrt >= 0) {
            result.setMessage("PRT in " + daysToPrt + " days. " + readiness);
        } else {
            result.setMessage("Forecast for branch " + branch + ". " + readiness);
        }

        return result;
    }

    private String assessReadiness(String branch, int projected) {
        // Simplified thresholds for demo purposes:
        Map<String, Integer> minimums = new HashMap<>();
        minimums.put("Navy", 40);
        minimums.put("Air Force", 45);
        minimums.put("Army", 35);
        minimums.put("Marines", 50);

        int min = minimums.getOrDefault(branch, 40);
        return projected >= min
                ? "On track to PASS " + branch + " standards."
                : "Risk of falling short for " + branch + " standards.";
    }
}
