package com.huaweichallenge.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class LoginService extends IntentService {
    public LoginService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent workingIntent) {
        String dataString = workingIntent.getDataString();
        // Implement here;

    }
}
