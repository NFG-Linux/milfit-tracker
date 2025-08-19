package com.example.milfittracker.forecasting;

import java.util.HashMap;
import java.util.Map;
import com.example.milfittracker.helpers.ScoreProjection;

public class ForecastResult {
    private boolean valid;
    private String message;
    private String branch;

    private Map<String, ScoreProjection> projections = new HashMap<>();

    public ForecastResult() {
        this.valid = true;
    }

    public ForecastResult(String message) {
        this.valid = true;
        this.message = message;
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }

    public String getBranch() {
        return branch;
    }

    public Map<String, ScoreProjection> getProjections() {
        return projections;
    }

    public void addProjection(String label, ScoreProjection proj) {
        projections.put(label, proj);
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ForecastResult insufficientData() {
        ForecastResult r = new ForecastResult();
        r.valid = false;
        r.message = "Insufficient data to generate forecast";
        return r;
    }
}
