package com.example.milfittracker.repo;

import androidx.lifecycle.LiveData;
import java.util.List;
import com.example.milfittracker.helpers.AppExec;
import com.example.milfittracker.helpers.Callback;
import com.example.milfittracker.room.MilFitDB;
import com.example.milfittracker.room.ScoreDAO;
import com.example.milfittracker.room.Scores;

public class ScoreRepo {
    private final ScoreDAO scoreDAO;
    private final AppExec appExec = AppExec.getInstance();

    public ScoreRepo(MilFitDB db) {
        this.scoreDAO = db.scoreDAO();
    }

    public void insert(Scores score, Callback<Long> callback) {
        appExec.execute(() -> {
            long id = scoreDAO.insert(score);
            appExec.main(() -> {
                if (callback != null) {
                    callback.onComplete(id);
                }
            });
        });
    }

    public void insertAll(List<Scores> list, Runnable done) {
        appExec.execute(() -> {
            scoreDAO.insertAll(list);
            if (done != null) {
                appExec.main(done);
            }
        });
    }

    public LiveData<List<Scores>> getAllLive() {
        return scoreDAO.getAllLive();
    }

    public void getAll(Callback<List<Scores>> callback) {
        appExec.execute(() -> {
            List<Scores> scores = scoreDAO.getAll();
            appExec.main(() -> callback.onComplete(scores));
        });
    }

    public LiveData<List<Scores>> observeAll() {
        return scoreDAO.observeAll();
    }

    public LiveData<List<Scores>> observeByEvent(String event) {
        return scoreDAO.observeByEvent(event);
    }

    public void getForBranchEvent(String branch, String event, Callback<List<Scores>> callback) {
        appExec.execute(() -> {
            List<Scores> scores = scoreDAO.getForBranchEvent(branch, event);
            appExec.main(() -> callback.onComplete(scores));
        });
    }

    public void getForBranchEventBetween(String branch, String event, String start, String end, Callback<List<Scores>> callback) {
        appExec.execute(() -> {
            List<Scores> scores = scoreDAO.getForBranchEventBetween(branch, event, start, end);
            appExec.main(() -> callback.onComplete(scores));
        });
    }

    public void clearAll(Runnable done) {
        appExec.execute(() -> {
            scoreDAO.clearAll();
            if (done != null) {
                appExec.main(done);
            }
        });
    }

    public List<Scores> getScoresSync(String branch, String event) {
        return scoreDAO.getForBranchEvent(branch, event);
    }
}
