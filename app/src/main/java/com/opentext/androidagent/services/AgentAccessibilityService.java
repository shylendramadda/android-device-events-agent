package com.opentext.androidagent.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.opentext.androidagent.utils.EncryptionUtils;

import java.io.File;

public class AgentAccessibilityService extends AccessibilityService {

    private static final String TAG = "OpenTextAgent";
    private static final String logFileName = "events_log.json";
    private SettingsObserver settingsObserver;
    File eventFile;
    Intent serviceIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        serviceIntent = new Intent(this, EventLoggerService.class);
        settingsObserver = new SettingsObserver(new Handler(), this);
        settingsObserver.registerObserver();
        addEvent(TAG, "settingsObserver registered");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (settingsObserver != null) {
            settingsObserver.unregisterObserver();
            addEvent(TAG, "unregisterObserver called");
        }
        if (serviceIntent != null) {
            stopService(serviceIntent);
            addEvent(TAG, "stopService called");
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
        info.notificationTimeout = 100;
        setServiceInfo(info);
        eventFile = new File(getExternalFilesDir(null), logFileName);
        addEvent("onServiceConnected", "Accessibility Service Connected");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        String eventText = event.getText().toString();
        String packageName = event.getPackageName() != null ? event.getPackageName().toString() : "Unknown";
        addEvent("onAccessibilityEvent", "Event received: " + eventType);

        switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                addEvent("View Clicked:", eventText);
                break;
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                addEvent("Long Clicked:", eventText);
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                addEvent("App Launched or Window Changed:", packageName);
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                addEvent("View Focused:", eventText);
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                addEvent("Text Changed:", eventText);
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                addEvent("View Scrolled", "");
                break;
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_START:
                addEvent("Gesture Detected", "");
                break;
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_START:
                addEvent("Touch Started", "");
                break;
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_END:
                addEvent("Touch Ended", "");
                break;
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                addEvent("System Dialog Opened", "");
                break;
            default:
                // Ignore all other events to save CPU cycles
                // Reduces the number of processed events, lowering CPU/memory usage.
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        String orientation = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? "LANDSCAPE" : "PORTRAIT";
        addEvent("onConfigurationChanged", "Screen rotated: " + orientation);
    }

    @Override
    public void onInterrupt() {
        addEvent("onInterrupt", "Accessibility Service Interrupted");
    }

    private void addEvent(String eventType, String details) {
        try {
            Log.d(TAG, "Saving event to file at path: " + eventFile.getAbsolutePath());
            EncryptionUtils.addEventToBuffer(eventType, details, eventFile);
            startService(serviceIntent);
            // Uncomment below code to encrypt the file
            /* FileEncryptionUtils.saveEncryptedFile("This is a test log event", file);
            FileEncryptionUtils.readEncryptedFile(file);*/
        } catch (Exception e) {
            Log.d(TAG, Log.getStackTraceString(e));
        }
    }
}
