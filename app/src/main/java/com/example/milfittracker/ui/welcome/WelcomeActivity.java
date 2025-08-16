package com.example.milfittracker.ui.welcome;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.example.milfittracker.R;
import com.example.milfittracker.ui.onboarding.OnboardingActivity;
import com.example.milfittracker.helpers.LaunchOrder;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        MaterialButton getStarted = findViewById(R.id.get_started);
        getStarted.setOnClickListener(v -> {
            LaunchOrder.setSeenWelcome(this, true);
            Intent i = new Intent(WelcomeActivity.this, OnboardingActivity.class);
            startActivity(i);
            finish();
        });
    }
}