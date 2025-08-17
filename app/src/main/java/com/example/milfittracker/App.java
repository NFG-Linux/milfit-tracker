package com.example.milfittracker;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.camera.core.CameraXConfig;
import androidx.camera.camera2.Camera2Config;

public class App extends Application implements CameraXConfig.Provider {
    @NonNull
    @Override
    public CameraXConfig getCameraXConfig() {
        return new CameraXConfig.Builder()
                .fromConfig(Camera2Config.defaultConfig())
                .setMinimumLoggingLevel(Log.DEBUG)
                .build();
        }
}