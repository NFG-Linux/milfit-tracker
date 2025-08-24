package com.example.milfittracker.ui.stopwatch;

import android.os.Bundle;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;
import com.example.milfittracker.R;
import com.example.milfittracker.room.Scores;
import com.example.milfittracker.room.MilFitDB;
import com.example.milfittracker.room.User;
import com.example.milfittracker.repo.UserRepo;


public class StopwatchFragment extends Fragment {

    private TextView timerText, lastLapText;
    private Button btnStartStop, btnPause, btnLap, btnReset, btnRest, btnComplete;
    private ListView lapsList;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean running = false;
    private long startRealtime = 0L;
    private long accumulatedMillis = 0L;
    private long lastLapMark = 0L;

    private ArrayAdapter<String> lapsAdapter;
    private final ArrayList<String> laps = new ArrayList<>();
    private String event;
    private String branch;

    private final Runnable ticker = new Runnable() {
        @Override public void run() {
            if (!running) return;
            long elapsed = currentElapsed();
            timerText.setText(formatTime(elapsed));
            handler.postDelayed(this, 50);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stopwatch, container, false);

        if (getArguments() != null) {
            branch = getArguments().getString("branch", "");
            event = getArguments().getString("event", "");
        }

        timerText = v.findViewById(R.id.timer);
        lastLapText = v.findViewById(R.id.last_lap);
        btnStartStop = v.findViewById(R.id.start_stop);
        btnPause = v.findViewById(R.id.pause);
        btnLap = v.findViewById(R.id.lap);
        btnReset = v.findViewById(R.id.reset);
        btnRest = v.findViewById(R.id.rest);
        lapsList = v.findViewById(R.id.list_laps);
        btnComplete = v.findViewById(R.id.session_complete);

        if (event != null && !event.isEmpty()) {
            startPracticeMode(event);
        }

        lapsAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, laps);
        lapsList.setAdapter(lapsAdapter);

        btnStartStop.setOnClickListener(v1 -> toggleStartStop());
        btnPause.setOnClickListener(v10 -> togglePause());
        btnLap.setOnClickListener(v12 -> addLap());
        btnReset.setOnClickListener(v13 -> resetAll());
        btnRest.setOnClickListener(v14 -> {
            if (running) {
                running = false;
                accumulatedMillis += SystemClock.elapsedRealtime() - startRealtime;
                btnStartStop.setText("Start");
            }
            new RestDialog().show(getParentFragmentManager(), "rest");
        });

        btnComplete.setOnClickListener(v1 -> {
            if (event == null || branch == null) return;

            long elapsedMillis = currentElapsed();

            if (event.equals("Plank")) {
                int secs = (int) (elapsedMillis / 1000);
                saveScore(branch, event, secs, "sec");
            } else if (event.equals("1.5-mile Run")) {
                int secs = (int) (elapsedMillis / 1000);
                saveScore(branch, event, secs, "sec");
            }

            requireActivity().onBackPressed();
        });

