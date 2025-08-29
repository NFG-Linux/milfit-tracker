package com.example.milfittracker.ui.log;

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

public class LogFragment extends Fragment {

    private ViewPager2 pager;
    private TabLayout tabs;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log, container, false);
        pager = view.findViewById(R.id.pager);
        tabs  = view.findViewById(R.id.tabs);

        pager.setAdapter(new LogPager(this));

        new TabLayoutMediator(tabs, pager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("PRTs"); break;
                case 1: tab.setText("Pushups"); break;
                case 2: tab.setText("Planks"); break;
                case 3: tab.setText("Runs"); break;
            }
        }).attach();

        return view;
    }
}