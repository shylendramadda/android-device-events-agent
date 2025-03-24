package com.opentext.androidagent.utils;

import android.util.Log;
import android.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class FileEncryptionUtils {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY = "0123456789abcdef"; // Use a securely generated key
    private static final String IV = "abcdef9876543210"; // Must be 16 bytes
    private static final String TAG = "FileEncryption"; // Must be 16 bytes

    private static SecretKey getSecretKey() {
        return new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");
    }

    private static IvParameterSpec getIv() {
        return new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
    }

    public static void saveEncryptedFile(String data, File file) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), getIv());

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            CipherOutputStream cipherOutputStream = new CipherOutputStream(fileOutputStream, cipher);
            cipherOutputStream.write(data.getBytes(StandardCharsets.UTF_8));
            cipherOutputStream.close();

            Log.d(TAG, "File saved successfully in encrypted format");
        } catch (Exception e) {
            Log.e(TAG, "Error encrypting file", e);
        }
    }

    public static String readEncryptedFile(File file) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), getIv());

            FileInputStream fileInputStream = new FileInputStream(file);
            CipherInputStream cipherInputStream = new CipherInputStream(fileInputStream, cipher);
            BufferedReader reader = new BufferedReader(new InputStreamReader(cipherInputStream));

            StringBuilder decryptedData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                decryptedData.append(line);
            }

            reader.close();
            return decryptedData.toString();
        } catch (Exception e) {
            Log.e(TAG, "Error decrypting file", e);
            return null;
        }
    }
}