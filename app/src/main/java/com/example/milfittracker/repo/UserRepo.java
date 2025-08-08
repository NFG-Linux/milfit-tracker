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

    public void getUser(Callback<User> callback) {
        appExec.execute(() -> {
            User user = userDAO.getUser();
            appExec.main(() -> callback.onComplete(user));
        });
    }

}
