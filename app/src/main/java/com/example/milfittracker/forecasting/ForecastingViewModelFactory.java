package com.example.milfittracker.forecasting;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.milfittracker.repo.ScoreRepo;
import com.example.milfittracker.repo.UserRepo;

public class ForecastingViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final ScoreRepo scoreRepo;
    private final UserRepo userRepo;

    public ForecastingViewModelFactory(Application application, ScoreRepo scoreRepo, UserRepo userRepo) {
        this.application = application;
        this.scoreRepo = scoreRepo;
        this.userRepo = userRepo;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ForecastingViewModel.class)) {
            return (T) new ForecastingViewModel(application, scoreRepo, userRepo);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
