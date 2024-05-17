package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.StatusBarManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.os.UserManager;
import androidx.annotation.VisibleForTesting;
import com.android.settingslib.Utils;
import com.android.systemui.statusbar.policy.LocationController;
import com.android.systemui.statusbar.policy.LocationControllerImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class LocationControllerImpl extends BroadcastReceiver implements LocationController {
    private static final int[] mHighPowerRequestAppOpArray = {42};
    private AppOpsManager mAppOpsManager;
    private boolean mAreActiveLocationRequests;
    private Context mContext;
    private StatusBarManager mStatusBarManager;
    private ArrayList<LocationController.LocationChangeCallback> mSettingsChangeCallbacks = new ArrayList<>();
    private final H mHandler = new H();

    @Inject
    public LocationControllerImpl(Context context, @Named("background_looper") Looper bgLooper) {
        this.mContext = context;
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.location.HIGH_POWER_REQUEST_CHANGE");
        filter.addAction("android.location.MODE_CHANGED");
        context.registerReceiverAsUser(this, UserHandle.ALL, filter, null, new Handler(bgLooper));
        this.mAppOpsManager = (AppOpsManager) context.getSystemService("appops");
        this.mStatusBarManager = (StatusBarManager) context.getSystemService("statusbar");
        updateActiveLocationRequests();
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void addCallback(LocationController.LocationChangeCallback cb) {
        this.mSettingsChangeCallbacks.add(cb);
        this.mHandler.sendEmptyMessage(1);
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void removeCallback(LocationController.LocationChangeCallback cb) {
        this.mSettingsChangeCallbacks.remove(cb);
    }

    @Override // com.android.systemui.statusbar.policy.LocationController
    public boolean setLocationEnabled(boolean enabled) {
        int currentUserId = ActivityManager.getCurrentUser();
        if (isUserLocationRestricted(currentUserId)) {
            return false;
        }
        Utils.updateLocationEnabled(this.mContext, enabled, currentUserId, 2);
        return true;
    }

    @Override // com.android.systemui.statusbar.policy.LocationController
    public boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) this.mContext.getSystemService("location");
        return locationManager.isLocationEnabledForUser(UserHandle.of(ActivityManager.getCurrentUser()));
    }

    @Override // com.android.systemui.statusbar.policy.LocationController
    public boolean isLocationActive() {
        return this.mAreActiveLocationRequests;
    }

    private boolean isUserLocationRestricted(int userId) {
        UserManager um = (UserManager) this.mContext.getSystemService("user");
        return um.hasUserRestriction("no_share_location", UserHandle.of(userId));
    }

    @VisibleForTesting
    protected boolean areActiveHighPowerLocationRequests() {
        List<AppOpsManager.PackageOps> packages = this.mAppOpsManager.getPackagesForOps(mHighPowerRequestAppOpArray);
        if (packages != null) {
            int numPackages = packages.size();
            for (int packageInd = 0; packageInd < numPackages; packageInd++) {
                AppOpsManager.PackageOps packageOp = packages.get(packageInd);
                List<AppOpsManager.OpEntry> opEntries = packageOp.getOps();
                if (opEntries != null) {
                    int numOps = opEntries.size();
                    for (int opInd = 0; opInd < numOps; opInd++) {
                        AppOpsManager.OpEntry opEntry = opEntries.get(opInd);
                        if (opEntry.getOp() == 42 && opEntry.isRunning()) {
                            return true;
                        }
                    }
                    continue;
                }
            }
            return false;
        }
        return false;
    }

    private void updateActiveLocationRequests() {
        boolean hadActiveLocationRequests = this.mAreActiveLocationRequests;
        this.mAreActiveLocationRequests = areActiveHighPowerLocationRequests();
        if (this.mAreActiveLocationRequests != hadActiveLocationRequests) {
            this.mHandler.sendEmptyMessage(2);
        }
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("android.location.HIGH_POWER_REQUEST_CHANGE".equals(action)) {
            updateActiveLocationRequests();
        } else if ("android.location.MODE_CHANGED".equals(action)) {
            this.mHandler.sendEmptyMessage(1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public final class H extends Handler {
        private static final int MSG_LOCATION_ACTIVE_CHANGED = 2;
        private static final int MSG_LOCATION_SETTINGS_CHANGED = 1;

        private H() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1) {
                locationSettingsChanged();
            } else if (i == 2) {
                locationActiveChanged();
            }
        }

        private void locationActiveChanged() {
            com.android.systemui.util.Utils.safeForeach(LocationControllerImpl.this.mSettingsChangeCallbacks, new Consumer() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$LocationControllerImpl$H$vKTe7eMzgWgCJvXCt8UIIkFyg78
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    LocationControllerImpl.H.this.lambda$locationActiveChanged$0$LocationControllerImpl$H((LocationController.LocationChangeCallback) obj);
                }
            });
        }

        public /* synthetic */ void lambda$locationActiveChanged$0$LocationControllerImpl$H(LocationController.LocationChangeCallback cb) {
            cb.onLocationActiveChanged(LocationControllerImpl.this.mAreActiveLocationRequests);
        }

        private void locationSettingsChanged() {
            final boolean isEnabled = LocationControllerImpl.this.isLocationEnabled();
            com.android.systemui.util.Utils.safeForeach(LocationControllerImpl.this.mSettingsChangeCallbacks, new Consumer() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$LocationControllerImpl$H$xXVOboFsQOHoRY-EFzvZu-IOYh0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((LocationController.LocationChangeCallback) obj).onLocationSettingsChanged(isEnabled);
                }
            });
        }
    }
}
