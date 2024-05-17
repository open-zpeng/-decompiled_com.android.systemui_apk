package com.xiaopeng.systemui.controller;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.ArraySet;
import android.view.WindowManager;
import com.xiaopeng.app.xpDialogInfo;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.controller.ActivityController2;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.statusbar.StatusBarGlobal;
import com.xiaopeng.view.SharedDisplayListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/* loaded from: classes24.dex */
public class ActivityController2 extends SharedDisplayListener {
    public static final int ID_SHARED_PRIMARY_REDEFINE;
    private static final int[] SUPPORT_SHARED_ID;
    private static final String TAG = "ActController2";
    private final HashMap<Integer, ActivityInfo> mActivityInfos;
    private boolean mActivityPositionChangingNotNotifyMaskWindow;
    private Context mContext;
    private final ArraySet<OnTopWindowChangedListener> mListeners;
    private DialogInfo mPrimaryTopDialog;
    private final BroadcastReceiver mReceiver;
    private final HashMap<Integer, String> mTopActivityForChangedCheck;
    private int mTopPrimaryTypeForChangedCheck;
    private final Handler mUIHandler;
    private WindowManager mWindowManager;
    private final ExecutorService sSingleThreadPool;

    /* loaded from: classes24.dex */
    public interface OnTopWindowChangedListener {
        void onTopWindowChanged(int i, int i2);
    }

    /* loaded from: classes24.dex */
    public static class WindowType {
        public static final int TYPE_DIALOG = 4;
        public static final int TYPE_FULLSCREEN = 3;
        public static final int TYPE_MINI_PROGRAM = 5;
        public static final int TYPE_NORMAL = 0;
        public static final int TYPE_PANEL = 2;
        public static final int TYPE_SUPER_PANEL = 1;
    }

    /* synthetic */ ActivityController2(AnonymousClass1 x0) {
        this();
    }

