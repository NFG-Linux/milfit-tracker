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
import com.example.milfittracker.helpers.FormatTime;
import com.example.milfittracker.repo.StandardsRepo;
import com.example.milfittracker.repo.StandardsTable;
import com.example.milfittracker.room.Scores;
import com.example.milfittracker.room.User;

public class ForecastEngine {
    private static final String TAG = "ForecastEngine";
    private final Context context;

    public ForecastEngine(Context context) {
        this.context = context;
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

        String branch = user.getBranch();
        String gender = user.getGender();

        int age = 0;

        assert user.getBDay() != null;

        LocalDate birth = LocalDate.parse(user.getBDay());
        age = (int) ChronoUnit.YEARS.between(birth, LocalDate.now());

        String prtDateStr = user.getPRT();
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

        String ages = mapAgeToRange(age);
        StandardsTable table = assessReadiness(context, branch, gender, ages, event, false);

        String category = "Unknown";
        int points = 0;

        if (table != null) {
            category = table.getCategoryForScore(event, projected, branch);
            points = table.getPoints(event, projected);
        }

        String feedback = getFeedbackMessage(category);

        ScoreProjection proj = new ScoreProjection(
                event != null ? event : "General",
                projected, null, LocalDate.now()
        );

        proj.setCategory(category);
        proj.setPoints(points);

        ForecastResult result = new ForecastResult("Forecast generated");
        result.addProjection(event != null ? event : "General", proj);
        result.setBranch(branch);

        if (daysToPrt >= 0) {
            result.setMessage("PRT in " + daysToPrt + " days. " + category + ". " + feedback);
        } else {
            result.setMessage("Forecast for branch " + branch + ". " + category + ". " + feedback);
        }

        return result;
    }

    private String getFeedbackMessage(String category) {
        if (category == null) return "Unknown";

        category = category.toLowerCase();

        if (category.startsWith("outstanding")) {
            return "You will have no problem exceeding this event.";
        } else if (category.startsWith("excellent")) {
            return "You have a great chance of meeting and exceeding standards for this event.";
        } else if (category.startsWith("good")) {
            return "You will most likely meet standards for this event.";
        } else if (category.startsWith("satisfactory") || category.startsWith("probationary")) {
            return "You are on the brink of not passing. Recommend focusing efforts towards this event.";
        } else {
            return "Performance below standards. Significant improvement required.";
        }
    }

