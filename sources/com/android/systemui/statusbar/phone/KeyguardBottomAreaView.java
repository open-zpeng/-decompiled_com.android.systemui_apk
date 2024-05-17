package com.android.systemui.statusbar.phone;

import android.app.ActivityOptions;
import android.app.ActivityTaskManager;
import android.app.IApplicationThread;
import android.app.ProfilerInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.UserHandle;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.ActivityIntentHelper;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.doze.util.BurnInHelperKt;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.IntentButtonProvider;
import com.android.systemui.statusbar.KeyguardAffordanceView;
import com.android.systemui.statusbar.phone.UnlockMethodCache;
import com.android.systemui.statusbar.policy.AccessibilityController;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.statusbar.policy.FlashlightController;
import com.android.systemui.statusbar.policy.PreviewInflater;
import com.android.systemui.tuner.LockscreenFragment;
import com.android.systemui.tuner.TunerService;
import java.util.function.Consumer;
import java.util.function.Supplier;
/* loaded from: classes21.dex */
public class KeyguardBottomAreaView extends FrameLayout implements View.OnClickListener, UnlockMethodCache.OnUnlockMethodChangedListener, AccessibilityController.AccessibilityStateChangedCallback {
    public static final String CAMERA_LAUNCH_SOURCE_AFFORDANCE = "lockscreen_affordance";
    public static final String CAMERA_LAUNCH_SOURCE_LIFT_TRIGGER = "lift_to_launch_ml";
    public static final String CAMERA_LAUNCH_SOURCE_POWER_DOUBLE_TAP = "power_double_tap";
    public static final String CAMERA_LAUNCH_SOURCE_WIGGLE = "wiggle_gesture";
    private static final int DOZE_ANIMATION_ELEMENT_DURATION = 250;
    private static final int DOZE_ANIMATION_STAGGER_DELAY = 48;
    public static final String EXTRA_CAMERA_LAUNCH_SOURCE = "com.android.systemui.camera_launch_source";
    private static final String LEFT_BUTTON_PLUGIN = "com.android.systemui.action.PLUGIN_LOCKSCREEN_LEFT_BUTTON";
    private static final String RIGHT_BUTTON_PLUGIN = "com.android.systemui.action.PLUGIN_LOCKSCREEN_RIGHT_BUTTON";
    static final String TAG = "StatusBar/KeyguardBottomAreaView";
    private AccessibilityController mAccessibilityController;
    private View.AccessibilityDelegate mAccessibilityDelegate;
    private ActivityIntentHelper mActivityIntentHelper;
    private ActivityStarter mActivityStarter;
    private KeyguardAffordanceHelper mAffordanceHelper;
    private AssistManager mAssistManager;
    private int mBurnInXOffset;
    private int mBurnInYOffset;
    private View mCameraPreview;
    private float mDarkAmount;
    private final BroadcastReceiver mDevicePolicyReceiver;
    private boolean mDozing;
    private TextView mEnterpriseDisclosure;
    private FlashlightController mFlashlightController;
    private ViewGroup mIndicationArea;
    private int mIndicationBottomMargin;
    private TextView mIndicationText;
    private KeyguardAffordanceView mLeftAffordanceView;
    private Drawable mLeftAssistIcon;
    private IntentButtonProvider.IntentButton mLeftButton;
    private String mLeftButtonStr;
    private ExtensionController.Extension<IntentButtonProvider.IntentButton> mLeftExtension;
    private boolean mLeftIsVoiceAssist;
    private View mLeftPreview;
    private LockPatternUtils mLockPatternUtils;
    private ViewGroup mOverlayContainer;
    private ViewGroup mPreviewContainer;
    private PreviewInflater mPreviewInflater;
    private boolean mPrewarmBound;
    private final ServiceConnection mPrewarmConnection;
    private Messenger mPrewarmMessenger;
    private KeyguardAffordanceView mRightAffordanceView;
    private IntentButtonProvider.IntentButton mRightButton;
    private String mRightButtonStr;
    private ExtensionController.Extension<IntentButtonProvider.IntentButton> mRightExtension;
    private StatusBar mStatusBar;
    private UnlockMethodCache mUnlockMethodCache;
    private final KeyguardUpdateMonitorCallback mUpdateMonitorCallback;
    private boolean mUserSetupComplete;
    private static final Intent SECURE_CAMERA_INTENT = new Intent("android.media.action.STILL_IMAGE_CAMERA_SECURE").addFlags(8388608);
    public static final Intent INSECURE_CAMERA_INTENT = new Intent("android.media.action.STILL_IMAGE_CAMERA");
    private static final Intent PHONE_INTENT = new Intent("android.intent.action.DIAL");

