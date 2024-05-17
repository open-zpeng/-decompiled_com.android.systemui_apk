package com.xiaopeng.systemui.ui.window;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import com.xiaopeng.systemui.Logger;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public class StatusBarWindow extends FrameLayout {
    private static final String TAG = "StatusBarWindow";
    private List<OnViewListener> mOnViewListeners;

    /* loaded from: classes24.dex */
    public interface OnViewListener {
        void dispatchTouchEvent(MotionEvent motionEvent);

        void onAttachedToWindow();

        void onFinishInflate();
    }

    public StatusBarWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mOnViewListeners = new ArrayList();
    }

    public void addListener(OnViewListener listener) {
        this.mOnViewListeners.add(listener);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        for (OnViewListener listener : this.mOnViewListeners) {
            listener.onAttachedToWindow();
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (OnViewListener listener : this.mOnViewListeners) {
            listener.onFinishInflate();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (OnViewListener listener : this.mOnViewListeners) {
            listener.dispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        Logger.d(TAG, "onWindowFocusChanged hasWindowFocus=" + hasWindowFocus);
    }

    @Override // android.view.View
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        Logger.d(TAG, "onWindowVisibilityChanged visibility=" + visibility);
    }
}
