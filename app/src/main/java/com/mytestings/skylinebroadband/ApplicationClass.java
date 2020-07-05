package com.mytestings.skylinebroadband;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class ApplicationClass extends Application {
    @Override
    public void onCreate() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        super.onCreate();
    }
}