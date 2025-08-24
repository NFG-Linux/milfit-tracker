package com.example.milfittracker.ui.airforce;

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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
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

public class airForceFragment extends Fragment {

    private ScoreViewModel vm;
    private TextView pushupTarget, pushupLast;
    private TextView plankTarget, plankLast;
    private TextView runTarget, runLast;

    @Nullable
    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_air_force, container, false);

        Button btnStandards = v.findViewById(R.id.Standards);
        Button btnMock      = v.findViewById(R.id.start_full_mock);
        Button btnGoals     = v.findViewById(R.id.Goals);

        pushupTarget = v.findViewById(R.id.pushup_target);
        pushupLast = v.findViewById(R.id.pushup_last);

        plankTarget = v.findViewById(R.id.plank_target);
        plankLast = v.findViewById(R.id.plank_last);

        runTarget = v.findViewById(R.id.run_target);
        runLast = v.findViewById(R.id.run_last);

        Button pushupPractice = v.findViewById(R.id.pushup_practice);
        Button plankPractice = v.findViewById(R.id.plank_practice);
        Button runPractice = v.findViewById(R.id.run_practice);
        Button startFullMock = v.findViewById(R.id.start_full_mock);

        pushupPractice.setOnClickListener(v1 ->
                Toast.makeText(getContext(), "Push-up Practice Started", Toast.LENGTH_SHORT).show());

        plankPractice.setOnClickListener(v1 ->
                Toast.makeText(getContext(), "Plank Practice Started", Toast.LENGTH_SHORT).show());

        runPractice.setOnClickListener(v1 ->
                Toast.makeText(getContext(), "Run Practice Started", Toast.LENGTH_SHORT).show());

        startFullMock.setOnClickListener(v1 ->
                Toast.makeText(getContext(), "Full Mock PT Test Started", Toast.LENGTH_SHORT).show());

        vm = new ViewModelProvider(requireActivity()).get(ScoreViewModel.class);

        vm.getAllLive().observe(getViewLifecycleOwner(), list -> {
            Scores latestPush = latestForBranch(list, "Air Force", "Push-ups");
            if (latestPush != null) pushupLast.setText("Last: " + latestPush.getEventValue() + " " + latestPush.getUnit());

            Scores latestPlank = latestForBranch(list, "Air Force", "Plank");
            if (latestPlank != null) plankLast.setText("Last: " + latestPlank.getEventValue() + " " + latestPlank.getUnit());

            Scores latestRun = latestForBranch(list, "Air Force", "1.5-mile Run");
            if (latestRun != null) runLast.setText("Last: " + formatSeconds(latestRun.getEventValue()));
        });

        btnStandards.setOnClickListener(vw -> {
            NavController navController = Navigation.findNavController(vw);
            navController.navigate(R.id.pdfViewerFragment);
        });


        btnGoals.setOnClickListener(vw ->
                btnGoals.setOnClickListener(vw2 -> showGoalDialog()));

        btnMock.setOnClickListener(vw -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.start_full_mock);
        });

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

                    SetGoal goal = new SetGoal("Air Force", event, val, unit, selectedDate[0]);
                    new SetGoalRepo(requireContext()).save(goal);
                    Toast.makeText(requireContext(), "Goal saved", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveScore(String event, int value, String unit) {
        Scores s = new Scores();
        s.setBranch("Air Force");
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

    private String formatSeconds(int secs) {
        int m = secs / 60;
        int s = secs % 60;
        return String.format("%d:%02d", m, s);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Scores latestForBranch(List<Scores> list, String branch, String event) {
        if (list == null || list.isEmpty()) return null;
        return list.stream()
                .filter(s -> branch.equalsIgnoreCase(s.getBranch()) && event.equalsIgnoreCase(s.getEvent()))
                .max(Comparator.comparing(Scores::getDate))
                .orElse(null);
    }
}