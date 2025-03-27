package com.opentext.androidagent.services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.opentext.androidagent.R;
import com.opentext.androidagent.utils.EncryptionUtils;

/*Best for real-time event logging (e.g., accessibility events).
Runs even when the app is in the background.
Prevents system from killing the service due to low memory.*/
public class EventLoggerService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1, createNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(EncryptionUtils::flushToFile).start();
        return START_STICKY;
    }

    private Notification createNotification() {
        NotificationManager manager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager = getSystemService(NotificationManager.class);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("EVENT_LOGGER", "Event Logger", NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(channel);
        }

        return new NotificationCompat.Builder(this, "EVENT_LOGGER")
                .setContentTitle("OpenText Event Logger Running")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}