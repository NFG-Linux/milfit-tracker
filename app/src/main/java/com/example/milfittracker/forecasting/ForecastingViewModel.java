package com.example.milfittracker.forecasting;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.milfittracker.helpers.AppExec;
import com.example.milfittracker.repo.ScoreRepo;
import com.example.milfittracker.repo.UserRepo;

public class ForecastingViewModel extends AndroidViewModel {

    private final ScoreRepo scoreRepo;
    private final UserRepo userRepo;
    private final AppExec appExec = AppExec.getInstance();

    private final MutableLiveData<ForecastResult> forecastLive = new MutableLiveData<>();

    public ForecastingViewModel(@NonNull Application application, ScoreRepo scoreRepo, UserRepo userRepo) {
        super(application);
        this.scoreRepo = scoreRepo;
        this.userRepo = userRepo;
    }

    public LiveData<ForecastResult> getForecastLive() {
        return forecastLive;
    }

    public void runForecast() {
        userRepo.getUser(user -> {
            if (user != null) {
                appExec.execute(() -> {
                    scoreRepo.getAll(scores -> {
                        ForecastEngine engine = new ForecastEngine();
                        ForecastResult result = engine.forecastWithContext(scores, user);
                        forecastLive.postValue(result);
                    });

                });
            }
        });
    }
}
