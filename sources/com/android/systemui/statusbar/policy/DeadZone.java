package com.android.systemui.statusbar.policy;

import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.Slog;
import android.view.MotionEvent;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.statusbar.NavigationBarController;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.android.systemui.statusbar.phone.NavigationBarView;
/* loaded from: classes21.dex */
public class DeadZone {
    private static final boolean CHATTY = true;
    public static final boolean DEBUG = false;
    public static final int HORIZONTAL = 0;
    public static final String TAG = "DeadZone";
    public static final int VERTICAL = 1;
    private int mDecay;
    private final int mDisplayId;
    private int mDisplayRotation;
    private int mHold;
    private long mLastPokeTime;
    private final NavigationBarView mNavigationBarView;
    private boolean mShouldFlash;
    private int mSizeMax;
    private int mSizeMin;
    private boolean mVertical;
    private float mFlashFrac = 0.0f;
    private final Runnable mDebugFlash = new Runnable() { // from class: com.android.systemui.statusbar.policy.DeadZone.1
        @Override // java.lang.Runnable
        public void run() {
            ObjectAnimator.ofFloat(DeadZone.this, "flash", 1.0f, 0.0f).setDuration(150L).start();
        }
    };
    private final NavigationBarController mNavBarController = (NavigationBarController) Dependency.get(NavigationBarController.class);

    public DeadZone(NavigationBarView view) {
        this.mNavigationBarView = view;
        this.mDisplayId = view.getContext().getDisplayId();
        onConfigurationChanged(0);
    }

    static float lerp(float a, float b, float f) {
        return ((b - a) * f) + a;
    }

    private float getSize(long now) {
        int i = this.mSizeMax;
        if (i == 0) {
            return 0.0f;
        }
        long dt = now - this.mLastPokeTime;
        int i2 = this.mHold;
        int i3 = this.mDecay;
        if (dt > i2 + i3) {
            return this.mSizeMin;
        }
        if (dt < i2) {
            return i;
        }
        return (int) lerp(i, this.mSizeMin, ((float) (dt - i2)) / i3);
    }

    public void setFlashOnTouchCapture(boolean dbg) {
        this.mShouldFlash = dbg;
        this.mFlashFrac = 0.0f;
        this.mNavigationBarView.postInvalidate();
    }

    public void onConfigurationChanged(int rotation) {
        this.mDisplayRotation = rotation;
        Resources res = this.mNavigationBarView.getResources();
        this.mHold = res.getInteger(R.integer.navigation_bar_deadzone_hold);
        this.mDecay = res.getInteger(R.integer.navigation_bar_deadzone_decay);
        this.mSizeMin = res.getDimensionPixelSize(R.dimen.navigation_bar_deadzone_size);
        this.mSizeMax = res.getDimensionPixelSize(R.dimen.navigation_bar_deadzone_size_max);
        int index = res.getInteger(R.integer.navigation_bar_deadzone_orientation);
        this.mVertical = index == 1;
        setFlashOnTouchCapture(res.getBoolean(R.bool.config_dead_zone_flash));
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean consumeEvent;
        if (event.getToolType(0) == 3) {
            return false;
        }
        int action = event.getAction();
        if (action == 4) {
            poke(event);
            return true;
        }
        if (action == 0) {
            this.mNavBarController.touchAutoDim(this.mDisplayId);
            int size = (int) getSize(event.getEventTime());
            if (this.mVertical) {
                if (this.mDisplayRotation == 3) {
                    consumeEvent = event.getX() > ((float) (this.mNavigationBarView.getWidth() - size));
                } else {
                    consumeEvent = event.getX() < ((float) size);
                }
            } else {
                consumeEvent = event.getY() < ((float) size);
            }
            if (consumeEvent) {
                Slog.v(TAG, "consuming errant click: (" + event.getX() + "," + event.getY() + NavigationBarInflaterView.KEY_CODE_END);
                if (this.mShouldFlash) {
                    this.mNavigationBarView.post(this.mDebugFlash);
                    this.mNavigationBarView.postInvalidate();
                }
                return true;
            }
        }
        return false;
    }

    private void poke(MotionEvent event) {
        this.mLastPokeTime = event.getEventTime();
        if (this.mShouldFlash) {
            this.mNavigationBarView.postInvalidate();
        }
    }

    public void setFlash(float f) {
        this.mFlashFrac = f;
        this.mNavigationBarView.postInvalidate();
    }

    public float getFlash() {
        return this.mFlashFrac;
    }

    public void onDraw(Canvas can) {
        if (!this.mShouldFlash || this.mFlashFrac <= 0.0f) {
            return;
        }
        int size = (int) getSize(SystemClock.uptimeMillis());
        if (this.mVertical) {
            if (this.mDisplayRotation == 3) {
                can.clipRect(can.getWidth() - size, 0, can.getWidth(), can.getHeight());
            } else {
                can.clipRect(0, 0, size, can.getHeight());
            }
        } else {
            can.clipRect(0, 0, can.getWidth(), size);
        }
        float frac = this.mFlashFrac;
        can.drawARGB((int) (255.0f * frac), 221, 238, 170);
    }
}
