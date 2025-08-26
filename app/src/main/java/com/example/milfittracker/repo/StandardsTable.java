package com.example.milfittracker.repo;

import java.util.Map;
import java.util.List;

public class StandardsTable {
        private final Map<String, List<StandardEntry>> eventStandards;

        public StandardsTable(Map<String, List<StandardEntry>> eventStandards) {
            this.eventStandards = eventStandards;
        }

        public String getPerformanceLevel(String event, int value) {
            List<StandardEntry> entries = eventStandards.get(event);
            if (entries == null) return "Unknown";

            for (StandardEntry e : entries) {
                if (value >= e.min && value <= e.max) {
                    return e.level;
                }
            }
            return "Below minimum";
        }

        public int getPoints(String event, int value) {
            List<StandardEntry> entries = eventStandards.get(event);
            if (entries == null) return 0;

            for (StandardEntry e : entries) {
                if (value >= e.min && value <= e.max) {
                    return e.points;
                }
            }
            return 0;
        }

        public static class StandardEntry {
            public int min;
            public int max;
            public int points;
            public String level;
        }
}
