package com.example.milfittracker.ui.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.milfittracker.R;
import com.example.milfittracker.forecasting.ForecastingViewModel;
import com.example.milfittracker.forecasting.ForecastingViewModelFactory;
import com.example.milfittracker.repo.ScoreRepo;
import com.example.milfittracker.repo.UserRepo;
import com.example.milfittracker.room.MilFitDB;

public class ForecastingFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecasting, container, false);

        TextView forecastText = view.findViewById(R.id.forecast_text);
        Button runForecastBtn = view.findViewById(R.id.btn_run_forecast);

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
                forecastText.setText(display);
            }
        });

        runForecastBtn.setOnClickListener(v -> vm.runForecast());

        return view;
    }
}