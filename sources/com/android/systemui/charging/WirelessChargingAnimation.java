package com.android.systemui.charging;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Slog;
import android.view.View;
import android.view.WindowManager;
import com.xiaopeng.systemui.controller.CarController;
/* loaded from: classes21.dex */
public class WirelessChargingAnimation {
    public static final long DURATION = 1133;
    private static WirelessChargingView mPreviousWirelessChargingView;
    private final WirelessChargingView mCurrentWirelessChargingView;
    private static final String TAG = "WirelessChargingView";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);

    /* loaded from: classes21.dex */
    public interface Callback {
        void onAnimationEnded();

        void onAnimationStarting();
    }

    public WirelessChargingAnimation(Context context, Looper looper, int batteryLevel, Callback callback, boolean isDozing) {
        this.mCurrentWirelessChargingView = new WirelessChargingView(context, looper, batteryLevel, callback, isDozing);
    }

    public static WirelessChargingAnimation makeWirelessChargingAnimation(Context context, Looper looper, int batteryLevel, Callback callback, boolean isDozing) {
        return new WirelessChargingAnimation(context, looper, batteryLevel, callback, isDozing);
    }

    public void show() {
        WirelessChargingView wirelessChargingView = this.mCurrentWirelessChargingView;
        if (wirelessChargingView == null || wirelessChargingView.mNextView == null) {
            throw new RuntimeException("setView must have been called");
        }
        WirelessChargingView wirelessChargingView2 = mPreviousWirelessChargingView;
        if (wirelessChargingView2 != null) {
            wirelessChargingView2.hide(0L);
        }
        WirelessChargingView wirelessChargingView3 = this.mCurrentWirelessChargingView;
        mPreviousWirelessChargingView = wirelessChargingView3;
        wirelessChargingView3.show();
        this.mCurrentWirelessChargingView.hide(DURATION);
    }

    /* loaded from: classes21.dex */
    private static class WirelessChargingView {
        private static final int HIDE = 1;
        private static final int SHOW = 0;
        private Callback mCallback;
        private final Handler mHandler;
        private View mNextView;
        private View mView;
        private WindowManager mWM;
        private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
        private int mGravity = 17;

        public WirelessChargingView(Context context, Looper looper, int batteryLevel, Callback callback, boolean isDozing) {
            this.mCallback = callback;
            this.mNextView = new WirelessChargingLayout(context, batteryLevel, isDozing);
            WindowManager.LayoutParams params = this.mParams;
            params.height = -2;
            params.width = -1;
            params.format = -3;
            params.type = CarController.TYPE_CAR_CLTC_DRIVE_DISTANCE;
            params.setTitle("Charging Animation");
            params.flags = 26;
            params.dimAmount = 0.3f;
            if (looper == null && (looper = Looper.myLooper()) == null) {
                throw new RuntimeException("Can't display wireless animation on a thread that has not called Looper.prepare()");
            }
            this.mHandler = new Handler(looper, null) { // from class: com.android.systemui.charging.WirelessChargingAnimation.WirelessChargingView.1
                @Override // android.os.Handler
                public void handleMessage(Message msg) {
                    int i = msg.what;
                    if (i == 0) {
                        WirelessChargingView.this.handleShow();
                    } else if (i == 1) {
                        WirelessChargingView.this.handleHide();
                        WirelessChargingView.this.mNextView = null;
                    }
                }
            };
        }

        public void show() {
            if (WirelessChargingAnimation.DEBUG) {
                Slog.d(WirelessChargingAnimation.TAG, "SHOW: " + this);
            }
            this.mHandler.obtainMessage(0).sendToTarget();
        }

        public void hide(long duration) {
            this.mHandler.removeMessages(1);
            if (WirelessChargingAnimation.DEBUG) {
                Slog.d(WirelessChargingAnimation.TAG, "HIDE: " + this);
            }
            Handler handler = this.mHandler;
            handler.sendMessageDelayed(Message.obtain(handler, 1), duration);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleShow() {
            if (WirelessChargingAnimation.DEBUG) {
                Slog.d(WirelessChargingAnimation.TAG, "HANDLE SHOW: " + this + " mView=" + this.mView + " mNextView=" + this.mNextView);
            }
            if (this.mView != this.mNextView) {
                handleHide();
                this.mView = this.mNextView;
                Context context = this.mView.getContext().getApplicationContext();
                String packageName = this.mView.getContext().getOpPackageName();
                if (context == null) {
                    context = this.mView.getContext();
                }
                this.mWM = (WindowManager) context.getSystemService("window");
                WindowManager.LayoutParams layoutParams = this.mParams;
                layoutParams.packageName = packageName;
                layoutParams.hideTimeoutMilliseconds = WirelessChargingAnimation.DURATION;
                if (this.mView.getParent() != null) {
                    if (WirelessChargingAnimation.DEBUG) {
                        Slog.d(WirelessChargingAnimation.TAG, "REMOVE! " + this.mView + " in " + this);
                    }
                    this.mWM.removeView(this.mView);
                }
                if (WirelessChargingAnimation.DEBUG) {
                    Slog.d(WirelessChargingAnimation.TAG, "ADD! " + this.mView + " in " + this);
                }
                try {
                    if (this.mCallback != null) {
                        this.mCallback.onAnimationStarting();
                    }
                    this.mWM.addView(this.mView, this.mParams);
                } catch (WindowManager.BadTokenException e) {
                    Slog.d(WirelessChargingAnimation.TAG, "Unable to add wireless charging view. " + e);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleHide() {
            if (WirelessChargingAnimation.DEBUG) {
                Slog.d(WirelessChargingAnimation.TAG, "HANDLE HIDE: " + this + " mView=" + this.mView);
            }
            View view = this.mView;
            if (view != null) {
                if (view.getParent() != null) {
                    if (WirelessChargingAnimation.DEBUG) {
                        Slog.d(WirelessChargingAnimation.TAG, "REMOVE! " + this.mView + " in " + this);
                    }
                    Callback callback = this.mCallback;
                    if (callback != null) {
                        callback.onAnimationEnded();
                    }
                    this.mWM.removeViewImmediate(this.mView);
                }
                this.mView = null;
            }
        }
    }
}
