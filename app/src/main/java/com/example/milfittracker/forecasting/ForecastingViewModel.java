package com.example.milfittracker.forecasting;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.time.Period;
import java.time.LocalDate;
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
            if (user == null) {
                forecastLive.postValue(new ForecastResult("No user profile found"));
                return;
            }

            String branch = user.getBranch();
            String gender = user.getGender();

            int age = 0;
            try {
                if (user.getBDay() != null) {
                    LocalDate birth = LocalDate.parse(user.getBDay());
                    age = Period.between(birth, LocalDate.now()).getYears();
                }
            } catch (Exception ignored) {}

            ForecastEngine engine = new ForecastEngine(getApplication().getApplicationContext());
            ForecastResult result = new ForecastResult("Forecast generated");
            result.setBranch(branch);

            String[] events = {"Push-ups", "Plank", "1.5-mile Run"};

            for (String event : events) {
                scoreRepo.getForBranchEvent(branch, event, history -> {
                    ForecastResult partial = engine.forecastWithContext(history, user, event);

                    if (partial.getProjections() != null) {
                        partial.getProjections().forEach(result::addProjection);
                    }

                    if (partial.getMessage() != null && !partial.getMessage().isEmpty()) {
                        result.setMessage(partial.getMessage());
                    }

                    forecastLive.postValue(result);
                });
            }
        });
    }
}
