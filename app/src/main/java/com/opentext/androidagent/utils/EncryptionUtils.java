package com.opentext.androidagent.utils;

import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtils {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY = "0123456789abcdef";
    private static final String IV = "abcdef9876543210";
    private static final String TAG = "EncryptionUtils";

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

    public static void saveEncryptedEvent(String eventType, String details, File file) {
        try {
            JSONObject jsonEvent = new JSONObject();
            jsonEvent.put("timestamp", System.currentTimeMillis());
            jsonEvent.put("eventType", eventType);
            jsonEvent.put("details", details);

            String encryptedData = encrypt(jsonEvent.toString());

            FileWriter writer = new FileWriter(file, true);
            writer.append(encryptedData).append("\n");
            writer.close();

            Log.d(TAG, "Event recorded (Encrypted): " + encryptedData);
//             Un comment below line to check the decrypted data
            Log.d(TAG, "Event recorded (Decrypted): " + decrypt(encryptedData));
        } catch (Exception e) {
            Log.e(TAG, "Error saving encrypted event: ", e);
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