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

import com.example.milfittracker.R;
import com.example.milfittracker.room.Scores;
import com.example.milfittracker.helpers.FormatTime;

public class ScoreList extends RecyclerView.Adapter<ScoreList.VH> {
    private final List<Scores> data = new ArrayList<>();

    public void submit(List<Scores> list) {
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.score, parent, false);
        return new VH(view);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Scores scores = data.get(pos);

        String value;
        if (scores.getUnit().equals("sec") || scores.getUnit().equals("time")) {
            value = FormatTime.formatSeconds(scores.getEventValue());
        } else {
            value = scores.getEventValue() + " " + scores.getUnit();
        }

        String title = scores.getEvent() + " • " + scores.getBranch();
        String sub = value + " • " + scores.getDate();
        h.title.setText(title);
        h.sub.setText(sub);
    }

    @Override public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView title, sub;
        VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.Title);
            sub   = itemView.findViewById(R.id.Sub);
        }
    }
}
