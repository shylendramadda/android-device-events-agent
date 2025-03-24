package com.opentext.androidagent.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.res.Configuration;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.opentext.androidagent.utils.EncryptionUtils;

import java.io.File;

public class AgentAccessibilityService extends AccessibilityService {

    private static final String TAG = "OpenTextAgent";
    private static final String logFileName = "events_log.json";
    File file;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
        info.notificationTimeout = 100;
        setServiceInfo(info);
        file = new File(getExternalFilesDir(null), logFileName);
        saveEventToFile("onServiceConnected", "Accessibility Service Connected");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        String eventText = event.getText().toString();
        String packageName = event.getPackageName() != null ? event.getPackageName().toString() : "Unknown";
        saveEventToFile("onAccessibilityEvent", "Event received: " + eventType);

        switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                saveEventToFile("View Clicked:", eventText);
                break;
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                saveEventToFile("Long Clicked:", eventText);
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                saveEventToFile("App Launched or Window Changed:", packageName);
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                saveEventToFile("View Focused:", eventText);
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                saveEventToFile("Text Changed:", eventText);
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                saveEventToFile("View Scrolled", "");
                break;
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_START:
                saveEventToFile("Gesture Detected", "");
                break;
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_START:
                saveEventToFile("Touch Started", "");
                break;
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_END:
                saveEventToFile("Touch Ended", "");
                break;
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                saveEventToFile("System Dialog Opened", "");
                break;
            default:
                saveEventToFile("Event received: ", String.valueOf(eventType));
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        String orientation = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? "LANDSCAPE" : "PORTRAIT";
        saveEventToFile("onConfigurationChanged", "Screen rotated: " + orientation);
    }

    @Override
    public void onInterrupt() {
        saveEventToFile("onInterrupt", "Accessibility Service Interrupted");
    }

    private void saveEventToFile(String eventType, String details) {
        try {
            Log.d(TAG, "Saving event to file at path: " + file.getAbsolutePath());
            EncryptionUtils.saveEncryptedEvent(eventType, details, file);
            // Uncomment below code to encrypt the file
            /* FileEncryptionUtils.saveEncryptedFile("This is a test log event", file);
            FileEncryptionUtils.readEncryptedFile(file);*/
        } catch (Exception e) {
            Log.d(TAG, Log.getStackTraceString(e));
        }
    }
}
