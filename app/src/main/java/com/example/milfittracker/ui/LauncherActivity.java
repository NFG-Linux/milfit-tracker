package com.example.milfittracker.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.milfittracker.MainActivity;
import com.example.milfittracker.helpers.LaunchOrder;
import com.example.milfittracker.ui.welcome.WelcomeActivity;
import com.example.milfittracker.ui.onboarding.OnboardingActivity;

public class LauncherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!LaunchOrder.seenWelcome(this)) {
            startActivity(new Intent(this, WelcomeActivity.class));
        } else if (!LaunchOrder.onboarded(this)) {
            startActivity(new Intent(this, OnboardingActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}
