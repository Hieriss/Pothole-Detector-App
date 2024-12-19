package com.example.prj.Map;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHandler extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setLogLevel(com.google.firebase.database.Logger.Level.DEBUG);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
