package com.example.milfittracker.ui.airforce;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
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
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import com.example.milfittracker.R;
import com.example.milfittracker.repo.UserRepo;
import com.example.milfittracker.room.MilFitDB;
import com.example.milfittracker.room.Scores;
import com.example.milfittracker.ui.log.ScoreViewModel;
import com.example.milfittracker.room.SetGoal;
import com.example.milfittracker.repo.SetGoalRepo;
import com.example.milfittracker.helpers.FormatTime;

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

        pushupPractice.setOnClickListener(v1 -> {
            Bundle args = new Bundle();
            args.putString("branch", "Air Force");
            args.putString("event", "Push-ups");
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.stopwatchFragment, args);
        });

        plankPractice.setOnClickListener(v1 -> {
            Bundle args = new Bundle();
            args.putString("branch", "Air Force");
            args.putString("event", "Plank");
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.stopwatchFragment, args);
        });

        runPractice.setOnClickListener(v1 -> {
            Bundle args = new Bundle();
            args.putString("branch", "Air Force");
            args.putString("event", "1.5-mile Run");
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.stopwatchFragment, args);
        });

        startFullMock.setOnClickListener(v1 -> {
            Bundle args = new Bundle();
            args.putString("branch", "Air Force");
            args.putBoolean("mock", true);
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.airforce_to_stopwatch, args);
        });

        vm = new ViewModelProvider(requireActivity()).get(ScoreViewModel.class);

        new SetGoalRepo(requireContext()).getAllLive().observe(
                getViewLifecycleOwner(), goals -> {
                    for (SetGoal g : goals) {
                        if ("Air Force".equalsIgnoreCase(g.getBranch())) {
                            switch (g.getEvent()) {
                                case "Push-ups":
                                    pushupTarget.setText("Target: " + g.getValue());
                                    break;
                                case "Plank":
                                    plankTarget.setText("Target: " + FormatTime.formatSeconds(g.getValue()));
                                    break;
                                case "1.5-mile Run":
                                    runTarget.setText("Target: " + FormatTime.formatSeconds(g.getValue()));
                                    break;
                            }
                        }
                    }
                });

        vm.getAllLive().observe(getViewLifecycleOwner(), list -> {
            Scores latestPush = latestForBranch(list, "Air Force", "Push-ups");
            if (latestPush != null) pushupLast.setText("Last: " + latestPush.getEventValue());

            Scores latestPlank = latestForBranch(list, "Air Force", "Plank");
            if (latestPlank != null) plankLast.setText("Last: " + FormatTime.formatSeconds(latestPlank.getEventValue()));

            Scores latestRun = latestForBranch(list, "Air Force", "1.5-mile Run");
            if (latestRun != null) runLast.setText("Last: " + FormatTime.formatSeconds(latestRun.getEventValue()));
        });

        btnStandards.setOnClickListener(vw -> {
            Bundle args = new Bundle();
            args.putString("branch", "AirForce");
            NavController navController = Navigation.findNavController(vw);
            navController.navigate(R.id.airforce_to_standards, args);
        });


        btnGoals.setOnClickListener(vw2 -> showGoalDialog());

        return v;
    }

    private void showGoalDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.set_goals_air_force, null);

        EditText inputPushups = dialogView.findViewById(R.id.input_pushups);
        EditText inputPlank = dialogView.findViewById(R.id.input_plank);
        EditText inputRun = dialogView.findViewById(R.id.input_run);

        new AlertDialog.Builder(requireContext())
                .setTitle("Set Air Force Goals")
                .setView(dialogView)
                .setPositiveButton("Save", (d, w) -> {
                    String pushStr = inputPushups.getText().toString().trim();
                    String plankStr = inputPlank.getText().toString().trim();
                    String runStr = inputRun.getText().toString().trim();

                    if (!pushStr.isEmpty()) {
                        int reps = Integer.parseInt(pushStr);
                        new SetGoalRepo(requireContext())
                                .save(new SetGoal("Air Force", "Push-ups", reps, "reps", null));
                    }

                    if (!plankStr.isEmpty()) {
                        int secs = parseMmSs(plankStr);
                        if (secs > 0) {
                            new SetGoalRepo(requireContext())
                                    .save(new SetGoal("Air Force", "Plank", secs, "sec", null));
                        }
                    }

                    if (!runStr.isEmpty()) {
                        int secs = parseMmSs(runStr);
                        if (secs > 0) {
                            new SetGoalRepo(requireContext())
                                    .save(new SetGoal("Air Force", "1.5-mile Run", secs, "sec", null));
                        }
                    }

                    Toast.makeText(requireContext(), "Goals saved", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveScore(String branch, String event, int value, String unit) {
        MilFitDB db = MilFitDB.getInstance(requireContext());
        UserRepo userRepo = new UserRepo(db);

        userRepo.getUser(user -> {
            if (user == null) {
                Toast.makeText(requireContext(), "No user profile found", Toast.LENGTH_SHORT).show();
                return;
            }

            Scores s = new Scores();
            s.setBranch(branch);
            s.setEvent(event);
            s.setGender(user.getGender());

            int age = 0;
            try {
                if (user.getBDay() != null) {
                    java.time.LocalDate birth = java.time.LocalDate.parse(user.getBDay());
                    age = java.time.Period.between(birth, java.time.LocalDate.now()).getYears();
                }
            } catch (Exception ignored) {
            }

            s.setAge(age);
            s.setEventValue(value);
            s.setUnit(unit);
            s.setDate(LocalDate.now().toString());

            new ViewModelProvider(requireActivity())
                    .get(com.example.milfittracker.ui.log.ScoreViewModel.class)
                    .insert(s);

            Toast.makeText(getContext(), "Saved " + event, android.widget.Toast.LENGTH_SHORT).show();
        });
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
    private Scores latestForBranch(List<Scores> list, String branch, String event) {
        if (list == null || list.isEmpty()) return null;
        return list.stream()
                .filter(s -> branch.equalsIgnoreCase(s.getBranch()) && event.equalsIgnoreCase(s.getEvent()))
                .max(Comparator.comparing(Scores::getDate))
                .orElse(null);
    }
}