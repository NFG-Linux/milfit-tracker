package com.example.milfittracker.helpers;


import java.time.LocalDate;
import java.util.List;
import com.example.milfittracker.room.Scores;

public class ScoreProjection {
    private String event;
    private int projectedValue;
    private int points;
    private String unit;
    private String category;
    private LocalDate forecastDate;

    public ScoreProjection(String event, int projectedValue, String category, LocalDate forecastDate) {
        this.event = event;
        this.projectedValue = projectedValue;
        this.category = category;
        this.forecastDate = forecastDate;
    }

    public ScoreProjection(String event, int projectedValue, int points, String category, LocalDate forecastDate) {
        this.event = event;
        this.projectedValue = projectedValue;
        this.points = points;
        this.category = category;
        this.forecastDate = forecastDate;
    }

    public String getEvent() {
        return event;
    }

    public String getCategory() {
        return category;
    }

    public int getPoints() {
        return points;
    }

    public int getProjectedValue() {
        return projectedValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getForecastDate() {
        return forecastDate;
    }
}
