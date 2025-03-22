package com.example.iceteastore.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String PREF_NAME = "LoginSession";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role"; // Thêm key để lưu role

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveLogin(String username, String role) {
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_ROLE, role); // Lưu role
        editor.apply();
    }

    public String getLoggedInUser() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public String getUserRole() {
        return sharedPreferences.getString(KEY_ROLE, null);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}