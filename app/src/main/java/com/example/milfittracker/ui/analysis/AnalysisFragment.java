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
        View root = inflater.inflate(R.layout.fragment_analysis, container, false);

        tabs = root.findViewById(R.id.tabs);
        pager = root.findViewById(R.id.pager);

        AnalysisPager adapter = new AnalysisPager(this);
        pager.setAdapter(adapter);

        new TabLayoutMediator(tabs, pager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Trends"); break;
                case 1: tab.setText("Forecasting"); break;
                case 2: tab.setText("Photos"); break;
            }
        }).attach();

        return root;
    }
}