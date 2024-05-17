package com.android.systemui.statusbar;

import com.android.systemui.statusbar.notification.TransformState;
/* loaded from: classes21.dex */
public interface TransformableView {
    public static final int TRANSFORMING_VIEW_ACTIONS = 5;
    public static final int TRANSFORMING_VIEW_ICON = 0;
    public static final int TRANSFORMING_VIEW_IMAGE = 3;
    public static final int TRANSFORMING_VIEW_PROGRESS = 4;
    public static final int TRANSFORMING_VIEW_TEXT = 2;
    public static final int TRANSFORMING_VIEW_TITLE = 1;

    TransformState getCurrentState(int i);

    void setVisible(boolean z);

    void transformFrom(TransformableView transformableView);

    void transformFrom(TransformableView transformableView, float f);

    void transformTo(TransformableView transformableView, float f);

    void transformTo(TransformableView transformableView, Runnable runnable);
}