    static {
        ID_SHARED_PRIMARY_REDEFINE = CarModelsManager.getFeature().isSecondaryWindowSupport() ? 0 : -1;
        SUPPORT_SHARED_ID = CarModelsManager.getFeature().isSecondaryWindowSupport() ? new int[]{ID_SHARED_PRIMARY_REDEFINE, 1} : new int[]{ID_SHARED_PRIMARY_REDEFINE};
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class SingleHolder {
        @SuppressLint({"StaticFieldLeak"})
        private static final ActivityController2 sInstance = new ActivityController2(null);

        private SingleHolder() {
        }
    }

    public static ActivityController2 get() {
        return SingleHolder.sInstance;
    }

    private ActivityController2() {
        this.mReceiver = new AnonymousClass1();
        this.mActivityInfos = new HashMap<>();
        this.mListeners = new ArraySet<>();
        this.mTopActivityForChangedCheck = new HashMap<>();
        this.sSingleThreadPool = Executors.newFixedThreadPool(1);
        this.mUIHandler = new Handler(Looper.getMainLooper());
    }

    public void init(Context context) {
        this.mContext = context.getApplicationContext();
        this.mWindowManager = StatusBarGlobal.getInstance(this.mContext).getWindowManager();
        this.mWindowManager.registerSharedListener(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(PackageHelper.ACTION_ACTIVITY_CHANGED);
        filter.addAction(PackageHelper.ACTION_DIALOG_CHANGED);
        this.mContext.registerReceiver(this.mReceiver, filter);
    }

    private void checkActDialogChanged(String from) {
        int[] iArr;
        long time = System.currentTimeMillis();
        HashMap<Integer, String> topActivity = this.mTopActivityForChangedCheck;
        for (int screenId : SUPPORT_SHARED_ID) {
            String activity = this.mWindowManager.getTopActivity(1, screenId);
            String lastAct = topActivity.get(Integer.valueOf(screenId));
            if (!activity.equals(lastAct)) {
                topActivity.put(Integer.valueOf(screenId), activity);
            }
        }
        long cast = System.currentTimeMillis() - time;
        Logger.d(TAG, String.format("checkActDialog cast:%s ,from=%s ,acts=%s ,mActInfos=%s", Long.valueOf(cast), from, topActivity, this.mActivityInfos));
        checkPrimaryMaskWindow(topActivity.get(Integer.valueOf(ID_SHARED_PRIMARY_REDEFINE)));
    }

    private void checkPrimaryMaskWindow(String primaryTopActivity) {
        if (this.mActivityPositionChangingNotNotifyMaskWindow) {
            Logger.d(TAG, "checkPrimaryMaskWindow not check when activity position changing");
            return;
        }
        long time = System.currentTimeMillis();
        ActivityInfo info = null;
        int type = 0;
        if (this.mPrimaryTopDialog != null) {
            type = 4;
        } else {
            ActivityInfo info2 = this.mActivityInfos.get(Integer.valueOf(ID_SHARED_PRIMARY_REDEFINE));
            info = info2;
            if (info != null && primaryTopActivity.equals(info.component)) {
                type = info.type;
            }
        }
        long cast = System.currentTimeMillis() - time;
        if (this.mTopPrimaryTypeForChangedCheck != type) {
            Logger.d(TAG, String.format("checkPrimaryMaskWindow cast:%s , oldtype:%s , newtype:%s ,primary:%s , info:%s ", Long.valueOf(cast), Integer.valueOf(this.mTopPrimaryTypeForChangedCheck), Integer.valueOf(type), primaryTopActivity, info));
            this.mTopPrimaryTypeForChangedCheck = type;
            notifyMaskWindowChanged(ID_SHARED_PRIMARY_REDEFINE, type);
        }
    }

    private void notifyMaskWindowChanged(final int screenId, final int type) {
        synchronized (this.mListeners) {
            this.mUIHandler.post(new Runnable() { // from class: com.xiaopeng.systemui.controller.-$$Lambda$ActivityController2$rZSeXTY61CijobVyr16GL3u3XRg
                @Override // java.lang.Runnable
                public final void run() {
                    ActivityController2.this.lambda$notifyMaskWindowChanged$0$ActivityController2(screenId, type);
                }
            });
        }
    }

    public /* synthetic */ void lambda$notifyMaskWindowChanged$0$ActivityController2(int screenId, int type) {
        Iterator<OnTopWindowChangedListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            OnTopWindowChangedListener l = it.next();
            l.onTopWindowChanged(screenId, type);
        }
    }

    public void addOnTopWindowChangedListener(OnTopWindowChangedListener listener) {
        synchronized (this.mListeners) {
            this.mListeners.add(listener);
        }
    }

    public void removeOnTopWindowChangedListener(OnTopWindowChangedListener listener) {
        synchronized (this.mListeners) {
            this.mListeners.remove(listener);
        }
    }

    public void onPositionChanged(final String packageName, final int event, final int from, final int to) throws RemoteException {
        super.onPositionChanged(packageName, event, from, to);
        this.sSingleThreadPool.execute(new Runnable() { // from class: com.xiaopeng.systemui.controller.-$$Lambda$ActivityController2$vnSHqWchuipvzJB_t2CII5cXar8
            @Override // java.lang.Runnable
            public final void run() {
                ActivityController2.this.lambda$onPositionChanged$1$ActivityController2(packageName, event, from, to);
            }
        });
    }

    public /* synthetic */ void lambda$onPositionChanged$1$ActivityController2(String packageName, int event, int from, int to) {
        try {
            Logger.d(TAG, String.format("onPosChanged packageName:%s , event:%s ,from:%s , to:%s ", packageName, Integer.valueOf(event), Integer.valueOf(from), Integer.valueOf(to)));
            this.mActivityPositionChangingNotNotifyMaskWindow = event == 1;
            ActivityInfo info = this.mActivityInfos.get(Integer.valueOf(from));
            this.mActivityInfos.put(Integer.valueOf(to), info);
            checkActDialogChanged("SharedPos");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onActivityChanged(int screenId, String property) throws RemoteException {
        super.onActivityChanged(screenId, property);
        this.sSingleThreadPool.execute(new Runnable() { // from class: com.xiaopeng.systemui.controller.-$$Lambda$ActivityController2$-TwCnRc9x-yDHKmmZ_oe7tpOuAg
            @Override // java.lang.Runnable
            public final void run() {
                ActivityController2.this.lambda$onActivityChanged$2$ActivityController2();
            }
        });
    }

    public /* synthetic */ void lambda$onActivityChanged$2$ActivityController2() {
        try {
            checkActDialogChanged("SharedAct");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ActivityChanged(Intent intent) {
        String component = intent.getStringExtra(PackageHelper.EXTRA_COMPONENT);
        if (TextUtils.isEmpty(component)) {
            return;
        }
        long time = System.currentTimeMillis();
        ComponentName componentName = ComponentName.unflattenFromString(component);
        boolean isLauncher = PackageHelper.isHomePackage(this.mContext, componentName.getPackageName());
        int topType = ActivityInfo.getType(intent, isLauncher);
        int screenId = this.mWindowManager.getScreenId(componentName.getPackageName());
        ActivityInfo activityInfo = this.mActivityInfos.get(Integer.valueOf(screenId));
        ActivityInfo newInfo = new ActivityInfo(component, topType, screenId);
        if (newInfo.equals(activityInfo)) {
            return;
        }
        this.mActivityInfos.put(Integer.valueOf(screenId), newInfo);
        int i = ID_SHARED_PRIMARY_REDEFINE;
        if (i == -1) {
            this.mActivityInfos.put(Integer.valueOf(i), newInfo);
            Logger.d(TAG, "ActivityChanged ID_SHARED_PRIMARY_REDEFINE is -1");
        }
        long cast = System.currentTimeMillis() - time;
        Logger.d(TAG, String.format("onReceive ACTIVITY cast:%s, isLauncher%s, info:%s", Long.valueOf(cast), Boolean.valueOf(isLauncher), newInfo));
        checkActDialogChanged("ReAct");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dialogChangedInPrimaryScreen(Intent intent) {
        boolean hasVisibleDialog = false;
        xpDialogInfo dialogInfo = null;
        long time = System.currentTimeMillis();
        if (intent.hasExtra(PackageHelper.EXTRA_TOPPING_DIALOG)) {
            dialogInfo = (xpDialogInfo) intent.getParcelableExtra(PackageHelper.EXTRA_TOPPING_DIALOG);
            hasVisibleDialog = dialogInfo.fullscreen && dialogInfo.visible && dialogInfo.dimAmount != 0.0f;
        }
        DialogInfo lastDialog = this.mPrimaryTopDialog;
        if (hasVisibleDialog) {
            int screenId = this.mWindowManager.getScreenId(dialogInfo.packageName);
            int i = ID_SHARED_PRIMARY_REDEFINE;
            if (screenId != i && screenId != -1 && i != -1) {
                Logger.d(TAG, String.format("onReceive DIALOG screenId: %s", Integer.valueOf(screenId)));
            } else {
                this.mPrimaryTopDialog = new DialogInfo(dialogInfo.packageName, screenId);
            }
        } else {
            this.mPrimaryTopDialog = null;
        }
        long cast = System.currentTimeMillis() - time;
        Logger.d(TAG, String.format("onReceive DIALOG cast:%s , hasVisibl :%s ,dialogInfo :%s ,old :%s , new: %s", Long.valueOf(cast), Boolean.valueOf(hasVisibleDialog), dialogInfo, lastDialog, this.mPrimaryTopDialog));
        if ((lastDialog == null && this.mPrimaryTopDialog != null) || (lastDialog != null && !lastDialog.equals(this.mPrimaryTopDialog))) {
            checkActDialogChanged("ReDia");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.xiaopeng.systemui.controller.ActivityController2$1  reason: invalid class name */
    /* loaded from: classes24.dex */
    public class AnonymousClass1 extends BroadcastReceiver {
        AnonymousClass1() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            if (PackageHelper.ACTION_ACTIVITY_CHANGED.equals(action)) {
                ActivityController2.this.sSingleThreadPool.execute(new Runnable() { // from class: com.xiaopeng.systemui.controller.-$$Lambda$ActivityController2$1$Vq5sud2YMRTnUTiEu4qW3Ix0e08
                    @Override // java.lang.Runnable
                    public final void run() {
                        ActivityController2.AnonymousClass1.this.lambda$onReceive$0$ActivityController2$1(intent);
                    }
                });
            } else if (PackageHelper.ACTION_DIALOG_CHANGED.equals(action)) {
                ActivityController2.this.sSingleThreadPool.execute(new Runnable() { // from class: com.xiaopeng.systemui.controller.-$$Lambda$ActivityController2$1$86UXetkwqXYx1M5tdOw_SGtseYM
                    @Override // java.lang.Runnable
                    public final void run() {
                        ActivityController2.AnonymousClass1.this.lambda$onReceive$1$ActivityController2$1(intent);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onReceive$0$ActivityController2$1(Intent intent) {
            try {
                ActivityController2.this.ActivityChanged(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public /* synthetic */ void lambda$onReceive$1$ActivityController2$1(Intent intent) {
            try {
                ActivityController2.this.dialogChangedInPrimaryScreen(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class ActivityInfo {
        public static final int FLAG_ACTIVITY_LAUNCH_PANEL = 256;
        private static final int FLAG_MINI_PROG = 512;
        private String component;
        private int screenId;
        private int type;

        public ActivityInfo(String component, int type, int screenId) {
            this.component = component;
            this.type = type;
            this.screenId = screenId;
        }

        public static int getType(Intent intent, boolean isLauncher) {
            int flags = intent.getIntExtra("android.intent.extra.FLAGS", 0);
            boolean miniProgVisible = intent.getBooleanExtra("android.intent.extra.mini.PROGRAM", false);
            boolean isMiniProgram = (flags & 512) == 512 && miniProgVisible;
            boolean isPanelVisible = (flags & 256) == 256;
            boolean isFullScreen = intent.getBooleanExtra(PackageHelper.EXTRA_FULLSCREEN_ACTIVITY, false);
            boolean isSuperPanel = intent.getIntExtra(PackageHelper.EXTRA_WINDOW_LEVEL, 0) > 0;
            if (isLauncher) {
                return 0;
            }
            if (isSuperPanel) {
                return 1;
            }
            if (isPanelVisible) {
                return 2;
            }
            if (isFullScreen) {
                return 3;
            }
            if (!isMiniProgram) {
                return 0;
            }
            return 5;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ActivityInfo that = (ActivityInfo) o;
            if (this.type == that.type && this.screenId == that.screenId && Objects.equals(this.component, that.component)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.component, Integer.valueOf(this.type), Integer.valueOf(this.screenId));
        }

        public String toString() {
            return "WindowInfo{component='" + this.component + "', type=" + this.type + ", screenId=" + this.screenId + '}';
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class DialogInfo {
        private String packageName;
        private int screenId;

        public DialogInfo(String packageName, int screenId) {
            this.packageName = packageName;
            this.screenId = screenId;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            DialogInfo that = (DialogInfo) o;
            if (this.screenId == that.screenId && Objects.equals(this.packageName, that.packageName)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.packageName, Integer.valueOf(this.screenId));
        }

        public String toString() {
            return "DialogInfo{packageName='" + this.packageName + "', screenId=" + this.screenId + '}';
        }
    }
}
