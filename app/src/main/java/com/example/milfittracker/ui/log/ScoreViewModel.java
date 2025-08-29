package com.example.milfittracker.ui.log;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.milfittracker.repo.ScoreRepo;
import com.example.milfittracker.room.MilFitDB;
import com.example.milfittracker.room.Scores;
import java.util.List;

public class ScoreViewModel extends AndroidViewModel {
    private final ScoreRepo repo;
    private final LiveData<List<Scores>> allScores;

    public ScoreViewModel(@NonNull Application app) {
        super(app);
        MilFitDB db = MilFitDB.getInstance(app);
        repo = new ScoreRepo(db);
        allScores = repo.observeAll();
    }

    public LiveData<List<Scores>> getAllLive() {
        return repo.getAllLive(); }

    public LiveData<List<Scores>> getAllScores() {
        return allScores;
    }

    public LiveData<List<Scores>> observeAllRuns() {
        return repo.observeAllRun();
    }

    public LiveData<List<Scores>> observeByEvent(String event) {
        return repo.observeByEvent(event);
    }

    public void insert(Scores scores) {
        repo.insert(scores, id -> { });
    }
}
