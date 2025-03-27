package com.opentext.androidagent.utils;

import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtils {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY = "0123456789abcdef";
    private static final String IV = "abcdef9876543210";
    private static final String TAG = "EncryptionUtils";
    private static final List<String> eventBuffer = new ArrayList<>();
    private static File logFile;

    private static SecretKey getSecretKey() {
        return new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");
    }

    private static IvParameterSpec getIv() {
        return new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
    }

    public static String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), getIv());
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);
    }

    public static void addEventToBuffer(String eventType, String details, File eventFile) {
        try {
            logFile = eventFile;
            JSONObject jsonEvent = new JSONObject();
            jsonEvent.put("timestamp", System.currentTimeMillis());
            jsonEvent.put("eventType", eventType);
            jsonEvent.put("details", details);

            String jsonEventString = jsonEvent.toString();
            eventBuffer.add(jsonEventString);
            // flushToFile(); // It runs on main thread which leads to ANR
            Log.d(TAG, "Event logged: " + jsonEventString);
        } catch (Exception e) {
            Log.e(TAG, "Error saving encrypted event: ", e);
        }
    }

    /*
    Batch writes instead of frequent I/O operations
	Store events in a memory buffer then encrypt and write them periodically (e.g., every 10 seconds or after 100 events).
    Use BufferedWriter instead of FileWriter
    Reduces the number of disk writes, reduces CPU cycles spent on encryption.
    Reduces I/O load and improves overall system performance.
    */
    public static synchronized void flushToFile() {
        try {
            if (!eventBuffer.isEmpty()) { // Prevent unnecessary file writes
                List<String> tempBuffer;
                synchronized (eventBuffer) { // Synchronize access to prevent concurrent modifications
                    tempBuffer = new ArrayList<>(eventBuffer); // Create a copy to avoid modifying while iterating
                    eventBuffer.clear(); // Clear buffer safely
                }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                    for (String event : tempBuffer) {
                        String encryptedData = encrypt(event);
                        writer.write(encryptedData + "\n");
                        Log.d(TAG, "Events recorded (Encrypted): " + encryptedData);
                        Log.d(TAG, "Events recorded (Decrypted): " + decrypt(encryptedData));
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error writing to file", e);
        }
    }

    public static String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), getIv());
            byte[] decodedBytes = Base64.decode(encryptedData, Base64.NO_WRAP);
            return new String(cipher.doFinal(decodedBytes), StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e(TAG, "Error decrypting data: ", e);
            return "";
        }
    }
}