package com.xiaopeng.systemui.controller.brightness;

import android.content.Context;
import android.os.Handler;
/* loaded from: classes24.dex */
public abstract class AbsBrightness {
    private static final String TAG = "AbsBrightness";
    protected int mBrightness;
    protected BrightnessAnimator mBrightnessAnimator;
    protected int mBrightnessDay;
    protected int mBrightnessNight;
    protected Context mContext;
    protected final Handler mHandler;
    protected int mType;
    protected int mMode = 1;
    protected int mLastMode = 1;
    protected boolean mDarkInit = false;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbsBrightness(int type, Context context, Handler handler) {
        this.mType = 0;
        this.mContext = context;
        this.mHandler = handler;
        this.mType = type;
        this.mBrightnessAnimator = new BrightnessAnimator(this.mContext, this.mHandler);
    }

    public void readSettings() {
    }

    public void setBrightness(int brightness, boolean includePublic) {
    }

    public int getBrightness() {
        return this.mBrightness;
    }

    public void animateBrightness(int to) {
    }

    public void CancelSetBrightness() {
    }

    public boolean IsSettingBrightness() {
        return false;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public int getType() {
        return this.mType;
    }

    public void setMode(int mode) {
        int i = this.mMode;
        if (mode == i) {
            i = this.mLastMode;
        }
        this.mLastMode = i;
        BrightnessSettings.putInt(this.mContext, BrightnessSettings.KEY_SYSTEM_REBOOT_MODE, mode);
        this.mMode = mode;
        this.mDarkInit = mode != 3 ? false : this.mDarkInit;
    }

    public int getMode() {
        return this.mMode;
    }

    public String toString(String tag) {
        StringBuffer buffer = new StringBuffer("");
        buffer.append("tag=" + tag);
        buffer.append(" type=" + this.mType);
        buffer.append(" mode=" + this.mMode);
        buffer.append(" brightness=" + this.mBrightness);
        buffer.append(" day=" + this.mBrightnessDay);
        buffer.append(" night=" + this.mBrightnessNight);
        buffer.append(" isAutoMode=" + BrightnessManager.isAutoMode(this.mContext));
        buffer.append(" isNightMode=" + BrightnessManager.isNightMode(this.mContext));
        return buffer.toString();
    }
}
