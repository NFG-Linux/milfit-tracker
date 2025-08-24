package com.example.milfittracker.ui.stopwatch;

import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.milfittracker.R;

import java.util.ArrayList;
import java.util.Locale;

public class StopwatchFragment extends Fragment {

    private TextView timerText, lastLapText;
    private Button btnStartStop, btnPause, btnLap, btnReset, btnRest;
    private ListView lapsList;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean running = false;
    private long startRealtime = 0L;
    private long accumulatedMillis = 0L;
    private long lastLapMark = 0L;

    private ArrayAdapter<String> lapsAdapter;
    private final ArrayList<String> laps = new ArrayList<>();

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

        timerText = v.findViewById(R.id.timer);
        lastLapText = v.findViewById(R.id.last_lap);
        btnStartStop = v.findViewById(R.id.start_stop);
        btnPause = v.findViewById(R.id.pause);
        btnLap = v.findViewById(R.id.lap);
        btnReset = v.findViewById(R.id.reset);
        btnRest = v.findViewById(R.id.rest);
        lapsList = v.findViewById(R.id.list_laps);

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

        timerText.setText(formatTime(currentElapsed()));
        btnStartStop.setText(running ? "Stop" : "Start");
        lastLapText.setText(formatTime(0));
        return v;
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