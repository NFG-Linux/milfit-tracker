package com.example.milfittracker.ui.analysis;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.milfittracker.R;

public class AnalysisRunsFragment extends Fragment {
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle b){
        View view = new View(requireContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(-1,-1));
        return view;
    }
}