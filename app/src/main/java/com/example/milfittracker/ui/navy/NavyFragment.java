package com.example.milfittracker.ui.navy;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import com.example.milfittracker.R;
import com.example.milfittracker.room.Scores;
import com.example.milfittracker.ui.log.ScoreViewModel;

public class NavyFragment extends Fragment {

    private ScoreViewModel vm;
    private TextView LastScore;
    private TextView LastDate;

    @Nullable
    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_navy, container, false);

        LastScore = v.findViewById(R.id.LastScore);
        LastDate  = v.findViewById(R.id.LastDate);

        Button btnStandards = v.findViewById(R.id.Standards);
        Button btnRun       = v.findViewById(R.id.Run);
        Button btnPush      = v.findViewById(R.id.Pushups);
        Button btnPlank     = v.findViewById(R.id.Plank);
        Button btnMock      = v.findViewById(R.id.Mock);
        Button btnGoals     = v.findViewById(R.id.Goals);

        vm = new ViewModelProvider(requireActivity()).get(ScoreViewModel.class);

        // Observe and show latest Navy entry (any event)
        vm.getAllLive().observe(getViewLifecycleOwner(), list -> {
            Scores latest = latestForBranch(list, "Navy");
            if (latest != null) {
                LastScore.setText(latest.getEvent() + ": " + latest.getEventValue() + " " + latest.getUnit());
                LastDate.setText(latest.getDate());
            } else {
                LastScore.setText("No entries yet");
                LastDate.setText("—");
            }
        });

        btnStandards.setOnClickListener(vw ->
                Toast.makeText(requireContext(), "Standards screen TBD", Toast.LENGTH_SHORT).show());

        btnGoals.setOnClickListener(vw ->
                Toast.makeText(requireContext(), "Goals dialog TBD", Toast.LENGTH_SHORT).show());

        btnMock.setOnClickListener(vw ->
                Toast.makeText(requireContext(), "Mock PRT flow TBD", Toast.LENGTH_SHORT).show());

        // Quick input dialogs
        btnRun.setOnClickListener(vw -> showRunDialog());
        btnPush.setOnClickListener(vw -> showRepsDialog("Push-ups", "reps"));
        btnPlank.setOnClickListener(vw -> showTimeDialog("Plank", "sec"));

        return v;
    }

    // ---------- dialogs / saving ----------

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveScore(String event, int value, String unit) {
        Scores s = new Scores();
        s.setBranch("Navy");
        s.setEvent(event);
        s.setGender("Unspecified");
        s.setAge(0);
        s.setEventValue(value);
        s.setUnit(unit);
        s.setDate(LocalDateTime.now().toString());

        vm.insert(s);
        Toast.makeText(requireContext(), "Saved " + event, Toast.LENGTH_SHORT).show();
    }

    private void showRepsDialog(String title, String unit) {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Enter reps");
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setView(input)
                .setPositiveButton("Save", (d, w) -> {
                    String t = input.getText().toString().trim();
                    if (!t.isEmpty()) {
                        int reps = Integer.parseInt(t);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            saveScore(title, reps, unit);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showTimeDialog(String title, String unit) {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Seconds");

        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setView(input)
                .setPositiveButton("Save", (d, w) -> {
                    String t = input.getText().toString().trim();
                    if (!t.isEmpty()) {
                        int secs = Integer.parseInt(t);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            saveScore(title, secs, unit);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showRunDialog() {
        // Simple mm:ss input -> seconds
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("mm:ss");

        new AlertDialog.Builder(requireContext())
                .setTitle("1.5-mile Run (mm:ss)")
                .setView(input)
                .setPositiveButton("Save", (d, w) -> {
                    String t = input.getText().toString().trim();
                    int secs = parseMmSs(t);
                    if (secs >= 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        saveScore("1.5-mile Run", secs, "sec");
                    } else {
                        Toast.makeText(requireContext(), "Format mm:ss", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    //helpers
    private int parseMmSs(String string) {
        if (string == null || !string.contains(":")) return -1;
        try {
            String[] p = string.split(":");
            int m = Integer.parseInt(p[0].trim());
            int sec = Integer.parseInt(p[1].trim());
            return m * 60 + sec;
        } catch (Exception e) {
            return -1;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Scores latestForBranch(List<Scores> list, String branch) {
        if (list == null || list.isEmpty()) return null;
        return list.stream()
                .filter(s -> branch.equalsIgnoreCase(s.getBranch()))
                .max(Comparator.comparing(Scores::getDate)) // ISO-8601 works lexically
                .orElse(null);
    }
}