package com.example.milfittracker.ui.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.example.milfittracker.R;

public class AnalysisFragment extends Fragment {

    private ViewPager2 pager;
    private TabLayout tabs;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analysis, container, false);
        pager = view.findViewById(R.id.pager);
        tabs  = view.findViewById(R.id.tabs);

        pager.setAdapter(new AnalysisPager(this));
        pager.setOffscreenPageLimit(1);

        new TabLayoutMediator(tabs, pager, (tab, pos) -> {
            tab.setText(pos == 0 ? "Trends" : (pos == 1 ? "Runs" : "Photos"));
        }).attach();

        return view;
    }
}