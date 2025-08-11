package com.example.milfittracker.repo;

import com.example.milfittracker.helpers.AppExec;
import com.example.milfittracker.helpers.Callback;
import com.example.milfittracker.room.MilFitDB;
import com.example.milfittracker.room.User;
import com.example.milfittracker.room.UserDAO;

public class UserRepo {
    private final UserDAO userDAO;
    private final AppExec appExec = AppExec.getInstance();

    public UserRepo(MilFitDB db) {
        this.userDAO = db.userDAO();
    }

    public void hasAny(Callback<Boolean> callback) {
        appExec.execute(() -> {
            boolean exists = userDAO.count() > 0;
            appExec.main(() -> {
                if (callback != null) callback.onComplete(exists);
            });
        });
    }

    public void save(User user, Callback<Long> callback) {
        appExec.execute(() -> {
            long id = userDAO.insert(user);   // requires @Insert in UserDAO
            appExec.main(() -> {
                if (callback != null) callback.onComplete(id);
            });
        });
    }

    public void getUser(Callback<User> callback) {
        appExec.execute(() -> {
            User user = userDAO.getUser();
            appExec.main(() -> callback.onComplete(user));
        });
    }

}
