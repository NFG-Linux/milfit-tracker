package com.example.milfittracker.ui.analysis;

import android.graphics.Color;
import android.os.Bundle;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.example.milfittracker.R;
import com.example.milfittracker.room.MilFitDB;
import com.example.milfittracker.room.Scores;
import com.example.milfittracker.repo.SetGoalRepo;
import com.example.milfittracker.repo.UserRepo;
import com.example.milfittracker.helpers.FormatTime;

public class TrendsFragment extends Fragment {
    private LinearLayout eventsContainer;
    private MilFitDB db;
    private List<Scores> cached = new ArrayList<>();
    private SetGoalRepo goalRepo;
    private UserRepo userRepo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventsContainer = view.findViewById(R.id.eventsContainer);
        db = MilFitDB.getInstance(requireContext());
        goalRepo = new SetGoalRepo(requireContext());
        userRepo = new UserRepo(db);

        Button export = view.findViewById(R.id.Export);
        Button share = view.findViewById(R.id.Share);

        export.setOnClickListener(v -> exportCsv(false));
        share.setOnClickListener(v -> exportCsv(true));

        loadTrends(getLayoutInflater());
    }

    private void loadTrends(LayoutInflater inflater) {
        db.scoreDAO().getDistinctEvents().observe(getViewLifecycleOwner(), eventList -> {
            eventsContainer.removeAllViews();
            cached.clear();

            for (String event : eventList) {
                View itemView = inflater.inflate(R.layout.event_trend_item, eventsContainer, false);
                TextView title = itemView.findViewById(R.id.eventTitle);
                title.setText(event);

                db.scoreDAO().observeByEvent(event).observe(getViewLifecycleOwner(), history -> {
                    if (history != null && !history.isEmpty()) {
                        updateEventUI(itemView, event, history);

                        synchronized (cached) {
                            cached.removeIf(s -> s.getEvent().equals(event));
                            cached.addAll(history);
                        }
                    }
                });

                eventsContainer.addView(itemView);
            }
        });
    }

    private void updateEventUI(View itemView, String event, List<Scores> history) {
        TextView tvCurrent = itemView.findViewById(R.id.tvCurrent);
        TextView tvBest = itemView.findViewById(R.id.tvBest);
        TextView tvDelta = itemView.findViewById(R.id.tvDelta);
        LineChart chart = itemView.findViewById(R.id.chart);

        Collections.sort(history, Comparator.comparing(Scores::getDate));

        Scores lastScore = history.get(history.size() - 1);

        int best;
        if (event.toLowerCase().contains("run")) {
            best = history.stream()
                    .mapToInt(Scores::getEventValue)
                    .min()
                    .orElse(0);
        } else {
            best = history.stream()
                    .mapToInt(Scores::getEventValue)
                    .max()
                    .orElse(0);
        }

        if (event.equalsIgnoreCase("Plank") || event.toLowerCase().contains("run")) {
            tvCurrent.setText(FormatTime.formatSeconds(lastScore.getEventValue()));
            tvBest.setText(FormatTime.formatSeconds(best));
        } else {
            tvCurrent.setText(String.valueOf(lastScore.getEventValue()));
            tvBest.setText(String.valueOf(best));
        }

        LocalDate cutoff = LocalDate.now().minusDays(30);
        List<Scores> last30 = new ArrayList<>();
        for (Scores s : history) {
            LocalDate d = LocalDate.parse(s.getDate());
            if (!d.isBefore(cutoff)) {
                last30.add(s);
            }
        }
        if (last30.size() > 1) {
            int delta = last30.get(last30.size() - 1).getEventValue()
                    - last30.get(0).getEventValue();
            if (event.equalsIgnoreCase("Plank") || event.toLowerCase().contains("run")) {
                tvDelta.setText((delta >= 0 ? "+" : "") + FormatTime.formatSeconds(Math.abs(delta)));
            } else {
                tvDelta.setText((delta >= 0 ? "+" : "") + delta);
            }
        } else {
            tvDelta.setText("–");
        }

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < history.size(); i++) {
            entries.add(new Entry(i, history.get(i).getEventValue()));
        }
        LineDataSet dataSet = new LineDataSet(entries, event + " Trend");
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        userRepo.getUser(currentUser -> {
            if (currentUser != null) {
                String userBranch = currentUser.getBranch();

                goalRepo.getForBranchEvent(userBranch, event, goal -> {
                    if (goal != null) {
                        int goalValue = goal.getValue();

                        LimitLine goalLine = new LimitLine(goalValue, "Goal");
                        goalLine.setLineColor(Color.GREEN);
                        goalLine.enableDashedLine(10f, 10f, 0f);
                        goalLine.setLineWidth(2f);
                        goalLine.setTextColor(Color.GREEN);
                        goalLine.setTextSize(10f);

                        chart.getAxisLeft().removeAllLimitLines();
                        chart.getAxisLeft().addLimitLine(goalLine);
                        chart.post(chart::invalidate);
                    }
                });
            }
        });

        chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                int index = Math.round(value);
                if (index >= 0 && index < history.size()) {
                    return history.get(index).getDate();
                }
                return "";
            }
        });

        if (event.equalsIgnoreCase("Plank") || event.toLowerCase().contains("run")) {
            chart.getAxisLeft().setValueFormatter(new ValueFormatter() {
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    return FormatTime.formatSeconds((int) value);
                }
            });
            chart.getAxisRight().setEnabled(false);
        }

        chart.getXAxis().setGranularity(1f);
        chart.invalidate();
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLACK);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawValues(false);

        chart.getDescription().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getLegend().setEnabled(false);
    }

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