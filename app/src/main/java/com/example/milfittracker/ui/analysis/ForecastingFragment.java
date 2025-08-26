package com.example.milfittracker.ui.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import java.util.Locale;
import com.example.milfittracker.R;
import com.example.milfittracker.forecasting.ForecastingViewModel;
import com.example.milfittracker.forecasting.ForecastingViewModelFactory;
import com.example.milfittracker.helpers.ScoreProjection;
import com.example.milfittracker.repo.ScoreRepo;
import com.example.milfittracker.repo.UserRepo;
import com.example.milfittracker.room.MilFitDB;

public class ForecastingFragment extends Fragment {
    private LinearLayout projectionsContainer;
    private TextView forecastTxt;
    private Button runForecast;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecasting, container, false);

        forecastTxt = view.findViewById(R.id.forecast_text);
        projectionsContainer = view.findViewById(R.id.projections_container);
        runForecast = view.findViewById(R.id.btn_run_forecast);

        MilFitDB db = MilFitDB.getInstance(requireContext());
        ScoreRepo scoreRepo = new ScoreRepo(db);
        UserRepo userRepo = new UserRepo(db);

        ForecastingViewModelFactory factory =
                new ForecastingViewModelFactory(requireActivity().getApplication(), scoreRepo, userRepo);

        ForecastingViewModel vm = new ViewModelProvider(this, factory)
                .get(ForecastingViewModel.class);

        vm.getForecastLive().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                String display = result.getMessage() != null
                        ? result.getMessage()
                        : "Forecast ready";
                forecastTxt.setText(display);

                projectionsContainer.removeAllViews();

                if (result.getProjections() != null) {
                    for (String event : result.getProjections().keySet()) {
                        ScoreProjection proj = result.getProjections().get(event);

                        String valueText;
                        String unit = proj.getUnit();

                        if (event.equalsIgnoreCase("Plank") || event.contains("Run")) {
                            valueText = formatSeconds(proj.getProjectedValue());
                            unit = "mins";
                        } else {
                            valueText = String.valueOf(proj.getProjectedValue());
                        }

                        TextView tv = new TextView(requireContext());
                        tv.setText(event + ": " + valueText + " " + unit);
                        tv.setTextSize(16f);
                        tv.setPadding(8, 8, 8, 8);

                        projectionsContainer.addView(tv);
                    }
                }
            }
        });

        runForecast.setOnClickListener(v -> vm.runForecast());

        return view;
    }

    private String formatSeconds(int secs) {
        int m = secs / 60;
        int s = secs % 60;
        return String.format(Locale.US, "%d:%02d", m, s);
    }
}