package com.xiaopeng.speech.common.util;

import android.app.ActivityThread;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
/* loaded from: classes23.dex */
public class SPConfig {
    private SharedPreferences mPreferences;
    private static Map<String, SPConfig> mConfigMaps = new HashMap();
    private static String NAME = null;

    public static synchronized SPConfig getInstance() {
        SPConfig sPConfig;
        synchronized (SPConfig.class) {
            sPConfig = getInstance(ActivityThread.currentApplication());
        }
        return sPConfig;
    }

    public static synchronized SPConfig getInstance(Context context) {
        SPConfig config;
        synchronized (SPConfig.class) {
            if (NAME == null) {
                NAME = ActivityThread.currentActivityThread().getProcessName() + ".configuration";
            }
            config = getConfig(context, NAME);
        }
        return config;
    }

    private static SPConfig getConfig(Context context, String name) {
        SPConfig SPConfig = null;
        if (mConfigMaps.containsKey(name)) {
            SPConfig SPConfig2 = mConfigMaps.get(name);
            SPConfig = SPConfig2;
        }
        if (SPConfig == null) {
            SPConfig SPConfig3 = new SPConfig(context, name);
            mConfigMaps.put(name, SPConfig3);
            return SPConfig3;
        }
        return SPConfig;
    }

    public static synchronized SPConfig getInstance(Context context, String name) {
        SPConfig config;
        synchronized (SPConfig.class) {
            if (name == null) {
                name = "";
            }
            config = getConfig(context, name);
        }
        return config;
    }

    private SPConfig(Context context, String name) {
        String sharePreferenceName = name + ".configuration";
        this.mPreferences = context.getSharedPreferences(sharePreferenceName, 0);
    }

    public void setOnChangeListenner(SharedPreferences.OnSharedPreferenceChangeListener listenner) {
        if (listenner != null) {
            this.mPreferences.registerOnSharedPreferenceChangeListener(listenner);
        }
    }

    public synchronized boolean setString(String key, String value) {
        SharedPreferences.Editor editor;
        editor = this.mPreferences.edit().putString(key, value);
        return doApply(editor);
    }

    public synchronized boolean setStringSync(String key, String value) {
        SharedPreferences.Editor editor;
        editor = this.mPreferences.edit().putString(key, value);
        return doCommit(editor);
    }

    public synchronized boolean setInt(String key, int value) {
        SharedPreferences.Editor editor;
        editor = this.mPreferences.edit().putInt(key, value);
        return doApply(editor);
    }

    public synchronized boolean setIntSync(String key, int value) {
        SharedPreferences.Editor editor;
        editor = this.mPreferences.edit().putInt(key, value);
        return doCommit(editor);
    }

    public synchronized boolean setLong(String key, long value) {
        SharedPreferences.Editor editor;
        editor = this.mPreferences.edit().putLong(key, value);
        return doApply(editor);
    }

    public synchronized boolean setLongSync(String key, long value) {
        SharedPreferences.Editor editor;
        editor = this.mPreferences.edit().putLong(key, value);
        return doCommit(editor);
    }

    public synchronized boolean setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor;
        editor = this.mPreferences.edit().putBoolean(key, value);
        return doApply(editor);
    }

    public synchronized boolean setBooleanSync(String key, boolean value) {
        SharedPreferences.Editor editor;
        editor = this.mPreferences.edit().putBoolean(key, value);
        return doCommit(editor);
    }

    public synchronized boolean setFloat(String key, float value) {
        SharedPreferences.Editor editor;
        editor = this.mPreferences.edit().putFloat(key, value);
        return doApply(editor);
    }

    public synchronized boolean setFloatSync(String key, float value) {
        SharedPreferences.Editor editor;
        editor = this.mPreferences.edit().putFloat(key, value);
        return doCommit(editor);
    }

    public synchronized boolean setStringSet(String key, Set<String> value) {
        SharedPreferences.Editor editor;
        editor = this.mPreferences.edit().putStringSet(key, value);
        return doApply(editor);
    }

    public synchronized boolean setStringSetSync(String key, Set<String> value) {
        SharedPreferences.Editor editor;
        editor = this.mPreferences.edit().putStringSet(key, value);
        return doCommit(editor);
    }

    public synchronized String getString(String key, String defaultValue) {
        return this.mPreferences.getString(key, defaultValue);
    }

    public synchronized int getInt(String key, int defaultValue) {
        return this.mPreferences.getInt(key, defaultValue);
    }

    public synchronized long getLong(String key, long defaultValue) {
        return this.mPreferences.getLong(key, defaultValue);
    }

    public synchronized boolean getBoolean(String key, boolean defaultValue) {
        return this.mPreferences.getBoolean(key, defaultValue);
    }

    public synchronized float getFloat(String key, float defaultValue) {
        return this.mPreferences.getFloat(key, defaultValue);
    }

    public synchronized Set<String> getStringSet(String key, Set<String> deaultValue) {
        return this.mPreferences.getStringSet(key, deaultValue);
    }

    public synchronized Map<String, ?> getAll() {
        return this.mPreferences.getAll();
    }

    public synchronized void removeSync(String key) {
        SharedPreferences.Editor editor = this.mPreferences.edit().remove(key);
        doCommit(editor);
    }

    public synchronized void clearAllSync() {
        SharedPreferences.Editor editor = this.mPreferences.edit().clear();
        doCommit(editor);
    }

    public synchronized void clearAllAsync() {
        this.mPreferences.edit().clear().apply();
    }

    private boolean doCommit(SharedPreferences.Editor editor) {
        try {
            boolean result = editor.commit();
            return result;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    private boolean doApply(SharedPreferences.Editor editor) {
        try {
            editor.apply();
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }
}
