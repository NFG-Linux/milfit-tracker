package com.example.milfittracker.ui.log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.example.milfittracker.R;
import com.example.milfittracker.room.Scores;

public class PRTScoreList extends RecyclerView.Adapter<PRTScoreList.VH>{
    private final List<Map.Entry<String, List<Scores>>> sessions = new ArrayList<>();

    public void submit(Map<String, List<Scores>> grouped) {
        sessions.clear();
        if (grouped != null) sessions.addAll(grouped.entrySet());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.score_prt, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Map.Entry<String, List<Scores>> entry = sessions.get(pos);
        List<Scores> scores = entry.getValue();

        if (scores.isEmpty()) return;

        Scores first = scores.get(0);
        h.branch.setText(first.getBranch());
        h.date.setText(first.getDate());

        h.pushups.setText("-");
        h.plank.setText("-");
        h.run.setText("-");

        for (Scores s : scores) {
            switch (s.getEvent()) {
                case "Push-ups":
                    h.pushups.setText("Push-up Reps: " + s.getEventValue() + " " + s.getUnit());
                    break;
                case "Plank":
                    h.plank.setText("Plank Time: " + formatSeconds(s.getEventValue()));
                    break;
                case "1.5-mile Run":
                case "2-mile Run":
                case "3-mile Run":
                    h.run.setText("Run Time: " + formatSeconds(s.getEventValue()));
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView branch, date, pushups, plank, run;

        VH(@NonNull View itemView) {
            super(itemView);
            branch = itemView.findViewById(R.id.prtBranch);
            date   = itemView.findViewById(R.id.prtDate);
            pushups = itemView.findViewById(R.id.prtPushups);
            plank   = itemView.findViewById(R.id.prtPlank);
            run     = itemView.findViewById(R.id.prtRun);
        }
    }

    private static String formatSeconds(int secs) {
        int m = secs / 60;
        int s = secs % 60;
        return String.format(Locale.US, "%d:%02d", m, s);
    }

}
