package com.example.milfittracker.forecasting;

import android.util.Log;
import android.content.Context;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import com.example.milfittracker.helpers.ScoreProjection;
import com.example.milfittracker.repo.StandardsRepo;
import com.example.milfittracker.repo.StandardsTable;
import com.example.milfittracker.room.Scores;
import com.example.milfittracker.room.User;

public class ForecastEngine {
    private static final String TAG = "ForecastEngine";
    private final StandardsRepo standardsRepo;

    public ForecastEngine(Context context) {
        this.standardsRepo = new StandardsRepo(context);
    }

    public ForecastResult forecast(List<Scores> history) {

        return forecastWithContext(history, null, null);
    }

    public ForecastResult forecastWithContext(List<Scores> history, User user, String event) {
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
        String gender = user != null ? user.getGender() : "Unspecified";

        int age = 0;
        LocalDate birth = LocalDate.parse(user.getBDay());
        age = (int) ChronoUnit.YEARS.between(birth, LocalDate.now());

        String prtDateStr = user != null ? user.getPRT() : null;
        int daysToPrt = -1;
        if (prtDateStr != null) {
            try {
                LocalDate prt = LocalDate.parse(prtDateStr);
                daysToPrt = (int) ChronoUnit.DAYS.between(LocalDate.now(), prt);
            } catch (Exception e) {
                Log.w(TAG, "Invalid PRT date: " + prtDateStr);
            }
        }

        int projected = (int) Math.round(latest + slope * Math.max(0, daysToPrt / 7.0));
        ScoreProjection proj = new ScoreProjection(event != null ? event : "General", projected, "reps", LocalDate.now());

        String readiness = assessReadiness(branch, gender, age, event, projected);

        ForecastResult result = new ForecastResult("Forecast generated");
        result.addProjection(event != null ? event : "General", proj);
        result.setBranch(branch);

        if (daysToPrt >= 0) {
            result.setMessage("PRT in " + daysToPrt + " days. " + readiness);
        } else {
            result.setMessage("Forecast for branch " + branch + ". " + readiness);
        }

        return result;
    }

    private String assessReadiness(String branch, String gender, int age, String event, int projected) {
        try {
            Map<String, Map<String, Map<String, List<StandardsTable.StandardEntry>>>> all =
                    standardsRepo.loadStandards(branch);

            if (all == null) return "No standards data available.";

            Map<String, Map<String, List<StandardsTable.StandardEntry>>> genderMap =
                    all.get(gender.toLowerCase());

            if (genderMap == null) return "No standards for gender " + gender;

            for (String range : genderMap.keySet()) {
                if (isInRange(age, range)) {
                    Map<String, List<StandardsTable.StandardEntry>> eventMap = genderMap.get(range);
                    StandardsTable table = new StandardsTable(eventMap);

                    String level = table.getPerformanceLevel(event, projected);
                    int points = table.getPoints(event, projected);

                    return "Projected: " + level + " (" + points + " pts)";
                }
            }

            return "No matching age range.";
        } catch (Exception e) {
            Log.e(TAG, "Error assessing readiness", e);
            return "Error assessing standards.";
        }
    }

    private boolean isInRange(int age, String ageRange) {
        try {
            String[] parts = ageRange.split("-");
            int low = Integer.parseInt(parts[0]);
            int high = Integer.parseInt(parts[1]);
            return age >= low && age <= high;
        } catch (Exception e) {
            return false;
        }
    }
}
