package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.hardware.display.AmbientDisplayConfiguration;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.util.MathUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.doze.AlwaysOnDisplayPolicy;
import com.android.systemui.tuner.TunerService;
import com.xiaopeng.systemui.controller.OsdController;
import java.io.PrintWriter;
/* loaded from: classes21.dex */
public class DozeParameters implements TunerService.Tunable, com.android.systemui.plugins.statusbar.DozeParameters {
    private static final int MAX_DURATION = 60000;
    private static DozeParameters sInstance;
    private final AlwaysOnDisplayPolicy mAlwaysOnPolicy;
    private final AmbientDisplayConfiguration mAmbientDisplayConfiguration;
    private final Context mContext;
    private boolean mControlScreenOffAnimation = !getDisplayNeedsBlanking();
    private boolean mDozeAlwaysOn;
    private final PowerManager mPowerManager;
    public static final boolean FORCE_NO_BLANKING = SystemProperties.getBoolean("debug.force_no_blanking", false);
    public static final boolean FORCE_BLANKING = SystemProperties.getBoolean("debug.force_blanking", false);

    public static DozeParameters getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DozeParameters(context);
        }
        return sInstance;
    }

    @VisibleForTesting
    protected DozeParameters(Context context) {
        this.mContext = context.getApplicationContext();
        this.mAmbientDisplayConfiguration = new AmbientDisplayConfiguration(this.mContext);
        this.mAlwaysOnPolicy = new AlwaysOnDisplayPolicy(this.mContext);
        this.mPowerManager = (PowerManager) this.mContext.getSystemService(PowerManager.class);
        this.mPowerManager.setDozeAfterScreenOff(!this.mControlScreenOffAnimation);
        ((TunerService) Dependency.get(TunerService.class)).addTunable(this, "doze_always_on", "accessibility_display_inversion_enabled");
    }

    public void dump(PrintWriter pw) {
        pw.println("  DozeParameters:");
        pw.print("    getDisplayStateSupported(): ");
        pw.println(getDisplayStateSupported());
        pw.print("    getPulseDuration(): ");
        pw.println(getPulseDuration());
        pw.print("    getPulseInDuration(): ");
        pw.println(getPulseInDuration());
        pw.print("    getPulseInVisibleDuration(): ");
        pw.println(getPulseVisibleDuration());
        pw.print("    getPulseOutDuration(): ");
        pw.println(getPulseOutDuration());
        pw.print("    getPulseOnSigMotion(): ");
        pw.println(getPulseOnSigMotion());
        pw.print("    getVibrateOnSigMotion(): ");
        pw.println(getVibrateOnSigMotion());
        pw.print("    getVibrateOnPickup(): ");
        pw.println(getVibrateOnPickup());
        pw.print("    getProxCheckBeforePulse(): ");
        pw.println(getProxCheckBeforePulse());
        pw.print("    getPickupVibrationThreshold(): ");
        pw.println(getPickupVibrationThreshold());
    }

    public boolean getDisplayStateSupported() {
        return getBoolean("doze.display.supported", R.bool.doze_display_state_supported);
    }

    public boolean getDozeSuspendDisplayStateSupported() {
        return this.mContext.getResources().getBoolean(R.bool.doze_suspend_display_state_supported);
    }

    public int getPulseDuration() {
        return getPulseInDuration() + getPulseVisibleDuration() + getPulseOutDuration();
    }

    public float getScreenBrightnessDoze() {
        return this.mContext.getResources().getInteger(17694882) / 255.0f;
    }

    public int getPulseInDuration() {
        return getInt("doze.pulse.duration.in", R.integer.doze_pulse_duration_in);
    }

    public int getPulseVisibleDuration() {
        return getInt("doze.pulse.duration.visible", R.integer.doze_pulse_duration_visible);
    }

    public int getPulseOutDuration() {
        return getInt("doze.pulse.duration.out", R.integer.doze_pulse_duration_out);
    }

    public boolean getPulseOnSigMotion() {
        return getBoolean("doze.pulse.sigmotion", R.bool.doze_pulse_on_significant_motion);
    }

    public boolean getVibrateOnSigMotion() {
        return SystemProperties.getBoolean("doze.vibrate.sigmotion", false);
    }

    public boolean getVibrateOnPickup() {
        return SystemProperties.getBoolean("doze.vibrate.pickup", false);
    }

    public boolean getProxCheckBeforePulse() {
        return getBoolean("doze.pulse.proxcheck", R.bool.doze_proximity_check_before_pulse);
    }

    public int getPickupVibrationThreshold() {
        return getInt("doze.pickup.vibration.threshold", R.integer.doze_pickup_vibration_threshold);
    }

    public long getWallpaperAodDuration() {
        if (shouldControlScreenOff()) {
            return OsdController.TN.DURATION_TIMEOUT_LONG;
        }
        return this.mAlwaysOnPolicy.wallpaperVisibilityDuration;
    }

    public long getWallpaperFadeOutDuration() {
        return this.mAlwaysOnPolicy.wallpaperFadeOutDuration;
    }

    public boolean getAlwaysOn() {
        return this.mDozeAlwaysOn;
    }

    public boolean getDisplayNeedsBlanking() {
        return FORCE_BLANKING || (!FORCE_NO_BLANKING && this.mContext.getResources().getBoolean(17891411));
    }

    @Override // com.android.systemui.plugins.statusbar.DozeParameters
    public boolean shouldControlScreenOff() {
        return this.mControlScreenOffAnimation;
    }

    public void setControlScreenOffAnimation(boolean controlScreenOffAnimation) {
        if (this.mControlScreenOffAnimation == controlScreenOffAnimation) {
            return;
        }
        this.mControlScreenOffAnimation = controlScreenOffAnimation;
        getPowerManager().setDozeAfterScreenOff(!controlScreenOffAnimation);
    }

    @VisibleForTesting
    protected PowerManager getPowerManager() {
        return this.mPowerManager;
    }

    private boolean getBoolean(String propName, int resId) {
        return SystemProperties.getBoolean(propName, this.mContext.getResources().getBoolean(resId));
    }

    private int getInt(String propName, int resId) {
        int value = SystemProperties.getInt(propName, this.mContext.getResources().getInteger(resId));
        return MathUtils.constrain(value, 0, 60000);
    }

    private String getString(String propName, int resId) {
        return SystemProperties.get(propName, this.mContext.getString(resId));
    }

    public int getPulseVisibleDurationExtended() {
        return getPulseVisibleDuration() * 2;
    }

    public boolean doubleTapReportsTouchCoordinates() {
        return this.mContext.getResources().getBoolean(R.bool.doze_double_tap_reports_touch_coordinates);
    }

    @Override // com.android.systemui.tuner.TunerService.Tunable
    public void onTuningChanged(String key, String newValue) {
        this.mDozeAlwaysOn = this.mAmbientDisplayConfiguration.alwaysOnEnabled(-2);
    }

    public AlwaysOnDisplayPolicy getPolicy() {
        return this.mAlwaysOnPolicy;
    }
}
