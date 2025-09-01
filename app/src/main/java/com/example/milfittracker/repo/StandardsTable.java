package com.example.milfittracker.repo;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;


public class StandardsTable {
        private final Map<String, List<StandardEntry>> eventStandards;

        public StandardsTable(Map<String, List<StandardEntry>> eventStandards) {
            this.eventStandards = eventStandards;
        }

    public StandardsTable(String event, String branch, List<StandardsRepo.StandardEntry> entries) {
        Map<String, List<StandardsTable.StandardEntry>> map = new HashMap<>();
        List<StandardsTable.StandardEntry> converted = new ArrayList<>();

        for (StandardsRepo.StandardEntry e : entries) {
            converted.add(new StandardsTable.StandardEntry(
                    e.getPoints(),
                    e.getValue(),
                    e.getCategory()
            ));
        }

        String normalized = normalizeEventKey(event, branch);
        map.put(normalized, converted);
        this.eventStandards = map;
    }

        public String getPerformanceLevel(String event, int value) {
            List<StandardEntry> entries = eventStandards.get(event);
            if (entries == null) return "Unknown";

            for (StandardEntry e : entries) {
                if (value >= e.min && value <= e.max) {
                    return e.level;
                }
            }
            return "Failing";
        }

        public int getPoints(String event, int value) {
            List<StandardEntry> entries = eventStandards.get(normalizeEventKey(event, ""));
            if (entries == null || entries.isEmpty()) return 0;

            boolean isTimeEvent = event.toLowerCase().contains("run") || event.toLowerCase().contains("swim");

            boolean ascending = entries.get(0).getValue() < entries.get(entries.size() - 1).getValue();

            if (isTimeEvent) {
                if (ascending) {
                    for (StandardEntry e : entries) {
                        if (value <= e.getValue()) {
                            return e.getPoints();
                        }
                    }
                } else {
                    for (int i = entries.size() - 1; i >= 0; i--) {
                        if (value <= entries.get(i).getValue()) {
                            return entries.get(i).getPoints();
                        }
                    }
                }
            } else {
                if (ascending) {
                    for (int i = entries.size() - 1; i >= 0; i--) {
                        if (value >= entries.get(i).getValue()) {
                            return entries.get(i).getPoints();
                        }
                    }
                } else {
                    for (StandardEntry e : entries) {
                        if (value >= e.getValue()) {
                            return e.getPoints();
                        }
                    }
                }
            }
            return 0;
        }

        public String getUnit(String event) {
            List<StandardEntry> entries = eventStandards.get(event);
            if (entries != null && !entries.isEmpty()) {
                return entries.get(0).unit;
            }
            return "";
        }

    public String getCategoryForScore(String event, int value, String branch) {
        String normalized = normalizeEventKey(event, branch);
        List<StandardEntry> entries = eventStandards.get(normalized);
        if (entries == null || entries.isEmpty()) return "Unknown";

        String category = "Failing";
        boolean isTimeEvent = normalized.contains("run") || normalized.contains("swim");
        boolean ascending = entries.get(0).getValue() < entries.get(entries.size() - 1).getValue();

        if (isTimeEvent) {
            if (ascending) {
                for (StandardEntry e : entries) {
                    if (value <= e.getValue()) {
                        return e.getCategory();
                    }
                }
            } else {
                for (int i = entries.size() - 1; i >= 0; i--) {
                    if (value <= entries.get(i).getValue()) {
                        return entries.get(i).getCategory();
                    }
                }
            }
        } else {
            if (ascending) {
                for (int i = entries.size() - 1; i >= 0; i--) {
                    if (value >= entries.get(i).getValue()) {
                        return entries.get(i).getCategory();
                    }
                }
            } else {
                for (StandardEntry e : entries) {
                    if (value >= e.getValue()) {
                        return e.getCategory();
                    }
                }
            }
        }

        return "Fail";
    }

    private String normalizeEventKey(String event, String branch) {
        if (event == null) return null;
        String cleaned = event.toLowerCase().replace("-", "").replace(" ", "");
        switch (cleaned) {
            case "pushups":
                return "pushups";
            case "plank":
                return "plank_seconds";
            case "run":
            case "run_1_5mile_seconds":
            case "run_2mile_seconds":
            case "run_3mile_seconds":
                return "run";
            case "swim":
                return "swim_500yd_seconds";
            default:
                return event;
        }
    }

    public static class StandardEntry {
        private int min;
        private int max;
        private int points;
        private String level;
        private String unit;
        private int value;
        private String category;

        public StandardEntry(int points, int value, String category) {
            this.points = points;
            this.value = value;
            this.category = category;
        }

        public int getValue() { return value; }
        public String getCategory() { return category; }
        public int getPoints() { return points; }
    }
}
