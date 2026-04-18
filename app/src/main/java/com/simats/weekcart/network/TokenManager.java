package com.simats.weekcart.network;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

public class TokenManager {
    private static final String PREF_NAME = "secure_prefs";
    private static final String KEY_TOKEN = "jwt_token";
    private SharedPreferences sharedPreferences;

    public TokenManager(Context context) {
        initPreferences(context);
    }

    private void initPreferences(Context context) {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            sharedPreferences = EncryptedSharedPreferences.create(
                    PREF_NAME,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (Exception e) {
            // Self-healing: If encrypted preferences fail (usually AEADBadTagException), 
            // wipe the corrupted file and try to recreate it once.
            e.printStackTrace();
            try {
                // Delete the corrupted preferences file
                context.deleteSharedPreferences(PREF_NAME);
                
                // Retry creation
                String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
                sharedPreferences = EncryptedSharedPreferences.create(
                        PREF_NAME,
                        masterKeyAlias,
                        context,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
            } catch (Exception fatal) {
                // Total failure (extreme case), fall back to standard unencrypted prefs to prevent splash crash
                sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                fatal.printStackTrace();
            }
        }
    }

    public void saveToken(String token) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(KEY_TOKEN, token).apply();
        }
    }

    public String getToken() {
        if (sharedPreferences != null) {
            try {
                return sharedPreferences.getString(KEY_TOKEN, null);
            } catch (Exception e) {
                // Handle decryption errors (like AEADBadTagException)
                e.printStackTrace();
                clearToken(); // Clear corrupted data
                return null;
            }
        }
        return null;
    }

    public void clearToken() {
        if (sharedPreferences != null) {
            sharedPreferences.edit().remove(KEY_TOKEN).apply();
        }
    }
}
