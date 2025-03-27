package com.opentext.androidagent.services;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

public class SettingsObserver extends ContentObserver {

    private static final String TAG = "SettingsObserver";
    private final Context context;

    public SettingsObserver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Log.d(TAG, "System settings changed!");
    }

    public void registerObserver() {
        context.getContentResolver().registerContentObserver(
                Settings.System.CONTENT_URI, true, this);
    }

    public void unregisterObserver() {
        context.getContentResolver().unregisterContentObserver(this);
    }
}