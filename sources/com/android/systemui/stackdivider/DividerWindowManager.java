package com.android.systemui.stackdivider;

import android.content.Context;
import android.os.Binder;
import android.view.View;
import android.view.WindowManager;
/* loaded from: classes21.dex */
public class DividerWindowManager {
    private static final String WINDOW_TITLE = "DockedStackDivider";
    private WindowManager.LayoutParams mLp;
    private View mView;
    private final WindowManager mWindowManager;

    public DividerWindowManager(Context ctx) {
        this.mWindowManager = (WindowManager) ctx.getSystemService(WindowManager.class);
    }

    public void add(View view, int width, int height) {
        this.mLp = new WindowManager.LayoutParams(width, height, 2034, 545521704, -3);
        this.mLp.token = new Binder();
        this.mLp.setTitle(WINDOW_TITLE);
        this.mLp.privateFlags |= 64;
        this.mLp.layoutInDisplayCutoutMode = 1;
        view.setSystemUiVisibility(1792);
        this.mWindowManager.addView(view, this.mLp);
        this.mView = view;
    }

    public void remove() {
        View view = this.mView;
        if (view != null) {
            this.mWindowManager.removeView(view);
        }
        this.mView = null;
    }

    public void setSlippery(boolean slippery) {
        boolean changed = false;
        if (slippery && (this.mLp.flags & 536870912) == 0) {
            WindowManager.LayoutParams layoutParams = this.mLp;
            layoutParams.flags = 536870912 | layoutParams.flags;
            changed = true;
        } else if (!slippery && (536870912 & this.mLp.flags) != 0) {
            this.mLp.flags &= -536870913;
            changed = true;
        }
        if (changed) {
            this.mWindowManager.updateViewLayout(this.mView, this.mLp);
        }
    }

    public void setTouchable(boolean touchable) {
        boolean changed = false;
        if (!touchable && (this.mLp.flags & 16) == 0) {
            this.mLp.flags |= 16;
            changed = true;
        } else if (touchable && (this.mLp.flags & 16) != 0) {
            this.mLp.flags &= -17;
            changed = true;
        }
        if (changed) {
            this.mWindowManager.updateViewLayout(this.mView, this.mLp);
        }
    }
}
