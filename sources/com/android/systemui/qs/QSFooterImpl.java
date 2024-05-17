package com.android.systemui.qs;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserManager;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.settingslib.Utils;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.settingslib.drawable.UserIconDrawable;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.qs.TouchAnimator;
import com.android.systemui.statusbar.phone.MultiUserSwitch;
import com.android.systemui.statusbar.phone.SettingsButton;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.tuner.TunerService;
import com.badlogic.gdx.net.HttpStatus;
import com.xiaopeng.libtheme.ThemeManager;
import javax.inject.Inject;
import javax.inject.Named;
/* loaded from: classes21.dex */
public class QSFooterImpl extends FrameLayout implements QSFooter, View.OnClickListener, UserInfoController.OnUserInfoChangedListener {
    private static final String TAG = "QSFooterImpl";
    private View mActionsContainer;
    private final ActivityStarter mActivityStarter;
    private final ContentObserver mDeveloperSettingsObserver;
    private final DeviceProvisionedController mDeviceProvisionedController;
    private View mDragHandle;
    protected View mEdit;
    protected View mEditContainer;
    private View.OnClickListener mExpandClickListener;
    private boolean mExpanded;
    private float mExpansionAmount;
    protected TouchAnimator mFooterAnimator;
    private boolean mListening;
    private ImageView mMultiUserAvatar;
    protected MultiUserSwitch mMultiUserSwitch;
    private PageIndicator mPageIndicator;
    private boolean mQsDisabled;
    private QSPanel mQsPanel;
    private SettingsButton mSettingsButton;
    private TouchAnimator mSettingsCogAnimator;
    protected View mSettingsContainer;
    private final UserInfoController mUserInfoController;

