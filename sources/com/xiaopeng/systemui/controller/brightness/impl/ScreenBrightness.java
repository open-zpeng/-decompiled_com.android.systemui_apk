package com.xiaopeng.systemui.controller.brightness.impl;

import android.content.Context;
import android.os.Handler;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.controller.brightness.AbsBrightness;
import com.xiaopeng.systemui.controller.brightness.BrightnessCarManager;
import com.xiaopeng.systemui.controller.brightness.BrightnessManager;
import com.xiaopeng.systemui.controller.brightness.BrightnessSettings;
/* loaded from: classes24.dex */
public class ScreenBrightness extends AbsBrightness {
    private static final String TAG = "XmartBrightness_Screen";

    public ScreenBrightness(int type, Context context, Handler handler) {
        super(type, context, handler);
        readSettings();
    }

    @Override // com.xiaopeng.systemui.controller.brightness.AbsBrightness
    public void readSettings() {
        super.readSettings();
        if (this.mType == 2) {
            Context context = this.mContext;
            this.mBrightness = BrightnessSettings.getInt(context, "screen_brightness_" + this.mType, 70);
            Context context2 = this.mContext;
            this.mBrightnessDay = BrightnessSettings.getInt(context2, "screen_brightness_day_" + this.mType, 70);
            Context context3 = this.mContext;
            this.mBrightnessNight = BrightnessSettings.getInt(context3, "screen_brightness_night_" + this.mType, 40);
        } else if (this.mType == 3) {
            Context context4 = this.mContext;
            this.mBrightness = BrightnessSettings.getInt(context4, "screen_brightness_" + this.mType, 70);
            Context context5 = this.mContext;
            this.mBrightnessDay = BrightnessSettings.getInt(context5, "screen_brightness_" + this.mType, 70);
            Context context6 = this.mContext;
            this.mBrightnessNight = BrightnessSettings.getInt(context6, "screen_brightness_" + this.mType, 70);
        } else {
            if (this.mType == 0) {
                this.mBrightness = BrightnessSettings.getInt(this.mContext, "screen_brightness", 179);
            } else {
                Context context7 = this.mContext;
                this.mBrightness = BrightnessSettings.getInt(context7, "screen_brightness_" + this.mType, 179);
            }
            Context context8 = this.mContext;
            this.mBrightnessDay = BrightnessSettings.getInt(context8, "screen_brightness_day_" + this.mType, 179);
            Context context9 = this.mContext;
            this.mBrightnessNight = BrightnessSettings.getInt(context9, "screen_brightness_night_" + this.mType, 102);
        }
        Context context10 = this.mContext;
        int autoSwitch = BrightnessSettings.getInt(context10, "screen_brightness_mode_" + this.mType, -1);
        if (autoSwitch == -1) {
            Context context11 = this.mContext;
            BrightnessSettings.putInt(context11, "screen_brightness_mode_" + this.mType, 1);
        }
    }

    @Override // com.xiaopeng.systemui.controller.brightness.AbsBrightness
    public void setBrightness(int brightness, boolean includePublic) {
        super.setBrightness(brightness, includePublic);
        int mode = getMode();
        if (mode == 1) {
            this.mBrightnessDay = brightness;
            Context context = this.mContext;
            BrightnessSettings.putInt(context, "screen_brightness_day_" + this.mType, brightness);
        } else if (mode == 2) {
            this.mBrightnessNight = brightness;
            Context context2 = this.mContext;
            BrightnessSettings.putInt(context2, "screen_brightness_night_" + this.mType, brightness);
        } else if (mode == 3 && !this.mDarkInit) {
            if (BrightnessManager.sRebootMode == 6 || this.mLastMode == 6) {
                brightness = BrightnessManager.isNightMode(this.mContext) ? this.mBrightnessNight : this.mBrightnessDay;
            }
            if (this.mType == 2) {
                brightness = Math.min(brightness, 20);
            } else if (this.mType != 3) {
                brightness = Math.min(brightness, 51);
            }
            this.mDarkInit = true;
        }
        this.mBrightness = brightness;
        if (this.mType == 0) {
            if (includePublic) {
                BrightnessSettings.putInt(this.mContext, "screen_brightness", brightness);
            }
        } else if (includePublic) {
            Context context3 = this.mContext;
            BrightnessSettings.putInt(context3, "screen_brightness_" + this.mType, brightness);
        }
    }

    @Override // com.xiaopeng.systemui.controller.brightness.AbsBrightness
    public int getBrightness() {
        int brightness;
        readSettings();
        int mode = getMode();
        if (mode == 1) {
            brightness = this.mBrightnessDay;
        } else if (mode == 2) {
            brightness = this.mBrightnessNight;
        } else {
            brightness = this.mBrightness;
        }
        Logger.i(TAG, toString("getBrightness mType=" + this.mType + "ret=" + brightness));
        return brightness;
    }

    @Override // com.xiaopeng.systemui.controller.brightness.AbsBrightness
    public void animateBrightness(int to) {
        int from;
        if (this.mType == 2) {
            int from2 = BrightnessSettings.getInt(this.mContext, "screen_brightness_callback_2", 70);
            boolean connected = BrightnessCarManager.get(this.mContext).isIcmConnected();
            Logger.i(TAG, "animateBrightness mType=" + this.mType + " from=" + from2 + " to=" + to + " connected=" + connected);
            this.mBrightnessAnimator.animateTo(this.mType, connected ? from2 : to, to);
            return;
        }
        if (this.mType == 0) {
            from = BrightnessSettings.getInt(this.mContext, "screen_brightness", 179);
        } else {
            Context context = this.mContext;
            from = BrightnessSettings.getInt(context, "screen_brightness_" + this.mType, 179);
        }
        Logger.i(TAG, "animateBrightness mType=" + this.mType + " from=" + from + " to=" + to);
        this.mBrightnessAnimator.animateTo(this.mType, from, to);
    }

    @Override // com.xiaopeng.systemui.controller.brightness.AbsBrightness
    public void CancelSetBrightness() {
        this.mBrightnessAnimator.cancelAnimator();
    }

    @Override // com.xiaopeng.systemui.controller.brightness.AbsBrightness
    public boolean IsSettingBrightness() {
        return this.mBrightnessAnimator.isRunning();
    }
}
