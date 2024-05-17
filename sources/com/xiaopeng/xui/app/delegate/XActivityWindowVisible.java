package com.xiaopeng.xui.app.delegate;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import com.xiaopeng.xui.utils.XLogUtils;
/* loaded from: classes25.dex */
public class XActivityWindowVisible implements XActivityLifecycle {
    private static final String TAG = "XActivityWindowVisible";
    private Activity mActivity;
    private boolean mAutoVisibility;
    private float mDimAlpha;

    public XActivityWindowVisible(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void setAutoVisibleEnableOnPause(boolean enable) {
        if (enable != this.mAutoVisibility) {
            XLogUtils.i(TAG, "setAutoVisibleEnableOnPause: " + enable);
        }
        this.mAutoVisibility = enable;
    }

    public void changeWindowVisible(boolean visible) {
        if (this.mAutoVisibility) {
            XLogUtils.i(TAG, "changeWindowVisible: " + visible);
            Window window = this.mActivity.getWindow();
            if (window != null) {
                WindowManager.LayoutParams lp = window.getAttributes();
                if (lp != null) {
                    lp.alpha = visible ? 1.0f : 0.0f;
                    lp.dimAmount = visible ? this.mDimAlpha : 0.0f;
                }
                window.setAttributes(lp);
            }
        }
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityLifecycle
    public void onCreate(Bundle savedInstanceState) {
        TypedArray array = this.mActivity.getTheme().obtainStyledAttributes(new int[]{16842802});
        this.mDimAlpha = array.getFloat(0, 1.0f);
        array.recycle();
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityLifecycle
    public void onResume() {
        changeWindowVisible(true);
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityLifecycle
    public void onPause() {
        changeWindowVisible(false);
    }

    @NonNull
    public String toString() {
        return "{mDimAlpha=" + this.mDimAlpha + ", mAutoVisibility=" + this.mAutoVisibility + ", mActivity=" + this.mActivity + '}';
    }
}
