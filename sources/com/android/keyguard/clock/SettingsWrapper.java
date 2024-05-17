package com.android.keyguard.clock;

import android.content.ContentResolver;
import android.provider.Settings;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes19.dex */
public class SettingsWrapper {
    private static final String CLOCK_FIELD = "clock";
    private static final String CUSTOM_CLOCK_FACE = "lock_screen_custom_clock_face";
    private static final String DOCKED_CLOCK_FACE = "docked_clock_face";
    private static final String TAG = "ClockFaceSettings";
    private final ContentResolver mContentResolver;
    private final Migration mMigration;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes19.dex */
    public interface Migration {
        void migrate(String str, int i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SettingsWrapper(ContentResolver contentResolver) {
        this(contentResolver, new Migrator(contentResolver));
    }

    @VisibleForTesting
    SettingsWrapper(ContentResolver contentResolver, Migration migration) {
        this.mContentResolver = contentResolver;
        this.mMigration = migration;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getLockScreenCustomClockFace(int userId) {
        return decode(Settings.Secure.getStringForUser(this.mContentResolver, CUSTOM_CLOCK_FACE, userId), userId);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getDockedClockFace(int userId) {
        return Settings.Secure.getStringForUser(this.mContentResolver, DOCKED_CLOCK_FACE, userId);
    }

    @VisibleForTesting
    String decode(String value, int userId) {
        if (value == null) {
            return value;
        }
        try {
            JSONObject json = new JSONObject(value);
            try {
                return json.getString("clock");
            } catch (JSONException ex) {
                Log.e(TAG, "JSON object does not contain clock field.", ex);
                return null;
            }
        } catch (JSONException ex2) {
            Log.e(TAG, "Settings value is not valid JSON", ex2);
            this.mMigration.migrate(value, userId);
            return value;
        }
    }

    /* loaded from: classes19.dex */
    private static final class Migrator implements Migration {
        private final ContentResolver mContentResolver;

        Migrator(ContentResolver contentResolver) {
            this.mContentResolver = contentResolver;
        }

        @Override // com.android.keyguard.clock.SettingsWrapper.Migration
        public void migrate(String value, int userId) {
            try {
                JSONObject json = new JSONObject();
                json.put("clock", value);
                Settings.Secure.putStringForUser(this.mContentResolver, SettingsWrapper.CUSTOM_CLOCK_FACE, json.toString(), userId);
            } catch (JSONException ex) {
                Log.e(SettingsWrapper.TAG, "Failed migrating settings value to JSON format", ex);
            }
        }
    }
}
