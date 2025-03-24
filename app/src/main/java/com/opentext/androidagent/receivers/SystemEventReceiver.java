package com.opentext.androidagent.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Objects;

public class SystemEventReceiver extends BroadcastReceiver {

    private static final String TAG = "OpenTextAgent";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;

        switch (action) {
            case Intent.ACTION_SCREEN_ON:
                Log.d(TAG, "Screen turned ON");
                break;
            case Intent.ACTION_SCREEN_OFF:
                Log.d(TAG, "Screen turned OFF");
                break;
            case Intent.ACTION_PACKAGE_ADDED:
                Log.d(TAG, "App Installed: " + Objects.requireNonNull(intent.getData()));
                break;
            case Intent.ACTION_PACKAGE_REMOVED:
                Log.d(TAG, "App Uninstalled: " + Objects.requireNonNull(intent.getData()));
                break;
            default:
                Log.d(TAG, "Received system event: " + action);
        }
    }
}