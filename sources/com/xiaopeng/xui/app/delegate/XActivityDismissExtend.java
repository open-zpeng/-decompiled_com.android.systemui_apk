package com.xiaopeng.xui.app.delegate;

import android.app.Activity;
import android.os.Handler;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.xui.app.delegate.XActivityDismiss;
import com.xiaopeng.xui.app.delegate.XActivityDismissExtend;
import com.xiaopeng.xui.utils.XActivityUtils;
import com.xiaopeng.xui.utils.XLogUtils;
/* loaded from: classes25.dex */
abstract class XActivityDismissExtend implements XActivityDismiss, XActivityLifecycle {

    /* loaded from: classes25.dex */
    interface OnDismissListenerEx {
        void afterDismiss(int i);

        void beforeDismiss(int i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void setOnDismissListenerEx(OnDismissListenerEx onDismissListenerEx);

    XActivityDismissExtend() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static XActivityDismissExtend create(Activity activity) {
        return new XActivityDismissExtendImpl(activity);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes25.dex */
    public static class XActivityDismissExtendImpl extends XActivityDismissExtend {
        private static final String TAG = "XActivityDismiss";
        private Activity mActivity;
        private Runnable mDelayRunnable;
        private XActivityDismiss.OnDismissListener mOnDismissListener;
        @Nullable
        private OnDismissListenerEx mOnDismissListenerEx;
        private int mDismissType = 0;
        private long mDismissDelay = 0;
        private final Handler mHandler = new Handler();

        XActivityDismissExtendImpl(Activity activity) {
            this.mActivity = activity;
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityDismiss
        public void setDismissDelay(long delay) {
            this.mDismissDelay = delay;
            XLogUtils.i(TAG, "setDismissDelay: " + delay);
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityDismiss
        public void setOnDismissListener(XActivityDismiss.OnDismissListener listener) {
            this.mOnDismissListener = listener;
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityDismissExtend
        void setOnDismissListenerEx(OnDismissListenerEx listenerEx) {
            this.mOnDismissListenerEx = listenerEx;
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityDismiss
        public void setDismissType(int type) {
            this.mDismissType = type;
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityDismiss
        @CallSuper
        public void dismiss(final int cause) {
            XActivityDismiss.OnDismissListener onDismissListener = this.mOnDismissListener;
            if (onDismissListener != null && onDismissListener.onDismiss(cause, this.mDismissType)) {
                XLogUtils.i(TAG, "dismiss: intercept for " + convertCauseToString(cause));
                return;
            }
            OnDismissListenerEx onDismissListenerEx = this.mOnDismissListenerEx;
            if (onDismissListenerEx != null) {
                onDismissListenerEx.beforeDismiss(cause);
            }
            if (this.mDismissDelay == 0) {
                _dismiss(cause);
                return;
            }
            XLogUtils.i(TAG, "dismiss: type : " + convertTypeToString(this.mDismissType) + " , cause : " + convertCauseToString(cause) + " " + this.mDelayRunnable);
            if (this.mDelayRunnable == null) {
                this.mDelayRunnable = new Runnable() { // from class: com.xiaopeng.xui.app.delegate.-$$Lambda$XActivityDismissExtend$XActivityDismissExtendImpl$tIuePlo6tgyIqeCB2NgnAPhvdYk
                    @Override // java.lang.Runnable
                    public final void run() {
                        XActivityDismissExtend.XActivityDismissExtendImpl.this.lambda$dismiss$0$XActivityDismissExtend$XActivityDismissExtendImpl(cause);
                    }
                };
                this.mHandler.postDelayed(this.mDelayRunnable, this.mDismissDelay);
            }
        }

        public /* synthetic */ void lambda$dismiss$0$XActivityDismissExtend$XActivityDismissExtendImpl(int cause) {
            _dismiss(cause);
            this.mDelayRunnable = null;
            OnDismissListenerEx onDismissListenerEx = this.mOnDismissListenerEx;
            if (onDismissListenerEx != null) {
                onDismissListenerEx.afterDismiss(cause);
            }
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityLifecycle
        public void onDestroy() {
            Runnable runnable = this.mDelayRunnable;
            if (runnable != null) {
                this.mHandler.removeCallbacks(runnable);
                this.mDelayRunnable = null;
            }
        }

        private void _dismiss(int cause) {
            XLogUtils.i(TAG, "_dismiss: type : " + convertTypeToString(this.mDismissType) + " , cause : " + convertCauseToString(cause));
            int type = this.mDismissType;
            if (type == 0) {
                XActivityUtils.finish(this.mActivity);
            } else if (type == 1) {
                XActivityUtils.moveTaskToBack(this.mActivity, true);
            }
        }

        private String convertCauseToString(int cause) {
            if (cause != 1) {
                if (cause != 2) {
                    if (cause != 3) {
                        if (cause != 4) {
                            return "user";
                        }
                        return "timeout";
                    }
                    return "outside";
                }
                return "pause";
            }
            return NavigationBarInflaterView.BACK;
        }

        private String convertTypeToString(int type) {
            if (type == 1) {
                return "moveToBack";
            }
            return "finish";
        }

        @NonNull
        public String toString() {
            return "{mDismissType=" + this.mDismissType + ", mDismissDelay=" + this.mDismissDelay + '}';
        }
    }
}
