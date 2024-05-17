package com.android.systemui.statusbar;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class NotificationLockscreenUserManagerImpl implements Dumpable, NotificationLockscreenUserManager, StatusBarStateController.StateListener {
    private static final boolean ENABLE_LOCK_SCREEN_ALLOW_REMOTE_INPUT = false;
    private static final String TAG = "LockscreenUserManager";
    private boolean mAllowLockscreenRemoteInput;
    protected final Context mContext;
    protected int mCurrentUserId;
    private final DevicePolicyManager mDevicePolicyManager;
    private NotificationEntryManager mEntryManager;
    protected KeyguardManager mKeyguardManager;
    private LockPatternUtils mLockPatternUtils;
    protected ContentObserver mLockscreenSettingsObserver;
    protected NotificationPresenter mPresenter;
    protected ContentObserver mSettingsObserver;
    private boolean mShowLockscreenNotifications;
    private final UserManager mUserManager;
    private final DeviceProvisionedController mDeviceProvisionedController = (DeviceProvisionedController) Dependency.get(DeviceProvisionedController.class);
    private final KeyguardMonitor mKeyguardMonitor = (KeyguardMonitor) Dependency.get(KeyguardMonitor.class);
    private final SparseBooleanArray mLockscreenPublicMode = new SparseBooleanArray();
    private final SparseBooleanArray mUsersWithSeperateWorkChallenge = new SparseBooleanArray();
    private final SparseBooleanArray mUsersAllowingPrivateNotifications = new SparseBooleanArray();
    private final SparseBooleanArray mUsersAllowingNotifications = new SparseBooleanArray();
    private final List<NotificationLockscreenUserManager.UserChangedListener> mListeners = new ArrayList();
    private int mState = 0;
    protected final BroadcastReceiver mAllUsersReceiver = new BroadcastReceiver() { // from class: com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED".equals(action) && NotificationLockscreenUserManagerImpl.this.isCurrentProfile(getSendingUserId())) {
                NotificationLockscreenUserManagerImpl.this.mUsersAllowingPrivateNotifications.clear();
                NotificationLockscreenUserManagerImpl.this.updateLockscreenNotificationSetting();
                NotificationLockscreenUserManagerImpl.this.getEntryManager().updateNotifications();
            }
        }
    };
    protected final BroadcastReceiver mBaseBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.USER_SWITCHED".equals(action)) {
                NotificationLockscreenUserManagerImpl.this.mCurrentUserId = intent.getIntExtra("android.intent.extra.user_handle", -1);
                NotificationLockscreenUserManagerImpl.this.updateCurrentProfilesCache();
                Log.v(NotificationLockscreenUserManagerImpl.TAG, "userId " + NotificationLockscreenUserManagerImpl.this.mCurrentUserId + " is in the house");
                NotificationLockscreenUserManagerImpl.this.updateLockscreenNotificationSetting();
                NotificationLockscreenUserManagerImpl.this.updatePublicMode();
                NotificationLockscreenUserManagerImpl.this.getEntryManager().getNotificationData().filterAndSort();
                NotificationLockscreenUserManagerImpl.this.mPresenter.onUserSwitched(NotificationLockscreenUserManagerImpl.this.mCurrentUserId);
                for (NotificationLockscreenUserManager.UserChangedListener listener : NotificationLockscreenUserManagerImpl.this.mListeners) {
                    listener.onUserChanged(NotificationLockscreenUserManagerImpl.this.mCurrentUserId);
                }
            } else if ("android.intent.action.USER_ADDED".equals(action)) {
                NotificationLockscreenUserManagerImpl.this.updateCurrentProfilesCache();
            } else if ("android.intent.action.USER_UNLOCKED".equals(action)) {
                ((OverviewProxyService) Dependency.get(OverviewProxyService.class)).startConnectionToCurrentUser();
            } else if (NotificationLockscreenUserManager.NOTIFICATION_UNLOCKED_BY_WORK_CHALLENGE_ACTION.equals(action)) {
                IntentSender intentSender = (IntentSender) intent.getParcelableExtra("android.intent.extra.INTENT");
                String notificationKey = intent.getStringExtra("android.intent.extra.INDEX");
                if (intentSender != null) {
                    try {
                        NotificationLockscreenUserManagerImpl.this.mContext.startIntentSender(intentSender, null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                    }
                }
                if (notificationKey != null) {
                    int count = NotificationLockscreenUserManagerImpl.this.getEntryManager().getNotificationData().getActiveNotifications().size();
                    int rank = NotificationLockscreenUserManagerImpl.this.getEntryManager().getNotificationData().getRank(notificationKey);
                    NotificationVisibility.NotificationLocation location = NotificationLogger.getNotificationLocation(NotificationLockscreenUserManagerImpl.this.getEntryManager().getNotificationData().get(notificationKey));
                    NotificationVisibility nv = NotificationVisibility.obtain(notificationKey, rank, count, true, location);
                    try {
                        NotificationLockscreenUserManagerImpl.this.mBarService.onNotificationClick(notificationKey, nv);
                    } catch (RemoteException e2) {
                    }
                }
            }
        }
    };
    protected final SparseArray<UserInfo> mCurrentProfiles = new SparseArray<>();
    private final IStatusBarService mBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));

    /* JADX INFO: Access modifiers changed from: private */
    public NotificationEntryManager getEntryManager() {
        if (this.mEntryManager == null) {
            this.mEntryManager = (NotificationEntryManager) Dependency.get(NotificationEntryManager.class);
        }
        return this.mEntryManager;
    }

    @Inject
    public NotificationLockscreenUserManagerImpl(Context context) {
        this.mCurrentUserId = 0;
        this.mContext = context;
        this.mDevicePolicyManager = (DevicePolicyManager) this.mContext.getSystemService("device_policy");
        this.mUserManager = (UserManager) this.mContext.getSystemService("user");
        this.mCurrentUserId = ActivityManager.getCurrentUser();
        ((StatusBarStateController) Dependency.get(StatusBarStateController.class)).addCallback(this);
        this.mLockPatternUtils = new LockPatternUtils(context);
        this.mKeyguardManager = (KeyguardManager) context.getSystemService(KeyguardManager.class);
    }

    @Override // com.android.systemui.statusbar.NotificationLockscreenUserManager
    public void setUpWithPresenter(NotificationPresenter presenter) {
        this.mPresenter = presenter;
        this.mLockscreenSettingsObserver = new ContentObserver((Handler) Dependency.get(Dependency.MAIN_HANDLER)) { // from class: com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.3
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                NotificationLockscreenUserManagerImpl.this.mUsersAllowingPrivateNotifications.clear();
                NotificationLockscreenUserManagerImpl.this.mUsersAllowingNotifications.clear();
                NotificationLockscreenUserManagerImpl.this.updateLockscreenNotificationSetting();
                NotificationLockscreenUserManagerImpl.this.getEntryManager().updateNotifications();
            }
        };
        this.mSettingsObserver = new ContentObserver((Handler) Dependency.get(Dependency.MAIN_HANDLER)) { // from class: com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.4
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                NotificationLockscreenUserManagerImpl.this.updateLockscreenNotificationSetting();
                if (NotificationLockscreenUserManagerImpl.this.mDeviceProvisionedController.isDeviceProvisioned()) {
                    NotificationLockscreenUserManagerImpl.this.getEntryManager().updateNotifications();
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("lock_screen_show_notifications"), false, this.mLockscreenSettingsObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("lock_screen_allow_private_notifications"), true, this.mLockscreenSettingsObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("zen_mode"), false, this.mSettingsObserver);
        this.mContext.registerReceiverAsUser(this.mAllUsersReceiver, UserHandle.ALL, new IntentFilter("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED"), null, null);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.USER_SWITCHED");
        filter.addAction("android.intent.action.USER_ADDED");
        filter.addAction("android.intent.action.USER_UNLOCKED");
        this.mContext.registerReceiver(this.mBaseBroadcastReceiver, filter);
        IntentFilter internalFilter = new IntentFilter();
        internalFilter.addAction(NotificationLockscreenUserManager.NOTIFICATION_UNLOCKED_BY_WORK_CHALLENGE_ACTION);
        this.mContext.registerReceiver(this.mBaseBroadcastReceiver, internalFilter, "com.android.systemui.permission.SELF", null);
        updateCurrentProfilesCache();
        this.mSettingsObserver.onChange(false);
    }

    @Override // com.android.systemui.statusbar.NotificationLockscreenUserManager
    public boolean shouldShowLockscreenNotifications() {
        return this.mShowLockscreenNotifications;
    }

    @Override // com.android.systemui.statusbar.NotificationLockscreenUserManager
    public boolean shouldAllowLockscreenRemoteInput() {
        return this.mAllowLockscreenRemoteInput;
    }

    @Override // com.android.systemui.statusbar.NotificationLockscreenUserManager
    public boolean isCurrentProfile(int userId) {
        boolean z;
        synchronized (this.mCurrentProfiles) {
            if (userId != -1) {
                try {
                    if (this.mCurrentProfiles.get(userId) == null) {
                        z = false;
                    }
                } finally {
                }
            }
            z = true;
        }
        return z;
    }

    private boolean shouldTemporarilyHideNotifications(int userId) {
        if (userId == -1) {
            userId = this.mCurrentUserId;
        }
        return KeyguardUpdateMonitor.getInstance(this.mContext).isUserInLockdown(userId);
    }

    @Override // com.android.systemui.statusbar.NotificationLockscreenUserManager
    public boolean shouldHideNotifications(int userId) {
        int i;
        return (isLockscreenPublicMode(userId) && !userAllowsNotificationsInPublic(userId)) || (userId != (i = this.mCurrentUserId) && shouldHideNotifications(i)) || shouldTemporarilyHideNotifications(userId);
    }

    @Override // com.android.systemui.statusbar.NotificationLockscreenUserManager
    public boolean shouldHideNotifications(String key) {
        if (getEntryManager() != null) {
            return isLockscreenPublicMode(this.mCurrentUserId) && getEntryManager().getNotificationData().getVisibilityOverride(key) == -1;
        }
        Log.wtf(TAG, "mEntryManager was null!", new Throwable());
        return true;
    }

    @Override // com.android.systemui.statusbar.NotificationLockscreenUserManager
    public boolean shouldShowOnKeyguard(NotificationEntry entry) {
        boolean exceedsPriorityThreshold;
        if (getEntryManager() == null) {
            Log.wtf(TAG, "mEntryManager was null!", new Throwable());
            return false;
        }
        if (NotificationUtils.useNewInterruptionModel(this.mContext) && hideSilentNotificationsOnLockscreen()) {
            exceedsPriorityThreshold = entry.isTopBucket();
        } else {
            exceedsPriorityThreshold = !getEntryManager().getNotificationData().isAmbient(entry.key);
        }
        return this.mShowLockscreenNotifications && exceedsPriorityThreshold;
    }

    private boolean hideSilentNotificationsOnLockscreen() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "lock_screen_show_silent_notifications", 1) == 0;
    }

    private void setShowLockscreenNotifications(boolean show) {
        this.mShowLockscreenNotifications = show;
    }

    private void setLockscreenAllowRemoteInput(boolean allowLockscreenRemoteInput) {
        this.mAllowLockscreenRemoteInput = allowLockscreenRemoteInput;
    }

    protected void updateLockscreenNotificationSetting() {
        boolean z = true;
        boolean show = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "lock_screen_show_notifications", 1, this.mCurrentUserId) != 0;
        int dpmFlags = this.mDevicePolicyManager.getKeyguardDisabledFeatures(null, this.mCurrentUserId);
        boolean allowedByDpm = (dpmFlags & 4) == 0;
        if (!show || !allowedByDpm) {
            z = false;
        }
        setShowLockscreenNotifications(z);
        setLockscreenAllowRemoteInput(false);
    }

    @Override // com.android.systemui.statusbar.NotificationLockscreenUserManager
    public boolean userAllowsPrivateNotificationsInPublic(int userHandle) {
        boolean allowedByUser;
        boolean allowed = true;
        if (userHandle == -1) {
            return true;
        }
        if (this.mUsersAllowingPrivateNotifications.indexOfKey(userHandle) < 0) {
            if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "lock_screen_allow_private_notifications", 0, userHandle) != 0) {
                allowedByUser = true;
            } else {
                allowedByUser = false;
            }
            boolean allowedByDpm = adminAllowsKeyguardFeature(userHandle, 8);
            if (!allowedByUser || !allowedByDpm) {
                allowed = false;
            }
            this.mUsersAllowingPrivateNotifications.append(userHandle, allowed);
            return allowed;
        }
        return this.mUsersAllowingPrivateNotifications.get(userHandle);
    }

    private boolean adminAllowsKeyguardFeature(int userHandle, int feature) {
        if (userHandle == -1) {
            return true;
        }
        int dpmFlags = this.mDevicePolicyManager.getKeyguardDisabledFeatures(null, userHandle);
        return (dpmFlags & feature) == 0;
    }

    @Override // com.android.systemui.statusbar.NotificationLockscreenUserManager
    public void setLockscreenPublicMode(boolean publicMode, int userId) {
        this.mLockscreenPublicMode.put(userId, publicMode);
    }

    @Override // com.android.systemui.statusbar.NotificationLockscreenUserManager
    public boolean isLockscreenPublicMode(int userId) {
        return userId == -1 ? this.mLockscreenPublicMode.get(this.mCurrentUserId, false) : this.mLockscreenPublicMode.get(userId, false);
    }

    @Override // com.android.systemui.statusbar.NotificationLockscreenUserManager
    public boolean needsSeparateWorkChallenge(int userId) {
        return this.mUsersWithSeperateWorkChallenge.get(userId, false);
    }

    private boolean userAllowsNotificationsInPublic(int userHandle) {
        boolean allowed = true;
        if (!isCurrentProfile(userHandle) || userHandle == this.mCurrentUserId) {
            if (this.mUsersAllowingNotifications.indexOfKey(userHandle) < 0) {
                boolean allowedByUser = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "lock_screen_show_notifications", 0, userHandle) != 0;
                boolean allowedByDpm = adminAllowsKeyguardFeature(userHandle, 4);
                boolean allowedBySystem = this.mKeyguardManager.getPrivateNotificationsAllowed();
                if (!allowedByUser || !allowedByDpm || !allowedBySystem) {
                    allowed = false;
                }
                this.mUsersAllowingNotifications.append(userHandle, allowed);
                return allowed;
            }
            return this.mUsersAllowingNotifications.get(userHandle);
        }
        return true;
    }

    @Override // com.android.systemui.statusbar.NotificationLockscreenUserManager
    public boolean needsRedaction(NotificationEntry ent) {
        int userId = ent.notification.getUserId();
        boolean currentUserWantsRedaction = !userAllowsPrivateNotificationsInPublic(this.mCurrentUserId);
        boolean notiUserWantsRedaction = !userAllowsPrivateNotificationsInPublic(userId);
        boolean redactedLockscreen = currentUserWantsRedaction || notiUserWantsRedaction;
        boolean notificationRequestsRedaction = ent.notification.getNotification().visibility == 0;
        boolean userForcesRedaction = packageHasVisibilityOverride(ent.notification.getKey());
        if (userForcesRedaction) {
            return true;
        }
        return notificationRequestsRedaction && redactedLockscreen;
    }

    private boolean packageHasVisibilityOverride(String key) {
        if (getEntryManager() != null) {
            return getEntryManager().getNotificationData().getVisibilityOverride(key) == 0;
        }
        Log.wtf(TAG, "mEntryManager was null!", new Throwable());
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCurrentProfilesCache() {
        synchronized (this.mCurrentProfiles) {
            this.mCurrentProfiles.clear();
            if (this.mUserManager != null) {
                for (UserInfo user : this.mUserManager.getProfiles(this.mCurrentUserId)) {
                    this.mCurrentProfiles.put(user.id, user);
                }
            }
        }
    }

    @Override // com.android.systemui.statusbar.NotificationLockscreenUserManager
    public boolean isAnyProfilePublicMode() {
        for (int i = this.mCurrentProfiles.size() - 1; i >= 0; i--) {
            if (isLockscreenPublicMode(this.mCurrentProfiles.valueAt(i).id)) {
                return true;
            }
        }
        return false;
    }

    @Override // com.android.systemui.statusbar.NotificationLockscreenUserManager
    public int getCurrentUserId() {
        return this.mCurrentUserId;
    }

    @Override // com.android.systemui.statusbar.NotificationLockscreenUserManager
    public SparseArray<UserInfo> getCurrentProfiles() {
        return this.mCurrentProfiles;
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onStateChanged(int newState) {
        this.mState = newState;
        updatePublicMode();
    }

    @Override // com.android.systemui.statusbar.NotificationLockscreenUserManager
    public void updatePublicMode() {
        boolean showingKeyguard = this.mState != 0 || this.mKeyguardMonitor.isShowing();
        boolean devicePublic = showingKeyguard && isSecure(getCurrentUserId());
        SparseArray<UserInfo> currentProfiles = getCurrentProfiles();
        this.mUsersWithSeperateWorkChallenge.clear();
        for (int i = currentProfiles.size() - 1; i >= 0; i--) {
            int userId = currentProfiles.valueAt(i).id;
            boolean isProfilePublic = devicePublic;
            boolean needsSeparateChallenge = this.mLockPatternUtils.isSeparateProfileChallengeEnabled(userId);
            if (!devicePublic && userId != getCurrentUserId() && needsSeparateChallenge && isSecure(userId)) {
                isProfilePublic = showingKeyguard || this.mKeyguardManager.isDeviceLocked(userId);
            }
            setLockscreenPublicMode(isProfilePublic, userId);
            this.mUsersWithSeperateWorkChallenge.put(userId, needsSeparateChallenge);
        }
        getEntryManager().updateNotifications();
    }

    @Override // com.android.systemui.statusbar.NotificationLockscreenUserManager
    public void addUserChangedListener(NotificationLockscreenUserManager.UserChangedListener listener) {
        this.mListeners.add(listener);
    }

    private boolean isSecure(int userId) {
        return this.mKeyguardMonitor.isSecure() || this.mLockPatternUtils.isSecure(userId);
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("NotificationLockscreenUserManager state:");
        pw.print("  mCurrentUserId=");
        pw.println(this.mCurrentUserId);
        pw.print("  mShowLockscreenNotifications=");
        pw.println(this.mShowLockscreenNotifications);
        pw.print("  mAllowLockscreenRemoteInput=");
        pw.println(this.mAllowLockscreenRemoteInput);
        pw.print("  mCurrentProfiles=");
        for (int i = this.mCurrentProfiles.size() - 1; i >= 0; i += -1) {
            int userId = this.mCurrentProfiles.valueAt(i).id;
            pw.print("" + userId + " ");
        }
        pw.println();
    }
}
