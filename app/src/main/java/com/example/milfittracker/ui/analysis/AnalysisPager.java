package com.example.milfittracker.ui.analysis;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AnalysisPager extends FragmentStateAdapter {
    public AnalysisPager(@NonNull Fragment parent) { super(parent); }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) return new TrendsFragment();
        if (position == 1) return new AnalysisRunsFragment();
        return new PhotosFragment();
    }

    @Override
    public int getItemCount() { return 3; }
}