    @Inject
    public QSFooterImpl(@Named("view_context") Context context, AttributeSet attrs, ActivityStarter activityStarter, UserInfoController userInfoController, DeviceProvisionedController deviceProvisionedController) {
        super(context, attrs);
        this.mDeveloperSettingsObserver = new ContentObserver(new Handler(this.mContext.getMainLooper())) { // from class: com.android.systemui.qs.QSFooterImpl.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                QSFooterImpl.this.setBuildText();
            }
        };
        this.mActivityStarter = activityStarter;
        this.mUserInfoController = userInfoController;
        this.mDeviceProvisionedController = deviceProvisionedController;
    }

    @VisibleForTesting
    public QSFooterImpl(Context context, AttributeSet attrs) {
        this(context, attrs, (ActivityStarter) Dependency.get(ActivityStarter.class), (UserInfoController) Dependency.get(UserInfoController.class), (DeviceProvisionedController) Dependency.get(DeviceProvisionedController.class));
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mEdit = findViewById(16908291);
        this.mEdit.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.qs.-$$Lambda$QSFooterImpl$3QBg0cgvu2IRpUDq3RvpL257x8c
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                QSFooterImpl.this.lambda$onFinishInflate$1$QSFooterImpl(view);
            }
        });
        this.mPageIndicator = (PageIndicator) findViewById(R.id.footer_page_indicator);
        this.mSettingsButton = (SettingsButton) findViewById(R.id.settings_button);
        this.mSettingsContainer = findViewById(R.id.settings_button_container);
        this.mSettingsButton.setOnClickListener(this);
        this.mMultiUserSwitch = (MultiUserSwitch) findViewById(R.id.multi_user_switch);
        this.mMultiUserAvatar = (ImageView) this.mMultiUserSwitch.findViewById(R.id.multi_user_avatar);
        this.mDragHandle = findViewById(R.id.qs_drag_handle_view);
        this.mActionsContainer = findViewById(R.id.qs_footer_actions_container);
        this.mEditContainer = findViewById(R.id.qs_footer_actions_edit_container);
        ((RippleDrawable) this.mSettingsButton.getBackground()).setForceSoftware(true);
        updateResources();
        addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: com.android.systemui.qs.-$$Lambda$QSFooterImpl$GSAG9gEF755NpvH4khVvAa75uPs
            @Override // android.view.View.OnLayoutChangeListener
            public final void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                QSFooterImpl.this.lambda$onFinishInflate$2$QSFooterImpl(view, i, i2, i3, i4, i5, i6, i7, i8);
            }
        });
        setImportantForAccessibility(1);
        updateEverything();
        setBuildText();
    }

    public /* synthetic */ void lambda$onFinishInflate$1$QSFooterImpl(final View view) {
        this.mActivityStarter.postQSRunnableDismissingKeyguard(new Runnable() { // from class: com.android.systemui.qs.-$$Lambda$QSFooterImpl$BPGtDaa2eU-tTCTVDpjGrKOXYOs
            @Override // java.lang.Runnable
            public final void run() {
                QSFooterImpl.this.lambda$onFinishInflate$0$QSFooterImpl(view);
            }
        });
    }

    public /* synthetic */ void lambda$onFinishInflate$0$QSFooterImpl(View view) {
        this.mQsPanel.showEdit(view);
    }

    public /* synthetic */ void lambda$onFinishInflate$2$QSFooterImpl(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        updateAnimator(right - left);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setBuildText() {
        TextView v = (TextView) findViewById(R.id.build);
        if (v == null) {
            return;
        }
        if (DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(this.mContext)) {
            v.setText(this.mContext.getString(17039623, Build.VERSION.RELEASE, Build.ID));
            v.setVisibility(0);
            return;
        }
        v.setVisibility(8);
    }

    private void updateAnimator(int width) {
        int numTiles = QuickQSPanel.getNumQuickTiles(this.mContext);
        int size = this.mContext.getResources().getDimensionPixelSize(R.dimen.qs_quick_tile_size) - this.mContext.getResources().getDimensionPixelSize(R.dimen.qs_quick_tile_padding);
        int remaining = (width - (numTiles * size)) / (numTiles - 1);
        int defSpace = this.mContext.getResources().getDimensionPixelOffset(R.dimen.default_gear_space);
        TouchAnimator.Builder builder = new TouchAnimator.Builder();
        View view = this.mSettingsContainer;
        float[] fArr = new float[2];
        fArr[0] = isLayoutRtl() ? remaining - defSpace : -(remaining - defSpace);
        fArr[1] = 0.0f;
        this.mSettingsCogAnimator = builder.addFloat(view, "translationX", fArr).addFloat(this.mSettingsButton, "rotation", -120.0f, 0.0f).build();
        setExpansion(this.mExpansionAmount);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateResources();
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);
        updateResources();
    }

    private void updateResources() {
        updateFooterAnimator();
    }

    private void updateFooterAnimator() {
        this.mFooterAnimator = createFooterAnimator();
    }

    @Nullable
    private TouchAnimator createFooterAnimator() {
        return new TouchAnimator.Builder().addFloat(this.mActionsContainer, ThemeManager.AttributeSet.ALPHA, 0.0f, 1.0f).addFloat(this.mEditContainer, ThemeManager.AttributeSet.ALPHA, 0.0f, 1.0f).addFloat(this.mDragHandle, ThemeManager.AttributeSet.ALPHA, 1.0f, 0.0f, 0.0f).addFloat(this.mPageIndicator, ThemeManager.AttributeSet.ALPHA, 0.0f, 1.0f).setStartDelay(0.15f).build();
    }

    @Override // com.android.systemui.qs.QSFooter
    public void setKeyguardShowing(boolean keyguardShowing) {
        setExpansion(this.mExpansionAmount);
    }

    @Override // com.android.systemui.qs.QSFooter
    public void setExpandClickListener(View.OnClickListener onClickListener) {
        this.mExpandClickListener = onClickListener;
    }

    @Override // com.android.systemui.qs.QSFooter
    public void setExpanded(boolean expanded) {
        if (this.mExpanded == expanded) {
            return;
        }
        this.mExpanded = expanded;
        updateEverything();
    }

    @Override // com.android.systemui.qs.QSFooter
    public void setExpansion(float headerExpansionFraction) {
        this.mExpansionAmount = headerExpansionFraction;
        TouchAnimator touchAnimator = this.mSettingsCogAnimator;
        if (touchAnimator != null) {
            touchAnimator.setPosition(headerExpansionFraction);
        }
        TouchAnimator touchAnimator2 = this.mFooterAnimator;
        if (touchAnimator2 != null) {
            touchAnimator2.setPosition(headerExpansionFraction);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("development_settings_enabled"), false, this.mDeveloperSettingsObserver, -1);
    }

    @Override // android.view.ViewGroup, android.view.View
    @VisibleForTesting
    public void onDetachedFromWindow() {
        setListening(false);
        this.mContext.getContentResolver().unregisterContentObserver(this.mDeveloperSettingsObserver);
        super.onDetachedFromWindow();
    }

    @Override // com.android.systemui.qs.QSFooter
    public void setListening(boolean listening) {
        if (listening == this.mListening) {
            return;
        }
        this.mListening = listening;
        updateListeners();
    }

    @Override // android.view.View
    public boolean performAccessibilityAction(int action, Bundle arguments) {
        View.OnClickListener onClickListener;
        if (action == 262144 && (onClickListener = this.mExpandClickListener) != null) {
            onClickListener.onClick(null);
            return true;
        }
        return super.performAccessibilityAction(action, arguments);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_EXPAND);
    }

    @Override // com.android.systemui.qs.QSFooter
    public void disable(int state1, int state2, boolean animate) {
        boolean disabled = (state2 & 1) != 0;
        if (disabled == this.mQsDisabled) {
            return;
        }
        this.mQsDisabled = disabled;
        updateEverything();
    }

    public void updateEverything() {
        post(new Runnable() { // from class: com.android.systemui.qs.-$$Lambda$QSFooterImpl$FK1In3z-Y3ppRrcllMggnruYa_s
            @Override // java.lang.Runnable
            public final void run() {
                QSFooterImpl.this.lambda$updateEverything$3$QSFooterImpl();
            }
        });
    }

    public /* synthetic */ void lambda$updateEverything$3$QSFooterImpl() {
        updateVisibilities();
        updateClickabilities();
        setClickable(false);
    }

    private void updateClickabilities() {
        MultiUserSwitch multiUserSwitch = this.mMultiUserSwitch;
        multiUserSwitch.setClickable(multiUserSwitch.getVisibility() == 0);
        View view = this.mEdit;
        view.setClickable(view.getVisibility() == 0);
        SettingsButton settingsButton = this.mSettingsButton;
        settingsButton.setClickable(settingsButton.getVisibility() == 0);
    }

    private void updateVisibilities() {
        int i = 0;
        this.mSettingsContainer.setVisibility(this.mQsDisabled ? 8 : 0);
        this.mSettingsContainer.findViewById(R.id.tuner_icon).setVisibility(TunerService.isTunerEnabled(this.mContext) ? 0 : 4);
        boolean isDemo = UserManager.isDeviceInDemoMode(this.mContext);
        this.mMultiUserSwitch.setVisibility(showUserSwitcher() ? 0 : 4);
        this.mEditContainer.setVisibility((isDemo || !this.mExpanded) ? 4 : 0);
        SettingsButton settingsButton = this.mSettingsButton;
        if (isDemo || !this.mExpanded) {
            i = 4;
        }
        settingsButton.setVisibility(i);
    }

    private boolean showUserSwitcher() {
        return this.mExpanded && this.mMultiUserSwitch.isMultiUserEnabled();
    }

    private void updateListeners() {
        if (this.mListening) {
            this.mUserInfoController.addCallback(this);
        } else {
            this.mUserInfoController.removeCallback(this);
        }
    }

    @Override // com.android.systemui.qs.QSFooter
    public void setQSPanel(QSPanel qsPanel) {
        this.mQsPanel = qsPanel;
        if (this.mQsPanel != null) {
            this.mMultiUserSwitch.setQsPanel(qsPanel);
            this.mQsPanel.setFooterPageIndicator(this.mPageIndicator);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (this.mExpanded && v == this.mSettingsButton) {
            if (!this.mDeviceProvisionedController.isCurrentUserSetup()) {
                this.mActivityStarter.postQSRunnableDismissingKeyguard(new Runnable() { // from class: com.android.systemui.qs.-$$Lambda$QSFooterImpl$ORlOcuwnOcEc1bdhJcTagEFJfI4
                    @Override // java.lang.Runnable
                    public final void run() {
                        QSFooterImpl.lambda$onClick$4();
                    }
                });
                return;
            }
            MetricsLogger.action(this.mContext, this.mExpanded ? HttpStatus.SC_NOT_ACCEPTABLE : 490);
            if (this.mSettingsButton.isTunerClick()) {
                this.mActivityStarter.postQSRunnableDismissingKeyguard(new Runnable() { // from class: com.android.systemui.qs.-$$Lambda$QSFooterImpl$QqFCwKmpQEaqoIsbaA3_odDeJWo
                    @Override // java.lang.Runnable
                    public final void run() {
                        QSFooterImpl.this.lambda$onClick$6$QSFooterImpl();
                    }
                });
            } else {
                lambda$onClick$5$QSFooterImpl();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$onClick$4() {
    }

    public /* synthetic */ void lambda$onClick$6$QSFooterImpl() {
        if (TunerService.isTunerEnabled(this.mContext)) {
            TunerService.showResetRequest(this.mContext, new Runnable() { // from class: com.android.systemui.qs.-$$Lambda$QSFooterImpl$p6Eelc3uV5Rv_Va6Mn0QpjivHN4
                @Override // java.lang.Runnable
                public final void run() {
                    QSFooterImpl.this.lambda$onClick$5$QSFooterImpl();
                }
            });
        } else {
            Toast.makeText(getContext(), R.string.tuner_toast, 1).show();
            TunerService.setTunerEnabled(this.mContext, true);
        }
        lambda$onClick$5$QSFooterImpl();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: startSettingsActivity */
    public void lambda$onClick$5$QSFooterImpl() {
        this.mActivityStarter.startActivity(new Intent("android.settings.SETTINGS"), true);
    }

    @Override // com.android.systemui.statusbar.policy.UserInfoController.OnUserInfoChangedListener
    public void onUserInfoChanged(String name, Drawable picture, String userAccount) {
        if (picture != null && UserManager.get(this.mContext).isGuestUser(KeyguardUpdateMonitor.getCurrentUser()) && !(picture instanceof UserIconDrawable)) {
            picture = picture.getConstantState().newDrawable(this.mContext.getResources()).mutate();
            picture.setColorFilter(Utils.getColorAttrDefaultColor(this.mContext, 16842800), PorterDuff.Mode.SRC_IN);
        }
        this.mMultiUserAvatar.setImageDrawable(picture);
    }
}
