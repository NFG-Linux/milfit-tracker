package com.example.milfittracker.ui.log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class LogPager extends FragmentStateAdapter {
    public LogPager(@NonNull Fragment parent) {
        super(parent);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new PRTLogFragment();
            case 1: return new PushupLogFragment();
            case 2: return new PlankLogFragment();
            case 3: return new RunsFragment();
            default: return new PRTLogFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
