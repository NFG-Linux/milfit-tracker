package com.example.milfittracker.ui.navy;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.milfittracker.R;

public class MockPRTNavyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mock_prt_navy, container, false);

        TextView pushupTarget = v.findViewById(R.id.pushup_target);
        TextView pushupLast = v.findViewById(R.id.pushup_last);

        TextView plankTarget = v.findViewById(R.id.plank_target);
        TextView plankLast = v.findViewById(R.id.plank_last);

        TextView runTarget = v.findViewById(R.id.run_target);
        TextView runLast = v.findViewById(R.id.run_last);

        CheckBox checkBeep = v.findViewById(R.id.check_beep);
        CheckBox checkVibrate = v.findViewById(R.id.check_vibrate);

        Button pushupPractice = v.findViewById(R.id.pushup_practice);
        Button pushupRest = v.findViewById(R.id.pushup_rest);
        Button plankPractice = v.findViewById(R.id.plank_practice);
        Button plankRest = v.findViewById(R.id.plank_rest);
        Button runPractice = v.findViewById(R.id.run_practice);
        Button runRest = v.findViewById(R.id.run_rest);
        Button startFullMock = v.findViewById(R.id.start_full_mock);

        pushupPractice.setOnClickListener(v1 ->
                Toast.makeText(getContext(), "Push-up Practice Started", Toast.LENGTH_SHORT).show());

        pushupRest.setOnClickListener(v1 ->
                Toast.makeText(getContext(), "Push-up Rest", Toast.LENGTH_SHORT).show());

        plankPractice.setOnClickListener(v1 ->
                Toast.makeText(getContext(), "Plank Practice Started", Toast.LENGTH_SHORT).show());

        plankRest.setOnClickListener(v1 ->
                Toast.makeText(getContext(), "Plank Rest", Toast.LENGTH_SHORT).show());

        runPractice.setOnClickListener(v1 ->
                Toast.makeText(getContext(), "Run Practice Started", Toast.LENGTH_SHORT).show());

        runRest.setOnClickListener(v1 ->
                Toast.makeText(getContext(), "Run Rest", Toast.LENGTH_SHORT).show());

        startFullMock.setOnClickListener(v1 -> {
            boolean beep = checkBeep.isChecked();
            boolean vibrate = checkVibrate.isChecked();
            Toast.makeText(getContext(),
                    "Starting Full Mock (Beep=" + beep + ", Vibrate=" + vibrate + ")",
                    Toast.LENGTH_LONG).show();
        });

        return v;
    }
}