package com.android.systemui.statusbar.phone;

import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.om.IOverlayManager;
import android.content.pm.PackageManager;
import android.content.res.ApkAssets;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseBooleanArray;
import androidx.mediarouter.media.SystemMediaRouteProvider;
import com.android.systemui.Dumpable;
import com.android.systemui.UiOffloadThread;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.PackageManagerWrapper;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class NavigationModeController implements Dumpable {
    private static final boolean DEBUG = false;
    private static final String TAG = NavigationModeController.class.getSimpleName();
    private final Context mContext;
    private Context mCurrentUserContext;
    private final DeviceProvisionedController mDeviceProvisionedController;
    private BroadcastReceiver mEnableGestureNavReceiver;
    private final UiOffloadThread mUiOffloadThread;
    private SparseBooleanArray mRestoreGesturalNavBarMode = new SparseBooleanArray();
    private int mMode = 0;
    private ArrayList<ModeChangedListener> mListeners = new ArrayList<>();
    private BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.systemui.statusbar.phone.NavigationModeController.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (((action.hashCode() == -1946981856 && action.equals("android.intent.action.OVERLAY_CHANGED")) ? (char) 0 : (char) 65535) == 0) {
                NavigationModeController.this.updateCurrentInteractionMode(true);
            }
        }
    };
    private final DeviceProvisionedController.DeviceProvisionedListener mDeviceProvisionedCallback = new DeviceProvisionedController.DeviceProvisionedListener() { // from class: com.android.systemui.statusbar.phone.NavigationModeController.2
        @Override // com.android.systemui.statusbar.policy.DeviceProvisionedController.DeviceProvisionedListener
        public void onDeviceProvisionedChanged() {
            NavigationModeController.this.restoreGesturalNavOverlayIfNecessary();
        }

        @Override // com.android.systemui.statusbar.policy.DeviceProvisionedController.DeviceProvisionedListener
        public void onUserSetupChanged() {
            NavigationModeController.this.restoreGesturalNavOverlayIfNecessary();
        }

        @Override // com.android.systemui.statusbar.policy.DeviceProvisionedController.DeviceProvisionedListener
        public void onUserSwitched() {
            NavigationModeController.this.updateCurrentInteractionMode(true);
            NavigationModeController.this.deferGesturalNavOverlayIfNecessary();
        }
    };
    private final IOverlayManager mOverlayManager = IOverlayManager.Stub.asInterface(ServiceManager.getService("overlay"));

    /* loaded from: classes21.dex */
    public interface ModeChangedListener {
        void onNavigationModeChanged(int i);
    }

    @Inject
    public NavigationModeController(Context context, DeviceProvisionedController deviceProvisionedController, UiOffloadThread uiOffloadThread) {
        this.mContext = context;
        this.mCurrentUserContext = context;
        this.mUiOffloadThread = uiOffloadThread;
        this.mDeviceProvisionedController = deviceProvisionedController;
        this.mDeviceProvisionedController.addCallback(this.mDeviceProvisionedCallback);
        IntentFilter overlayFilter = new IntentFilter("android.intent.action.OVERLAY_CHANGED");
        overlayFilter.addDataScheme("package");
        overlayFilter.addDataSchemeSpecificPart(SystemMediaRouteProvider.PACKAGE_NAME, 0);
        this.mContext.registerReceiverAsUser(this.mReceiver, UserHandle.ALL, overlayFilter, null, null);
        IntentFilter preferredActivityFilter = new IntentFilter(PackageManagerWrapper.ACTION_PREFERRED_ACTIVITY_CHANGED);
        this.mContext.registerReceiverAsUser(this.mReceiver, UserHandle.ALL, preferredActivityFilter, null, null);
        updateCurrentInteractionMode(false);
        deferGesturalNavOverlayIfNecessary();
    }

    private void removeEnableGestureNavListener() {
        BroadcastReceiver broadcastReceiver = this.mEnableGestureNavReceiver;
        if (broadcastReceiver != null) {
            this.mContext.unregisterReceiver(broadcastReceiver);
            this.mEnableGestureNavReceiver = null;
        }
    }

    private boolean setGestureModeOverlayForMainLauncher() {
        removeEnableGestureNavListener();
        if (getCurrentInteractionMode(this.mCurrentUserContext) == 2) {
            return true;
        }
        String str = TAG;
        Log.d(str, "Switching system navigation to full-gesture mode: contextUser=" + this.mCurrentUserContext.getUserId());
        setModeOverlay(QuickStepContract.NAV_BAR_MODE_GESTURAL_OVERLAY, -2);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean enableGestureNav(Intent intent) {
        if (!(intent.getParcelableExtra(QuickStepContract.EXTRA_RESULT_INTENT) instanceof PendingIntent)) {
            Log.e(TAG, "No callback pending intent was attached");
            return false;
        }
        PendingIntent callback = (PendingIntent) intent.getParcelableExtra(QuickStepContract.EXTRA_RESULT_INTENT);
        Intent callbackIntent = callback.getIntent();
        if (callbackIntent == null || !QuickStepContract.ACTION_ENABLE_GESTURE_NAV_RESULT.equals(callbackIntent.getAction())) {
            Log.e(TAG, "Invalid callback intent");
            return false;
        }
        String callerPackage = callback.getCreatorPackage();
        UserHandle callerUser = callback.getCreatorUserHandle();
        DevicePolicyManager dpm = (DevicePolicyManager) this.mCurrentUserContext.getSystemService(DevicePolicyManager.class);
        ComponentName ownerComponent = dpm.getDeviceOwnerComponentOnCallingUser();
        if (ownerComponent != null) {
            if (!ownerComponent.getPackageName().equals(callerPackage) || !this.mCurrentUserContext.getUser().equals(callerUser)) {
                Log.e(TAG, "Callback must be from the device owner");
                return false;
            }
        } else {
            UserHandle callerParent = ((UserManager) this.mCurrentUserContext.getSystemService(UserManager.class)).getProfileParent(callerUser);
            if (callerParent == null || !callerParent.equals(this.mCurrentUserContext.getUser())) {
                Log.e(TAG, "Callback must be from a managed user");
                return false;
            }
            ComponentName profileOwner = dpm.getProfileOwnerAsUser(callerUser);
            if (profileOwner == null || !profileOwner.getPackageName().equals(callerPackage)) {
                Log.e(TAG, "Callback must be from the profile owner");
                return false;
            }
        }
        return setGestureModeOverlayForMainLauncher();
    }

    public void updateCurrentInteractionMode(boolean notify) {
        this.mCurrentUserContext = getCurrentUserContext();
        final int mode = getCurrentInteractionMode(this.mCurrentUserContext);
        this.mMode = mode;
        this.mUiOffloadThread.submit(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NavigationModeController$Az4iHIVUWwUXS_IGosEIyzFux8w
            @Override // java.lang.Runnable
            public final void run() {
                NavigationModeController.this.lambda$updateCurrentInteractionMode$0$NavigationModeController(mode);
            }
        });
        if (notify) {
            for (int i = 0; i < this.mListeners.size(); i++) {
                this.mListeners.get(i).onNavigationModeChanged(mode);
            }
        }
    }

    public /* synthetic */ void lambda$updateCurrentInteractionMode$0$NavigationModeController(int mode) {
        Settings.Secure.putString(this.mCurrentUserContext.getContentResolver(), "navigation_mode", String.valueOf(mode));
    }

    public int addListener(ModeChangedListener listener) {
        this.mListeners.add(listener);
        return getCurrentInteractionMode(this.mCurrentUserContext);
    }

    public void removeListener(ModeChangedListener listener) {
        this.mListeners.remove(listener);
    }

    private int getCurrentInteractionMode(Context context) {
        int mode = context.getResources().getInteger(17694848);
        return mode;
    }

    public Context getCurrentUserContext() {
        int userId = ActivityManagerWrapper.getInstance().getCurrentUserId();
        if (this.mContext.getUserId() == userId) {
            return this.mContext;
        }
        try {
            return this.mContext.createPackageContextAsUser(this.mContext.getPackageName(), 0, UserHandle.of(userId));
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private boolean supportsDeviceAdmin() {
        return this.mContext.getPackageManager().hasSystemFeature("android.software.device_admin");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deferGesturalNavOverlayIfNecessary() {
        int userId = this.mDeviceProvisionedController.getCurrentUser();
        this.mRestoreGesturalNavBarMode.put(userId, false);
        if (this.mDeviceProvisionedController.isDeviceProvisioned() && this.mDeviceProvisionedController.isCurrentUserSetup()) {
            removeEnableGestureNavListener();
            return;
        }
        ArrayList<String> defaultOverlays = new ArrayList<>();
        try {
            defaultOverlays.addAll(Arrays.asList(this.mOverlayManager.getDefaultOverlayPackages()));
        } catch (RemoteException e) {
            Log.e(TAG, "deferGesturalNavOverlayIfNecessary: failed to fetch default overlays");
        }
        if (!defaultOverlays.contains(QuickStepContract.NAV_BAR_MODE_GESTURAL_OVERLAY)) {
            removeEnableGestureNavListener();
            return;
        }
        setModeOverlay(QuickStepContract.NAV_BAR_MODE_3BUTTON_OVERLAY, -2);
        this.mRestoreGesturalNavBarMode.put(userId, true);
        if (supportsDeviceAdmin() && this.mEnableGestureNavReceiver == null) {
            this.mEnableGestureNavReceiver = new BroadcastReceiver() { // from class: com.android.systemui.statusbar.phone.NavigationModeController.3
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    setResultCode(NavigationModeController.this.enableGestureNav(intent) ? -1 : 0);
                }
            };
            this.mContext.registerReceiverAsUser(this.mEnableGestureNavReceiver, UserHandle.ALL, new IntentFilter(QuickStepContract.ACTION_ENABLE_GESTURE_NAV), null, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restoreGesturalNavOverlayIfNecessary() {
        int userId = this.mDeviceProvisionedController.getCurrentUser();
        if (this.mRestoreGesturalNavBarMode.get(userId)) {
            if (!supportsDeviceAdmin() || ((DevicePolicyManager) this.mCurrentUserContext.getSystemService(DevicePolicyManager.class)).getUserProvisioningState() == 0) {
                setGestureModeOverlayForMainLauncher();
            }
            this.mRestoreGesturalNavBarMode.put(userId, false);
        }
    }

    public void setModeOverlay(final String overlayPkg, final int userId) {
        this.mUiOffloadThread.submit(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NavigationModeController$XNbfE14hTqTsqzjGfhml_ek2wAw
            @Override // java.lang.Runnable
            public final void run() {
                NavigationModeController.this.lambda$setModeOverlay$1$NavigationModeController(overlayPkg, userId);
            }
        });
    }

    public /* synthetic */ void lambda$setModeOverlay$1$NavigationModeController(String overlayPkg, int userId) {
        try {
            this.mOverlayManager.setEnabledExclusiveInCategory(overlayPkg, userId);
        } catch (RemoteException e) {
            String str = TAG;
            Log.e(str, "Failed to enable overlay " + overlayPkg + " for user " + userId);
        }
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        String defaultOverlays;
        pw.println("NavigationModeController:");
        pw.println("  mode=" + this.mMode);
        try {
            defaultOverlays = String.join(", ", this.mOverlayManager.getDefaultOverlayPackages());
        } catch (RemoteException e) {
            defaultOverlays = "failed_to_fetch";
        }
        pw.println("  defaultOverlays=" + defaultOverlays);
        dumpAssetPaths(this.mCurrentUserContext);
    }

    private void dumpAssetPaths(Context context) {
        Log.d(TAG, "assetPaths=");
        ApkAssets[] assets = context.getResources().getAssets().getApkAssets();
        for (ApkAssets a : assets) {
            Log.d(TAG, "    " + a.getAssetPath());
        }
    }
}
