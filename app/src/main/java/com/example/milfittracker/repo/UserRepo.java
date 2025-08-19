package com.example.milfittracker.repo;

import androidx.lifecycle.LiveData;
import com.example.milfittracker.helpers.AppExec;
import com.example.milfittracker.helpers.Callback;
import com.example.milfittracker.room.MilFitDB;
import com.example.milfittracker.room.Scores;
import com.example.milfittracker.room.User;
import com.example.milfittracker.room.UserDAO;

import java.util.List;

public class UserRepo {
    private final UserDAO userDao;
    private final AppExec appExec = AppExec.getInstance();

    public UserRepo(MilFitDB db) {
        this.userDao = db.userDAO();
    }

    public void getuser(Callback<User> callback) {
        appExec.execute(() -> {
            User user = userDao.getUser();
            appExec.main(() -> callback.onComplete(user));
        });
    }

    public void hasAny(Callback<Boolean> callback) {
        appExec.execute(() -> {
            boolean exists = userDao.count() > 0;
            appExec.main(() -> {
                if (callback != null) callback.onComplete(exists);
            });
        });
    }

    public void save(User user, Callback<Long> callback) {
        appExec.execute(() -> {
            long id = userDao.insert(user);
            appExec.main(() -> {
                if (callback != null) callback.onComplete(id);
            });
        });
    }

    public void getUser(Callback<User> callback) {
        appExec.execute(() -> {
            User user = userDao.getUser();
            appExec.main(() -> callback.onComplete(user));
        });
    }

}
