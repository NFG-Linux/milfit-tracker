package com.example.milfittracker.ui.navy;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import com.example.milfittracker.R;
import com.example.milfittracker.room.Scores;
import com.example.milfittracker.ui.log.ScoreViewModel;
import com.example.milfittracker.room.SetGoal;
import com.example.milfittracker.repo.SetGoalRepo;
import com.example.milfittracker.room.MilFitDB;

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
                Toast.makeText(requireContext(), "Standards dialog TBD", Toast.LENGTH_SHORT).show());


        btnGoals.setOnClickListener(vw ->
                btnGoals.setOnClickListener(vw2 -> showGoalDialog()));

        btnMock.setOnClickListener(vw ->
                Toast.makeText(requireContext(), "Mock PRT flow TBD", Toast.LENGTH_SHORT).show());

        // Quick input dialogs
        btnRun.setOnClickListener(vw -> showRunDialog());
        btnPush.setOnClickListener(vw -> showRepsDialog("Push-ups", "reps"));
        btnPlank.setOnClickListener(vw -> showTimeDialog("Plank", "sec"));

        return v;
    }

    private void showGoalDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_set_goal, null);
        EditText inputValue = dialogView.findViewById(R.id.input_value);
        EditText inputUnit = dialogView.findViewById(R.id.input_unit);
        EditText inputEvent = dialogView.findViewById(R.id.input_event);
        Button dateBtn = dialogView.findViewById(R.id.select_date_btn);

        final String[] selectedDate = {null};
        dateBtn.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (dp, y, m, d) -> {
                selectedDate[0] = String.format("%04d-%02d-%02d", y, m + 1, d);
                dateBtn.setText(selectedDate[0]);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        new AlertDialog.Builder(requireContext())
                .setTitle("Set Goal")
                .setView(dialogView)
                .setPositiveButton("Save", (d, w) -> {
                    String event = inputEvent.getText().toString().trim();
                    String valStr = inputValue.getText().toString().trim();
                    String unit = inputUnit.getText().toString().trim();
                    if (event.isEmpty() || valStr.isEmpty() || unit.isEmpty() || selectedDate[0] == null) {
                        Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int val = Integer.parseInt(valStr);

                    SetGoal goal = new SetGoal("Navy", event, val, unit, selectedDate[0]);
                    new SetGoalRepo(requireContext()).save(goal);
                    Toast.makeText(requireContext(), "Goal saved", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

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