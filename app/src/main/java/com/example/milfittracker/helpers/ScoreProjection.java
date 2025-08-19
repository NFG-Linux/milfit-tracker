package com.example.milfittracker.helpers;


import java.time.LocalDate;
import java.util.List;
import com.example.milfittracker.room.Scores;

public class ScoreProjection {
    private String event;
    private int projectedValue;
    private String unit;
    private LocalDate forecastDate;

    public ScoreProjection(String event, int projectedValue, String unit, LocalDate forecastDate) {
        this.event = event;
        this.projectedValue = projectedValue;
        this.unit = unit;
        this.forecastDate = forecastDate;
    }

    public String getEvent() { return event; }
    public int getProjectedValue() { return projectedValue; }
    public String getUnit() { return unit; }
    public LocalDate getForecastDate() { return forecastDate; }
}
