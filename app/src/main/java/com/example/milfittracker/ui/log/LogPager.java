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
        return (position == 0) ? new PRTLogFragment()
                : new RunsFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
