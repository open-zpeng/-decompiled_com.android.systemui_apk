package com.android.systemui.qs;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArraySet;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Prefs;
import com.android.systemui.statusbar.phone.AutoTileManager;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.inject.Inject;
/* loaded from: classes21.dex */
public class AutoAddTracker {
    private static final String[][] CONVERT_PREFS = {new String[]{Prefs.Key.QS_HOTSPOT_ADDED, AutoTileManager.HOTSPOT}, new String[]{Prefs.Key.QS_DATA_SAVER_ADDED, AutoTileManager.SAVER}, new String[]{Prefs.Key.QS_INVERT_COLORS_ADDED, AutoTileManager.INVERSION}, new String[]{Prefs.Key.QS_WORK_ADDED, "work"}, new String[]{Prefs.Key.QS_NIGHTDISPLAY_ADDED, AutoTileManager.NIGHT}};
    private final Context mContext;
    @VisibleForTesting
    protected final ContentObserver mObserver = new ContentObserver(new Handler()) { // from class: com.android.systemui.qs.AutoAddTracker.1
        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            AutoAddTracker.this.mAutoAdded.addAll(AutoAddTracker.this.getAdded());
        }
    };
    private final ArraySet<String> mAutoAdded = new ArraySet<>(getAdded());

    @Inject
    public AutoAddTracker(Context context) {
        String[][] strArr;
        this.mContext = context;
        for (String[] convertPref : CONVERT_PREFS) {
            if (Prefs.getBoolean(context, convertPref[0], false)) {
                setTileAdded(convertPref[1]);
                Prefs.remove(context, convertPref[0]);
            }
        }
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("qs_auto_tiles"), false, this.mObserver);
    }

    public boolean isAdded(String tile) {
        return this.mAutoAdded.contains(tile);
    }

    public void setTileAdded(String tile) {
        if (this.mAutoAdded.add(tile)) {
            saveTiles();
        }
    }

    public void setTileRemoved(String tile) {
        if (this.mAutoAdded.remove(tile)) {
            saveTiles();
        }
    }

    public void destroy() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mObserver);
    }

    private void saveTiles() {
        Settings.Secure.putString(this.mContext.getContentResolver(), "qs_auto_tiles", TextUtils.join(",", this.mAutoAdded));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Collection<String> getAdded() {
        String current = Settings.Secure.getString(this.mContext.getContentResolver(), "qs_auto_tiles");
        if (current == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(current.split(","));
    }
}
