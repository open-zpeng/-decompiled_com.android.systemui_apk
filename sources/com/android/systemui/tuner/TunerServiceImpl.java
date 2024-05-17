package com.android.systemui.tuner;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import com.android.internal.util.ArrayUtils;
import com.android.systemui.DemoMode;
import com.android.systemui.qs.QSTileHost;
import com.android.systemui.settings.CurrentUserTracker;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.leak.LeakDetector;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class TunerServiceImpl extends TunerService {
    private static final int CURRENT_TUNER_VERSION = 4;
    private static final String[] RESET_BLACKLIST = {QSTileHost.TILES_SETTING, "doze_always_on"};
    private static final String TUNER_VERSION = "sysui_tuner_version";
    private ContentResolver mContentResolver;
    private final Context mContext;
    private int mCurrentUser;
    private final LeakDetector mLeakDetector;
    private final HashSet<TunerService.Tunable> mTunables;
    private CurrentUserTracker mUserTracker;
    private final Observer mObserver = new Observer();
    private final ArrayMap<Uri, String> mListeningUris = new ArrayMap<>();
    private final HashMap<String, Set<TunerService.Tunable>> mTunableLookup = new HashMap<>();

    @Inject
    public TunerServiceImpl(Context context, @Named("main_handler") Handler mainHandler, LeakDetector leakDetector) {
        this.mTunables = LeakDetector.ENABLED ? new HashSet<>() : null;
        this.mContext = context;
        this.mContentResolver = this.mContext.getContentResolver();
        this.mLeakDetector = leakDetector;
        for (UserInfo user : UserManager.get(this.mContext).getUsers()) {
            this.mCurrentUser = user.getUserHandle().getIdentifier();
            if (getValue(TUNER_VERSION, 0) != 4) {
                upgradeTuner(getValue(TUNER_VERSION, 0), 4, mainHandler);
            }
        }
        this.mCurrentUser = ActivityManager.getCurrentUser();
        this.mUserTracker = new CurrentUserTracker(this.mContext) { // from class: com.android.systemui.tuner.TunerServiceImpl.1
            @Override // com.android.systemui.settings.CurrentUserTracker
            public void onUserSwitched(int newUserId) {
                TunerServiceImpl.this.mCurrentUser = newUserId;
                TunerServiceImpl.this.reloadAll();
                TunerServiceImpl.this.reregisterAll();
            }
        };
        this.mUserTracker.startTracking();
    }

    @Override // com.android.systemui.tuner.TunerService
    public void destroy() {
        this.mUserTracker.stopTracking();
    }

    private void upgradeTuner(int oldVersion, int newVersion, Handler mainHandler) {
        String blacklistStr;
        if (oldVersion < 1 && (blacklistStr = getValue(StatusBarIconController.ICON_BLACKLIST)) != null) {
            ArraySet<String> iconBlacklist = StatusBarIconController.getIconBlacklist(blacklistStr);
            iconBlacklist.add("rotate");
            iconBlacklist.add("headset");
            Settings.Secure.putStringForUser(this.mContentResolver, StatusBarIconController.ICON_BLACKLIST, TextUtils.join(",", iconBlacklist), this.mCurrentUser);
        }
        if (oldVersion < 2) {
            setTunerEnabled(this.mContext, false);
        }
        if (oldVersion < 4) {
            final int user = this.mCurrentUser;
            mainHandler.postDelayed(new Runnable() { // from class: com.android.systemui.tuner.-$$Lambda$TunerServiceImpl$SZ83wU1GcnmYXjZCH1hfw7pCVvY
                @Override // java.lang.Runnable
                public final void run() {
                    TunerServiceImpl.this.lambda$upgradeTuner$0$TunerServiceImpl(user);
                }
            }, 5000L);
        }
        setValue(TUNER_VERSION, newVersion);
    }

    @Override // com.android.systemui.tuner.TunerService
    public String getValue(String setting) {
        return Settings.Secure.getStringForUser(this.mContentResolver, setting, this.mCurrentUser);
    }

    @Override // com.android.systemui.tuner.TunerService
    public void setValue(String setting, String value) {
        Settings.Secure.putStringForUser(this.mContentResolver, setting, value, this.mCurrentUser);
    }

    @Override // com.android.systemui.tuner.TunerService
    public int getValue(String setting, int def) {
        return Settings.Secure.getIntForUser(this.mContentResolver, setting, def, this.mCurrentUser);
    }

    @Override // com.android.systemui.tuner.TunerService
    public String getValue(String setting, String def) {
        String ret = Settings.Secure.getStringForUser(this.mContentResolver, setting, this.mCurrentUser);
        return ret == null ? def : ret;
    }

    @Override // com.android.systemui.tuner.TunerService
    public void setValue(String setting, int value) {
        Settings.Secure.putIntForUser(this.mContentResolver, setting, value, this.mCurrentUser);
    }

    @Override // com.android.systemui.tuner.TunerService
    public void addTunable(TunerService.Tunable tunable, String... keys) {
        for (String key : keys) {
            addTunable(tunable, key);
        }
    }

    private void addTunable(TunerService.Tunable tunable, String key) {
        if (!this.mTunableLookup.containsKey(key)) {
            this.mTunableLookup.put(key, new ArraySet());
        }
        this.mTunableLookup.get(key).add(tunable);
        if (LeakDetector.ENABLED) {
            this.mTunables.add(tunable);
            this.mLeakDetector.trackCollection(this.mTunables, "TunerService.mTunables");
        }
        Uri uri = Settings.Secure.getUriFor(key);
        if (!this.mListeningUris.containsKey(uri)) {
            this.mListeningUris.put(uri, key);
            this.mContentResolver.registerContentObserver(uri, false, this.mObserver, this.mCurrentUser);
        }
        String value = Settings.Secure.getStringForUser(this.mContentResolver, key, this.mCurrentUser);
        tunable.onTuningChanged(key, value);
    }

    @Override // com.android.systemui.tuner.TunerService
    public void removeTunable(TunerService.Tunable tunable) {
        for (Set<TunerService.Tunable> list : this.mTunableLookup.values()) {
            list.remove(tunable);
        }
        if (LeakDetector.ENABLED) {
            this.mTunables.remove(tunable);
        }
    }

    protected void reregisterAll() {
        if (this.mListeningUris.size() == 0) {
            return;
        }
        this.mContentResolver.unregisterContentObserver(this.mObserver);
        for (Uri uri : this.mListeningUris.keySet()) {
            this.mContentResolver.registerContentObserver(uri, false, this.mObserver, this.mCurrentUser);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reloadSetting(Uri uri) {
        String key = this.mListeningUris.get(uri);
        Set<TunerService.Tunable> tunables = this.mTunableLookup.get(key);
        if (tunables == null) {
            return;
        }
        String value = Settings.Secure.getStringForUser(this.mContentResolver, key, this.mCurrentUser);
        for (TunerService.Tunable tunable : tunables) {
            tunable.onTuningChanged(key, value);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reloadAll() {
        for (String key : this.mTunableLookup.keySet()) {
            String value = Settings.Secure.getStringForUser(this.mContentResolver, key, this.mCurrentUser);
            for (TunerService.Tunable tunable : this.mTunableLookup.get(key)) {
                tunable.onTuningChanged(key, value);
            }
        }
    }

    @Override // com.android.systemui.tuner.TunerService
    public void clearAll() {
        lambda$upgradeTuner$0$TunerServiceImpl(this.mCurrentUser);
    }

    /* renamed from: clearAllFromUser */
    public void lambda$upgradeTuner$0$TunerServiceImpl(int user) {
        Settings.Global.putString(this.mContentResolver, DemoMode.DEMO_MODE_ALLOWED, null);
        Intent intent = new Intent(DemoMode.ACTION_DEMO);
        intent.putExtra("command", DemoMode.COMMAND_EXIT);
        this.mContext.sendBroadcast(intent);
        for (String key : this.mTunableLookup.keySet()) {
            if (!ArrayUtils.contains(RESET_BLACKLIST, key)) {
                Settings.Secure.putStringForUser(this.mContentResolver, key, null, user);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class Observer extends ContentObserver {
        public Observer() {
            super(new Handler(Looper.getMainLooper()));
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri, int userId) {
            if (userId == ActivityManager.getCurrentUser()) {
                TunerServiceImpl.this.reloadSetting(uri);
            }
        }
    }
}
