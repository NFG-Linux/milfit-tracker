package com.example.milfittracker.ui.log;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.example.milfittracker.R;
import com.example.milfittracker.room.Scores;

public class PlankLogFragment extends Fragment {
    private ScoreViewModel vm;
    private ScoreList scores;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score_list, container, false);

        RecyclerView recycler = view.findViewById(R.id.recycler_view);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        scores = new ScoreList();
        recycler.setAdapter(scores);

        vm = new ViewModelProvider(requireActivity()).get(ScoreViewModel.class);

        vm.observeByEvent("Plank").observe(getViewLifecycleOwner(), this::updateScores);

        return view;
    }

    private void updateScores(List<Scores> list) {
        scores.submit(list);
    }
}