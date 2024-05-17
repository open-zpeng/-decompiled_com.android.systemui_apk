package com.xiaopeng.systemui.ui.window;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public class NavigationBarWindow extends FrameLayout {
    private List<OnViewListener> mOnViewListeners;

    /* loaded from: classes24.dex */
    public interface OnViewListener {
        void dispatchTouchEvent(MotionEvent motionEvent);

        void onAttachedToWindow();

        void onFinishInflate();
    }

    public NavigationBarWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mOnViewListeners = new ArrayList();
    }

    public void addListener(OnViewListener listener) {
        this.mOnViewListeners.add(listener);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mOnViewListeners.size() > 0) {
            for (int i = 0; i < this.mOnViewListeners.size(); i++) {
                this.mOnViewListeners.get(i).onAttachedToWindow();
            }
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (this.mOnViewListeners.size() > 0) {
            for (int i = 0; i < this.mOnViewListeners.size(); i++) {
                this.mOnViewListeners.get(i).onFinishInflate();
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (this.mOnViewListeners.size() > 0) {
            for (int i = 0; i < this.mOnViewListeners.size(); i++) {
                this.mOnViewListeners.get(i).dispatchTouchEvent(ev);
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
