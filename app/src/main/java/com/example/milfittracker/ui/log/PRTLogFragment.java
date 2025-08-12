package com.example.milfittracker.ui.log;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import com.example.milfittracker.R;
import com.example.milfittracker.helpers.AppExec;
import com.example.milfittracker.repo.ScoreRepo;
import com.example.milfittracker.room.MilFitDB;
import com.example.milfittracker.room.Scores;

public class PRTLogFragment extends Fragment {

    private ScoreList adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prt_log, container, false);

        RecyclerView rv = view.findViewById(R.id.list);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter = new ScoreList();
        rv.setAdapter(adapter);

        loadData();

        return view;
    }

    private void loadData() {
        ScoreRepo repo = new ScoreRepo(MilFitDB.getInstance(requireContext().getApplicationContext()));
        repo.getAll(list -> {
            // Filter out run sessions. Adjust to your event names if needed.
            List<Scores> PRTOnly = new ArrayList<>();
            for (Scores s : list) {
                String e = s.getEvent() == null ? "" : s.getEvent().toLowerCase();
                if (!e.contains("run")) { // keep push-ups, sit-ups, plank, etc.
                    PRTOnly.add(s);
                }
            }
            // Ensure UI on main thread
            AppExec.getInstance().main(() -> adapter.submit(PRTOnly));
        });
    }
}