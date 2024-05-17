package com.android.systemui.pip.phone;

import android.app.AppOpsManager;
import android.app.IActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Pair;
import com.android.systemui.pip.phone.PipAppOpsListener;
/* loaded from: classes21.dex */
public class PipAppOpsListener {
    private static final String TAG = PipAppOpsListener.class.getSimpleName();
    private IActivityManager mActivityManager;
    private AppOpsManager.OnOpChangedListener mAppOpsChangedListener = new AnonymousClass1();
    private AppOpsManager mAppOpsManager;
    private Callback mCallback;
    private Context mContext;
    private Handler mHandler;

    /* loaded from: classes21.dex */
    public interface Callback {
        void dismissPip();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.pip.phone.PipAppOpsListener$1  reason: invalid class name */
    /* loaded from: classes21.dex */
    public class AnonymousClass1 implements AppOpsManager.OnOpChangedListener {
        AnonymousClass1() {
        }

        @Override // android.app.AppOpsManager.OnOpChangedListener
        public void onOpChanged(String op, String packageName) {
            try {
                Pair<ComponentName, Integer> topPipActivityInfo = PipUtils.getTopPinnedActivity(PipAppOpsListener.this.mContext, PipAppOpsListener.this.mActivityManager);
                if (topPipActivityInfo.first != null) {
                    ApplicationInfo appInfo = PipAppOpsListener.this.mContext.getPackageManager().getApplicationInfoAsUser(packageName, 0, ((Integer) topPipActivityInfo.second).intValue());
                    if (appInfo.packageName.equals(((ComponentName) topPipActivityInfo.first).getPackageName()) && PipAppOpsListener.this.mAppOpsManager.checkOpNoThrow(67, appInfo.uid, packageName) != 0) {
                        PipAppOpsListener.this.mHandler.post(new Runnable() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipAppOpsListener$1$UK38MrwiG74h0N6r_NQ6zq34Mqo
                            @Override // java.lang.Runnable
                            public final void run() {
                                PipAppOpsListener.AnonymousClass1.this.lambda$onOpChanged$0$PipAppOpsListener$1();
                            }
                        });
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                PipAppOpsListener.this.unregisterAppOpsListener();
            }
        }

        public /* synthetic */ void lambda$onOpChanged$0$PipAppOpsListener$1() {
            PipAppOpsListener.this.mCallback.dismissPip();
        }
    }

    public PipAppOpsListener(Context context, IActivityManager activityManager, Callback callback) {
        this.mContext = context;
        this.mHandler = new Handler(this.mContext.getMainLooper());
        this.mActivityManager = activityManager;
        this.mAppOpsManager = (AppOpsManager) context.getSystemService("appops");
        this.mCallback = callback;
    }

    public void onActivityPinned(String packageName) {
        registerAppOpsListener(packageName);
    }

    public void onActivityUnpinned() {
        unregisterAppOpsListener();
    }

    private void registerAppOpsListener(String packageName) {
        this.mAppOpsManager.startWatchingMode(67, packageName, this.mAppOpsChangedListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterAppOpsListener() {
        this.mAppOpsManager.stopWatchingMode(this.mAppOpsChangedListener);
    }
}
