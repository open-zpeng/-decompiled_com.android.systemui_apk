package com.android.systemui.recents;

import android.app.ActivityManager;
import android.app.trust.TrustManager;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.shared.recents.IOverviewProxy;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.stackdivider.Divider;
import com.android.systemui.statusbar.phone.StatusBar;
/* loaded from: classes21.dex */
public class OverviewProxyRecentsImpl implements RecentsImplementation {
    private static final String TAG = "OverviewProxyRecentsImpl";
    private Context mContext;
    private Handler mHandler;
    private OverviewProxyService mOverviewProxyService;
    private SysUiServiceProvider mSysUiServiceProvider;
    private TrustManager mTrustManager;

    @Override // com.android.systemui.recents.RecentsImplementation
    public void onStart(Context context, SysUiServiceProvider sysUiServiceProvider) {
        this.mContext = context;
        this.mSysUiServiceProvider = sysUiServiceProvider;
        this.mHandler = new Handler();
        this.mTrustManager = (TrustManager) context.getSystemService("trust");
        this.mOverviewProxyService = (OverviewProxyService) Dependency.get(OverviewProxyService.class);
    }

    @Override // com.android.systemui.recents.RecentsImplementation
    public void showRecentApps(boolean triggeredFromAltTab) {
        IOverviewProxy overviewProxy = this.mOverviewProxyService.getProxy();
        if (overviewProxy != null) {
            try {
                overviewProxy.onOverviewShown(triggeredFromAltTab);
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to send overview show event to launcher.", e);
            }
        }
    }

    @Override // com.android.systemui.recents.RecentsImplementation
    public void hideRecentApps(boolean triggeredFromAltTab, boolean triggeredFromHomeKey) {
        IOverviewProxy overviewProxy = this.mOverviewProxyService.getProxy();
        if (overviewProxy != null) {
            try {
                overviewProxy.onOverviewHidden(triggeredFromAltTab, triggeredFromHomeKey);
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to send overview hide event to launcher.", e);
            }
        }
    }

    @Override // com.android.systemui.recents.RecentsImplementation
    public void toggleRecentApps() {
        IOverviewProxy overviewProxy = this.mOverviewProxyService.getProxy();
        if (overviewProxy != null) {
            final Runnable toggleRecents = new Runnable() { // from class: com.android.systemui.recents.-$$Lambda$OverviewProxyRecentsImpl$ZzsBj6p_GVl3rLvpPg-WKT0NW9E
                @Override // java.lang.Runnable
                public final void run() {
                    OverviewProxyRecentsImpl.this.lambda$toggleRecentApps$0$OverviewProxyRecentsImpl();
                }
            };
            StatusBar statusBar = (StatusBar) this.mSysUiServiceProvider.getComponent(StatusBar.class);
            if (statusBar != null && statusBar.isKeyguardShowing()) {
                statusBar.executeRunnableDismissingKeyguard(new Runnable() { // from class: com.android.systemui.recents.-$$Lambda$OverviewProxyRecentsImpl$PUSBynP3ZsSZrPqXO1jJqSKnayU
                    @Override // java.lang.Runnable
                    public final void run() {
                        OverviewProxyRecentsImpl.this.lambda$toggleRecentApps$1$OverviewProxyRecentsImpl(toggleRecents);
                    }
                }, null, true, false, true);
            } else {
                toggleRecents.run();
            }
        }
    }

    public /* synthetic */ void lambda$toggleRecentApps$0$OverviewProxyRecentsImpl() {
        try {
            if (this.mOverviewProxyService.getProxy() != null) {
                this.mOverviewProxyService.getProxy().onOverviewToggle();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Cannot send toggle recents through proxy service.", e);
        }
    }

    public /* synthetic */ void lambda$toggleRecentApps$1$OverviewProxyRecentsImpl(Runnable toggleRecents) {
        this.mTrustManager.reportKeyguardShowingChanged();
        this.mHandler.post(toggleRecents);
    }

    @Override // com.android.systemui.recents.RecentsImplementation
    public boolean splitPrimaryTask(int stackCreateMode, Rect initialBounds, int metricsDockAction) {
        int activityType;
        Point realSize = new Point();
        if (initialBounds == null) {
            ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).getDisplay(0).getRealSize(realSize);
            initialBounds = new Rect(0, 0, realSize.x, realSize.y);
        }
        ActivityManager.RunningTaskInfo runningTask = ActivityManagerWrapper.getInstance().getRunningTask();
        if (runningTask != null) {
            activityType = runningTask.configuration.windowConfiguration.getActivityType();
        } else {
            activityType = 0;
        }
        boolean screenPinningActive = ActivityManagerWrapper.getInstance().isScreenPinningActive();
        boolean isRunningTaskInHomeOrRecentsStack = activityType == 2 || activityType == 3;
        if (runningTask != null && !isRunningTaskInHomeOrRecentsStack && !screenPinningActive) {
            if (runningTask.supportsSplitScreenMultiWindow) {
                if (ActivityManagerWrapper.getInstance().setTaskWindowingModeSplitScreenPrimary(runningTask.id, stackCreateMode, initialBounds)) {
                    Divider divider = (Divider) this.mSysUiServiceProvider.getComponent(Divider.class);
                    if (divider != null) {
                        divider.onRecentsDrawn();
                    }
                    return true;
                }
            } else {
                Toast.makeText(this.mContext, R.string.dock_non_resizeble_failed_to_dock_text, 0).show();
            }
        }
        return false;
    }
}
