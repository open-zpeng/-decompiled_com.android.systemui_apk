package com.android.systemui.globalactions;

import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.IStopUserCallback;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.app.trust.TrustManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.service.dreams.IDreamManager;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.FeatureFlagUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.colorextraction.ColorExtractor;
import com.android.internal.colorextraction.drawable.ScrimDrawable;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.util.EmergencyAffordanceManager;
import com.android.internal.util.ScreenRecordHelper;
import com.android.internal.util.ScreenshotHelper;
import com.android.internal.view.RotationPolicy;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.MultiListLayout;
import com.android.systemui.R;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.globalactions.GlobalActionsDialog;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.GlobalActions;
import com.android.systemui.plugins.GlobalActionsPanelPlugin;
import com.android.systemui.statusbar.phone.StatusBarWindowController;
import com.android.systemui.statusbar.phone.UnlockMethodCache;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.util.EmergencyDialerConstants;
import com.android.systemui.util.leak.RotationUtils;
import com.android.systemui.volume.SystemUIInterpolators;
import com.xiaopeng.speech.speechwidget.ListWidget;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class GlobalActionsDialog implements DialogInterface.OnDismissListener, DialogInterface.OnShowListener, ConfigurationController.ConfigurationListener {
    private static final int DIALOG_DISMISS_DELAY = 300;
    private static final String GLOBAL_ACTION_KEY_AIRPLANE = "airplane";
    private static final String GLOBAL_ACTION_KEY_ASSIST = "assist";
    private static final String GLOBAL_ACTION_KEY_BUGREPORT = "bugreport";
    private static final String GLOBAL_ACTION_KEY_EMERGENCY = "emergency";
    private static final String GLOBAL_ACTION_KEY_LOCKDOWN = "lockdown";
    private static final String GLOBAL_ACTION_KEY_LOGOUT = "logout";
    private static final String GLOBAL_ACTION_KEY_POWER = "power";
    private static final String GLOBAL_ACTION_KEY_RESTART = "restart";
    private static final String GLOBAL_ACTION_KEY_SCREENSHOT = "screenshot";
    private static final String GLOBAL_ACTION_KEY_SETTINGS = "settings";
    private static final String GLOBAL_ACTION_KEY_SILENT = "silent";
    private static final String GLOBAL_ACTION_KEY_USERS = "users";
    private static final String GLOBAL_ACTION_KEY_VOICEASSIST = "voiceassist";
    private static final int MESSAGE_DISMISS = 0;
    private static final int MESSAGE_REFRESH = 1;
    private static final int MESSAGE_SHOW = 2;
    private static final boolean SHOW_SILENT_TOGGLE = true;
    public static final String SYSTEM_DIALOG_REASON_DREAM = "dream";
    public static final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
    public static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private static final String TAG = "GlobalActionsDialog";
    private final ActivityStarter mActivityStarter;
    private MyAdapter mAdapter;
    private ToggleAction mAirplaneModeOn;
    private final AudioManager mAudioManager;
    private final Context mContext;
    private final DevicePolicyManager mDevicePolicyManager;
    private ActionsDialog mDialog;
    private final EmergencyAffordanceManager mEmergencyAffordanceManager;
    private boolean mHasLockdownButton;
    private boolean mHasLogoutButton;
    private boolean mHasTelephony;
    private boolean mHasVibrator;
    private ArrayList<Action> mItems;
    private final KeyguardManager mKeyguardManager;
    private final LockPatternUtils mLockPatternUtils;
    private GlobalActionsPanelPlugin mPanelPlugin;
    private final ScreenRecordHelper mScreenRecordHelper;
    private final ScreenshotHelper mScreenshotHelper;
    private final boolean mShowSilentToggle;
    private Action mSilentModeAction;
    private final GlobalActions.GlobalActionsManager mWindowManagerFuncs;
    private boolean mKeyguardShowing = false;
    private boolean mDeviceProvisioned = false;
    private ToggleAction.State mAirplaneState = ToggleAction.State.Off;
    private boolean mIsWaitingForEcmExit = false;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.systemui.globalactions.GlobalActionsDialog.8
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(action) || "android.intent.action.SCREEN_OFF".equals(action)) {
                String reason = intent.getStringExtra(GlobalActionsDialog.SYSTEM_DIALOG_REASON_KEY);
                if (!GlobalActionsDialog.SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS.equals(reason)) {
                    GlobalActionsDialog.this.mHandler.sendMessage(GlobalActionsDialog.this.mHandler.obtainMessage(0, reason));
                }
            } else if ("android.intent.action.EMERGENCY_CALLBACK_MODE_CHANGED".equals(action) && !intent.getBooleanExtra("PHONE_IN_ECM_STATE", false) && GlobalActionsDialog.this.mIsWaitingForEcmExit) {
                GlobalActionsDialog.this.mIsWaitingForEcmExit = false;
                GlobalActionsDialog.this.changeAirplaneModeSystemSetting(true);
            }
        }
    };
    PhoneStateListener mPhoneStateListener = new PhoneStateListener() { // from class: com.android.systemui.globalactions.GlobalActionsDialog.9
        @Override // android.telephony.PhoneStateListener
        public void onServiceStateChanged(ServiceState serviceState) {
            if (GlobalActionsDialog.this.mHasTelephony) {
                boolean inAirplaneMode = serviceState.getState() == 3;
                GlobalActionsDialog.this.mAirplaneState = inAirplaneMode ? ToggleAction.State.On : ToggleAction.State.Off;
                GlobalActionsDialog.this.mAirplaneModeOn.updateState(GlobalActionsDialog.this.mAirplaneState);
                GlobalActionsDialog.this.mAdapter.notifyDataSetChanged();
            }
        }
    };
    private BroadcastReceiver mRingerModeReceiver = new BroadcastReceiver() { // from class: com.android.systemui.globalactions.GlobalActionsDialog.10
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.media.RINGER_MODE_CHANGED")) {
                GlobalActionsDialog.this.mHandler.sendEmptyMessage(1);
            }
        }
    };
    private ContentObserver mAirplaneModeObserver = new ContentObserver(new Handler()) { // from class: com.android.systemui.globalactions.GlobalActionsDialog.11
        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            GlobalActionsDialog.this.onAirplaneModeChanged();
        }
    };
    private Handler mHandler = new Handler() { // from class: com.android.systemui.globalactions.GlobalActionsDialog.12
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 0) {
                if (GlobalActionsDialog.this.mDialog != null) {
                    if (GlobalActionsDialog.SYSTEM_DIALOG_REASON_DREAM.equals(msg.obj)) {
                        GlobalActionsDialog.this.mDialog.dismissImmediately();
                    } else {
                        GlobalActionsDialog.this.mDialog.dismiss();
                    }
                    GlobalActionsDialog.this.mDialog = null;
                }
            } else if (i == 1) {
                GlobalActionsDialog.this.refreshSilentMode();
                GlobalActionsDialog.this.mAdapter.notifyDataSetChanged();
            } else if (i == 2) {
                GlobalActionsDialog.this.handleShow();
            }
        }
    };
    private final IDreamManager mDreamManager = IDreamManager.Stub.asInterface(ServiceManager.getService("dreams"));

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public interface LongPressAction extends Action {
        boolean onLongPress();
    }

    static /* synthetic */ boolean access$1100() {
        return shouldUseSeparatedView();
    }

    public GlobalActionsDialog(Context context, GlobalActions.GlobalActionsManager windowManagerFuncs) {
        boolean z = false;
        this.mContext = new ContextThemeWrapper(context, R.style.qs_theme);
        this.mWindowManagerFuncs = windowManagerFuncs;
        this.mAudioManager = (AudioManager) this.mContext.getSystemService(ListWidget.EXTRA_TYPE_AUDIO);
        this.mDevicePolicyManager = (DevicePolicyManager) this.mContext.getSystemService("device_policy");
        this.mLockPatternUtils = new LockPatternUtils(this.mContext);
        this.mKeyguardManager = (KeyguardManager) this.mContext.getSystemService("keyguard");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        filter.addAction("android.intent.action.SCREEN_OFF");
        filter.addAction("android.intent.action.EMERGENCY_CALLBACK_MODE_CHANGED");
        context.registerReceiver(this.mBroadcastReceiver, filter);
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService("connectivity");
        this.mHasTelephony = cm.isNetworkSupported(0);
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        telephonyManager.listen(this.mPhoneStateListener, 1);
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("airplane_mode_on"), true, this.mAirplaneModeObserver);
        Vibrator vibrator = (Vibrator) this.mContext.getSystemService("vibrator");
        if (vibrator != null && vibrator.hasVibrator()) {
            z = true;
        }
        this.mHasVibrator = z;
        this.mShowSilentToggle = !this.mContext.getResources().getBoolean(17891563);
        this.mEmergencyAffordanceManager = new EmergencyAffordanceManager(context);
        this.mScreenshotHelper = new ScreenshotHelper(context);
        this.mScreenRecordHelper = new ScreenRecordHelper(context);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this);
        this.mActivityStarter = (ActivityStarter) Dependency.get(ActivityStarter.class);
        final KeyguardUpdateMonitor keyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(context);
        final UnlockMethodCache unlockMethodCache = UnlockMethodCache.getInstance(context);
        unlockMethodCache.addListener(new UnlockMethodCache.OnUnlockMethodChangedListener() { // from class: com.android.systemui.globalactions.-$$Lambda$GlobalActionsDialog$NxSDqubsrXIyjP1lCUP3BAtymNY
            @Override // com.android.systemui.statusbar.phone.UnlockMethodCache.OnUnlockMethodChangedListener
            public final void onUnlockMethodStateChanged() {
                GlobalActionsDialog.this.lambda$new$0$GlobalActionsDialog(unlockMethodCache, keyguardUpdateMonitor);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$GlobalActionsDialog(UnlockMethodCache unlockMethodCache, KeyguardUpdateMonitor keyguardUpdateMonitor) {
        ActionsDialog actionsDialog = this.mDialog;
        if (actionsDialog != null && actionsDialog.mPanelController != null) {
            boolean locked = !unlockMethodCache.canSkipBouncer() && keyguardUpdateMonitor.isKeyguardVisible();
            this.mDialog.mPanelController.onDeviceLockStateChanged(locked);
        }
    }

    public void showDialog(boolean keyguardShowing, boolean isDeviceProvisioned, GlobalActionsPanelPlugin panelPlugin) {
        this.mKeyguardShowing = keyguardShowing;
        this.mDeviceProvisioned = isDeviceProvisioned;
        this.mPanelPlugin = panelPlugin;
        ActionsDialog actionsDialog = this.mDialog;
        if (actionsDialog != null) {
            actionsDialog.dismiss();
            this.mDialog = null;
            this.mHandler.sendEmptyMessage(2);
            return;
        }
        handleShow();
    }

    public void dismissDialog() {
        this.mHandler.removeMessages(0);
        this.mHandler.sendEmptyMessage(0);
    }

    private void awakenIfNecessary() {
        IDreamManager iDreamManager = this.mDreamManager;
        if (iDreamManager != null) {
            try {
                if (iDreamManager.isDreaming()) {
                    this.mDreamManager.awaken();
                }
            } catch (RemoteException e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleShow() {
        awakenIfNecessary();
        this.mDialog = createDialog();
        prepareDialog();
        if (this.mAdapter.getCount() == 1 && (this.mAdapter.getItem(0) instanceof SinglePressAction) && !(this.mAdapter.getItem(0) instanceof LongPressAction)) {
            ((SinglePressAction) this.mAdapter.getItem(0)).onPress();
            return;
        }
        WindowManager.LayoutParams attrs = this.mDialog.getWindow().getAttributes();
        attrs.setTitle("ActionsDialog");
        attrs.layoutInDisplayCutoutMode = 1;
        this.mDialog.getWindow().setAttributes(attrs);
        this.mDialog.show();
        this.mWindowManagerFuncs.onGlobalActionsShown();
    }

    private ActionsDialog createDialog() {
        if (!this.mHasVibrator) {
            this.mSilentModeAction = new SilentModeToggleAction();
        } else {
            this.mSilentModeAction = new SilentModeTriStateAction(this.mAudioManager, this.mHandler);
        }
        this.mAirplaneModeOn = new ToggleAction(17302453, 17302455, 17040077, 17040076, 17040075) { // from class: com.android.systemui.globalactions.GlobalActionsDialog.1
            @Override // com.android.systemui.globalactions.GlobalActionsDialog.ToggleAction
            void onToggle(boolean on) {
                if (!GlobalActionsDialog.this.mHasTelephony || !Boolean.parseBoolean(SystemProperties.get("ril.cdma.inecmmode"))) {
                    GlobalActionsDialog.this.changeAirplaneModeSystemSetting(on);
                    return;
                }
                GlobalActionsDialog.this.mIsWaitingForEcmExit = true;
                Intent ecmDialogIntent = new Intent("com.android.internal.intent.action.ACTION_SHOW_NOTICE_ECM_BLOCK_OTHERS", (Uri) null);
                ecmDialogIntent.addFlags(268435456);
                GlobalActionsDialog.this.mContext.startActivity(ecmDialogIntent);
            }

            @Override // com.android.systemui.globalactions.GlobalActionsDialog.ToggleAction
            protected void changeStateFromPress(boolean buttonOn) {
                if (GlobalActionsDialog.this.mHasTelephony && !Boolean.parseBoolean(SystemProperties.get("ril.cdma.inecmmode"))) {
                    this.mState = buttonOn ? ToggleAction.State.TurningOn : ToggleAction.State.TurningOff;
                    GlobalActionsDialog.this.mAirplaneState = this.mState;
                }
            }

            @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
            public boolean showDuringKeyguard() {
                return true;
            }

            @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
            public boolean showBeforeProvisioning() {
                return false;
            }
        };
        onAirplaneModeChanged();
        this.mItems = new ArrayList<>();
        String[] defaultActions = this.mContext.getResources().getStringArray(17236037);
        ArraySet<String> addedKeys = new ArraySet<>();
        this.mHasLogoutButton = false;
        this.mHasLockdownButton = false;
        int i = 0;
        while (true) {
            if (i >= defaultActions.length) {
                break;
            }
            String actionKey = defaultActions[i];
            if (!addedKeys.contains(actionKey)) {
                if (GLOBAL_ACTION_KEY_POWER.equals(actionKey)) {
                    this.mItems.add(new PowerAction());
                } else if (GLOBAL_ACTION_KEY_AIRPLANE.equals(actionKey)) {
                    this.mItems.add(this.mAirplaneModeOn);
                } else if (GLOBAL_ACTION_KEY_BUGREPORT.equals(actionKey)) {
                    if (Settings.Global.getInt(this.mContext.getContentResolver(), "bugreport_in_power_menu", 0) != 0 && isCurrentUserOwner()) {
                        this.mItems.add(new BugReportAction());
                    }
                } else if (GLOBAL_ACTION_KEY_SILENT.equals(actionKey)) {
                    if (this.mShowSilentToggle) {
                        this.mItems.add(this.mSilentModeAction);
                    }
                } else if (GLOBAL_ACTION_KEY_USERS.equals(actionKey)) {
                    if (SystemProperties.getBoolean("fw.power_user_switcher", false)) {
                        addUsersToMenu(this.mItems);
                    }
                } else if (GLOBAL_ACTION_KEY_SETTINGS.equals(actionKey)) {
                    this.mItems.add(getSettingsAction());
                } else if (GLOBAL_ACTION_KEY_LOCKDOWN.equals(actionKey)) {
                    if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "lockdown_in_power_menu", 0, getCurrentUser().id) != 0 && shouldDisplayLockdown()) {
                        this.mItems.add(getLockdownAction());
                        this.mHasLockdownButton = true;
                    }
                } else if (GLOBAL_ACTION_KEY_VOICEASSIST.equals(actionKey)) {
                    this.mItems.add(getVoiceAssistAction());
                } else if (GLOBAL_ACTION_KEY_ASSIST.equals(actionKey)) {
                    this.mItems.add(getAssistAction());
                } else if (GLOBAL_ACTION_KEY_RESTART.equals(actionKey)) {
                    this.mItems.add(new RestartAction());
                } else if ("screenshot".equals(actionKey)) {
                    this.mItems.add(new ScreenshotAction());
                } else if (GLOBAL_ACTION_KEY_LOGOUT.equals(actionKey)) {
                    if (this.mDevicePolicyManager.isLogoutEnabled() && getCurrentUser().id != 0) {
                        this.mItems.add(new LogoutAction());
                        this.mHasLogoutButton = true;
                    }
                } else if (GLOBAL_ACTION_KEY_EMERGENCY.equals(actionKey)) {
                    if (!this.mEmergencyAffordanceManager.needsEmergencyAffordance()) {
                        this.mItems.add(new EmergencyDialerAction());
                    }
                } else {
                    Log.e(TAG, "Invalid global action key " + actionKey);
                }
                addedKeys.add(actionKey);
            }
            i++;
        }
        if (this.mEmergencyAffordanceManager.needsEmergencyAffordance()) {
            this.mItems.add(new EmergencyAffordanceAction());
        }
        this.mAdapter = new MyAdapter();
        GlobalActionsPanelPlugin globalActionsPanelPlugin = this.mPanelPlugin;
        GlobalActionsPanelPlugin.PanelViewController panelViewController = globalActionsPanelPlugin != null ? globalActionsPanelPlugin.onPanelShown(new GlobalActionsPanelPlugin.Callbacks() { // from class: com.android.systemui.globalactions.GlobalActionsDialog.2
            @Override // com.android.systemui.plugins.GlobalActionsPanelPlugin.Callbacks
            public void dismissGlobalActionsMenu() {
                GlobalActionsDialog.this.dismissDialog();
            }

            @Override // com.android.systemui.plugins.GlobalActionsPanelPlugin.Callbacks
            public void startPendingIntentDismissingKeyguard(PendingIntent intent) {
                GlobalActionsDialog.this.mActivityStarter.startPendingIntentDismissingKeyguard(intent);
            }
        }, this.mKeyguardManager.isDeviceLocked()) : null;
        ActionsDialog dialog = new ActionsDialog(this.mContext, this.mAdapter, panelViewController);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setKeyguardShowing(this.mKeyguardShowing);
        dialog.setOnDismissListener(this);
        dialog.setOnShowListener(this);
        return dialog;
    }

    private boolean shouldDisplayLockdown() {
        int userId = getCurrentUser().id;
        if (this.mKeyguardManager.isDeviceSecure(userId)) {
            int state = this.mLockPatternUtils.getStrongAuthForUser(userId);
            return state == 0 || state == 4;
        }
        return false;
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onUiModeChanged() {
        this.mContext.getTheme().applyStyle(this.mContext.getThemeResId(), true);
        ActionsDialog actionsDialog = this.mDialog;
        if (actionsDialog != null && actionsDialog.isShowing()) {
            this.mDialog.refreshDialog();
        }
    }

    public void destroy() {
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).removeCallback(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public final class PowerAction extends SinglePressAction implements LongPressAction {
        private PowerAction() {
            super(17301552, 17040066);
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.LongPressAction
        public boolean onLongPress() {
            UserManager um = (UserManager) GlobalActionsDialog.this.mContext.getSystemService("user");
            if (!um.hasUserRestriction("no_safe_boot")) {
                GlobalActionsDialog.this.mWindowManagerFuncs.reboot(true);
                return true;
            }
            return false;
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public boolean showDuringKeyguard() {
            return true;
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public boolean showBeforeProvisioning() {
            return true;
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.SinglePressAction, com.android.systemui.globalactions.GlobalActionsDialog.Action
        public void onPress() {
            GlobalActionsDialog.this.mWindowManagerFuncs.shutdown();
        }
    }

    /* loaded from: classes21.dex */
    private abstract class EmergencyAction extends SinglePressAction {
        EmergencyAction(int iconResId, int messageResId) {
            super(iconResId, messageResId);
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public boolean shouldBeSeparated() {
            return GlobalActionsDialog.access$1100();
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.SinglePressAction, com.android.systemui.globalactions.GlobalActionsDialog.Action
        public View create(Context context, View convertView, ViewGroup parent, LayoutInflater inflater) {
            int textColor;
            View v = super.create(context, convertView, parent, inflater);
            if (shouldBeSeparated()) {
                textColor = v.getResources().getColor(R.color.global_actions_alert_text);
            } else {
                textColor = v.getResources().getColor(R.color.global_actions_text);
            }
            TextView messageView = (TextView) v.findViewById(16908299);
            messageView.setTextColor(textColor);
            messageView.setSelected(true);
            ImageView icon = (ImageView) v.findViewById(16908294);
            icon.getDrawable().setTint(textColor);
            return v;
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public boolean showDuringKeyguard() {
            return true;
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public boolean showBeforeProvisioning() {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class EmergencyAffordanceAction extends EmergencyAction {
        EmergencyAffordanceAction() {
            super(17302204, 17040062);
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.SinglePressAction, com.android.systemui.globalactions.GlobalActionsDialog.Action
        public void onPress() {
            GlobalActionsDialog.this.mEmergencyAffordanceManager.performEmergencyCall();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class EmergencyDialerAction extends EmergencyAction {
        private EmergencyDialerAction() {
            super(R.drawable.ic_emergency_star, 17040062);
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.SinglePressAction, com.android.systemui.globalactions.GlobalActionsDialog.Action
        public void onPress() {
            MetricsLogger.action(GlobalActionsDialog.this.mContext, 1569);
            Intent intent = new Intent(EmergencyDialerConstants.ACTION_DIAL);
            intent.addFlags(343932928);
            intent.putExtra(EmergencyDialerConstants.EXTRA_ENTRY_TYPE, 2);
            GlobalActionsDialog.this.mContext.startActivityAsUser(intent, UserHandle.CURRENT);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public final class RestartAction extends SinglePressAction implements LongPressAction {
        private RestartAction() {
            super(17302793, 17040067);
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.LongPressAction
        public boolean onLongPress() {
            UserManager um = (UserManager) GlobalActionsDialog.this.mContext.getSystemService("user");
            if (!um.hasUserRestriction("no_safe_boot")) {
                GlobalActionsDialog.this.mWindowManagerFuncs.reboot(true);
                return true;
            }
            return false;
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public boolean showDuringKeyguard() {
            return true;
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public boolean showBeforeProvisioning() {
            return true;
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.SinglePressAction, com.android.systemui.globalactions.GlobalActionsDialog.Action
        public void onPress() {
            GlobalActionsDialog.this.mWindowManagerFuncs.reboot(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class ScreenshotAction extends SinglePressAction implements LongPressAction {
        public ScreenshotAction() {
            super(17302795, 17040068);
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.SinglePressAction, com.android.systemui.globalactions.GlobalActionsDialog.Action
        public void onPress() {
            GlobalActionsDialog.this.mHandler.postDelayed(new Runnable() { // from class: com.android.systemui.globalactions.GlobalActionsDialog.ScreenshotAction.1
                @Override // java.lang.Runnable
                public void run() {
                    GlobalActionsDialog.this.mScreenshotHelper.takeScreenshot(1, true, true, GlobalActionsDialog.this.mHandler, (Consumer) null);
                    MetricsLogger.action(GlobalActionsDialog.this.mContext, 1282);
                }
            }, 500L);
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public boolean showDuringKeyguard() {
            return true;
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public boolean showBeforeProvisioning() {
            return false;
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.LongPressAction
        public boolean onLongPress() {
            if (FeatureFlagUtils.isEnabled(GlobalActionsDialog.this.mContext, "settings_screenrecord_long_press")) {
                GlobalActionsDialog.this.mScreenRecordHelper.launchRecordPrompt();
                return true;
            }
            onPress();
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class BugReportAction extends SinglePressAction implements LongPressAction {
        public BugReportAction() {
            super(17302457, 17039624);
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.SinglePressAction, com.android.systemui.globalactions.GlobalActionsDialog.Action
        public void onPress() {
            if (!ActivityManager.isUserAMonkey()) {
                GlobalActionsDialog.this.mHandler.postDelayed(new Runnable() { // from class: com.android.systemui.globalactions.GlobalActionsDialog.BugReportAction.1
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            MetricsLogger.action(GlobalActionsDialog.this.mContext, 292);
                            ActivityManager.getService().requestBugReport(1);
                        } catch (RemoteException e) {
                        }
                    }
                }, 500L);
            }
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.LongPressAction
        public boolean onLongPress() {
            if (ActivityManager.isUserAMonkey()) {
                return false;
            }
            try {
                MetricsLogger.action(GlobalActionsDialog.this.mContext, 293);
                ActivityManager.getService().requestBugReport(0);
            } catch (RemoteException e) {
            }
            return false;
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public boolean showDuringKeyguard() {
            return true;
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public boolean showBeforeProvisioning() {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public final class LogoutAction extends SinglePressAction {
        private LogoutAction() {
            super(17302507, 17040065);
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public boolean showDuringKeyguard() {
            return true;
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public boolean showBeforeProvisioning() {
            return false;
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.SinglePressAction, com.android.systemui.globalactions.GlobalActionsDialog.Action
        public void onPress() {
            GlobalActionsDialog.this.mHandler.postDelayed(new Runnable() { // from class: com.android.systemui.globalactions.-$$Lambda$GlobalActionsDialog$LogoutAction$3H17sX2I_BqMu2dZ5Dekk1AEv-U
                @Override // java.lang.Runnable
                public final void run() {
                    GlobalActionsDialog.LogoutAction.this.lambda$onPress$0$GlobalActionsDialog$LogoutAction();
                }
            }, 500L);
        }

        public /* synthetic */ void lambda$onPress$0$GlobalActionsDialog$LogoutAction() {
            try {
                int currentUserId = GlobalActionsDialog.this.getCurrentUser().id;
                ActivityManager.getService().switchUser(0);
                ActivityManager.getService().stopUser(currentUserId, true, (IStopUserCallback) null);
            } catch (RemoteException re) {
                Log.e(GlobalActionsDialog.TAG, "Couldn't logout user " + re);
            }
        }
    }

    private Action getSettingsAction() {
        return new SinglePressAction(17302801, 17040069) { // from class: com.android.systemui.globalactions.GlobalActionsDialog.3
            @Override // com.android.systemui.globalactions.GlobalActionsDialog.SinglePressAction, com.android.systemui.globalactions.GlobalActionsDialog.Action
            public void onPress() {
                Intent intent = new Intent("android.settings.SETTINGS");
                intent.addFlags(335544320);
                GlobalActionsDialog.this.mContext.startActivity(intent);
            }

            @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
            public boolean showDuringKeyguard() {
                return true;
            }

            @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
            public boolean showBeforeProvisioning() {
                return true;
            }
        };
    }

    private Action getAssistAction() {
        return new SinglePressAction(17302286, 17040060) { // from class: com.android.systemui.globalactions.GlobalActionsDialog.4
            @Override // com.android.systemui.globalactions.GlobalActionsDialog.SinglePressAction, com.android.systemui.globalactions.GlobalActionsDialog.Action
            public void onPress() {
                Intent intent = new Intent("android.intent.action.ASSIST");
                intent.addFlags(335544320);
                GlobalActionsDialog.this.mContext.startActivity(intent);
            }

            @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
            public boolean showDuringKeyguard() {
                return true;
            }

            @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
            public boolean showBeforeProvisioning() {
                return true;
            }
        };
    }

    private Action getVoiceAssistAction() {
        return new SinglePressAction(17302841, 17040073) { // from class: com.android.systemui.globalactions.GlobalActionsDialog.5
            @Override // com.android.systemui.globalactions.GlobalActionsDialog.SinglePressAction, com.android.systemui.globalactions.GlobalActionsDialog.Action
            public void onPress() {
                Intent intent = new Intent("android.intent.action.VOICE_ASSIST");
                intent.addFlags(335544320);
                GlobalActionsDialog.this.mContext.startActivity(intent);
            }

            @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
            public boolean showDuringKeyguard() {
                return true;
            }

            @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
            public boolean showBeforeProvisioning() {
                return true;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.globalactions.GlobalActionsDialog$6  reason: invalid class name */
    /* loaded from: classes21.dex */
    public class AnonymousClass6 extends SinglePressAction {
        AnonymousClass6(int iconResId, int messageResId) {
            super(iconResId, messageResId);
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.SinglePressAction, com.android.systemui.globalactions.GlobalActionsDialog.Action
        public void onPress() {
            new LockPatternUtils(GlobalActionsDialog.this.mContext).requireStrongAuth(32, -1);
            try {
                WindowManagerGlobal.getWindowManagerService().lockNow((Bundle) null);
                Handler bgHandler = new Handler((Looper) Dependency.get(Dependency.BG_LOOPER));
                bgHandler.post(new Runnable() { // from class: com.android.systemui.globalactions.-$$Lambda$GlobalActionsDialog$6$WgIPOZvSRFzb_yD8-G_WZbXNLMU
                    @Override // java.lang.Runnable
                    public final void run() {
                        GlobalActionsDialog.AnonymousClass6.this.lambda$onPress$0$GlobalActionsDialog$6();
                    }
                });
            } catch (RemoteException e) {
                Log.e(GlobalActionsDialog.TAG, "Error while trying to lock device.", e);
            }
        }

        public /* synthetic */ void lambda$onPress$0$GlobalActionsDialog$6() {
            GlobalActionsDialog.this.lockProfiles();
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public boolean showDuringKeyguard() {
            return true;
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public boolean showBeforeProvisioning() {
            return false;
        }
    }

    private Action getLockdownAction() {
        return new AnonymousClass6(17302460, 17040064);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void lockProfiles() {
        UserManager um = (UserManager) this.mContext.getSystemService("user");
        TrustManager tm = (TrustManager) this.mContext.getSystemService("trust");
        int currentUserId = getCurrentUser().id;
        int[] profileIds = um.getEnabledProfileIds(currentUserId);
        for (int id : profileIds) {
            if (id != currentUserId) {
                tm.setDeviceLockedForUser(id, true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public UserInfo getCurrentUser() {
        try {
            return ActivityManager.getService().getCurrentUser();
        } catch (RemoteException e) {
            return null;
        }
    }

    private boolean isCurrentUserOwner() {
        UserInfo currentUser = getCurrentUser();
        return currentUser == null || currentUser.isPrimary();
    }

    private void addUsersToMenu(ArrayList<Action> items) {
        UserManager um = (UserManager) this.mContext.getSystemService("user");
        if (um.isUserSwitcherEnabled()) {
            List<UserInfo> users = um.getUsers();
            UserInfo currentUser = getCurrentUser();
            for (final UserInfo user : users) {
                if (user.supportsSwitchToByUser()) {
                    boolean z = true;
                    if (currentUser != null ? currentUser.id != user.id : user.id != 0) {
                        z = false;
                    }
                    boolean isCurrentUser = z;
                    Drawable icon = user.iconPath != null ? Drawable.createFromPath(user.iconPath) : null;
                    StringBuilder sb = new StringBuilder();
                    sb.append(user.name != null ? user.name : "Primary");
                    sb.append(isCurrentUser ? " ✔" : "");
                    SinglePressAction switchToUser = new SinglePressAction(17302676, icon, sb.toString()) { // from class: com.android.systemui.globalactions.GlobalActionsDialog.7
                        @Override // com.android.systemui.globalactions.GlobalActionsDialog.SinglePressAction, com.android.systemui.globalactions.GlobalActionsDialog.Action
                        public void onPress() {
                            try {
                                ActivityManager.getService().switchUser(user.id);
                            } catch (RemoteException re) {
                                Log.e(GlobalActionsDialog.TAG, "Couldn't switch user " + re);
                            }
                        }

                        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
                        public boolean showDuringKeyguard() {
                            return true;
                        }

                        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
                        public boolean showBeforeProvisioning() {
                            return false;
                        }
                    };
                    items.add(switchToUser);
                }
            }
        }
    }

    private void prepareDialog() {
        refreshSilentMode();
        this.mAirplaneModeOn.updateState(this.mAirplaneState);
        this.mAdapter.notifyDataSetChanged();
        if (this.mShowSilentToggle) {
            IntentFilter filter = new IntentFilter("android.media.RINGER_MODE_CHANGED");
            this.mContext.registerReceiver(this.mRingerModeReceiver, filter);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshSilentMode() {
        if (!this.mHasVibrator) {
            boolean silentModeOn = this.mAudioManager.getRingerMode() != 2;
            ((ToggleAction) this.mSilentModeAction).updateState(silentModeOn ? ToggleAction.State.On : ToggleAction.State.Off);
        }
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        if (this.mDialog == dialog) {
            this.mDialog = null;
        }
        this.mWindowManagerFuncs.onGlobalActionsHidden();
        if (this.mShowSilentToggle) {
            try {
                this.mContext.unregisterReceiver(this.mRingerModeReceiver);
            } catch (IllegalArgumentException ie) {
                Log.w(TAG, ie);
            }
        }
    }

    @Override // android.content.DialogInterface.OnShowListener
    public void onShow(DialogInterface dialog) {
        MetricsLogger.visible(this.mContext, 1568);
    }

    /* loaded from: classes21.dex */
    public class MyAdapter extends MultiListLayout.MultiListAdapter {
        public MyAdapter() {
        }

        private int countItems(boolean separated) {
            int count = 0;
            for (int i = 0; i < GlobalActionsDialog.this.mItems.size(); i++) {
                Action action = (Action) GlobalActionsDialog.this.mItems.get(i);
                if (shouldBeShown(action) && action.shouldBeSeparated() == separated) {
                    count++;
                }
            }
            return count;
        }

        private boolean shouldBeShown(Action action) {
            if (!GlobalActionsDialog.this.mKeyguardShowing || action.showDuringKeyguard()) {
                return GlobalActionsDialog.this.mDeviceProvisioned || action.showBeforeProvisioning();
            }
            return false;
        }

        @Override // com.android.systemui.MultiListLayout.MultiListAdapter
        public int countSeparatedItems() {
            return countItems(true);
        }

        @Override // com.android.systemui.MultiListLayout.MultiListAdapter
        public int countListItems() {
            return countItems(false);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return countSeparatedItems() + countListItems();
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean isEnabled(int position) {
            return getItem(position).isEnabled();
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override // android.widget.Adapter
        public Action getItem(int position) {
            int filteredPos = 0;
            for (int i = 0; i < GlobalActionsDialog.this.mItems.size(); i++) {
                Action action = (Action) GlobalActionsDialog.this.mItems.get(i);
                if (shouldBeShown(action)) {
                    if (filteredPos == position) {
                        return action;
                    }
                    filteredPos++;
                }
            }
            throw new IllegalArgumentException("position " + position + " out of range of showable actions, filtered count=" + getCount() + ", keyguardshowing=" + GlobalActionsDialog.this.mKeyguardShowing + ", provisioned=" + GlobalActionsDialog.this.mDeviceProvisioned);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(final int position, View convertView, ViewGroup parent) {
            Action action = getItem(position);
            View view = action.create(GlobalActionsDialog.this.mContext, convertView, parent, LayoutInflater.from(GlobalActionsDialog.this.mContext));
            view.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.globalactions.-$$Lambda$GlobalActionsDialog$MyAdapter$mHwNDdvU6gX4bdQUg9ucB10QA0w
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    GlobalActionsDialog.MyAdapter.this.lambda$getView$0$GlobalActionsDialog$MyAdapter(position, view2);
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.android.systemui.globalactions.-$$Lambda$GlobalActionsDialog$MyAdapter$VSUDyewgk86XHamZik1hS11jzxk
                @Override // android.view.View.OnLongClickListener
                public final boolean onLongClick(View view2) {
                    return GlobalActionsDialog.MyAdapter.this.lambda$getView$1$GlobalActionsDialog$MyAdapter(position, view2);
                }
            });
            return view;
        }

        public /* synthetic */ void lambda$getView$0$GlobalActionsDialog$MyAdapter(int position, View v) {
            onClickItem(position);
        }

        public /* synthetic */ boolean lambda$getView$1$GlobalActionsDialog$MyAdapter(int position, View v) {
            return onLongClickItem(position);
        }

        @Override // com.android.systemui.MultiListLayout.MultiListAdapter
        public boolean onLongClickItem(int position) {
            Action action = GlobalActionsDialog.this.mAdapter.getItem(position);
            if (action instanceof LongPressAction) {
                GlobalActionsDialog.this.mDialog.dismiss();
                return ((LongPressAction) action).onLongPress();
            }
            return false;
        }

        @Override // com.android.systemui.MultiListLayout.MultiListAdapter
        public void onClickItem(int position) {
            Action item = GlobalActionsDialog.this.mAdapter.getItem(position);
            if (!(item instanceof SilentModeTriStateAction)) {
                GlobalActionsDialog.this.mDialog.dismiss();
            }
            item.onPress();
        }

        @Override // com.android.systemui.MultiListLayout.MultiListAdapter
        public boolean shouldBeSeparated(int position) {
            return getItem(position).shouldBeSeparated();
        }
    }

    /* loaded from: classes21.dex */
    public interface Action {
        View create(Context context, View view, ViewGroup viewGroup, LayoutInflater layoutInflater);

        CharSequence getLabelForAccessibility(Context context);

        boolean isEnabled();

        void onPress();

        boolean showBeforeProvisioning();

        boolean showDuringKeyguard();

        default boolean shouldBeSeparated() {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static abstract class SinglePressAction implements Action {
        private final Drawable mIcon;
        private final int mIconResId;
        private final CharSequence mMessage;
        private final int mMessageResId;

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public abstract void onPress();

        protected SinglePressAction(int iconResId, int messageResId) {
            this.mIconResId = iconResId;
            this.mMessageResId = messageResId;
            this.mMessage = null;
            this.mIcon = null;
        }

        protected SinglePressAction(int iconResId, Drawable icon, CharSequence message) {
            this.mIconResId = iconResId;
            this.mMessageResId = 0;
            this.mMessage = message;
            this.mIcon = icon;
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public boolean isEnabled() {
            return true;
        }

        public String getStatus() {
            return null;
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public CharSequence getLabelForAccessibility(Context context) {
            CharSequence charSequence = this.mMessage;
            if (charSequence != null) {
                return charSequence;
            }
            return context.getString(this.mMessageResId);
        }

        protected int getActionLayoutId(Context context) {
            return R.layout.global_actions_grid_item;
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public View create(Context context, View convertView, ViewGroup parent, LayoutInflater inflater) {
            View v = inflater.inflate(getActionLayoutId(context), parent, false);
            ImageView icon = (ImageView) v.findViewById(16908294);
            TextView messageView = (TextView) v.findViewById(16908299);
            messageView.setSelected(true);
            TextView statusView = (TextView) v.findViewById(16909506);
            String status = getStatus();
            if (!TextUtils.isEmpty(status)) {
                statusView.setText(status);
            } else {
                statusView.setVisibility(8);
            }
            Drawable drawable = this.mIcon;
            if (drawable != null) {
                icon.setImageDrawable(drawable);
                icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                int i = this.mIconResId;
                if (i != 0) {
                    icon.setImageDrawable(context.getDrawable(i));
                }
            }
            CharSequence charSequence = this.mMessage;
            if (charSequence != null) {
                messageView.setText(charSequence);
            } else {
                messageView.setText(this.mMessageResId);
            }
            return v;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static abstract class ToggleAction implements Action {
        protected int mDisabledIconResid;
        protected int mDisabledStatusMessageResId;
        protected int mEnabledIconResId;
        protected int mEnabledStatusMessageResId;
        protected int mMessageResId;
        protected State mState = State.Off;

        abstract void onToggle(boolean z);

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes21.dex */
        public enum State {
            Off(false),
            TurningOn(true),
            TurningOff(true),
            On(false);
            
            private final boolean inTransition;

            State(boolean intermediate) {
                this.inTransition = intermediate;
            }

            public boolean inTransition() {
                return this.inTransition;
            }
        }

        public ToggleAction(int enabledIconResId, int disabledIconResid, int message, int enabledStatusMessageResId, int disabledStatusMessageResId) {
            this.mEnabledIconResId = enabledIconResId;
            this.mDisabledIconResid = disabledIconResid;
            this.mMessageResId = message;
            this.mEnabledStatusMessageResId = enabledStatusMessageResId;
            this.mDisabledStatusMessageResId = disabledStatusMessageResId;
        }

        void willCreate() {
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public CharSequence getLabelForAccessibility(Context context) {
            return context.getString(this.mMessageResId);
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public View create(Context context, View convertView, ViewGroup parent, LayoutInflater inflater) {
            willCreate();
            View v = inflater.inflate(17367161, parent, false);
            ImageView icon = (ImageView) v.findViewById(16908294);
            TextView messageView = (TextView) v.findViewById(16908299);
            TextView statusView = (TextView) v.findViewById(16909506);
            boolean enabled = isEnabled();
            boolean on = true;
            if (messageView != null) {
                messageView.setText(this.mMessageResId);
                messageView.setEnabled(enabled);
                messageView.setSelected(true);
            }
            if (this.mState != State.On && this.mState != State.TurningOn) {
                on = false;
            }
            if (icon != null) {
                icon.setImageDrawable(context.getDrawable(on ? this.mEnabledIconResId : this.mDisabledIconResid));
                icon.setEnabled(enabled);
            }
            if (statusView != null) {
                statusView.setText(on ? this.mEnabledStatusMessageResId : this.mDisabledStatusMessageResId);
                statusView.setVisibility(0);
                statusView.setEnabled(enabled);
            }
            v.setEnabled(enabled);
            return v;
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public final void onPress() {
            if (this.mState.inTransition()) {
                Log.w(GlobalActionsDialog.TAG, "shouldn't be able to toggle when in transition");
                return;
            }
            boolean nowOn = this.mState != State.On;
            onToggle(nowOn);
            changeStateFromPress(nowOn);
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public boolean isEnabled() {
            return !this.mState.inTransition();
        }

        protected void changeStateFromPress(boolean buttonOn) {
            this.mState = buttonOn ? State.On : State.Off;
        }

        public void updateState(State state) {
            this.mState = state;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class SilentModeToggleAction extends ToggleAction {
        public SilentModeToggleAction() {
            super(17302304, 17302303, 17040072, 17040071, 17040070);
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.ToggleAction
        void onToggle(boolean on) {
            if (on) {
                GlobalActionsDialog.this.mAudioManager.setRingerMode(0);
            } else {
                GlobalActionsDialog.this.mAudioManager.setRingerMode(2);
            }
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public boolean showDuringKeyguard() {
            return true;
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public boolean showBeforeProvisioning() {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class SilentModeTriStateAction implements Action, View.OnClickListener {
        private final int[] ITEM_IDS = {16909297, 16909298, 16909299};
        private final AudioManager mAudioManager;
        private final Handler mHandler;

        SilentModeTriStateAction(AudioManager audioManager, Handler handler) {
            this.mAudioManager = audioManager;
            this.mHandler = handler;
        }

        private int ringerModeToIndex(int ringerMode) {
            return ringerMode;
        }

        private int indexToRingerMode(int index) {
            return index;
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public CharSequence getLabelForAccessibility(Context context) {
            return null;
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public View create(Context context, View convertView, ViewGroup parent, LayoutInflater inflater) {
            View v = inflater.inflate(17367162, parent, false);
            int selectedIndex = ringerModeToIndex(this.mAudioManager.getRingerMode());
            int i = 0;
            while (i < 3) {
                View itemView = v.findViewById(this.ITEM_IDS[i]);
                itemView.setSelected(selectedIndex == i);
                itemView.setTag(Integer.valueOf(i));
                itemView.setOnClickListener(this);
                i++;
            }
            return v;
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public void onPress() {
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public boolean showDuringKeyguard() {
            return true;
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public boolean showBeforeProvisioning() {
            return false;
        }

        @Override // com.android.systemui.globalactions.GlobalActionsDialog.Action
        public boolean isEnabled() {
            return true;
        }

        void willCreate() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            if (v.getTag() instanceof Integer) {
                int index = ((Integer) v.getTag()).intValue();
                this.mAudioManager.setRingerMode(indexToRingerMode(index));
                this.mHandler.sendEmptyMessageDelayed(0, 300L);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAirplaneModeChanged() {
        if (this.mHasTelephony) {
            return;
        }
        boolean airplaneModeOn = Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) == 1;
        this.mAirplaneState = airplaneModeOn ? ToggleAction.State.On : ToggleAction.State.Off;
        this.mAirplaneModeOn.updateState(this.mAirplaneState);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeAirplaneModeSystemSetting(boolean on) {
        Settings.Global.putInt(this.mContext.getContentResolver(), "airplane_mode_on", on ? 1 : 0);
        Intent intent = new Intent("android.intent.action.AIRPLANE_MODE");
        intent.addFlags(536870912);
        intent.putExtra("state", on);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
        if (!this.mHasTelephony) {
            this.mAirplaneState = on ? ToggleAction.State.On : ToggleAction.State.Off;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static final class ActionsDialog extends Dialog implements DialogInterface, ColorExtractor.OnColorsChangedListener {
        private final MyAdapter mAdapter;
        private Drawable mBackgroundDrawable;
        private final SysuiColorExtractor mColorExtractor;
        private final Context mContext;
        private MultiListLayout mGlobalActionsLayout;
        private boolean mHadTopUi;
        private boolean mKeyguardShowing;
        private final GlobalActionsPanelPlugin.PanelViewController mPanelController;
        private ResetOrientationData mResetOrientationData;
        private float mScrimAlpha;
        private boolean mShowing;
        private final IStatusBarService mStatusBarService;
        private final StatusBarWindowController mStatusBarWindowController;
        private final IBinder mToken;

        ActionsDialog(Context context, MyAdapter adapter, GlobalActionsPanelPlugin.PanelViewController plugin) {
            super(context, R.style.Theme_SystemUI_Dialog_GlobalActions);
            this.mToken = new Binder();
            this.mContext = context;
            this.mAdapter = adapter;
            this.mColorExtractor = (SysuiColorExtractor) Dependency.get(SysuiColorExtractor.class);
            this.mStatusBarService = (IStatusBarService) Dependency.get(IStatusBarService.class);
            this.mStatusBarWindowController = (StatusBarWindowController) Dependency.get(StatusBarWindowController.class);
            Window window = getWindow();
            window.requestFeature(1);
            window.getDecorView();
            window.getAttributes().systemUiVisibility |= 1792;
            window.setLayout(-1, -1);
            window.clearFlags(2);
            window.addFlags(17629472);
            window.setType(2020);
            setTitle(17040074);
            this.mPanelController = plugin;
            initializeLayout();
        }

        private boolean shouldUsePanel() {
            GlobalActionsPanelPlugin.PanelViewController panelViewController = this.mPanelController;
            return (panelViewController == null || panelViewController.getPanelContent() == null) ? false : true;
        }

        private void initializePanel() {
            int rotation = RotationUtils.getRotation(this.mContext);
            boolean rotationLocked = RotationPolicy.isRotationLocked(this.mContext);
            if (rotation != 0) {
                if (rotationLocked) {
                    if (this.mResetOrientationData == null) {
                        this.mResetOrientationData = new ResetOrientationData();
                        ResetOrientationData resetOrientationData = this.mResetOrientationData;
                        resetOrientationData.locked = true;
                        resetOrientationData.rotation = rotation;
                    }
                    this.mGlobalActionsLayout.post(new Runnable() { // from class: com.android.systemui.globalactions.-$$Lambda$GlobalActionsDialog$ActionsDialog$KOOsXb68KZ6uVivL8nC_5NKKiBk
                        @Override // java.lang.Runnable
                        public final void run() {
                            GlobalActionsDialog.ActionsDialog.this.lambda$initializePanel$0$GlobalActionsDialog$ActionsDialog();
                        }
                    });
                    return;
                }
                return;
            }
            if (!rotationLocked) {
                if (this.mResetOrientationData == null) {
                    this.mResetOrientationData = new ResetOrientationData();
                    this.mResetOrientationData.locked = false;
                }
                this.mGlobalActionsLayout.post(new Runnable() { // from class: com.android.systemui.globalactions.-$$Lambda$GlobalActionsDialog$ActionsDialog$RJgtbpfP8gfKx4bDDYXf9gg3qxs
                    @Override // java.lang.Runnable
                    public final void run() {
                        GlobalActionsDialog.ActionsDialog.this.lambda$initializePanel$1$GlobalActionsDialog$ActionsDialog();
                    }
                });
            }
            setRotationSuggestionsEnabled(false);
            FrameLayout panelContainer = (FrameLayout) findViewById(R.id.global_actions_panel_container);
            FrameLayout.LayoutParams panelParams = new FrameLayout.LayoutParams(-1, -1);
            panelContainer.addView(this.mPanelController.getPanelContent(), panelParams);
            this.mBackgroundDrawable = this.mPanelController.getBackgroundDrawable();
            this.mScrimAlpha = 1.0f;
        }

        public /* synthetic */ void lambda$initializePanel$0$GlobalActionsDialog$ActionsDialog() {
            RotationPolicy.setRotationLockAtAngle(this.mContext, false, 0);
        }

        public /* synthetic */ void lambda$initializePanel$1$GlobalActionsDialog$ActionsDialog() {
            RotationPolicy.setRotationLockAtAngle(this.mContext, true, 0);
        }

        private void initializeLayout() {
            setContentView(getGlobalActionsLayoutId(this.mContext));
            fixNavBarClipping();
            this.mGlobalActionsLayout = (MultiListLayout) findViewById(R.id.global_actions_view);
            this.mGlobalActionsLayout.setOutsideTouchListener(new View.OnClickListener() { // from class: com.android.systemui.globalactions.-$$Lambda$GlobalActionsDialog$ActionsDialog$dNZefhFQEiKyxgSvmP1LBM0gtx4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    GlobalActionsDialog.ActionsDialog.this.lambda$initializeLayout$2$GlobalActionsDialog$ActionsDialog(view);
                }
            });
            ((View) this.mGlobalActionsLayout.getParent()).setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.globalactions.-$$Lambda$GlobalActionsDialog$ActionsDialog$qLnbwfmuMw-GJ7JUyo3Qt6_cEh4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    GlobalActionsDialog.ActionsDialog.this.lambda$initializeLayout$3$GlobalActionsDialog$ActionsDialog(view);
                }
            });
            this.mGlobalActionsLayout.setListViewAccessibilityDelegate(new View.AccessibilityDelegate() { // from class: com.android.systemui.globalactions.GlobalActionsDialog.ActionsDialog.1
                @Override // android.view.View.AccessibilityDelegate
                public boolean dispatchPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
                    event.getText().add(ActionsDialog.this.mContext.getString(17040074));
                    return true;
                }
            });
            this.mGlobalActionsLayout.setRotationListener(new MultiListLayout.RotationListener() { // from class: com.android.systemui.globalactions.-$$Lambda$yTIuIImgAFK3eAYSmNsa3QUABJI
                @Override // com.android.systemui.MultiListLayout.RotationListener
                public final void onRotate(int i, int i2) {
                    GlobalActionsDialog.ActionsDialog.this.onRotate(i, i2);
                }
            });
            this.mGlobalActionsLayout.setAdapter(this.mAdapter);
            if (shouldUsePanel()) {
                initializePanel();
            }
            if (this.mBackgroundDrawable == null) {
                this.mBackgroundDrawable = new ScrimDrawable();
                this.mScrimAlpha = 0.2f;
            }
            getWindow().setBackgroundDrawable(this.mBackgroundDrawable);
        }

        public /* synthetic */ void lambda$initializeLayout$2$GlobalActionsDialog$ActionsDialog(View view) {
            dismiss();
        }

        public /* synthetic */ void lambda$initializeLayout$3$GlobalActionsDialog$ActionsDialog(View view) {
            dismiss();
        }

        private void fixNavBarClipping() {
            ViewGroup content = (ViewGroup) findViewById(16908290);
            content.setClipChildren(false);
            content.setClipToPadding(false);
            ViewGroup contentParent = (ViewGroup) content.getParent();
            contentParent.setClipChildren(false);
            contentParent.setClipToPadding(false);
        }

        private int getGlobalActionsLayoutId(Context context) {
            int rotation = RotationUtils.getRotation(context);
            boolean useGridLayout = GlobalActionsDialog.isForceGridEnabled(context) || (shouldUsePanel() && rotation == 0);
            if (rotation == 2) {
                if (useGridLayout) {
                    return R.layout.global_actions_grid_seascape;
                }
                return R.layout.global_actions_column_seascape;
            } else if (useGridLayout) {
                return R.layout.global_actions_grid;
            } else {
                return R.layout.global_actions_column;
            }
        }

        @Override // android.app.Dialog
        protected void onStart() {
            super.setCanceledOnTouchOutside(true);
            super.onStart();
            this.mGlobalActionsLayout.updateList();
            if (this.mBackgroundDrawable instanceof ScrimDrawable) {
                this.mColorExtractor.addOnColorsChangedListener(this);
                ColorExtractor.GradientColors colors = this.mColorExtractor.getNeutralColors();
                updateColors(colors, false);
            }
        }

        private void updateColors(ColorExtractor.GradientColors colors, boolean animate) {
            ScrimDrawable scrimDrawable = this.mBackgroundDrawable;
            if (!(scrimDrawable instanceof ScrimDrawable)) {
                return;
            }
            scrimDrawable.setColor(colors.getMainColor(), animate);
            View decorView = getWindow().getDecorView();
            if (colors.supportsDarkText()) {
                decorView.setSystemUiVisibility(8208);
            } else {
                decorView.setSystemUiVisibility(0);
            }
        }

        @Override // android.app.Dialog
        protected void onStop() {
            super.onStop();
            this.mColorExtractor.removeOnColorsChangedListener(this);
        }

        @Override // android.app.Dialog
        public void show() {
            super.show();
            this.mShowing = true;
            this.mHadTopUi = this.mStatusBarWindowController.getForceHasTopUi();
            this.mStatusBarWindowController.setForceHasTopUi(true);
            this.mBackgroundDrawable.setAlpha(0);
            MultiListLayout multiListLayout = this.mGlobalActionsLayout;
            multiListLayout.setTranslationX(multiListLayout.getAnimationOffsetX());
            MultiListLayout multiListLayout2 = this.mGlobalActionsLayout;
            multiListLayout2.setTranslationY(multiListLayout2.getAnimationOffsetY());
            this.mGlobalActionsLayout.setAlpha(0.0f);
            this.mGlobalActionsLayout.animate().alpha(1.0f).translationX(0.0f).translationY(0.0f).setDuration(300L).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).setUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.globalactions.-$$Lambda$GlobalActionsDialog$ActionsDialog$5VTsKfzFediL_BcyTcZsABCvLU0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    GlobalActionsDialog.ActionsDialog.this.lambda$show$4$GlobalActionsDialog$ActionsDialog(valueAnimator);
                }
            }).start();
        }

        public /* synthetic */ void lambda$show$4$GlobalActionsDialog$ActionsDialog(ValueAnimator animation) {
            int alpha = (int) (((Float) animation.getAnimatedValue()).floatValue() * this.mScrimAlpha * 255.0f);
            this.mBackgroundDrawable.setAlpha(alpha);
        }

        @Override // android.app.Dialog, android.content.DialogInterface
        public void dismiss() {
            if (!this.mShowing) {
                return;
            }
            this.mShowing = false;
            this.mGlobalActionsLayout.setTranslationX(0.0f);
            this.mGlobalActionsLayout.setTranslationY(0.0f);
            this.mGlobalActionsLayout.setAlpha(1.0f);
            this.mGlobalActionsLayout.animate().alpha(0.0f).translationX(this.mGlobalActionsLayout.getAnimationOffsetX()).translationY(this.mGlobalActionsLayout.getAnimationOffsetY()).setDuration(300L).withEndAction(new Runnable() { // from class: com.android.systemui.globalactions.-$$Lambda$GlobalActionsDialog$ActionsDialog$b7BjyiDlA1YYZd2S_4WLEfoJbac
                @Override // java.lang.Runnable
                public final void run() {
                    GlobalActionsDialog.ActionsDialog.this.completeDismiss();
                }
            }).setInterpolator(new SystemUIInterpolators.LogAccelerateInterpolator()).setUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.globalactions.-$$Lambda$GlobalActionsDialog$ActionsDialog$fCJNrp-FfqPrZqs-Y0ogCK3Vd_w
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    GlobalActionsDialog.ActionsDialog.this.lambda$dismiss$5$GlobalActionsDialog$ActionsDialog(valueAnimator);
                }
            }).start();
            dismissPanel();
            resetOrientation();
        }

        public /* synthetic */ void lambda$dismiss$5$GlobalActionsDialog$ActionsDialog(ValueAnimator animation) {
            int alpha = (int) ((1.0f - ((Float) animation.getAnimatedValue()).floatValue()) * this.mScrimAlpha * 255.0f);
            this.mBackgroundDrawable.setAlpha(alpha);
        }

        void dismissImmediately() {
            this.mShowing = false;
            dismissPanel();
            resetOrientation();
            completeDismiss();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void completeDismiss() {
            this.mStatusBarWindowController.setForceHasTopUi(this.mHadTopUi);
            super.dismiss();
        }

        private void dismissPanel() {
            GlobalActionsPanelPlugin.PanelViewController panelViewController = this.mPanelController;
            if (panelViewController != null) {
                panelViewController.onDismissed();
            }
        }

        private void setRotationSuggestionsEnabled(boolean enabled) {
            int what;
            try {
                int userId = Binder.getCallingUserHandle().getIdentifier();
                if (enabled) {
                    what = 0;
                } else {
                    what = 16;
                }
                this.mStatusBarService.disable2ForUser(what, this.mToken, this.mContext.getPackageName(), userId);
            } catch (RemoteException ex) {
                throw ex.rethrowFromSystemServer();
            }
        }

        private void resetOrientation() {
            ResetOrientationData resetOrientationData = this.mResetOrientationData;
            if (resetOrientationData != null) {
                RotationPolicy.setRotationLockAtAngle(this.mContext, resetOrientationData.locked, this.mResetOrientationData.rotation);
            }
            setRotationSuggestionsEnabled(true);
        }

        public void onColorsChanged(ColorExtractor extractor, int which) {
            if (this.mKeyguardShowing) {
                if ((which & 2) != 0) {
                    updateColors(extractor.getColors(2), true);
                }
            } else if ((which & 1) != 0) {
                updateColors(extractor.getColors(1), true);
            }
        }

        public void setKeyguardShowing(boolean keyguardShowing) {
            this.mKeyguardShowing = keyguardShowing;
        }

        public void refreshDialog() {
            initializeLayout();
            this.mGlobalActionsLayout.updateList();
        }

        public void onRotate(int from, int to) {
            if (this.mShowing) {
                refreshDialog();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes21.dex */
        public static class ResetOrientationData {
            public boolean locked;
            public int rotation;

            private ResetOrientationData() {
            }
        }
    }

    private static boolean isPanelDebugModeEnabled(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "global_actions_panel_debug_enabled", 0) == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isForceGridEnabled(Context context) {
        return isPanelDebugModeEnabled(context);
    }

    private static boolean shouldUseSeparatedView() {
        return true;
    }
}
