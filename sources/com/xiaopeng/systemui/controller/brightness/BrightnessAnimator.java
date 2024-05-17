package com.xiaopeng.systemui.controller.brightness;

import android.content.Context;
import android.hardware.display.IDisplayManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.SystemProperties;
import com.xiaopeng.systemui.Logger;
/* loaded from: classes24.dex */
public class BrightnessAnimator {
    private static final double FRAME_MILLIS = 0.016d;
    private static final String TAG = "XmartBrightness_Animator";
    private int mAnimateFrames;
    private int mAnimateValue;
    private Context mContext;
    private int mFromValue;
    private Handler mHandler;
    private boolean mIsRange100;
    private boolean mIsRunning;
    private int mToValue;
    private int mTotalFrames;
    private int mType;
    private final int STEP = SystemProperties.getInt("persist.sys.xp.brightness.icm.step", 2);
    private final int RATE = SystemProperties.getInt("persist.sys.xp.brightness.icm.rate", 40);
    private volatile int mSteps = 0;
    private Runnable mTimeoutRunnable = new Runnable() { // from class: com.xiaopeng.systemui.controller.brightness.BrightnessAnimator.1
        @Override // java.lang.Runnable
        public void run() {
            if (BrightnessAnimator.this.mToValue != BrightnessAnimator.this.mAnimateValue) {
                BrightnessAnimator brightnessAnimator = BrightnessAnimator.this;
                brightnessAnimator.animateBrightness(brightnessAnimator.mType, BrightnessAnimator.this.mToValue);
            }
        }
    };
    private Runnable mAnimatorRunnable = new Runnable() { // from class: com.xiaopeng.systemui.controller.brightness.BrightnessAnimator.2
        @Override // java.lang.Runnable
        public void run() {
            BrightnessAnimator.this.mIsRunning = true;
            float percent = BrightnessAnimator.this.mTotalFrames > 0 ? BrightnessAnimator.this.mAnimateFrames / BrightnessAnimator.this.mTotalFrames : 1.0f;
            BrightnessAnimator brightnessAnimator = BrightnessAnimator.this;
            brightnessAnimator.mAnimateValue = brightnessAnimator.getSmoothBrightness(brightnessAnimator.mFromValue, BrightnessAnimator.this.mToValue, percent);
            BrightnessAnimator.access$608(BrightnessAnimator.this);
            BrightnessAnimator.access$808(BrightnessAnimator.this);
            if (BrightnessAnimator.this.mToValue != BrightnessAnimator.this.mAnimateValue && BrightnessAnimator.this.mSteps >= BrightnessAnimator.this.STEP) {
                BrightnessAnimator brightnessAnimator2 = BrightnessAnimator.this;
                brightnessAnimator2.animateBrightness(brightnessAnimator2.mType, BrightnessAnimator.this.mAnimateValue);
                BrightnessAnimator.this.mSteps = 0;
            }
            if (BrightnessAnimator.this.mToValue != BrightnessAnimator.this.mAnimateValue) {
                BrightnessAnimator.this.postAnimator();
                return;
            }
            BrightnessAnimator brightnessAnimator3 = BrightnessAnimator.this;
            brightnessAnimator3.animateBrightness(brightnessAnimator3.mType, BrightnessAnimator.this.mAnimateValue);
            BrightnessAnimator.this.cancelTimeout();
            BrightnessAnimator.this.mSteps = 0;
        }
    };
    private IDisplayManager mDisplayManager = getDisplay();

    static /* synthetic */ int access$608(BrightnessAnimator x0) {
        int i = x0.mAnimateFrames;
        x0.mAnimateFrames = i + 1;
        return i;
    }

    static /* synthetic */ int access$808(BrightnessAnimator x0) {
        int i = x0.mSteps;
        x0.mSteps = i + 1;
        return i;
    }

    public BrightnessAnimator(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    private void set(int type, int from, int to) {
        InternalRunnable setor = new InternalRunnable(type, from, to);
        this.mHandler.post(setor);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void internalSet(int type, int from, int to) {
        this.mType = type;
        this.mToValue = to;
        this.mFromValue = from;
        this.mAnimateValue = from;
        this.mTotalFrames = getFrame(from, to);
        this.mIsRange100 = this.mType == 2;
        this.mAnimateFrames = 0;
        this.mIsRunning = false;
    }

    private void reset() {
        this.mType = 0;
        this.mToValue = 0;
        this.mFromValue = 0;
        this.mAnimateValue = 0;
        this.mTotalFrames = 0;
        this.mAnimateFrames = 0;
        this.mIsRunning = false;
        this.mIsRange100 = false;
    }

    public int getFrame(int from, int to) {
        float delta = Math.abs(to - from);
        double amount = ((1.0f * delta) * 100.0f) / (this.mIsRange100 ? 100 : 255);
        return (int) Math.ceil(amount);
    }

    public int getSmoothBrightness(int from, int to, float percent) {
        int brightness = to;
        if (percent >= 1.0f) {
            return to;
        }
        IDisplayManager iDisplayManager = this.mDisplayManager;
        if (iDisplayManager != null) {
            try {
                if (this.mIsRange100) {
                    int brightness2 = iDisplayManager.getSmoothBrightness(BrightnessSettings.getRealBrightnessByPercent(from), BrightnessSettings.getRealBrightnessByPercent(to), percent);
                    brightness = BrightnessSettings.getPercentProgressByReal(brightness2);
                } else {
                    brightness = iDisplayManager.getSmoothBrightness(from, to, percent);
                }
                return brightness;
            } catch (Exception e) {
                return brightness;
            }
        }
        int brightness3 = (int) (((to - from) * percent) + from);
        return brightness3;
    }

    public IDisplayManager getDisplay() {
        IBinder b = ServiceManager.getService("display");
        if (b != null) {
            return IDisplayManager.Stub.asInterface(b);
        }
        return null;
    }

    public void animateTo(int type, int from, int to) {
        cancelAnimator();
        cancelTimeout();
        set(type, from, to);
        postAnimator();
        postTimeout();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animateBrightness(int type, int brightness) {
        if (type < BrightnessSettings.DISPLAY_MAX) {
            if (type == 0) {
                BrightnessSettings.putInt(this.mContext, "screen_brightness", brightness);
                return;
            }
            Context context = this.mContext;
            BrightnessSettings.putInt(context, "screen_brightness_" + type, brightness);
            return;
        }
        Logger.i(TAG, "animateBrightness wrong. type= " + type + " brightness= " + brightness);
    }

    private void postTimeout() {
        this.mHandler.postDelayed(this.mTimeoutRunnable, 5000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelTimeout() {
        this.mHandler.removeCallbacks(this.mTimeoutRunnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postAnimator() {
        this.mHandler.postDelayed(this.mAnimatorRunnable, 50L);
    }

    public boolean isRunning() {
        return this.mIsRunning;
    }

    public void cancelAnimator() {
        this.mHandler.removeCallbacks(this.mAnimatorRunnable);
        this.mHandler.removeCallbacks(this.mTimeoutRunnable);
        this.mIsRunning = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes24.dex */
    public class InternalRunnable implements Runnable {
        private int from;
        private int to;
        private int type;

        public InternalRunnable(int type, int from, int to) {
            this.type = type;
            this.from = from;
            this.to = to;
        }

        @Override // java.lang.Runnable
        public void run() {
            BrightnessAnimator.this.internalSet(this.type, this.from, this.to);
        }
    }
}
