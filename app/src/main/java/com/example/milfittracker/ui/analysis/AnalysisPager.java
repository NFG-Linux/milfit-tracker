package com.example.milfittracker.ui.analysis;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.milfittracker.forecasting.PhotosFragment;

public class AnalysisPager extends FragmentStateAdapter {
    public AnalysisPager(@NonNull Fragment parent) {
        super(parent);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new TrendsFragment();
            case 1:
                return new ForecastingFragment();
            case 2:
                return new PhotosFragment();
            default:
                return new TrendsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
