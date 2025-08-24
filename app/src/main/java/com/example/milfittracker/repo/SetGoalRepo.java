package com.example.milfittracker.repo;

import android.content.Context;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import com.example.milfittracker.room.MilFitDB;
import com.example.milfittracker.room.Scores;
import com.example.milfittracker.room.SetGoal;
import com.example.milfittracker.room.SetGoalDAO;

public class SetGoalRepo {
    private final SetGoalDAO goalDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public SetGoalRepo(Context context) {
        MilFitDB db = MilFitDB.getInstance(context);
        goalDao = db.setGoalDAO();
    }

    public LiveData<List<SetGoal>> getAllLive() {
        return goalDao.getAllLive();
    }

    public void save(SetGoal goal) {
        Executors.newSingleThreadExecutor().execute(() -> goalDao.upsert(goal));
    }

    public void insert(SetGoal goal, Callback<Long> callback) {
        executor.execute(() -> {
            long id = goalDao.insert(goal);
            if (callback != null) callback.onComplete(id);
        });
    }

    public void upsert(SetGoal goal, Callback<Long> callback) {
        executor.execute(() -> {
            long id = goalDao.upsert(goal);
            if (callback != null) callback.onComplete(id);
        });
    }

    public void update(SetGoal goal, Callback<Integer> callback) {
        executor.execute(() -> {
            int count = goalDao.update(goal);
            if (callback != null) callback.onComplete(count);
        });
    }

    public void delete(SetGoal goal, Callback<Integer> callback) {
        executor.execute(() -> {
            int count = goalDao.delete(goal);
            if (callback != null) callback.onComplete(count);
        });
    }

    public void getAllGoals(Callback<List<SetGoal>> callback) {
        executor.execute(() -> {
            List<SetGoal> list = goalDao.getAllGoals();
            if (callback != null) callback.onComplete(list);
        });
    }

    public void getGoalById(int id, Callback<SetGoal> callback) {
        executor.execute(() -> {
            SetGoal goal = goalDao.getGoalById(id);
            if (callback != null) callback.onComplete(goal);
        });
    }

    public void getForBranch(String branch, Callback<List<SetGoal>> callback) {
        executor.execute(() -> {
            List<SetGoal> list = goalDao.getForBranch(branch);
            if (callback != null) callback.onComplete(list);
        });
    }

    public void getForBranchEvent(String branch, String event, Callback<SetGoal> callback) {
        executor.execute(() -> {
            SetGoal goal = goalDao.getForBranchEvent(branch, event);
            if (callback != null) callback.onComplete(goal);
        });
    }

    // Optional generic callback interface
    public interface Callback<T> {
        void onComplete(T result);
    }
}
