package com.example.milfittracker.helpers;

import java.util.List;

public class ForecastEngine {
    public static double nextValue(List<Integer> scores){
        int n = scores.size();
        if (n < 2) return scores.isEmpty()?0:scores.get(n-1);
        double sx=0, sy=0, sxy=0, sxx=0;
        for (int i=0;i<n;i++){
            double x=i, y=scores.get(i);
            sx+=x; sy+=y; sxy+=x*y; sxx+=x*x;
        }
        double b = (n*sxy - sx*sy) / (n*sxx - sx*sx);
        double a = (sy - b*sx) / n;
        double nextX = n;
        return a + b*nextX;
    }
}
