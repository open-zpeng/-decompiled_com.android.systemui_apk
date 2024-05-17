package com.android.systemui.appops;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.ArraySet;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dumpable;
import com.android.systemui.appops.AppOpsController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class AppOpsControllerImpl implements AppOpsController, AppOpsManager.OnOpActiveChangedListener, AppOpsManager.OnOpNotedListener, Dumpable {
    private static final boolean DEBUG = false;
    private static final long NOTED_OP_TIME_DELAY_MS = 5000;
    protected static final int[] OPS = {26, 24, 27, 0, 1};
    private static final String TAG = "AppOpsControllerImpl";
    private final AppOpsManager mAppOps;
    private H mBGHandler;
    private final Context mContext;
    private boolean mListening;
    private final List<AppOpsController.Callback> mCallbacks = new ArrayList();
    private final ArrayMap<Integer, Set<AppOpsController.Callback>> mCallbacksByCode = new ArrayMap<>();
    @GuardedBy({"mActiveItems"})
    private final List<AppOpItem> mActiveItems = new ArrayList();
    @GuardedBy({"mNotedItems"})
    private final List<AppOpItem> mNotedItems = new ArrayList();

    @Inject
    public AppOpsControllerImpl(Context context, @Named("background_looper") Looper bgLooper) {
        this.mContext = context;
        this.mAppOps = (AppOpsManager) context.getSystemService("appops");
        this.mBGHandler = new H(bgLooper);
        int numOps = OPS.length;
        for (int i = 0; i < numOps; i++) {
            this.mCallbacksByCode.put(Integer.valueOf(OPS[i]), new ArraySet());
        }
    }

    @VisibleForTesting
    protected void setBGHandler(H handler) {
        this.mBGHandler = handler;
    }

    @VisibleForTesting
    protected void setListening(boolean listening) {
        this.mListening = listening;
        if (listening) {
            this.mAppOps.startWatchingActive(OPS, this);
            this.mAppOps.startWatchingNoted(OPS, this);
            return;
        }
        this.mAppOps.stopWatchingActive(this);
        this.mAppOps.stopWatchingNoted(this);
        this.mBGHandler.removeCallbacksAndMessages(null);
        synchronized (this.mActiveItems) {
            this.mActiveItems.clear();
        }
        synchronized (this.mNotedItems) {
            this.mNotedItems.clear();
        }
    }

    @Override // com.android.systemui.appops.AppOpsController
    public void addCallback(int[] opsCodes, AppOpsController.Callback callback) {
        boolean added = false;
        int numCodes = opsCodes.length;
        for (int i = 0; i < numCodes; i++) {
            if (this.mCallbacksByCode.containsKey(Integer.valueOf(opsCodes[i]))) {
                this.mCallbacksByCode.get(Integer.valueOf(opsCodes[i])).add(callback);
                added = true;
            }
        }
        if (added) {
            this.mCallbacks.add(callback);
        }
        if (!this.mCallbacks.isEmpty()) {
            setListening(true);
        }
    }

    @Override // com.android.systemui.appops.AppOpsController
    public void removeCallback(int[] opsCodes, AppOpsController.Callback callback) {
        int numCodes = opsCodes.length;
        for (int i = 0; i < numCodes; i++) {
            if (this.mCallbacksByCode.containsKey(Integer.valueOf(opsCodes[i]))) {
                this.mCallbacksByCode.get(Integer.valueOf(opsCodes[i])).remove(callback);
            }
        }
        this.mCallbacks.remove(callback);
        if (this.mCallbacks.isEmpty()) {
            setListening(false);
        }
    }

    private AppOpItem getAppOpItem(List<AppOpItem> appOpList, int code, int uid, String packageName) {
        int itemsQ = appOpList.size();
        for (int i = 0; i < itemsQ; i++) {
            AppOpItem item = appOpList.get(i);
            if (item.getCode() == code && item.getUid() == uid && item.getPackageName().equals(packageName)) {
                return item;
            }
        }
        return null;
    }

    private boolean updateActives(int code, int uid, String packageName, boolean active) {
        synchronized (this.mActiveItems) {
            AppOpItem item = getAppOpItem(this.mActiveItems, code, uid, packageName);
            if (item == null && active) {
                this.mActiveItems.add(new AppOpItem(code, uid, packageName, System.currentTimeMillis()));
                return true;
            } else if (item != null && !active) {
                this.mActiveItems.remove(item);
                return true;
            } else {
                return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeNoted(int code, int uid, String packageName) {
        boolean active;
        synchronized (this.mNotedItems) {
            AppOpItem item = getAppOpItem(this.mNotedItems, code, uid, packageName);
            if (item == null) {
                return;
            }
            this.mNotedItems.remove(item);
            synchronized (this.mActiveItems) {
                active = getAppOpItem(this.mActiveItems, code, uid, packageName) != null;
            }
            if (!active) {
                lambda$onOpActiveChanged$0$AppOpsControllerImpl(code, uid, packageName, false);
            }
        }
    }

    private boolean addNoted(int code, int uid, String packageName) {
        AppOpItem item;
        boolean createdNew = false;
        synchronized (this.mNotedItems) {
            item = getAppOpItem(this.mNotedItems, code, uid, packageName);
            if (item == null) {
                item = new AppOpItem(code, uid, packageName, System.currentTimeMillis());
                this.mNotedItems.add(item);
                createdNew = true;
            }
        }
        this.mBGHandler.removeCallbacksAndMessages(item);
        this.mBGHandler.scheduleRemoval(item, NOTED_OP_TIME_DELAY_MS);
        return createdNew;
    }

    @Override // com.android.systemui.appops.AppOpsController
    public List<AppOpItem> getActiveAppOps() {
        return getActiveAppOpsForUser(-1);
    }

    @Override // com.android.systemui.appops.AppOpsController
    public List<AppOpItem> getActiveAppOpsForUser(int userId) {
        List<AppOpItem> list = new ArrayList<>();
        synchronized (this.mActiveItems) {
            int numActiveItems = this.mActiveItems.size();
            for (int i = 0; i < numActiveItems; i++) {
                AppOpItem item = this.mActiveItems.get(i);
                if (userId == -1 || UserHandle.getUserId(item.getUid()) == userId) {
                    list.add(item);
                }
            }
        }
        synchronized (this.mNotedItems) {
            int numNotedItems = this.mNotedItems.size();
            for (int i2 = 0; i2 < numNotedItems; i2++) {
                AppOpItem item2 = this.mNotedItems.get(i2);
                if (userId == -1 || UserHandle.getUserId(item2.getUid()) == userId) {
                    list.add(item2);
                }
            }
        }
        return list;
    }

    public void onOpActiveChanged(final int code, final int uid, final String packageName, final boolean active) {
        boolean alsoNoted;
        boolean activeChanged = updateActives(code, uid, packageName, active);
        if (activeChanged) {
            synchronized (this.mNotedItems) {
                alsoNoted = getAppOpItem(this.mNotedItems, code, uid, packageName) != null;
            }
            if (!alsoNoted) {
                this.mBGHandler.post(new Runnable() { // from class: com.android.systemui.appops.-$$Lambda$AppOpsControllerImpl$ytWudla0eUXQNol33KSx7VyQvYM
                    @Override // java.lang.Runnable
                    public final void run() {
                        AppOpsControllerImpl.this.lambda$onOpActiveChanged$0$AppOpsControllerImpl(code, uid, packageName, active);
                    }
                });
            }
        }
    }

    public void onOpNoted(final int code, final int uid, final String packageName, int result) {
        boolean alsoActive;
        if (result != 0) {
            return;
        }
        boolean notedAdded = addNoted(code, uid, packageName);
        if (notedAdded) {
            synchronized (this.mActiveItems) {
                alsoActive = getAppOpItem(this.mActiveItems, code, uid, packageName) != null;
            }
            if (!alsoActive) {
                this.mBGHandler.post(new Runnable() { // from class: com.android.systemui.appops.-$$Lambda$AppOpsControllerImpl$Ik-chvj1nqb8W_dVPetwy70ZXqg
                    @Override // java.lang.Runnable
                    public final void run() {
                        AppOpsControllerImpl.this.lambda$onOpNoted$1$AppOpsControllerImpl(code, uid, packageName);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$onOpNoted$1$AppOpsControllerImpl(int code, int uid, String packageName) {
        lambda$onOpActiveChanged$0$AppOpsControllerImpl(code, uid, packageName, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: notifySuscribers */
    public void lambda$onOpActiveChanged$0$AppOpsControllerImpl(int code, int uid, String packageName, boolean active) {
        if (this.mCallbacksByCode.containsKey(Integer.valueOf(code))) {
            for (AppOpsController.Callback cb : this.mCallbacksByCode.get(Integer.valueOf(code))) {
                cb.onActiveStateChanged(code, uid, packageName, active);
            }
        }
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("AppOpsController state:");
        pw.println("  Listening: " + this.mListening);
        pw.println("  Active Items:");
        for (int i = 0; i < this.mActiveItems.size(); i++) {
            AppOpItem item = this.mActiveItems.get(i);
            pw.print("    ");
            pw.println(item.toString());
        }
        pw.println("  Noted Items:");
        for (int i2 = 0; i2 < this.mNotedItems.size(); i2++) {
            AppOpItem item2 = this.mNotedItems.get(i2);
            pw.print("    ");
            pw.println(item2.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes21.dex */
    public class H extends Handler {
        H(Looper looper) {
            super(looper);
        }

        public void scheduleRemoval(final AppOpItem item, long timeToRemoval) {
            removeCallbacksAndMessages(item);
            postDelayed(new Runnable() { // from class: com.android.systemui.appops.AppOpsControllerImpl.H.1
                @Override // java.lang.Runnable
                public void run() {
                    AppOpsControllerImpl.this.removeNoted(item.getCode(), item.getUid(), item.getPackageName());
                }
            }, item, timeToRemoval);
        }
    }
}