    public KeyguardBottomAreaView(Context context) {
        this(context, null);
    }

    public KeyguardBottomAreaView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyguardBottomAreaView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public KeyguardBottomAreaView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mPrewarmConnection = new ServiceConnection() { // from class: com.android.systemui.statusbar.phone.KeyguardBottomAreaView.1
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName name, IBinder service) {
                KeyguardBottomAreaView.this.mPrewarmMessenger = new Messenger(service);
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName name) {
                KeyguardBottomAreaView.this.mPrewarmMessenger = null;
            }
        };
        this.mRightButton = new DefaultRightButton();
        this.mLeftButton = new DefaultLeftButton();
        this.mAccessibilityDelegate = new View.AccessibilityDelegate() { // from class: com.android.systemui.statusbar.phone.KeyguardBottomAreaView.2
            @Override // android.view.View.AccessibilityDelegate
            public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                String label = null;
                if (host != KeyguardBottomAreaView.this.mRightAffordanceView) {
                    if (host == KeyguardBottomAreaView.this.mLeftAffordanceView) {
                        if (KeyguardBottomAreaView.this.mLeftIsVoiceAssist) {
                            label = KeyguardBottomAreaView.this.getResources().getString(R.string.voice_assist_label);
                        } else {
                            label = KeyguardBottomAreaView.this.getResources().getString(R.string.phone_label);
                        }
                    }
                } else {
                    label = KeyguardBottomAreaView.this.getResources().getString(R.string.camera_label);
                }
                info.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, label));
            }

            @Override // android.view.View.AccessibilityDelegate
            public boolean performAccessibilityAction(View host, int action, Bundle args) {
                if (action == 16) {
                    if (host != KeyguardBottomAreaView.this.mRightAffordanceView) {
                        if (host == KeyguardBottomAreaView.this.mLeftAffordanceView) {
                            KeyguardBottomAreaView.this.launchLeftAffordance();
                            return true;
                        }
                    } else {
                        KeyguardBottomAreaView.this.launchCamera(KeyguardBottomAreaView.CAMERA_LAUNCH_SOURCE_AFFORDANCE);
                        return true;
                    }
                }
                return super.performAccessibilityAction(host, action, args);
            }
        };
        this.mDevicePolicyReceiver = new BroadcastReceiver() { // from class: com.android.systemui.statusbar.phone.KeyguardBottomAreaView.7
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                KeyguardBottomAreaView.this.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.KeyguardBottomAreaView.7.1
                    @Override // java.lang.Runnable
                    public void run() {
                        KeyguardBottomAreaView.this.updateCameraVisibility();
                    }
                });
            }
        };
        this.mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() { // from class: com.android.systemui.statusbar.phone.KeyguardBottomAreaView.8
            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onUserSwitchComplete(int userId) {
                KeyguardBottomAreaView.this.updateCameraVisibility();
            }

            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onUserUnlocked() {
                KeyguardBottomAreaView.this.inflateCameraPreview();
                KeyguardBottomAreaView.this.updateCameraVisibility();
                KeyguardBottomAreaView.this.updateLeftAffordance();
            }
        };
    }

    public void initFrom(KeyguardBottomAreaView oldBottomArea) {
        setStatusBar(oldBottomArea.mStatusBar);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mLockPatternUtils = new LockPatternUtils(this.mContext);
        this.mPreviewInflater = new PreviewInflater(this.mContext, new LockPatternUtils(this.mContext), new ActivityIntentHelper(this.mContext));
        this.mPreviewContainer = (ViewGroup) findViewById(R.id.preview_container);
        this.mOverlayContainer = (ViewGroup) findViewById(R.id.overlay_container);
        this.mRightAffordanceView = (KeyguardAffordanceView) findViewById(R.id.camera_button);
        this.mLeftAffordanceView = (KeyguardAffordanceView) findViewById(R.id.left_button);
        this.mIndicationArea = (ViewGroup) findViewById(R.id.keyguard_indication_area);
        this.mEnterpriseDisclosure = (TextView) findViewById(R.id.keyguard_indication_enterprise_disclosure);
        this.mIndicationText = (TextView) findViewById(R.id.keyguard_indication_text);
        this.mIndicationBottomMargin = getResources().getDimensionPixelSize(R.dimen.keyguard_indication_margin_bottom);
        this.mBurnInYOffset = getResources().getDimensionPixelSize(R.dimen.default_burn_in_prevention_offset);
        updateCameraVisibility();
        this.mUnlockMethodCache = UnlockMethodCache.getInstance(getContext());
        this.mUnlockMethodCache.addListener(this);
        setClipChildren(false);
        setClipToPadding(false);
        inflateCameraPreview();
        this.mRightAffordanceView.setOnClickListener(this);
        this.mLeftAffordanceView.setOnClickListener(this);
        initAccessibility();
        this.mActivityStarter = (ActivityStarter) Dependency.get(ActivityStarter.class);
        this.mFlashlightController = (FlashlightController) Dependency.get(FlashlightController.class);
        this.mAccessibilityController = (AccessibilityController) Dependency.get(AccessibilityController.class);
        this.mAssistManager = (AssistManager) Dependency.get(AssistManager.class);
        this.mActivityIntentHelper = new ActivityIntentHelper(getContext());
        updateLeftAffordance();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAccessibilityController.addStateChangedCallback(this);
        this.mRightExtension = ((ExtensionController) Dependency.get(ExtensionController.class)).newExtension(IntentButtonProvider.IntentButton.class).withPlugin(IntentButtonProvider.class, RIGHT_BUTTON_PLUGIN, new ExtensionController.PluginConverter() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$KeyguardBottomAreaView$g4KaNPI9kzVsHrOlMY-mA_f9J2Y
            @Override // com.android.systemui.statusbar.policy.ExtensionController.PluginConverter
            public final Object getInterfaceFromPlugin(Object obj) {
                IntentButtonProvider.IntentButton intentButton;
                intentButton = ((IntentButtonProvider) obj).getIntentButton();
                return intentButton;
            }
        }).withTunerFactory(new LockscreenFragment.LockButtonFactory(this.mContext, LockscreenFragment.LOCKSCREEN_RIGHT_BUTTON)).withDefault(new Supplier() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$KeyguardBottomAreaView$41MKD52m3LHIf9RRtKFf6LfUif0
            @Override // java.util.function.Supplier
            public final Object get() {
                return KeyguardBottomAreaView.this.lambda$onAttachedToWindow$1$KeyguardBottomAreaView();
            }
        }).withCallback(new Consumer() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$KeyguardBottomAreaView$Z_R5g5wpXUcfPYLHCfZHekG4xK0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                KeyguardBottomAreaView.this.lambda$onAttachedToWindow$2$KeyguardBottomAreaView((IntentButtonProvider.IntentButton) obj);
            }
        }).build();
        this.mLeftExtension = ((ExtensionController) Dependency.get(ExtensionController.class)).newExtension(IntentButtonProvider.IntentButton.class).withPlugin(IntentButtonProvider.class, LEFT_BUTTON_PLUGIN, new ExtensionController.PluginConverter() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$KeyguardBottomAreaView$Eh9_ou4HbbT4H4ZFilpDDtanY4k
            @Override // com.android.systemui.statusbar.policy.ExtensionController.PluginConverter
            public final Object getInterfaceFromPlugin(Object obj) {
                IntentButtonProvider.IntentButton intentButton;
                intentButton = ((IntentButtonProvider) obj).getIntentButton();
                return intentButton;
            }
        }).withTunerFactory(new LockscreenFragment.LockButtonFactory(this.mContext, LockscreenFragment.LOCKSCREEN_LEFT_BUTTON)).withDefault(new Supplier() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$KeyguardBottomAreaView$W-hTEBW5YZVW2MsKtz0LzBCynHY
            @Override // java.util.function.Supplier
            public final Object get() {
                return KeyguardBottomAreaView.this.lambda$onAttachedToWindow$4$KeyguardBottomAreaView();
            }
        }).withCallback(new Consumer() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$KeyguardBottomAreaView$owXxFBBnubMOAUdfyf5a48bf-Zo
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                KeyguardBottomAreaView.this.lambda$onAttachedToWindow$5$KeyguardBottomAreaView((IntentButtonProvider.IntentButton) obj);
            }
        }).build();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
        getContext().registerReceiverAsUser(this.mDevicePolicyReceiver, UserHandle.ALL, filter, null, null);
        KeyguardUpdateMonitor.getInstance(this.mContext).registerCallback(this.mUpdateMonitorCallback);
    }

    public /* synthetic */ IntentButtonProvider.IntentButton lambda$onAttachedToWindow$1$KeyguardBottomAreaView() {
        return new DefaultRightButton();
    }

    public /* synthetic */ IntentButtonProvider.IntentButton lambda$onAttachedToWindow$4$KeyguardBottomAreaView() {
        return new DefaultLeftButton();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mAccessibilityController.removeStateChangedCallback(this);
        this.mRightExtension.destroy();
        this.mLeftExtension.destroy();
        getContext().unregisterReceiver(this.mDevicePolicyReceiver);
        KeyguardUpdateMonitor.getInstance(this.mContext).removeCallback(this.mUpdateMonitorCallback);
    }

    private void initAccessibility() {
        this.mLeftAffordanceView.setAccessibilityDelegate(this.mAccessibilityDelegate);
        this.mRightAffordanceView.setAccessibilityDelegate(this.mAccessibilityDelegate);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mIndicationBottomMargin = getResources().getDimensionPixelSize(R.dimen.keyguard_indication_margin_bottom);
        this.mBurnInYOffset = getResources().getDimensionPixelSize(R.dimen.default_burn_in_prevention_offset);
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) this.mIndicationArea.getLayoutParams();
        int i = mlp.bottomMargin;
        int i2 = this.mIndicationBottomMargin;
        if (i != i2) {
            mlp.bottomMargin = i2;
            this.mIndicationArea.setLayoutParams(mlp);
        }
        this.mEnterpriseDisclosure.setTextSize(0, getResources().getDimensionPixelSize(17105466));
        this.mIndicationText.setTextSize(0, getResources().getDimensionPixelSize(17105466));
        ViewGroup.LayoutParams lp = this.mRightAffordanceView.getLayoutParams();
        lp.width = getResources().getDimensionPixelSize(R.dimen.keyguard_affordance_width);
        lp.height = getResources().getDimensionPixelSize(R.dimen.keyguard_affordance_height);
        this.mRightAffordanceView.setLayoutParams(lp);
        updateRightAffordanceIcon();
        ViewGroup.LayoutParams lp2 = this.mLeftAffordanceView.getLayoutParams();
        lp2.width = getResources().getDimensionPixelSize(R.dimen.keyguard_affordance_width);
        lp2.height = getResources().getDimensionPixelSize(R.dimen.keyguard_affordance_height);
        this.mLeftAffordanceView.setLayoutParams(lp2);
        updateLeftAffordanceIcon();
    }

    private void updateRightAffordanceIcon() {
        IntentButtonProvider.IntentButton.IconState state = this.mRightButton.getIcon();
        this.mRightAffordanceView.setVisibility((this.mDozing || !state.isVisible) ? 8 : 0);
        if (state.drawable != this.mRightAffordanceView.getDrawable() || state.tint != this.mRightAffordanceView.shouldTint()) {
            this.mRightAffordanceView.setImageDrawable(state.drawable, state.tint);
        }
        this.mRightAffordanceView.setContentDescription(state.contentDescription);
    }

    public void setStatusBar(StatusBar statusBar) {
        this.mStatusBar = statusBar;
        updateCameraVisibility();
    }

    public void setAffordanceHelper(KeyguardAffordanceHelper affordanceHelper) {
        this.mAffordanceHelper = affordanceHelper;
    }

    public void setUserSetupComplete(boolean userSetupComplete) {
        this.mUserSetupComplete = userSetupComplete;
        updateCameraVisibility();
        updateLeftAffordanceIcon();
    }

    private Intent getCameraIntent() {
        return this.mRightButton.getIntent();
    }

    public ResolveInfo resolveCameraIntent() {
        return this.mContext.getPackageManager().resolveActivityAsUser(getCameraIntent(), 65536, KeyguardUpdateMonitor.getCurrentUser());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCameraVisibility() {
        KeyguardAffordanceView keyguardAffordanceView = this.mRightAffordanceView;
        if (keyguardAffordanceView == null) {
            return;
        }
        keyguardAffordanceView.setVisibility((this.mDozing || !this.mRightButton.getIcon().isVisible) ? 8 : 0);
    }

    public void setLeftAssistIcon(Drawable drawable) {
        this.mLeftAssistIcon = drawable;
        updateLeftAffordanceIcon();
    }

    private void updateLeftAffordanceIcon() {
        IntentButtonProvider.IntentButton.IconState state = this.mLeftButton.getIcon();
        this.mLeftAffordanceView.setVisibility((this.mDozing || !state.isVisible) ? 8 : 0);
        if (state.drawable != this.mLeftAffordanceView.getDrawable() || state.tint != this.mLeftAffordanceView.shouldTint()) {
            this.mLeftAffordanceView.setImageDrawable(state.drawable, state.tint);
        }
        this.mLeftAffordanceView.setContentDescription(state.contentDescription);
    }

    public boolean isLeftVoiceAssist() {
        return this.mLeftIsVoiceAssist;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPhoneVisible() {
        PackageManager pm = this.mContext.getPackageManager();
        return pm.hasSystemFeature("android.hardware.telephony") && pm.resolveActivity(PHONE_INTENT, 0) != null;
    }

    @Override // com.android.systemui.statusbar.policy.AccessibilityController.AccessibilityStateChangedCallback
    public void onStateChanged(boolean accessibilityEnabled, boolean touchExplorationEnabled) {
        this.mRightAffordanceView.setClickable(touchExplorationEnabled);
        this.mLeftAffordanceView.setClickable(touchExplorationEnabled);
        this.mRightAffordanceView.setFocusable(accessibilityEnabled);
        this.mLeftAffordanceView.setFocusable(accessibilityEnabled);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v == this.mRightAffordanceView) {
            launchCamera(CAMERA_LAUNCH_SOURCE_AFFORDANCE);
        } else if (v == this.mLeftAffordanceView) {
            launchLeftAffordance();
        }
    }

    public void bindCameraPrewarmService() {
        String clazz;
        Intent intent = getCameraIntent();
        ActivityInfo targetInfo = this.mActivityIntentHelper.getTargetActivityInfo(intent, KeyguardUpdateMonitor.getCurrentUser(), true);
        if (targetInfo != null && targetInfo.metaData != null && (clazz = targetInfo.metaData.getString("android.media.still_image_camera_preview_service")) != null) {
            Intent serviceIntent = new Intent();
            serviceIntent.setClassName(targetInfo.packageName, clazz);
            serviceIntent.setAction("android.service.media.CameraPrewarmService.ACTION_PREWARM");
            try {
                if (getContext().bindServiceAsUser(serviceIntent, this.mPrewarmConnection, 67108865, new UserHandle(-2))) {
                    this.mPrewarmBound = true;
                }
            } catch (SecurityException e) {
                Log.w(TAG, "Unable to bind to prewarm service package=" + targetInfo.packageName + " class=" + clazz, e);
            }
        }
    }

    public void unbindCameraPrewarmService(boolean launched) {
        if (this.mPrewarmBound) {
            Messenger messenger = this.mPrewarmMessenger;
            if (messenger != null && launched) {
                try {
                    messenger.send(Message.obtain((Handler) null, 1));
                } catch (RemoteException e) {
                    Log.w(TAG, "Error sending camera fired message", e);
                }
            }
            this.mContext.unbindService(this.mPrewarmConnection);
            this.mPrewarmBound = false;
        }
    }

    public void launchCamera(String source) {
        final Intent intent = getCameraIntent();
        intent.putExtra(EXTRA_CAMERA_LAUNCH_SOURCE, source);
        boolean wouldLaunchResolverActivity = this.mActivityIntentHelper.wouldLaunchResolverActivity(intent, KeyguardUpdateMonitor.getCurrentUser());
        if (intent == SECURE_CAMERA_INTENT && !wouldLaunchResolverActivity) {
            AsyncTask.execute(new Runnable() { // from class: com.android.systemui.statusbar.phone.KeyguardBottomAreaView.3
                @Override // java.lang.Runnable
                public void run() {
                    int result = -96;
                    ActivityOptions o = ActivityOptions.makeBasic();
                    o.setDisallowEnterPictureInPictureWhileLaunching(true);
                    o.setRotationAnimationHint(3);
                    try {
                        result = ActivityTaskManager.getService().startActivityAsUser((IApplicationThread) null, KeyguardBottomAreaView.this.getContext().getBasePackageName(), intent, intent.resolveTypeIfNeeded(KeyguardBottomAreaView.this.getContext().getContentResolver()), (IBinder) null, (String) null, 0, 268435456, (ProfilerInfo) null, o.toBundle(), UserHandle.CURRENT.getIdentifier());
                    } catch (RemoteException e) {
                        Log.w(KeyguardBottomAreaView.TAG, "Unable to start camera activity", e);
                    }
                    final boolean launched = KeyguardBottomAreaView.isSuccessfulLaunch(result);
                    KeyguardBottomAreaView.this.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.KeyguardBottomAreaView.3.1
                        @Override // java.lang.Runnable
                        public void run() {
                            KeyguardBottomAreaView.this.unbindCameraPrewarmService(launched);
                        }
                    });
                }
            });
        } else {
            this.mActivityStarter.startActivity(intent, false, new ActivityStarter.Callback() { // from class: com.android.systemui.statusbar.phone.KeyguardBottomAreaView.4
                @Override // com.android.systemui.plugins.ActivityStarter.Callback
                public void onActivityStarted(int resultCode) {
                    KeyguardBottomAreaView.this.unbindCameraPrewarmService(KeyguardBottomAreaView.isSuccessfulLaunch(resultCode));
                }
            });
        }
    }

    public void setDarkAmount(float darkAmount) {
        if (darkAmount == this.mDarkAmount) {
            return;
        }
        this.mDarkAmount = darkAmount;
        dozeTimeTick();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isSuccessfulLaunch(int result) {
        return result == 0 || result == 3 || result == 2;
    }

    public void launchLeftAffordance() {
        if (this.mLeftIsVoiceAssist) {
            launchVoiceAssist();
        } else {
            launchPhone();
        }
    }

    @VisibleForTesting
    void launchVoiceAssist() {
        Runnable runnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.KeyguardBottomAreaView.5
            @Override // java.lang.Runnable
            public void run() {
                KeyguardBottomAreaView.this.mAssistManager.launchVoiceAssistFromKeyguard();
            }
        };
        if (this.mStatusBar.isKeyguardCurrentlySecure()) {
            AsyncTask.execute(runnable);
            return;
        }
        boolean dismissShade = (TextUtils.isEmpty(this.mRightButtonStr) || ((TunerService) Dependency.get(TunerService.class)).getValue(LockscreenFragment.LOCKSCREEN_RIGHT_UNLOCK, 1) == 0) ? false : true;
        this.mStatusBar.executeRunnableDismissingKeyguard(runnable, null, dismissShade, false, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean canLaunchVoiceAssist() {
        return this.mAssistManager.canVoiceAssistBeLaunchedFromKeyguard();
    }

    private void launchPhone() {
        final TelecomManager tm = TelecomManager.from(this.mContext);
        if (tm.isInCall()) {
            AsyncTask.execute(new Runnable() { // from class: com.android.systemui.statusbar.phone.KeyguardBottomAreaView.6
                @Override // java.lang.Runnable
                public void run() {
                    tm.showInCallScreen(false);
                }
            });
            return;
        }
        boolean z = true;
        boolean dismissShade = (TextUtils.isEmpty(this.mLeftButtonStr) || ((TunerService) Dependency.get(TunerService.class)).getValue(LockscreenFragment.LOCKSCREEN_LEFT_UNLOCK, 1) == 0) ? false : false;
        this.mActivityStarter.startActivity(this.mLeftButton.getIntent(), dismissShade);
    }

    @Override // android.view.View
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (changedView == this && visibility == 0) {
            updateCameraVisibility();
        }
    }

    public KeyguardAffordanceView getLeftView() {
        return this.mLeftAffordanceView;
    }

    public KeyguardAffordanceView getRightView() {
        return this.mRightAffordanceView;
    }

    public View getLeftPreview() {
        return this.mLeftPreview;
    }

    public View getRightPreview() {
        return this.mCameraPreview;
    }

    public View getIndicationArea() {
        return this.mIndicationArea;
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override // com.android.systemui.statusbar.phone.UnlockMethodCache.OnUnlockMethodChangedListener
    public void onUnlockMethodStateChanged() {
        updateCameraVisibility();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void inflateCameraPreview() {
        View previewBefore = this.mCameraPreview;
        boolean visibleBefore = false;
        if (previewBefore != null) {
            this.mPreviewContainer.removeView(previewBefore);
            visibleBefore = previewBefore.getVisibility() == 0;
        }
        this.mCameraPreview = this.mPreviewInflater.inflatePreview(getCameraIntent());
        View view = this.mCameraPreview;
        if (view != null) {
            this.mPreviewContainer.addView(view);
            this.mCameraPreview.setVisibility(visibleBefore ? 0 : 4);
        }
        KeyguardAffordanceHelper keyguardAffordanceHelper = this.mAffordanceHelper;
        if (keyguardAffordanceHelper != null) {
            keyguardAffordanceHelper.updatePreviews();
        }
    }

    private void updateLeftPreview() {
        View previewBefore = this.mLeftPreview;
        if (previewBefore != null) {
            this.mPreviewContainer.removeView(previewBefore);
        }
        ComponentName voiceInteractorComponentName = this.mAssistManager.getVoiceInteractorComponentName();
        if (this.mLeftIsVoiceAssist && voiceInteractorComponentName != null) {
            this.mLeftPreview = this.mPreviewInflater.inflatePreviewFromService(voiceInteractorComponentName);
        } else {
            this.mLeftPreview = this.mPreviewInflater.inflatePreview(this.mLeftButton.getIntent());
        }
        View view = this.mLeftPreview;
        if (view != null) {
            this.mPreviewContainer.addView(view);
            this.mLeftPreview.setVisibility(4);
        }
        KeyguardAffordanceHelper keyguardAffordanceHelper = this.mAffordanceHelper;
        if (keyguardAffordanceHelper != null) {
            keyguardAffordanceHelper.updatePreviews();
        }
    }

    public void startFinishDozeAnimation() {
        long delay = 0;
        if (this.mLeftAffordanceView.getVisibility() == 0) {
            startFinishDozeAnimationElement(this.mLeftAffordanceView, 0L);
            delay = 0 + 48;
        }
        if (this.mRightAffordanceView.getVisibility() == 0) {
            startFinishDozeAnimationElement(this.mRightAffordanceView, delay);
        }
    }

    private void startFinishDozeAnimationElement(View element, long delay) {
        element.setAlpha(0.0f);
        element.setTranslationY(element.getHeight() / 2);
        element.animate().alpha(1.0f).translationY(0.0f).setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN).setStartDelay(delay).setDuration(250L);
    }

    public void updateLeftAffordance() {
        updateLeftAffordanceIcon();
        updateLeftPreview();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: setRightButton */
    public void lambda$onAttachedToWindow$2$KeyguardBottomAreaView(IntentButtonProvider.IntentButton button) {
        this.mRightButton = button;
        updateRightAffordanceIcon();
        updateCameraVisibility();
        inflateCameraPreview();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: setLeftButton */
    public void lambda$onAttachedToWindow$5$KeyguardBottomAreaView(IntentButtonProvider.IntentButton button) {
        this.mLeftButton = button;
        if (!(this.mLeftButton instanceof DefaultLeftButton)) {
            this.mLeftIsVoiceAssist = false;
        }
        updateLeftAffordance();
    }

    public void setDozing(boolean dozing, boolean animate) {
        this.mDozing = dozing;
        updateCameraVisibility();
        updateLeftAffordanceIcon();
        if (dozing) {
            this.mOverlayContainer.setVisibility(4);
            return;
        }
        this.mOverlayContainer.setVisibility(0);
        if (animate) {
            startFinishDozeAnimation();
        }
    }

    public void dozeTimeTick() {
        int burnInYOffset = BurnInHelperKt.getBurnInOffset(this.mBurnInYOffset * 2, false) - this.mBurnInYOffset;
        this.mIndicationArea.setTranslationY(burnInYOffset * this.mDarkAmount);
    }

    public void setAntiBurnInOffsetX(int burnInXOffset) {
        if (this.mBurnInXOffset == burnInXOffset) {
            return;
        }
        this.mBurnInXOffset = burnInXOffset;
        this.mIndicationArea.setTranslationX(burnInXOffset);
    }

    public void setAffordanceAlpha(float alpha) {
        this.mLeftAffordanceView.setAlpha(alpha);
        this.mRightAffordanceView.setAlpha(alpha);
        this.mIndicationArea.setAlpha(alpha);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class DefaultLeftButton implements IntentButtonProvider.IntentButton {
        private IntentButtonProvider.IntentButton.IconState mIconState;

        private DefaultLeftButton() {
            this.mIconState = new IntentButtonProvider.IntentButton.IconState();
        }

        @Override // com.android.systemui.plugins.IntentButtonProvider.IntentButton
        public IntentButtonProvider.IntentButton.IconState getIcon() {
            KeyguardBottomAreaView keyguardBottomAreaView = KeyguardBottomAreaView.this;
            keyguardBottomAreaView.mLeftIsVoiceAssist = keyguardBottomAreaView.canLaunchVoiceAssist();
            boolean showAffordance = KeyguardBottomAreaView.this.getResources().getBoolean(R.bool.config_keyguardShowLeftAffordance);
            boolean z = true;
            if (KeyguardBottomAreaView.this.mLeftIsVoiceAssist) {
                IntentButtonProvider.IntentButton.IconState iconState = this.mIconState;
                if (!KeyguardBottomAreaView.this.mUserSetupComplete || !showAffordance) {
                    z = false;
                }
                iconState.isVisible = z;
                if (KeyguardBottomAreaView.this.mLeftAssistIcon == null) {
                    this.mIconState.drawable = KeyguardBottomAreaView.this.mContext.getDrawable(R.drawable.ic_mic_26dp);
                } else {
                    this.mIconState.drawable = KeyguardBottomAreaView.this.mLeftAssistIcon;
                }
                this.mIconState.contentDescription = KeyguardBottomAreaView.this.mContext.getString(R.string.accessibility_voice_assist_button);
            } else {
                IntentButtonProvider.IntentButton.IconState iconState2 = this.mIconState;
                if (!KeyguardBottomAreaView.this.mUserSetupComplete || !showAffordance || !KeyguardBottomAreaView.this.isPhoneVisible()) {
                    z = false;
                }
                iconState2.isVisible = z;
                this.mIconState.drawable = KeyguardBottomAreaView.this.mContext.getDrawable(17302773);
                this.mIconState.contentDescription = KeyguardBottomAreaView.this.mContext.getString(R.string.accessibility_phone_button);
            }
            return this.mIconState;
        }

        @Override // com.android.systemui.plugins.IntentButtonProvider.IntentButton
        public Intent getIntent() {
            return KeyguardBottomAreaView.PHONE_INTENT;
        }
    }

    /* loaded from: classes21.dex */
    private class DefaultRightButton implements IntentButtonProvider.IntentButton {
        private IntentButtonProvider.IntentButton.IconState mIconState;

        private DefaultRightButton() {
            this.mIconState = new IntentButtonProvider.IntentButton.IconState();
        }

        @Override // com.android.systemui.plugins.IntentButtonProvider.IntentButton
        public IntentButtonProvider.IntentButton.IconState getIcon() {
            ResolveInfo resolved = KeyguardBottomAreaView.this.resolveCameraIntent();
            boolean z = true;
            boolean isCameraDisabled = (KeyguardBottomAreaView.this.mStatusBar == null || KeyguardBottomAreaView.this.mStatusBar.isCameraAllowedByAdmin()) ? false : true;
            IntentButtonProvider.IntentButton.IconState iconState = this.mIconState;
            if (isCameraDisabled || resolved == null || !KeyguardBottomAreaView.this.getResources().getBoolean(R.bool.config_keyguardShowCameraAffordance) || !KeyguardBottomAreaView.this.mUserSetupComplete) {
                z = false;
            }
            iconState.isVisible = z;
            this.mIconState.drawable = KeyguardBottomAreaView.this.mContext.getDrawable(R.drawable.ic_camera_alt_24dp);
            this.mIconState.contentDescription = KeyguardBottomAreaView.this.mContext.getString(R.string.accessibility_camera_button);
            return this.mIconState;
        }

        @Override // com.android.systemui.plugins.IntentButtonProvider.IntentButton
        public Intent getIntent() {
            KeyguardUpdateMonitor updateMonitor = KeyguardUpdateMonitor.getInstance(KeyguardBottomAreaView.this.mContext);
            boolean canSkipBouncer = updateMonitor.getUserCanSkipBouncer(KeyguardUpdateMonitor.getCurrentUser());
            boolean secure = KeyguardBottomAreaView.this.mLockPatternUtils.isSecure(KeyguardUpdateMonitor.getCurrentUser());
            return (!secure || canSkipBouncer) ? KeyguardBottomAreaView.INSECURE_CAMERA_INTENT : KeyguardBottomAreaView.SECURE_CAMERA_INTENT;
        }
    }

    @Override // android.view.View
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        int bottom = insets.getDisplayCutout() != null ? insets.getDisplayCutout().getSafeInsetBottom() : 0;
        if (isPaddingRelative()) {
            setPaddingRelative(getPaddingStart(), getPaddingTop(), getPaddingEnd(), bottom);
        } else {
            setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), bottom);
        }
        return insets;
    }
}
