package com.example.milfittracker.repo;

import android.content.Context;
import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class StandardsRepo {
    public static StandardsRoot loadStandards(Context context, String filename) {
        InputStream is = null;
        InputStreamReader reader = null;
        try {
            is = context.getAssets().open(filename);
            reader = new InputStreamReader(is);

            Gson gson = new Gson();
            return gson.fromJson(reader, StandardsRoot.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (reader != null) reader.close();
                if (is != null) is.close();
            } catch (Exception ignored) {}
        }
    }

    public static class StandardsRoot {
        private Map<String, Map<String, GenderStandards>> altitude;
        public Map<String, Map<String, GenderStandards>> getAltitude() {
            return altitude;
        }
    }

    public static class GenderStandards {
        private Map<String, AgeGroup> male;
        private Map<String, AgeGroup> female;

        public Map<String, AgeGroup> getMale() {
            return male;
        }
        public Map<String, AgeGroup> getFemale() {
            return female;
        }
    }

    public static class AgeGroup {
        private List<StandardEntry> pushups;
        private List<StandardEntry> plank_seconds;
        private List<StandardEntry> run_1_5mile_seconds;
        private List<StandardEntry> run_2mile_seconds;
        private List<StandardEntry> run_3mile_seconds;
        private List<StandardEntry> swim_500yd_seconds;

        public List<StandardEntry> getPushups() {
            return pushups;
        }
        public List<StandardEntry> getPlank_seconds() {
            return plank_seconds;
        }
        public List<StandardEntry> getRun_1_5mile_seconds() {
            return run_1_5mile_seconds;
        }
        public List<StandardEntry> getRun_2mile_seconds() {
            return run_2mile_seconds;
        }
        public List<StandardEntry> getRun_3mile_seconds() {
            return run_3mile_seconds;
        }
        public List<StandardEntry> getSwim_500yd_seconds() {
            return swim_500yd_seconds;
        }
    }

    public static class StandardEntry {
        private int points;
        private int value;
        private String category;

        public int getPoints() {
            return points;
        }
        public int getValue() {
            return value;
        }
        public String getCategory() {
            return category;
        }
    }
}
