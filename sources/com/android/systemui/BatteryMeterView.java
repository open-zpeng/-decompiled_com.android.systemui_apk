package com.android.systemui;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.StyleRes;
import androidx.lifecycle.LifecycleOwner;
import com.android.settingslib.graph.ThemedBatteryDrawable;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.settings.CurrentUserTracker;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.SysuiLifecycle;
import com.android.systemui.util.Utils;
import com.xiaopeng.libtheme.ThemeManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.NumberFormat;
/* loaded from: classes21.dex */
public class BatteryMeterView extends LinearLayout implements BatteryController.BatteryStateChangeCallback, TunerService.Tunable, DarkIconDispatcher.DarkReceiver, ConfigurationController.ConfigurationListener {
    public static final int MODE_DEFAULT = 0;
    public static final int MODE_ESTIMATE = 3;
    public static final int MODE_OFF = 2;
    public static final int MODE_ON = 1;
    private BatteryController mBatteryController;
    private final ImageView mBatteryIconView;
    private TextView mBatteryPercentView;
    private boolean mCharging;
    private final ThemedBatteryDrawable mDrawable;
    private DualToneHandler mDualToneHandler;
    private boolean mForceShowPercent;
    private boolean mIgnoreTunerUpdates;
    private boolean mIsSubscribedForTunerUpdates;
    private int mLevel;
    private int mNonAdaptedBackgroundColor;
    private int mNonAdaptedForegroundColor;
    private int mNonAdaptedSingleToneColor;
    @StyleRes
    private final int mPercentageStyleId;
    private SettingObserver mSettingObserver;
    private boolean mShowPercentAvailable;
    private int mShowPercentMode;
    private final String mSlotBattery;
    private int mTextColor;
    private boolean mUseWallpaperTextColors;
    private int mUser;
    private final CurrentUserTracker mUserTracker;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes21.dex */
    public @interface BatteryPercentMode {
    }

    public BatteryMeterView(Context context) {
        this(context, null, 0);
    }

