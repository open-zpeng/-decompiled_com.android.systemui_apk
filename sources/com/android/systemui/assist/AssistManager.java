package com.android.systemui.assist;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.metrics.LogMaker;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import com.android.internal.app.AssistUtils;
import com.android.internal.app.IVoiceInteractionSessionListener;
import com.android.internal.app.IVoiceInteractionSessionShowCallback;
import com.android.internal.logging.MetricsLogger;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.settingslib.applications.InterestingConfigChanges;
import com.android.systemui.R;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.assist.ui.DefaultUiController;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class AssistManager {
    protected static final String ACTION_KEY = "action";
    private static final String ASSIST_ICON_METADATA_NAME = "com.android.systemui.action_assist_icon";
    protected static final String CONSTRAINED_KEY = "should_constrain";
    public static final int DISMISS_REASON_BACK = 3;
    public static final int DISMISS_REASON_INVOCATION_CANCELLED = 1;
    public static final int DISMISS_REASON_TAP = 2;
    public static final int DISMISS_REASON_TIMEOUT = 4;
    public static final int INVOCATION_HOME_BUTTON_LONG_PRESS = 5;
    private static final String INVOCATION_PHONE_STATE_KEY = "invocation_phone_state";
    private static final String INVOCATION_TIME_MS_KEY = "invocation_time_ms";
    public static final int INVOCATION_TYPE_ACTIVE_EDGE = 2;
    public static final int INVOCATION_TYPE_GESTURE = 1;
    public static final String INVOCATION_TYPE_KEY = "invocation_type";
    public static final int INVOCATION_TYPE_QUICK_SEARCH_BAR = 4;
    public static final int INVOCATION_TYPE_VOICE = 3;
    protected static final String SET_ASSIST_GESTURE_CONSTRAINED_ACTION = "set_assist_gesture_constrained";
    protected static final String SHOW_ASSIST_HANDLES_ACTION = "show_assist_handles";
    private static final String TAG = "AssistManager";
    private static final long TIMEOUT_ACTIVITY = 1000;
    private static final long TIMEOUT_SERVICE = 2500;
    private static final boolean VERBOSE = false;
    private final AssistDisclosure mAssistDisclosure;
    protected final AssistUtils mAssistUtils;
    protected final Context mContext;
    private final DeviceProvisionedController mDeviceProvisionedController;
    private final AssistHandleBehaviorController mHandleController;
    private final InterestingConfigChanges mInterestingConfigChanges;
    protected final OverviewProxyService mOverviewProxyService;
    private final PhoneStateMonitor mPhoneStateMonitor;
    private final boolean mShouldEnableOrb;
    private final UiController mUiController;
    private AssistOrbContainer mView;
    private final WindowManager mWindowManager;
    private IVoiceInteractionSessionShowCallback mShowCallback = new IVoiceInteractionSessionShowCallback.Stub() { // from class: com.android.systemui.assist.AssistManager.1
        public void onFailed() throws RemoteException {
            AssistManager.this.mView.post(AssistManager.this.mHideRunnable);
        }

        public void onShown() throws RemoteException {
            AssistManager.this.mView.post(AssistManager.this.mHideRunnable);
        }
    };
    private Runnable mHideRunnable = new Runnable() { // from class: com.android.systemui.assist.AssistManager.2
        @Override // java.lang.Runnable
        public void run() {
            AssistManager.this.mView.removeCallbacks(this);
            AssistManager.this.mView.show(false, true);
        }
    };
    private ConfigurationController.ConfigurationListener mConfigurationListener = new ConfigurationController.ConfigurationListener() { // from class: com.android.systemui.assist.AssistManager.3
        @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
        public void onConfigChanged(Configuration newConfig) {
            if (!AssistManager.this.mInterestingConfigChanges.applyNewConfig(AssistManager.this.mContext.getResources())) {
                return;
            }
            boolean visible = false;
            if (AssistManager.this.mView != null) {
                visible = AssistManager.this.mView.isShowing();
                AssistManager.this.mWindowManager.removeView(AssistManager.this.mView);
            }
            AssistManager assistManager = AssistManager.this;
            assistManager.mView = (AssistOrbContainer) LayoutInflater.from(assistManager.mContext).inflate(R.layout.assist_orb, (ViewGroup) null);
            AssistManager.this.mView.setVisibility(8);
            AssistManager.this.mView.setSystemUiVisibility(1792);
            WindowManager.LayoutParams lp = AssistManager.this.getLayoutParams();
            AssistManager.this.mWindowManager.addView(AssistManager.this.mView, lp);
            if (visible) {
                AssistManager.this.mView.show(true, false);
            }
        }
    };

    /* loaded from: classes21.dex */
    public interface UiController {
        void hide();

        void onGestureCompletion(float f);

        void onInvocationProgress(int i, float f);

        void processBundle(Bundle bundle);
    }

    @Inject
    public AssistManager(DeviceProvisionedController controller, Context context, AssistUtils assistUtils, AssistHandleBehaviorController handleController, ConfigurationController configurationController, OverviewProxyService overviewProxyService) {
        this.mContext = context;
        this.mDeviceProvisionedController = controller;
        this.mWindowManager = (WindowManager) this.mContext.getSystemService("window");
        this.mAssistUtils = assistUtils;
        this.mAssistDisclosure = new AssistDisclosure(context, new Handler());
        this.mPhoneStateMonitor = new PhoneStateMonitor(context);
        this.mHandleController = handleController;
        configurationController.addCallback(this.mConfigurationListener);
        registerVoiceInteractionSessionListener();
        this.mInterestingConfigChanges = new InterestingConfigChanges(-2147482748);
        this.mConfigurationListener.onConfigChanged(context.getResources().getConfiguration());
        this.mShouldEnableOrb = !ActivityManager.isLowRamDeviceStatic();
        this.mUiController = new DefaultUiController(this.mContext);
        this.mOverviewProxyService = overviewProxyService;
        this.mOverviewProxyService.addCallback(new OverviewProxyService.OverviewProxyListener() { // from class: com.android.systemui.assist.AssistManager.4
            @Override // com.android.systemui.recents.OverviewProxyService.OverviewProxyListener
            public void onAssistantProgress(float progress) {
                AssistManager.this.onInvocationProgress(1, progress);
            }

            @Override // com.android.systemui.recents.OverviewProxyService.OverviewProxyListener
            public void onAssistantGestureCompletion(float velocity) {
                AssistManager.this.onGestureCompletion(velocity);
            }
        });
    }

    protected void registerVoiceInteractionSessionListener() {
        this.mAssistUtils.registerVoiceInteractionSessionListener(new IVoiceInteractionSessionListener.Stub() { // from class: com.android.systemui.assist.AssistManager.5
            public void onVoiceSessionShown() throws RemoteException {
            }

            public void onVoiceSessionHidden() throws RemoteException {
            }

            public void onSetUiHints(Bundle hints) {
                String action = hints.getString(AssistManager.ACTION_KEY);
                if (AssistManager.SHOW_ASSIST_HANDLES_ACTION.equals(action)) {
                    AssistManager.this.requestAssistHandles();
                } else if (AssistManager.SET_ASSIST_GESTURE_CONSTRAINED_ACTION.equals(action)) {
                    AssistManager.this.mOverviewProxyService.setSystemUiStateFlag(4096, hints.getBoolean(AssistManager.CONSTRAINED_KEY, false), 0);
                }
            }
        });
    }

    protected boolean shouldShowOrb() {
        return false;
    }

    public void startAssist(Bundle args) {
        long j;
        ComponentName assistComponent = getAssistInfo();
        if (assistComponent == null) {
            return;
        }
        boolean isService = assistComponent.equals(getVoiceInteractorComponentName());
        if (!isService || (!isVoiceSessionRunning() && shouldShowOrb())) {
            showOrb(assistComponent, isService);
            AssistOrbContainer assistOrbContainer = this.mView;
            Runnable runnable = this.mHideRunnable;
            if (isService) {
                j = 2500;
            } else {
                j = 1000;
            }
            assistOrbContainer.postDelayed(runnable, j);
        }
        if (args == null) {
            args = new Bundle();
        }
        int invocationType = args.getInt(INVOCATION_TYPE_KEY, 0);
        if (invocationType == 1) {
            this.mHandleController.onAssistantGesturePerformed();
        }
        int phoneState = this.mPhoneStateMonitor.getPhoneState();
        args.putInt(INVOCATION_PHONE_STATE_KEY, phoneState);
        args.putLong(INVOCATION_TIME_MS_KEY, SystemClock.elapsedRealtime());
        logStartAssist(invocationType, phoneState);
        startAssistInternal(args, assistComponent, isService);
    }

    public void onInvocationProgress(int type, float progress) {
        this.mUiController.onInvocationProgress(type, progress);
    }

    public void onGestureCompletion(float velocity) {
        this.mUiController.onGestureCompletion(velocity);
    }

    protected void requestAssistHandles() {
        this.mHandleController.onAssistHandlesRequested();
    }

    public void hideAssist() {
        this.mAssistUtils.hideCurrentSession();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public WindowManager.LayoutParams getLayoutParams() {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(-1, this.mContext.getResources().getDimensionPixelSize(R.dimen.assist_orb_scrim_height), 2033, 280, -3);
        lp.token = new Binder();
        lp.gravity = 8388691;
        lp.setTitle("AssistPreviewPanel");
        lp.softInputMode = 49;
        return lp;
    }

    private void showOrb(ComponentName assistComponent, boolean isService) {
        maybeSwapSearchIcon(assistComponent, isService);
        if (this.mShouldEnableOrb) {
            this.mView.show(true, true);
        }
    }

    private void startAssistInternal(Bundle args, ComponentName assistComponent, boolean isService) {
        if (isService) {
            startVoiceInteractor(args);
        } else {
            startAssistActivity(args, assistComponent);
        }
    }

    private void startAssistActivity(Bundle args, ComponentName assistComponent) {
        final Intent intent;
        if (!this.mDeviceProvisionedController.isDeviceProvisioned()) {
            return;
        }
        ((CommandQueue) SysUiServiceProvider.getComponent(this.mContext, CommandQueue.class)).animateCollapsePanels(3, false);
        boolean structureEnabled = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "assist_structure_enabled", 1, -2) != 0;
        SearchManager searchManager = (SearchManager) this.mContext.getSystemService(SpeechWidget.TYPE_SEARCH);
        if (searchManager == null || (intent = searchManager.getAssistIntent(structureEnabled)) == null) {
            return;
        }
        intent.setComponent(assistComponent);
        intent.putExtras(args);
        if (structureEnabled) {
            showDisclosure();
        }
        try {
            final ActivityOptions opts = ActivityOptions.makeCustomAnimation(this.mContext, R.anim.search_launch_enter, R.anim.search_launch_exit);
            intent.addFlags(268435456);
            AsyncTask.execute(new Runnable() { // from class: com.android.systemui.assist.AssistManager.6
                @Override // java.lang.Runnable
                public void run() {
                    AssistManager.this.mContext.startActivityAsUser(intent, opts.toBundle(), new UserHandle(-2));
                }
            });
        } catch (ActivityNotFoundException e) {
            Log.w(TAG, "Activity not found for " + intent.getAction());
        }
    }

    private void startVoiceInteractor(Bundle args) {
        this.mAssistUtils.showSessionForActiveService(args, 4, this.mShowCallback, (IBinder) null);
    }

    public void launchVoiceAssistFromKeyguard() {
        this.mAssistUtils.launchVoiceAssistFromKeyguard();
    }

    public boolean canVoiceAssistBeLaunchedFromKeyguard() {
        return this.mAssistUtils.activeServiceSupportsLaunchFromKeyguard();
    }

    public ComponentName getVoiceInteractorComponentName() {
        return this.mAssistUtils.getActiveServiceComponentName();
    }

    private boolean isVoiceSessionRunning() {
        return this.mAssistUtils.isSessionRunning();
    }

    private void maybeSwapSearchIcon(ComponentName assistComponent, boolean isService) {
        replaceDrawable(this.mView.getOrb().getLogo(), assistComponent, ASSIST_ICON_METADATA_NAME, isService);
    }

    public void replaceDrawable(ImageView v, ComponentName component, String name, boolean isService) {
        Bundle metaData;
        int iconResId;
        if (component != null) {
            try {
                PackageManager packageManager = this.mContext.getPackageManager();
                if (isService) {
                    metaData = packageManager.getServiceInfo(component, 128).metaData;
                } else {
                    metaData = packageManager.getActivityInfo(component, 128).metaData;
                }
                if (metaData != null && (iconResId = metaData.getInt(name)) != 0) {
                    Resources res = packageManager.getResourcesForApplication(component.getPackageName());
                    v.setImageDrawable(res.getDrawable(iconResId));
                    return;
                }
            } catch (PackageManager.NameNotFoundException e) {
            } catch (Resources.NotFoundException nfe) {
                Log.w(TAG, "Failed to swap drawable from " + component.flattenToShortString(), nfe);
            }
        }
        v.setImageDrawable(null);
    }

    protected AssistHandleBehaviorController getHandleBehaviorController() {
        return this.mHandleController;
    }

    public ComponentName getAssistInfoForUser(int userId) {
        return this.mAssistUtils.getAssistComponentForUser(userId);
    }

    private ComponentName getAssistInfo() {
        return getAssistInfoForUser(KeyguardUpdateMonitor.getCurrentUser());
    }

    public void showDisclosure() {
        this.mAssistDisclosure.postShow();
    }

    public void onLockscreenShown() {
        this.mAssistUtils.onLockscreenShown();
    }

    public long getAssistHandleShowAndGoRemainingDurationMs() {
        return this.mHandleController.getShowAndGoRemainingTimeMs();
    }

    public int toLoggingSubType(int invocationType) {
        return toLoggingSubType(invocationType, this.mPhoneStateMonitor.getPhoneState());
    }

    protected void logStartAssist(int invocationType, int phoneState) {
        MetricsLogger.action(new LogMaker(1716).setType(1).setSubtype(toLoggingSubType(invocationType, phoneState)));
    }

    protected final int toLoggingSubType(int invocationType, int phoneState) {
        int subType = !this.mHandleController.areHandlesShowing();
        return subType | (invocationType << 1) | (phoneState << 4);
    }
}
