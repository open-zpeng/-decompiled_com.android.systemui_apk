package com.xiaopeng.systemui.infoflow.speech.utils;

import android.app.UiModeManager;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.media.subtitle.Cea708CCParser;
import com.alibaba.fastjson.asm.Opcodes;
import com.android.systemui.R;
import com.android.systemui.statusbar.notification.stack.StackStateAnimator;
import com.badlogic.gdx.Input;
import com.xiaopeng.speech.protocol.node.navi.bean.NaviPreferenceBean;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
/* loaded from: classes24.dex */
public class ColorUtils {
    private static final ColorUtils ourInstance = new ColorUtils();
    private String TAG = "ColorUtils";

    private ColorUtils() {
    }

    public static ColorUtils getInstance() {
        return ourInstance;
    }

    public int[] createLightBrightnessColor() {
        if (isDayMode()) {
            int[] brightColors = createLightBrightnessColorDay();
            return brightColors;
        }
        int[] brightColors2 = createLightBrightnessNightColor();
        return brightColors2;
    }

    private int[] createLightBrightnessColorDay() {
        int[] cs = {Color.rgb((int) Cea708CCParser.Const.CODE_C1_HDW, (int) Cea708CCParser.Const.CODE_C1_DF3, 174), Color.rgb(171, 189, (int) NaviPreferenceBean.PATH_PREF_AVOID_HIGHWAY)};
        return cs;
    }

    private int[] createLightBrightnessNightColor() {
        int[] cs = {Color.rgb(124, 154, 205), Color.rgb(89, 115, 159)};
        return cs;
    }

    public int[] createAmbientLightingColor() {
        if (isDayMode()) {
            int[] lightingColors = createAmbientLightingColorDay();
            return lightingColors;
        }
        int[] lightingColors2 = createAmbientLightingColorDay();
        return lightingColors2;
    }

    private int[] createAmbientLightingColorDay() {
        int[] cs = {ContextUtils.getColor(R.color.ctrl_alt_color1), ContextUtils.getColor(R.color.ctrl_alt_color2), ContextUtils.getColor(R.color.ctrl_alt_color3), ContextUtils.getColor(R.color.ctrl_alt_color4), ContextUtils.getColor(R.color.ctrl_alt_color5), ContextUtils.getColor(R.color.ctrl_alt_color6), ContextUtils.getColor(R.color.ctrl_alt_color7), ContextUtils.getColor(R.color.ctrl_alt_color8), ContextUtils.getColor(R.color.ctrl_alt_color9), ContextUtils.getColor(R.color.ctrl_alt_color10), ContextUtils.getColor(R.color.ctrl_alt_color11), ContextUtils.getColor(R.color.ctrl_alt_color12), ContextUtils.getColor(R.color.ctrl_alt_color13), ContextUtils.getColor(R.color.ctrl_alt_color14), ContextUtils.getColor(R.color.ctrl_alt_color15), ContextUtils.getColor(R.color.ctrl_alt_color16), ContextUtils.getColor(R.color.ctrl_alt_color17), ContextUtils.getColor(R.color.ctrl_alt_color18), ContextUtils.getColor(R.color.ctrl_alt_color19), ContextUtils.getColor(R.color.ctrl_alt_color20)};
        return cs;
    }

    public int[] createHotWindColor() {
        if (isDayMode()) {
            int[] windHotColor = createHotWindColorDay();
            return windHotColor;
        }
        int[] windHotColor2 = createHotWindColorNight();
        return windHotColor2;
    }

    private int[] createHotWindColorNight() {
        int[] cs = {Color.rgb(255, 146, 59), Color.rgb(255, 50, 0)};
        return cs;
    }

    private int[] createHotWindColorDay() {
        int[] cs = {Color.rgb(255, 204, 0), Color.rgb(255, 152, 0)};
        return cs;
    }

    public int[] createColdWindColor() {
        if (isDayMode()) {
            int[] windColdColor = createProgressColdWindColorDay();
            return windColdColor;
        }
        int[] windColdColor2 = createColdWindColorNight();
        return windColdColor2;
    }