    public BatteryMeterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BatteryMeterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mShowPercentMode = 0;
        setOrientation(0);
        setGravity(8388627);
        TypedArray atts = context.obtainStyledAttributes(attrs, R.styleable.BatteryMeterView, defStyle, 0);
        int frameColor = atts.getColor(R.styleable.BatteryMeterView_frameColor, context.getColor(R.color.meter_background_color));
        this.mPercentageStyleId = atts.getResourceId(R.styleable.BatteryMeterView_textAppearance, 0);
        this.mDrawable = new ThemedBatteryDrawable(context, frameColor);
        atts.recycle();
        this.mSettingObserver = new SettingObserver(new Handler(context.getMainLooper()));
        this.mShowPercentAvailable = context.getResources().getBoolean(17891373);
        addOnAttachStateChangeListener(new Utils.DisableStateTracker(0, 2));
        setupLayoutTransition();
        this.mSlotBattery = context.getString(17041080);
        this.mBatteryIconView = new ImageView(context);
        this.mBatteryIconView.setImageDrawable(this.mDrawable);
        ViewGroup.MarginLayoutParams mlp = new ViewGroup.MarginLayoutParams(getResources().getDimensionPixelSize(R.dimen.status_bar_battery_icon_width), getResources().getDimensionPixelSize(R.dimen.status_bar_battery_icon_height));
        mlp.setMargins(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.battery_margin_bottom));
        addView(this.mBatteryIconView, mlp);
        updateShowPercent();
        this.mDualToneHandler = new DualToneHandler(context);
        onDarkChanged(new Rect(), 0.0f, -1);
        this.mUserTracker = new CurrentUserTracker(this.mContext) { // from class: com.android.systemui.BatteryMeterView.1
            @Override // com.android.systemui.settings.CurrentUserTracker
            public void onUserSwitched(int newUserId) {
                BatteryMeterView.this.mUser = newUserId;
                BatteryMeterView.this.getContext().getContentResolver().unregisterContentObserver(BatteryMeterView.this.mSettingObserver);
                BatteryMeterView.this.getContext().getContentResolver().registerContentObserver(Settings.System.getUriFor("status_bar_show_battery_percent"), false, BatteryMeterView.this.mSettingObserver, newUserId);
                BatteryMeterView.this.updateShowPercent();
            }
        };
        setClipChildren(false);
        setClipToPadding(false);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).observe(SysuiLifecycle.viewAttachLifecycle(this), (LifecycleOwner) this);
    }

    private void setupLayoutTransition() {
        LayoutTransition transition = new LayoutTransition();
        transition.setDuration(200L);
        ObjectAnimator appearAnimator = ObjectAnimator.ofFloat((Object) null, ThemeManager.AttributeSet.ALPHA, 0.0f, 1.0f);
        transition.setAnimator(2, appearAnimator);
        transition.setInterpolator(2, Interpolators.ALPHA_IN);
        ObjectAnimator disappearAnimator = ObjectAnimator.ofFloat((Object) null, ThemeManager.AttributeSet.ALPHA, 1.0f, 0.0f);
        transition.setInterpolator(3, Interpolators.ALPHA_OUT);
        transition.setAnimator(3, disappearAnimator);
        setLayoutTransition(transition);
    }

    public void setForceShowPercent(boolean show) {
        setPercentShowMode(show ? 1 : 0);
    }

    public void setPercentShowMode(int mode) {
        this.mShowPercentMode = mode;
        updateShowPercent();
    }

    public void setIgnoreTunerUpdates(boolean ignore) {
        this.mIgnoreTunerUpdates = ignore;
        updateTunerSubscription();
    }

    private void updateTunerSubscription() {
        if (this.mIgnoreTunerUpdates) {
            unsubscribeFromTunerUpdates();
        } else {
            subscribeForTunerUpdates();
        }
    }

    private void subscribeForTunerUpdates() {
        if (this.mIsSubscribedForTunerUpdates || this.mIgnoreTunerUpdates) {
            return;
        }
        ((TunerService) Dependency.get(TunerService.class)).addTunable(this, StatusBarIconController.ICON_BLACKLIST);
        this.mIsSubscribedForTunerUpdates = true;
    }

    private void unsubscribeFromTunerUpdates() {
        if (!this.mIsSubscribedForTunerUpdates) {
            return;
        }
        ((TunerService) Dependency.get(TunerService.class)).removeTunable(this);
        this.mIsSubscribedForTunerUpdates = false;
    }

    public void useWallpaperTextColor(boolean shouldUseWallpaperTextColor) {
        if (shouldUseWallpaperTextColor == this.mUseWallpaperTextColors) {
            return;
        }
        this.mUseWallpaperTextColors = shouldUseWallpaperTextColor;
        if (this.mUseWallpaperTextColors) {
            updateColors(com.android.settingslib.Utils.getColorAttrDefaultColor(this.mContext, R.attr.wallpaperTextColor), com.android.settingslib.Utils.getColorAttrDefaultColor(this.mContext, R.attr.wallpaperTextColorSecondary), com.android.settingslib.Utils.getColorAttrDefaultColor(this.mContext, R.attr.wallpaperTextColor));
        } else {
            updateColors(this.mNonAdaptedForegroundColor, this.mNonAdaptedBackgroundColor, this.mNonAdaptedSingleToneColor);
        }
    }

    public void setColorsFromContext(Context context) {
        if (context == null) {
            return;
        }
        this.mDualToneHandler.setColorsFromContext(context);
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override // com.android.systemui.tuner.TunerService.Tunable
    public void onTuningChanged(String key, String newValue) {
        if (StatusBarIconController.ICON_BLACKLIST.equals(key)) {
            StatusBarIconController.getIconBlacklist(newValue);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mBatteryController = (BatteryController) Dependency.get(BatteryController.class);
        this.mBatteryController.addCallback(this);
        this.mUser = ActivityManager.getCurrentUser();
        getContext().getContentResolver().registerContentObserver(Settings.System.getUriFor("status_bar_show_battery_percent"), false, this.mSettingObserver, this.mUser);
        getContext().getContentResolver().registerContentObserver(Settings.Global.getUriFor("battery_estimates_last_update_time"), false, this.mSettingObserver);
        updateShowPercent();
        subscribeForTunerUpdates();
        this.mUserTracker.startTracking();
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mUserTracker.stopTracking();
        this.mBatteryController.removeCallback(this);
        getContext().getContentResolver().unregisterContentObserver(this.mSettingObserver);
        unsubscribeFromTunerUpdates();
    }

    @Override // com.android.systemui.statusbar.policy.BatteryController.BatteryStateChangeCallback
    public void onBatteryLevelChanged(int level, boolean pluggedIn, boolean charging) {
        this.mDrawable.setCharging(pluggedIn);
        this.mDrawable.setBatteryLevel(level);
        this.mCharging = pluggedIn;
        this.mLevel = level;
        updatePercentText();
    }

    @Override // com.android.systemui.statusbar.policy.BatteryController.BatteryStateChangeCallback
    public void onPowerSaveChanged(boolean isPowerSave) {
        this.mDrawable.setPowerSaveEnabled(isPowerSave);
    }

    private TextView loadPercentView() {
        return (TextView) LayoutInflater.from(getContext()).inflate(R.layout.battery_percentage_view, (ViewGroup) null);
    }

    public void updatePercentView() {
        TextView textView = this.mBatteryPercentView;
        if (textView != null) {
            removeView(textView);
            this.mBatteryPercentView = null;
        }
        updateShowPercent();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePercentText() {
        BatteryController batteryController = this.mBatteryController;
        if (batteryController == null) {
            return;
        }
        if (this.mBatteryPercentView != null) {
            if (this.mShowPercentMode == 3 && !this.mCharging) {
                batteryController.getEstimatedTimeRemainingString(new BatteryController.EstimateFetchCompletion() { // from class: com.android.systemui.-$$Lambda$BatteryMeterView$yZDQalqWJG2q_49RDLUqR8bhWwM
                    @Override // com.android.systemui.statusbar.policy.BatteryController.EstimateFetchCompletion
                    public final void onBatteryRemainingEstimateRetrieved(String str) {
                        BatteryMeterView.this.lambda$updatePercentText$0$BatteryMeterView(str);
                    }
                });
                return;
            } else {
                setPercentTextAtCurrentLevel();
                return;
            }
        }
        setContentDescription(getContext().getString(this.mCharging ? R.string.accessibility_battery_level_charging : R.string.accessibility_battery_level, Integer.valueOf(this.mLevel)));
    }

    public /* synthetic */ void lambda$updatePercentText$0$BatteryMeterView(String estimate) {
        if (estimate != null) {
            this.mBatteryPercentView.setText(estimate);
            setContentDescription(getContext().getString(R.string.accessibility_battery_level_with_estimate, Integer.valueOf(this.mLevel), estimate));
            return;
        }
        setPercentTextAtCurrentLevel();
    }

    private void setPercentTextAtCurrentLevel() {
        this.mBatteryPercentView.setText(NumberFormat.getPercentInstance().format(this.mLevel / 100.0f));
        setContentDescription(getContext().getString(this.mCharging ? R.string.accessibility_battery_level_charging : R.string.accessibility_battery_level, Integer.valueOf(this.mLevel)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateShowPercent() {
        int i;
        boolean showing = this.mBatteryPercentView != null;
        boolean systemSetting = Settings.System.getIntForUser(getContext().getContentResolver(), "status_bar_show_battery_percent", 0, this.mUser) != 0;
        if ((this.mShowPercentAvailable && systemSetting && this.mShowPercentMode != 2) || (i = this.mShowPercentMode) == 1 || i == 3) {
            if (!showing) {
                this.mBatteryPercentView = loadPercentView();
                int i2 = this.mPercentageStyleId;
                if (i2 != 0) {
                    this.mBatteryPercentView.setTextAppearance(i2);
                }
                int i3 = this.mTextColor;
                if (i3 != 0) {
                    this.mBatteryPercentView.setTextColor(i3);
                }
                updatePercentText();
                addView(this.mBatteryPercentView, new ViewGroup.LayoutParams(-2, -1));
            }
        } else if (showing) {
            removeView(this.mBatteryPercentView);
            this.mBatteryPercentView = null;
        }
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onDensityOrFontScaleChanged() {
        scaleBatteryMeterViews();
    }

    private void scaleBatteryMeterViews() {
        Resources res = getContext().getResources();
        TypedValue typedValue = new TypedValue();
        res.getValue(R.dimen.status_bar_icon_scale_factor, typedValue, true);
        float iconScaleFactor = typedValue.getFloat();
        int batteryHeight = res.getDimensionPixelSize(R.dimen.status_bar_battery_icon_height);
        int batteryWidth = res.getDimensionPixelSize(R.dimen.status_bar_battery_icon_width);
        int marginBottom = res.getDimensionPixelSize(R.dimen.battery_margin_bottom);
        LinearLayout.LayoutParams scaledLayoutParams = new LinearLayout.LayoutParams((int) (batteryWidth * iconScaleFactor), (int) (batteryHeight * iconScaleFactor));
        scaledLayoutParams.setMargins(0, 0, 0, marginBottom);
        this.mBatteryIconView.setLayoutParams(scaledLayoutParams);
    }

    @Override // com.android.systemui.plugins.DarkIconDispatcher.DarkReceiver
    public void onDarkChanged(Rect area, float darkIntensity, int tint) {
        float intensity = DarkIconDispatcher.isInArea(area, this) ? darkIntensity : 0.0f;
        this.mNonAdaptedSingleToneColor = this.mDualToneHandler.getSingleColor(intensity);
        this.mNonAdaptedForegroundColor = this.mDualToneHandler.getFillColor(intensity);
        this.mNonAdaptedBackgroundColor = this.mDualToneHandler.getBackgroundColor(intensity);
        if (!this.mUseWallpaperTextColors) {
            updateColors(this.mNonAdaptedForegroundColor, this.mNonAdaptedBackgroundColor, this.mNonAdaptedSingleToneColor);
        }
    }

    private void updateColors(int foregroundColor, int backgroundColor, int singleToneColor) {
        this.mDrawable.setColors(foregroundColor, backgroundColor, singleToneColor);
        this.mTextColor = singleToneColor;
        TextView textView = this.mBatteryPercentView;
        if (textView != null) {
            textView.setTextColor(singleToneColor);
        }
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        String powerSave;
        if (this.mDrawable == null) {
            powerSave = null;
        } else {
            powerSave = this.mDrawable.getPowerSaveEnabled() + "";
        }
        TextView textView = this.mBatteryPercentView;
        CharSequence percent = textView != null ? textView.getText() : null;
        pw.println("  BatteryMeterView:");
        pw.println("    mDrawable.getPowerSave: " + powerSave);
        pw.println("    mBatteryPercentView.getText(): " + ((Object) percent));
        pw.println("    mTextColor: #" + Integer.toHexString(this.mTextColor));
        pw.println("    mLevel: " + this.mLevel);
        pw.println("    mForceShowPercent: " + this.mForceShowPercent);
    }

    /* loaded from: classes21.dex */
    private final class SettingObserver extends ContentObserver {
        public SettingObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            BatteryMeterView.this.updateShowPercent();
            if (TextUtils.equals(uri.getLastPathSegment(), "battery_estimates_last_update_time")) {
                BatteryMeterView.this.updatePercentText();
            }
        }
    }
}