        timerText.setText(formatTime(currentElapsed()));
        btnStartStop.setText(running ? "Stop" : "Start");
        lastLapText.setText(formatTime(0));
        return v;
    }

    private void startPracticeMode(String event) {
        btnStartStop.setEnabled(false);
        btnPause.setEnabled(false);
        btnLap.setEnabled(false);
        btnReset.setEnabled(false);

        new android.os.CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerText.setText("Starting in " + (millisUntilFinished / 1000));
            }
            public void onFinish() {
                btnStartStop.setEnabled(true);
                btnPause.setEnabled(true);
                btnReset.setEnabled(true);

                if (event.equals("Push-ups")) {
                    startPushupCountdown();
                } else {
                    toggleStartStop();
                }
            }
        }.start();
    }

    private void startPushupCountdown() {
        new android.os.CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                long sec = millisUntilFinished / 1000;
                timerText.setText(String.format(Locale.US, "00:%02d", sec));
            }
            public void onFinish() {
                timerText.setText("00:00");
                promptPushupReps();
            }
        }.start();
    }

    private void promptPushupReps() {
        android.widget.EditText input = new android.widget.EditText(getContext());
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Push-ups completed")
                .setView(input)
                .setPositiveButton("Save", (d, w) -> {
                    int reps = Integer.parseInt(input.getText().toString().trim());
                    saveScore(branch, event, reps, "reps");
                    requireActivity().onBackPressed();
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
            s.setDate(LocalDateTime.now().toString());

            new ViewModelProvider(requireActivity())
                    .get(com.example.milfittracker.ui.log.ScoreViewModel.class)
                    .insert(s);

            Toast.makeText(getContext(), "Saved " + event, android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUIStopped() {
        running = false;
        accumulatedMillis = 0L;
        startRealtime = 0L;
        lastLapMark = 0L;

        if (timerText != null) timerText.setText(formatTime(0));

        if (btnStartStop != null) btnStartStop.setText("Start");

        if (lastLapText != null) lastLapText.setText("00:00.00");
        if (laps != null) {
            laps.clear();
            if (lapsAdapter != null) lapsAdapter.notifyDataSetChanged();
        }
    }

    private void toggleStartStop() {
        if (running) {
            running = false;
            accumulatedMillis += SystemClock.elapsedRealtime() - startRealtime;
            btnStartStop.setText("Start");
        } else {
            running = true;
            startRealtime = SystemClock.elapsedRealtime();
            handler.post(ticker);
            btnStartStop.setText("Stop");
        }
    }

    private void togglePause() {
        if (running) {
            running = false;
            accumulatedMillis += SystemClock.elapsedRealtime() - startRealtime;
            btnStartStop.setText("Start");
        }
    }

    private void addLap() {
        long total = currentElapsed();
        long lap = total - lastLapMark;
        lastLapMark = total;
        String entry = String.format(Locale.US, "Lap %d — %s",
                laps.size() + 1, formatTime(lap));
        laps.add(0, entry);
        lapsAdapter.notifyDataSetChanged();
        lastLapText.setText(getString(R.string.last_lap_fmt, formatTime(lap)));
    }

    private void resetAll() {
        running = false;
        accumulatedMillis = 0L;
        lastLapMark = 0L;
        timerText.setText(formatTime(0));
        lastLapText.setText(getString(R.string.last_lap_fmt, formatTime(0)));
        laps.clear();
        lapsAdapter.notifyDataSetChanged();
        btnStartStop.setText("Start");
    }

    private long currentElapsed() {
        if (running) {
            return accumulatedMillis + (SystemClock.elapsedRealtime() - startRealtime);
        } else {
            return accumulatedMillis;
        }
    }

    private static String formatTime(long millis) {
        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        long hundredths = (millis % 1000) / 10;
        return String.format(Locale.US, "%02d:%02d.%02d", minutes, seconds, hundredths);
    }

    public static class RestDialog extends DialogFragment {

        private final Handler handler = new Handler(Looper.getMainLooper());
        private boolean running;
        private long startRealtime;
        private long total;

        private TextView restTime;
        private final Runnable tick = new Runnable() {
            @Override public void run() {
                if (!running) return;
                long elapsed = total + (SystemClock.elapsedRealtime() - startRealtime);
                restTime.setText(formatTime(elapsed));
                handler.postDelayed(this, 100);
            }
        };

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.rest_timer, container, false);
            restTime = view.findViewById(R.id.rest_time);

            view.findViewById(R.id.rest_start).setOnClickListener(v10 -> {
                if (!running) {
                    running = true;
                    startRealtime = SystemClock.elapsedRealtime();
                    handler.post(tick);
                }
            });

            view.findViewById(R.id.rest_stop).setOnClickListener(v1 -> {
                if (running) {
                    running = false;
                    total = total + (SystemClock.elapsedRealtime() - startRealtime);
                }
            });
            view.findViewById(R.id.rest_reset).setOnClickListener(v12 -> {
                running = false;
                total = 0L;
                restTime.setText(formatTime(0));
            });
            view.findViewById(R.id.rest_done).setOnClickListener(v13 -> dismiss());

            // auto-start
            running = true;
            startRealtime = SystemClock.elapsedRealtime();
            handler.post(tick);
            return view;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            running = false;
            handler.removeCallbacksAndMessages(null);
        }
    }
}