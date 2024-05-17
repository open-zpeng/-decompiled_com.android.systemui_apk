package com.android.systemui.charging;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Animatable;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.xiaopeng.libtheme.ThemeManager;
import java.text.NumberFormat;
/* loaded from: classes21.dex */
public class WirelessChargingLayout extends FrameLayout {
    private static final int UNKNOWN_BATTERY_LEVEL = -1;

    public WirelessChargingLayout(Context context) {
        super(context);
        init(context, null, false);
    }

    public WirelessChargingLayout(Context context, int batteryLevel, boolean isDozing) {
        super(context);
        init(context, null, batteryLevel, isDozing);
    }

    public WirelessChargingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, false);
    }

    private void init(Context c, AttributeSet attrs, boolean isDozing) {
        init(c, attrs, -1, false);
    }

    private void init(Context context, AttributeSet attrs, int batteryLevel, boolean isDozing) {
        int style = R.style.ChargingAnim_WallpaperBackground;
        if (isDozing) {
            style = R.style.ChargingAnim_DarkBackground;
        }
        inflate(new ContextThemeWrapper(context, style), R.layout.wireless_charging_layout, this);
        ImageView chargingView = (ImageView) findViewById(R.id.wireless_charging_view);
        Animatable chargingAnimation = (Animatable) chargingView.getDrawable();
        TextView mPercentage = (TextView) findViewById(R.id.wireless_charging_percentage);
        if (batteryLevel != -1) {
            mPercentage.setText(NumberFormat.getPercentInstance().format(batteryLevel / 100.0f));
            mPercentage.setAlpha(0.0f);
        }
        long chargingAnimationFadeStartOffset = context.getResources().getInteger(R.integer.wireless_charging_fade_offset);
        long chargingAnimationFadeDuration = context.getResources().getInteger(R.integer.wireless_charging_fade_duration);
        float batteryLevelTextSizeStart = context.getResources().getFloat(R.dimen.wireless_charging_anim_battery_level_text_size_start);
        float batteryLevelTextSizeEnd = context.getResources().getFloat(R.dimen.wireless_charging_anim_battery_level_text_size_end);
        ValueAnimator textSizeAnimator = ObjectAnimator.ofFloat(mPercentage, "textSize", batteryLevelTextSizeStart, batteryLevelTextSizeEnd);
        textSizeAnimator.setInterpolator(new PathInterpolator(0.0f, 0.0f, 0.0f, 1.0f));
        textSizeAnimator.setDuration(context.getResources().getInteger(R.integer.wireless_charging_battery_level_text_scale_animation_duration));
        ValueAnimator textOpacityAnimator = ObjectAnimator.ofFloat(mPercentage, ThemeManager.AttributeSet.ALPHA, 0.0f, 1.0f);
        textOpacityAnimator.setInterpolator(Interpolators.LINEAR);
        Resources resources = context.getResources();
        int style2 = R.integer.wireless_charging_battery_level_text_opacity_duration;
        textOpacityAnimator.setDuration(resources.getInteger(style2));
        textOpacityAnimator.setStartDelay(context.getResources().getInteger(R.integer.wireless_charging_anim_opacity_offset));
        ValueAnimator textFadeAnimator = ObjectAnimator.ofFloat(mPercentage, ThemeManager.AttributeSet.ALPHA, 1.0f, 0.0f);
        textFadeAnimator.setDuration(chargingAnimationFadeDuration);
        textFadeAnimator.setInterpolator(Interpolators.LINEAR);
        textFadeAnimator.setStartDelay(chargingAnimationFadeStartOffset);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(textSizeAnimator, textOpacityAnimator, textFadeAnimator);
        chargingAnimation.start();
        animatorSet.start();
    }
}
