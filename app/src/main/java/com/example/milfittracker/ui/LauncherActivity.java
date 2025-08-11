package com.example.milfittracker.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.milfittracker.MainActivity;
import com.example.milfittracker.repo.UserRepo;
import com.example.milfittracker.room.MilFitDB;
import com.example.milfittracker.room.User;
import com.example.milfittracker.ui.onboarding.OnboardingActivity;

public class LauncherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MilFitDB db = MilFitDB.getInstance(getApplicationContext());
        UserRepo userRepo = new UserRepo(db);

        userRepo.getUser(user -> {
            Intent intent;
            if (user == null) {
                intent = new Intent(this, OnboardingActivity.class);
            } else {
                intent = new Intent(this, MainActivity.class);
            }
            startActivity(intent);
            finish();
        });
    }
}
