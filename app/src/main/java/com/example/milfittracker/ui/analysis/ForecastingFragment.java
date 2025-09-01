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
import com.example.milfittracker.R;
import com.example.milfittracker.forecasting.ForecastingViewModel;
import com.example.milfittracker.forecasting.ForecastingViewModelFactory;
import com.example.milfittracker.helpers.ScoreProjection;
import com.example.milfittracker.helpers.FormatTime;
import com.example.milfittracker.repo.ScoreRepo;
import com.example.milfittracker.repo.UserRepo;
import com.example.milfittracker.room.MilFitDB;

public class ForecastingFragment extends Fragment {
    private LinearLayout projectionsContainer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecasting, container, false);

        projectionsContainer = view.findViewById(R.id.projections_container);
        Button pushupFC = view.findViewById(R.id.btn_pushup_forecast);
        Button plankFC = view.findViewById(R.id.btn_plank_forecast);
        Button cardioFC = view.findViewById(R.id.btn_cardio_forecast);
        Button prtFC = view.findViewById(R.id.btn_prt_forecast);

        TextView forecastTxt = view.findViewById(R.id.forecast_txt);

        MilFitDB db = MilFitDB.getInstance(requireContext());
        ScoreRepo scoreRepo = new ScoreRepo(db);
        UserRepo userRepo = new UserRepo(db);

        ForecastingViewModelFactory factory =
                new ForecastingViewModelFactory(requireActivity().getApplication(), scoreRepo, userRepo);

        ForecastingViewModel vm = new ViewModelProvider(this, factory)
                .get(ForecastingViewModel.class);

        vm.getForecastLive().observe(getViewLifecycleOwner(), result -> {
            projectionsContainer.removeAllViews();

            String display = result.getMessage() != null
                    ? result.getMessage()
                    : "Forecast ready";
            forecastTxt.setText(display);

            if (result.getProjections() != null) {
                for (String event : result.getProjections().keySet()) {
                    ScoreProjection proj = result.getProjections().get(event);
                    if (proj == null) {
                        continue;
                    }

                    String valueText;
                    String unit = proj.getUnit();

                    if (event.equalsIgnoreCase("Plank") || event.contains("Run")) {
                        valueText = FormatTime.formatSeconds(proj.getProjectedValue());
                    } else {
                        valueText = String.valueOf(proj.getProjectedValue());
                    }

                    TextView tView = new TextView(requireContext());

                    tView.setText(event + ": " + valueText + " " + unit);
                    tView.setTextSize(16f);
                    tView.setPadding(8, 8, 8, 8);

                    projectionsContainer.addView(tView);
                }
            }
        });

        pushupFC.setOnClickListener(v -> vm.runForecast("Pushups"));
        plankFC.setOnClickListener(v -> vm.runForecast("Plank"));
        cardioFC.setOnClickListener(v -> vm.runForecast("Cardio"));
        prtFC.setOnClickListener(v -> vm.runForecast("MockPRT"));

        return view;
    }
}