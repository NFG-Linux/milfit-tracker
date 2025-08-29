package com.example.milfittracker.ui.analysis;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.example.milfittracker.R;
import com.example.milfittracker.room.Scores;
import com.example.milfittracker.ui.log.ScoreViewModel;

public class TrendsFragment extends Fragment {
    private TextView Current, Best, Delta;
    private Button Export, Share;
    private List<Scores> cached = new ArrayList<>();
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trends, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Current = view.findViewById(R.id.Current);
        Best    = view.findViewById(R.id.Best);
        Delta   = view.findViewById(R.id.Delta);
        Export  = view.findViewById(R.id.Export);
        Share   = view.findViewById(R.id.Share);

        ScoreViewModel vm = new ViewModelProvider(requireActivity()).get(ScoreViewModel.class);
        vm.getAllLive().observe(getViewLifecycleOwner(), scores -> {
            cached = scores != null ? scores : new ArrayList<>();
            render(cached);
        });

        Export.setOnClickListener(vw -> exportCsv(false));
        Share.setOnClickListener(vw -> exportCsv(true));
    }

    private void render(List<Scores> list) {
        if (list.isEmpty()) {
            Current.setText("–");
            Best.setText("–");
            Delta.setText("0");
            return;
        }
        // Sort by date if you store ISO timestamps; otherwise by id desc
        List<Scores> sorted = new ArrayList<>(list);
        Collections.sort(sorted, (a,b) -> safeDate(b).compareTo(safeDate(a)));

        int current = safeValue(sorted.get(0));
        int best = 0;
        for (Scores scores : sorted) best = Math.max(best, safeValue(scores));

        // find latest score older than 30 days
        LocalDate cutoff = LocalDate.now().minusDays(30);
        Integer older = null;
        for (Scores s : sorted) {
            if (safeDate(s).isBefore(cutoff)) { older = safeValue(s); break; }
        }
        int delta = (older == null) ? 0 : (current - older);

        Current.setText(String.valueOf(current));
        Best.setText(String.valueOf(best));
        Delta.setText((delta >= 0 ? "+" : "") + delta);
    }

    private LocalDate safeDate(Scores scores) {
        try {
            return LocalDate.parse(scores.getDate());
        } catch (Exception e) {
            return LocalDate.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        }
    }
    private int safeValue(Scores scores) {
        try { return scores.getEventValue(); } catch (Exception e) { return 0; }
    }

    /** Build a CSV and either share (as text) or just copy to clipboard / toast */
    private void exportCsv(boolean share) {
        if (cached.isEmpty()) {
            Toast.makeText(requireContext(), "No data", Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("id,branch,event,gender,ageInt,eventValue,unit,date\n");
        for (Scores scores : cached) {
            sb.append(scores.getId()).append(',')
                    .append(esc(scores.getBranch())).append(',')
                    .append(esc(scores.getEvent())).append(',')
                    .append(esc(scores.getGender())).append(',')
                    .append(scores.getAge()).append(',')
                    .append(scores.getEventValue()).append(',')
                    .append(esc(scores.getUnit())).append(',')
                    .append(esc(scores.getDate())).append('\n');
        }
        String csv = sb.toString();

        if (share) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/csv");
            i.putExtra(Intent.EXTRA_SUBJECT, "MilFitTracker Scores");
            i.putExtra(Intent.EXTRA_TEXT, csv);
            startActivity(Intent.createChooser(i, "Share CSV"));
        } else {
            Toast.makeText(requireContext(), "CSV prepared (" + cached.size() + " rows). Use Share to send.", Toast.LENGTH_SHORT).show();
        }
    }

    private String esc(String string) {
        if (string == null) return "";
        if (string.contains(",") || string.contains("\"")) {
            return "\"" + string.replace("\"","\"\"") + "\"";
        }
        return string;
    }
}