    private StandardsTable assessReadiness(Context context, String branch, String gender, String ageRange, String event, boolean isHighAltitude) {
        StandardsRepo.StandardsRoot root = StandardsRepo.loadStandards(context, branch + ".json");
        if (root == null) {
            return null;
        }

        try {
            String altitudeKey = isHighAltitude ? "high" : "low";

            Map<String, StandardsRepo.GenderStandards> altitudeMap = root.getAltitude().get(altitudeKey);
            if (altitudeMap == null) return null;

            StandardsRepo.GenderStandards standards = altitudeMap.get("standards");
            if (standards == null) return null;


            Map<String, StandardsRepo.AgeGroup> ageMap;
            if (gender.equalsIgnoreCase("male")) {
                ageMap = standards.getMale();
            } else {
                ageMap = standards.getFemale();
            }
            if (ageMap == null) return null;

            StandardsRepo.AgeGroup group = ageMap.get(ageRange);
            if (group == null) return null;

            List<StandardsRepo.StandardEntry> entries = null;
            switch (event.toLowerCase()) {
                case "push-ups":
                    entries = group.getPushups();
                    break;
                case "plank":
                    entries = group.getPlank_seconds();
                    break;
                case "1.5-mile run":
                    entries = group.getRun_1_5mile_seconds();
                    break;
                case "2-mile run":
                    entries = group.getRun_2mile_seconds();
                    break;
                case "3-mile run":
                    entries = group.getRun_3mile_seconds();
                    break;
                case "swim":
                    entries = group.getSwim_500yd_seconds();
                    break;
            }

            if (entries == null) return null;

            return new StandardsTable(event, branch, entries);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String mapAgeToRange(int age) {
        if (age >= 17 && age <= 19) return "17-19";
        if (age >= 20 && age <= 24) return "20-24";
        if (age >= 25 && age <= 29) return "25-29";
        if (age >= 30 && age <= 34) return "30-34";
        if (age >= 35 && age <= 39) return "35-39";
        if (age >= 40 && age <= 44) return "40-44";
        if (age >= 45 && age <= 49) return "45-49";
        if (age >= 50 && age <= 54) return "50-54";
        if (age >= 55 && age <= 59) {
            return "55-59";
        } else {
            return "60+";
        }
    }

    public ForecastResult forecastMockPRT(User user,
                                          List<Scores> pushupHistory,
                                          List<Scores> plankHistory,
                                          List<Scores> cardioHistory) {
        ForecastResult pushupResult = forecastWithContext(pushupHistory, user, "push-ups");
        ForecastResult plankResult  = forecastWithContext(plankHistory, user, "plank");

        String runEvent;
        switch (user.getBranch()) {
            case "Army":
                runEvent = "2-mile run";
                break;
            case "Marines":
                runEvent = "3-mile run";
                break;
            default:
                runEvent = "1.5-mile run";
                break;
        }
        ForecastResult runResult    = forecastWithContext(cardioHistory, user, runEvent);

        ScoreProjection pushupProj = pushupResult.getProjections().get("push-ups");
        ScoreProjection plankProj  = plankResult.getProjections().get("plank");
        ScoreProjection runProj    = runResult.getProjections().get(runEvent);

        String ageRange = mapAgeToRange(
                (int) ChronoUnit.YEARS.between(LocalDate.parse(user.getBDay()), LocalDate.now())
        );

        StandardsTable pushupTable = assessReadiness(context, user.getBranch(), user.getGender(), ageRange, "push-ups", false);
        StandardsTable plankTable  = assessReadiness(context, user.getBranch(), user.getGender(), ageRange, "plank", false);
        StandardsTable runTable    = assessReadiness(context, user.getBranch(), user.getGender(), ageRange, runEvent, false);

        int pushupPoints = (pushupProj != null && pushupTable != null)
                ? pushupTable.getPoints("push-ups", pushupProj.getProjectedValue())
                : 0;

        int plankPoints = (plankProj != null && plankTable != null)
                ? plankTable.getPoints("plank", plankProj.getProjectedValue())
                : 0;

        int runPoints = (runProj != null && runTable != null)
                ? runTable.getPoints(runEvent, runProj.getProjectedValue())
                : 0;

        String pushupCat = (pushupProj != null && pushupTable != null)
                ? pushupTable.getCategoryForScore("push-ups", pushupProj.getProjectedValue(), user.getBranch())
                : "Unknown";

        String plankCat = (plankProj != null && plankTable != null)
                ? plankTable.getCategoryForScore("plank", plankProj.getProjectedValue(), user.getBranch())
                : "Unknown";

        String runCat = (runProj != null && runTable != null)
                ? runTable.getCategoryForScore(runEvent, runProj.getProjectedValue(), user.getBranch())
                : "Unknown";

        int avgPoints = (pushupPoints + plankPoints + runPoints) / 3;
        String overallCat = mapPointsToCategory(avgPoints);

        assert pushupProj != null;
        assert plankProj != null;
        assert runProj != null;

        if (isBelowProbationary(pushupCat) || isBelowProbationary(plankCat) || isBelowProbationary(runCat)) {
            ForecastResult overall = new ForecastResult("Mock PRT Result: FAIL");
            overall.setMessage("Mock PRT Result: FAIL\nAt least one event is below Probationary. You will not pass the PRT.\n\n" +
                    "--- Projected Event Scores ---\n" +
                    "Overall Score: " + avgPoints + " (" + overallCat + ")\n" +
                    "Pushups: " + pushupProj.getProjectedValue() + " (" + pushupCat + ", " + pushupPoints + " pts)\n" +
                    "Plank: " + FormatTime.formatSeconds(plankProj.getProjectedValue()) + " (" + plankCat + ", " + plankPoints + " pts)\n" +
                    "Run: " + FormatTime.formatSeconds(runProj.getProjectedValue()) + " (" + runCat + ", " + runPoints + " pts)"
            );
            return overall;
        } else if (isBelowGoodLow(pushupCat) || isBelowGoodLow(plankCat) || isBelowGoodLow(runCat)) {
            ForecastResult overall = new ForecastResult("Mock PRT Result: PASS (FEP)");
            overall.setMessage("Mock PRT Result: PASS (FEP)\nAt least one event is Satisfactory or Probationary. You will pass, but be placed on FEP.\n\n" +
                    "--- Projected Event Scores ---\n" +
                    "Overall Score: " + avgPoints + " (" + overallCat + ")\n" +
                    "Pushups: " + pushupProj.getProjectedValue() + " (" + pushupCat + ", " + pushupPoints + " pts)\n" +
                    "Plank: " + FormatTime.formatSeconds(plankProj.getProjectedValue()) + " (" + plankCat + ", " + plankPoints + " pts)\n" +
                    "Run: " + FormatTime.formatSeconds(runProj.getProjectedValue()) + " (" + runCat + ", " + runPoints + " pts)"
            );
            return overall;
        } else {

            ForecastResult overall = new ForecastResult("Mock PRT Result: " + overallCat);
            overall.setMessage("Based on your scores, you will pass the PRT!\n\n" +
                    "--- Projected Event Scores ---\n" +
                    "Overall Score: " + avgPoints + " (" + overallCat + ")\n" +
                    "Pushups: " + pushupProj.getProjectedValue() + " (" + pushupCat + ", " + pushupPoints + " pts)\n" +
                    "Plank: " + FormatTime.formatSeconds(plankProj.getProjectedValue()) + " (" + plankCat + ", " + plankPoints + " pts)\n" +
                    "Run: " + FormatTime.formatSeconds(runProj.getProjectedValue()) + " (" + runCat + ", " + runPoints + " pts)"
            );
            return overall;
        }
    }

    private boolean isBelowProbationary(String category) {
        return category != null && category.equalsIgnoreCase("fail");
    }

    private boolean isBelowGoodLow(String category) {
        if (category == null) return false;
        category = category.toLowerCase();
        return category.contains("satisfactory") || category.contains("probationary");
    }

    private String mapPointsToCategory(int points) {
        String category = "";
        if (points == 100) category = "Outstanding High";
        if (points >= 95 && points < 100) category = "Outstanding Medium";
        if (points >= 90 && points < 95) category =  "Outstanding Low";
        if (points >= 85 && points < 90) category =  "Excellent High";
        if (points >= 80 && points < 85) category =  "Excellent Medium";
        if (points >= 75 && points < 80) category =  "Excellent Low";
        if (points >= 70 && points < 75) category =  "Good High";
        if (points >= 65 && points < 70) category =  "Good Medium";
        if (points >= 60 && points < 65) category =  "Good Low";
        if (points >= 55 && points < 60) category =  "Satisfactory High";
        if (points >= 50 && points < 55) category =  "Satisfactory Medium";
        if (points >= 45 && points < 50) category =  "Probationary";
        if (points <= 44) category = "Fail";

        return category;
    }

    private String formatProjection(ScoreProjection proj, String event) {
        if (proj == null) return event + ": No data";

        String valueText;
        if (event.equalsIgnoreCase("Plank") || event.contains("Run")) {
            valueText = FormatTime.formatSeconds(proj.getProjectedValue());
        } else {
            valueText = String.valueOf(proj.getProjectedValue());
        }

        return event + ": " + valueText + " (" + proj.getCategory() + ", " + proj.getPoints() + " pts)";
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
