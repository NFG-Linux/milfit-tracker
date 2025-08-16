package com.example.milfittracker.ui.onboarding;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import com.example.milfittracker.R;
import com.example.milfittracker.repo.UserRepo;
import com.example.milfittracker.room.MilFitDB;
import com.example.milfittracker.MainActivity;

public class OnboardingActivity extends AppCompatActivity {

    private UserRepo userRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userRepo = new UserRepo(MilFitDB.getInstance(getApplicationContext()));

        userRepo.hasAny(exists -> {
            if (exists) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                setContentView(R.layout.activity_onboarding);
                if (savedInstanceState == null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.onboarding_container, new OnboardingFragment())
                            .commit();
                }
            }
        });
    }
}