package com.example.milfittracker.forecasting;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.milfittracker.repo.ScoreRepo;
import com.example.milfittracker.repo.UserRepo;

public class ForecastingViewModel extends AndroidViewModel {

    private final ScoreRepo scoreRepo;
    private final UserRepo userRepo;

    private final MutableLiveData<ForecastResult> forecastLive = new MutableLiveData<>();

    public ForecastingViewModel(@NonNull Application application, ScoreRepo scoreRepo, UserRepo userRepo) {
        super(application);
        this.scoreRepo = scoreRepo;
        this.userRepo = userRepo;
    }

    public LiveData<ForecastResult> getForecastLive() {
        return forecastLive;
    }

    public void runForecast(String eventType) {
        userRepo.getUser(user -> {
            String branch = user.getBranch();

            ForecastEngine engine = new ForecastEngine(getApplication().getApplicationContext());
            ForecastResult result = new ForecastResult("Forecast generated");
            result.setBranch(branch);

            switch (eventType) {
                case "Pushups":
                    scoreRepo.getForBranchEvent(user.getBranch(), "Push-ups", history -> {
                        ForecastResult r = engine.forecastWithContext(history, user, "Push-ups");
                        forecastLive.postValue(r);
                    });
                    break;
                case "Plank":
                    scoreRepo.getForBranchEvent(user.getBranch(), "Plank", history -> {
                        ForecastResult r = engine.forecastWithContext(history, user, "Plank");
                        forecastLive.postValue(r);
                    });
                    break;
                case "Cardio":
                    String runEvent = "";
                    switch (user.getBranch()) {
                        case "Navy":
                        case "Coast Guard":
                        case "Air Force":
                        case "Space Force":
                            runEvent = "1.5-mile Run";
                            break;
                        case "Army":
                            runEvent = "2-mile Run";
                            break;
                        case "Marines":
                            runEvent = "3-mile Run";
                            break;
                    }

                    final String finalRunEvent = runEvent;

                    scoreRepo.getForBranchEvent(user.getBranch(), runEvent, history -> {
                        ForecastResult r = engine.forecastWithContext(history, user, finalRunEvent);
                        forecastLive.postValue(r);
                    });
                    break;
                case "MockPRT":
                    String MockrunEvent;
                    switch (user.getBranch()) {
                        case "Army":
                            MockrunEvent = "2-mile Run";
                            break;
                        case "Marines":
                            MockrunEvent = "3-mile Run";
                            break;
                        case "Navy":
                        case "Coast Guard":
                        case "Air Force":
                        case "Space Force":
                        default:
                            MockrunEvent = "1.5-mile Run";
                            break;
                    }

                    scoreRepo.getForBranchEvent(user.getBranch(), "Push-ups", pushupHistory -> {
                        scoreRepo.getForBranchEvent(user.getBranch(), "Plank", plankHistory -> {
                            scoreRepo.getForBranchEvent(user.getBranch(), MockrunEvent, cardioHistory -> {
                                ForecastResult mockResult = engine.forecastMockPRT(user, pushupHistory, plankHistory, cardioHistory);
                                forecastLive.postValue(mockResult);
                            });
                        });
                    });

            }
        });
    }
}
