package com.android.systemui.plugins.qs;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.plugins.FragmentBase;
import com.android.systemui.plugins.annotations.DependsOn;
import com.android.systemui.plugins.annotations.ProvidesInterface;
@ProvidesInterface(action = QS.ACTION, version = 7)
@DependsOn(target = HeightListener.class)
/* loaded from: classes21.dex */
public interface QS extends FragmentBase {
    public static final String ACTION = "com.android.systemui.action.PLUGIN_QS";
    public static final String TAG = "QS";
    public static final int VERSION = 7;

    @ProvidesInterface(version = 1)
    /* loaded from: classes21.dex */
    public interface HeightListener {
        public static final int VERSION = 1;

        void onQsHeightChanged();
    }

    void animateHeaderSlidingIn(long j);

    void animateHeaderSlidingOut();

    void closeDetail();

    int getDesiredHeight();

    View getHeader();

    int getQsMinExpansionHeight();

    void hideImmediately();

    boolean isCustomizing();

    boolean isShowingDetail();

    void notifyCustomizeChanged();

    void setContainer(ViewGroup viewGroup);

    void setExpandClickListener(View.OnClickListener onClickListener);

    void setExpanded(boolean z);

    void setHeaderClickable(boolean z);

    void setHeaderListening(boolean z);

    void setHeightOverride(int i);

    void setListening(boolean z);

    void setOverscrolling(boolean z);

    void setPanelView(HeightListener heightListener);

    void setQsExpansion(float f, float f2);

    default void setShowCollapsedOnKeyguard(boolean showCollapsedOnKeyguard) {
    }

    default void setHasNotifications(boolean hasNotifications) {
    }

    default boolean onInterceptTouchEvent(MotionEvent event) {
        return isCustomizing();
    }
}
