package com.android.systemui;

import android.util.ArrayMap;
import android.util.ArraySet;
import java.util.Arrays;
/* loaded from: classes21.dex */
class ForegroundServicesUserState {
    private static final long FG_SERVICE_GRACE_MILLIS = 5000;
    private String[] mRunning = null;
    private long mServiceStartTime = 0;
    private ArrayMap<String, ArraySet<String>> mImportantNotifications = new ArrayMap<>(1);
    private ArrayMap<String, ArraySet<String>> mStandardLayoutNotifications = new ArrayMap<>(1);
    private ArrayMap<String, ArraySet<Integer>> mAppOps = new ArrayMap<>(1);

    public void setRunningServices(String[] pkgs, long serviceStartTime) {
        this.mRunning = pkgs != null ? (String[]) Arrays.copyOf(pkgs, pkgs.length) : null;
        this.mServiceStartTime = serviceStartTime;
    }

    public void addOp(String pkg, int op) {
        if (this.mAppOps.get(pkg) == null) {
            this.mAppOps.put(pkg, new ArraySet<>(3));
        }
        this.mAppOps.get(pkg).add(Integer.valueOf(op));
    }

    public boolean removeOp(String pkg, int op) {
        ArraySet<Integer> keys = this.mAppOps.get(pkg);
        if (keys == null) {
            return false;
        }
        boolean found = keys.remove(Integer.valueOf(op));
        if (keys.size() == 0) {
            this.mAppOps.remove(pkg);
            return found;
        }
        return found;
    }

    public void addImportantNotification(String pkg, String key) {
        addNotification(this.mImportantNotifications, pkg, key);
    }

    public boolean removeImportantNotification(String pkg, String key) {
        return removeNotification(this.mImportantNotifications, pkg, key);
    }

    public void addStandardLayoutNotification(String pkg, String key) {
        addNotification(this.mStandardLayoutNotifications, pkg, key);
    }

    public boolean removeStandardLayoutNotification(String pkg, String key) {
        return removeNotification(this.mStandardLayoutNotifications, pkg, key);
    }

    public boolean removeNotification(String pkg, String key) {
        boolean removed = false | removeImportantNotification(pkg, key);
        return removed | removeStandardLayoutNotification(pkg, key);
    }

    public void addNotification(ArrayMap<String, ArraySet<String>> map, String pkg, String key) {
        if (map.get(pkg) == null) {
            map.put(pkg, new ArraySet<>());
        }
        map.get(pkg).add(key);
    }

    public boolean removeNotification(ArrayMap<String, ArraySet<String>> map, String pkg, String key) {
        ArraySet<String> keys = map.get(pkg);
        if (keys == null) {
            return false;
        }
        boolean found = keys.remove(key);
        if (keys.size() == 0) {
            map.remove(pkg);
            return found;
        }
        return found;
    }

    public boolean isDisclosureNeeded() {
        String[] strArr;
        if (this.mRunning != null && System.currentTimeMillis() - this.mServiceStartTime >= FG_SERVICE_GRACE_MILLIS) {
            for (String pkg : this.mRunning) {
                ArraySet<String> set = this.mImportantNotifications.get(pkg);
                if (set == null || set.size() == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public ArraySet<Integer> getFeatures(String pkg) {
        return this.mAppOps.get(pkg);
    }

    public String getStandardLayoutKey(String pkg) {
        ArraySet<String> set = this.mStandardLayoutNotifications.get(pkg);
        if (set == null || set.size() == 0) {
            return null;
        }
        return set.valueAt(0);
    }

    public String toString() {
        return "UserServices{mRunning=" + Arrays.toString(this.mRunning) + ", mServiceStartTime=" + this.mServiceStartTime + ", mImportantNotifications=" + this.mImportantNotifications + ", mStandardLayoutNotifications=" + this.mStandardLayoutNotifications + '}';
    }
}
