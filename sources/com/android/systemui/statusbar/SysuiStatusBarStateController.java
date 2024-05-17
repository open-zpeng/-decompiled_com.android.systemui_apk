package com.android.systemui.statusbar;

import com.android.systemui.plugins.statusbar.StatusBarStateController;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes21.dex */
public interface SysuiStatusBarStateController extends StatusBarStateController {
    public static final int RANK_SHELF = 3;
    public static final int RANK_STACK_SCROLLER = 2;
    public static final int RANK_STATUS_BAR = 0;
    public static final int RANK_STATUS_BAR_WINDOW_CONTROLLER = 1;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes21.dex */
    public @interface SbStateListenerRank {
    }

    @Deprecated
    void addCallback(StatusBarStateController.StateListener stateListener, int i);

    boolean fromShadeLocked();

    float getInterpolatedDozeAmount();

    boolean goingToFullShade();

    boolean isKeyguardRequested();

    boolean leaveOpenOnKeyguardHide();

    void setDozeAmount(float f, boolean z);

    boolean setIsDozing(boolean z);

    void setKeyguardRequested(boolean z);

    void setLeaveOpenOnKeyguardHide(boolean z);

    void setPulsing(boolean z);

    boolean setState(int i);

    void setSystemUiVisibility(int i);

    /* loaded from: classes21.dex */
    public static class RankedListener {
        final StatusBarStateController.StateListener mListener;
        final int mRank;

        /* JADX INFO: Access modifiers changed from: package-private */
        public RankedListener(StatusBarStateController.StateListener l, int r) {
            this.mListener = l;
            this.mRank = r;
        }
    }
}
