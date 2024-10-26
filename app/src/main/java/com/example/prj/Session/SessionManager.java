package com.example.prj.Session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.prj.IntroPage;
import com.example.prj.SignIn;

import java.util.HashMap;

import kotlin.UShortArray;

public class SessionManager {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;

    int private_mode = 0;

    private static final String PREF_NAME = "AndroidHivePref";
    private static final String PASS_INTRO = "PassedIntro";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_NAME = "name";
    public static final String KEY_PASSWORD = "password";

    public SessionManager (Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, private_mode);
        editor = sharedPreferences.edit();
    }

    public void createLoginSession(String name, String password) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_PASSWORD, password);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGIN, false);
    }
    public boolean PassedIntro() {
        return sharedPreferences.getBoolean(PASS_INTRO, false);
    }

    public void checkPassIntro() {
        if (!this.PassedIntro()) {
            Intent intent = new Intent(context, IntroPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
        }
    }

    public void checkLogin() {
        if (!this.isLoggedIn()) {
            Intent intent = new Intent(context, SignIn.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
        }
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_NAME, sharedPreferences.getString(KEY_NAME, null));
        user.put(KEY_PASSWORD, sharedPreferences.getString(KEY_PASSWORD, null));

        return user;
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();

        Intent intent = new Intent(context, SignIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }
}
