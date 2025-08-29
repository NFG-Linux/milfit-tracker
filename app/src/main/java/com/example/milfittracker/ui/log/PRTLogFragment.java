package com.example.milfittracker.ui.log;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.example.milfittracker.R;
import com.example.milfittracker.repo.ScoreRepo;
import com.example.milfittracker.room.MilFitDB;
import com.example.milfittracker.room.Scores;

public class PRTLogFragment extends Fragment {

    private PRTScoreList adapter;
    private ScoreRepo scoreRepo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prt_log, container, false);

        RecyclerView recycler = view.findViewById(R.id.prt_log_recycler);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new PRTScoreList();
        recycler.setAdapter(adapter);

        MilFitDB db = MilFitDB.getInstance(requireContext());
        scoreRepo = new ScoreRepo(db);

        loadMockPRTData();

        return view;
    }

    private void loadMockPRTData() {
        scoreRepo.getAllSID(sessionIds -> {
            Map<String, List<Scores>> grouped = new HashMap<>();

            for (String sid : sessionIds) {
                scoreRepo.getScoresBySID(sid, scores -> {
                    if (scores != null && !scores.isEmpty()) {
                        grouped.put(sid, scores);

                        List<Map.Entry<String, List<Scores>>> entries =
                                new ArrayList<>(grouped.entrySet());

                        entries.sort((e1, e2) -> {
                            LocalDate d1 = LocalDate.parse(e1.getValue().get(0).getDate());
                            LocalDate d2 = LocalDate.parse(e2.getValue().get(0).getDate());
                            return d2.compareTo(d1);
                        });

                        Map<String, List<Scores>> sorted = new HashMap<>();
                        for (Map.Entry<String, List<Scores>> e : entries) {
                            sorted.put(e.getKey(), e.getValue());
                        }

                        adapter.submit(sorted);
                    }
                });
            }
        });
    }
}