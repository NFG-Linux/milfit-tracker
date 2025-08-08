package com.example.milfittracker.helpers;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExec {
    private static volatile AppExec INSTANCE;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private AppExec() {}

    public static AppExec getInstance() {
        if (INSTANCE == null) {
            synchronized (AppExec.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppExec();
                }
            }
        }
        return INSTANCE;
    }

    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }
    public void main(Runnable runnable) {
        handler.post(runnable);
    }

    public void mainDelayed(Runnable runnable, long delayMs) {
        handler.postDelayed(runnable, delayMs);
    }

    public void shutdown() {
        executor.shutdown();
    }

    public ExecutorService getExecutor() {
        return executor;
    }
    public Handler getHandler() {
        return handler;
    }
}