    private int[] createColdWindColorNight() {
        int[] cs = {Color.rgb(54, (int) Opcodes.PUTFIELD, (int) StackStateAnimator.ANIMATION_DURATION_BLOCKING_HELPER_FADE), Color.rgb(22, 63, (int) Input.Keys.F6)};
        return cs;
    }

    private int[] createProgressColdWindColorDay() {
        int[] cs = {Color.rgb(59, 250, 255), Color.rgb(59, 153, 255)};
        return cs;
    }

    public int[] createRadialBgColdWindColorDay(int progress) {
        int[] cs = {Color.argb(progress * 255, 59, 153, 255), Color.rgb(255, 255, 255)};
        return cs;
    }

    public int[] createRadialBgColdWindColorNight() {
        int[] cs = {Color.argb(0, 255, 255, 255), Color.argb(0, 255, 255, 255)};
        return cs;
    }

    public int[] createRadialBgHotWindColorDay(int progress) {
        int[] cs = {Color.argb(progress * 255, 255, 152, 0), Color.rgb(255, 255, 255)};
        return cs;
    }

    public int[] createRadialBgHotWindColorNight(int progress) {
        int[] cs = {Color.argb(progress * 255, 255, 152, 0), Color.argb(0, 255, 255, 255)};
        return cs;
    }

    public int[] createRadialBgBlowingWindColorDay(int progress) {
        int[] cs = {Color.argb(progress * 255, (int) Cea708CCParser.Const.CODE_C1_HDW, (int) Cea708CCParser.Const.CODE_C1_DF3, 174), Color.rgb(255, 255, 255)};
        return cs;
    }

    public int[] createRadialBgBlowingWindColorNight(int progress) {
        int[] cs = {Color.argb(progress * 255, (int) Cea708CCParser.Const.CODE_C1_HDW, (int) Cea708CCParser.Const.CODE_C1_DF3, 174), Color.argb(0, 255, 255, 255)};
        return cs;
    }

    public int[] createBlowWindColor() {
        if (isDayMode()) {
            int[] windColdColor = createBlowWindColorDay();
            return windColdColor;
        }
        int[] windColdColor2 = createBlowWindColorNight();
        return windColdColor2;
    }

    private int[] createBlowWindColorNight() {
        int[] cs = {Color.rgb((int) Opcodes.IF_ICMPLT, 171, 190), Color.rgb(127, (int) Cea708CCParser.Const.CODE_C1_TGW, 159)};
        return cs;
    }

    private int[] createBlowWindColorDay() {
        int[] cs = {Color.rgb((int) Cea708CCParser.Const.CODE_C1_HDW, (int) Cea708CCParser.Const.CODE_C1_DF3, 174), Color.rgb(171, 189, (int) NaviPreferenceBean.PATH_PREF_AVOID_HIGHWAY)};
        return cs;
    }

    public int getDayNightMode(Context context) {
        UiModeManager uim = (UiModeManager) context.getSystemService("uimode");
        return uim.getDayNightMode();
    }

    public boolean isDayMode() {
        int dayNightMode = getDayNightMode(ContextUtils.getContext());
        if (dayNightMode != 1 && dayNightMode != 0) {
            return false;
        }
        return true;
    }

    public int[] createBrightnessColor() {
        if (isDayMode()) {
            int[] brightnessColdColor = createProgressBrightnessColorDay();
            return brightnessColdColor;
        }
        int[] brightnessColdColor2 = createRadialBgBrightnessColorNight();
        return brightnessColdColor2;
    }

    private int[] createProgressBrightnessColorDay() {
        int[] cs = {Color.rgb((int) Cea708CCParser.Const.CODE_C1_HDW, (int) Cea708CCParser.Const.CODE_C1_DF3, 174), Color.rgb(171, 189, (int) NaviPreferenceBean.PATH_PREF_AVOID_HIGHWAY)};
        return cs;
    }

    public int[] createRadialBgBrightnessColorNight() {
        int[] cs = {Color.rgb(124, 154, 205), Color.rgb(89, 115, 159)};
        return cs;
    }
}
