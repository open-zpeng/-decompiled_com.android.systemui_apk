package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import com.android.systemui.settings.CurrentUserTracker;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class DeviceProvisionedControllerImpl extends CurrentUserTracker implements DeviceProvisionedController {
    private static final String TAG = DeviceProvisionedControllerImpl.class.getSimpleName();
    private final ContentResolver mContentResolver;
    private final Context mContext;
    private final Uri mDeviceProvisionedUri;
    private final ArrayList<DeviceProvisionedController.DeviceProvisionedListener> mListeners;
    protected final ContentObserver mSettingsObserver;
    private final Uri mUserSetupUri;

    @Inject
    public DeviceProvisionedControllerImpl(Context context, @Named("main_handler") Handler mainHandler) {
        super(context);
        this.mListeners = new ArrayList<>();
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        this.mDeviceProvisionedUri = Settings.Global.getUriFor("device_provisioned");
        this.mUserSetupUri = Settings.Secure.getUriFor("user_setup_complete");
        this.mSettingsObserver = new ContentObserver(mainHandler) { // from class: com.android.systemui.statusbar.policy.DeviceProvisionedControllerImpl.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Uri uri, int userId) {
                String str = DeviceProvisionedControllerImpl.TAG;
                Log.d(str, "Setting change: " + uri);
                if (DeviceProvisionedControllerImpl.this.mUserSetupUri.equals(uri)) {
                    DeviceProvisionedControllerImpl.this.notifySetupChanged();
                } else {
                    DeviceProvisionedControllerImpl.this.notifyProvisionedChanged();
                }
            }
        };
    }

    @Override // com.android.systemui.statusbar.policy.DeviceProvisionedController
    public boolean isDeviceProvisioned() {
        return Settings.Global.getInt(this.mContentResolver, "device_provisioned", 0) != 0;
    }

    @Override // com.android.systemui.statusbar.policy.DeviceProvisionedController
    public boolean isUserSetup(int currentUser) {
        return Settings.Secure.getIntForUser(this.mContentResolver, "user_setup_complete", 0, currentUser) != 0;
    }

    @Override // com.android.systemui.statusbar.policy.DeviceProvisionedController
    public int getCurrentUser() {
        return ActivityManager.getCurrentUser();
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void addCallback(DeviceProvisionedController.DeviceProvisionedListener listener) {
        this.mListeners.add(listener);
        if (this.mListeners.size() == 1) {
            startListening(getCurrentUser());
        }
        listener.onUserSetupChanged();
        listener.onDeviceProvisionedChanged();
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void removeCallback(DeviceProvisionedController.DeviceProvisionedListener listener) {
        this.mListeners.remove(listener);
        if (this.mListeners.size() == 0) {
            stopListening();
        }
    }

    private void startListening(int user) {
        this.mContentResolver.registerContentObserver(this.mDeviceProvisionedUri, true, this.mSettingsObserver, 0);
        this.mContentResolver.registerContentObserver(this.mUserSetupUri, true, this.mSettingsObserver, user);
        startTracking();
    }

    private void stopListening() {
        stopTracking();
        this.mContentResolver.unregisterContentObserver(this.mSettingsObserver);
    }

    @Override // com.android.systemui.settings.CurrentUserTracker
    public void onUserSwitched(int newUserId) {
        this.mContentResolver.unregisterContentObserver(this.mSettingsObserver);
        this.mContentResolver.registerContentObserver(this.mDeviceProvisionedUri, true, this.mSettingsObserver, 0);
        this.mContentResolver.registerContentObserver(this.mUserSetupUri, true, this.mSettingsObserver, newUserId);
        notifyUserChanged();
    }

    private void notifyUserChanged() {
        for (int i = this.mListeners.size() - 1; i >= 0; i--) {
            this.mListeners.get(i).onUserSwitched();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifySetupChanged() {
        for (int i = this.mListeners.size() - 1; i >= 0; i--) {
            this.mListeners.get(i).onUserSetupChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyProvisionedChanged() {
        for (int i = this.mListeners.size() - 1; i >= 0; i--) {
            this.mListeners.get(i).onDeviceProvisionedChanged();
        }
    }
}
