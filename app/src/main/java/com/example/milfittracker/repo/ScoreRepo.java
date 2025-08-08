package com.example.milfittracker.repo;

import com.example.milfittracker.helpers.AppExec;
import com.example.milfittracker.helpers.Callback;
import com.example.milfittracker.room.MilFitDB;
import com.example.milfittracker.room.ScoreDAO;
import com.example.milfittracker.room.Scores;

import java.util.List;

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

    public void getAll(Callback<List<Scores>> callback) {
        appExec.execute(() -> {
            List<Scores> scores = scoreDAO.getAll();
            appExec.main(() -> callback.onComplete(scores));
        });
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
}
