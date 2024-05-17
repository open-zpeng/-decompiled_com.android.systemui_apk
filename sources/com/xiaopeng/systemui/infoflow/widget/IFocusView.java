package com.xiaopeng.systemui.infoflow.widget;
/* loaded from: classes24.dex */
public interface IFocusView {

    /* loaded from: classes24.dex */
    public interface OnFocusChangedListener {
        void onFocusChangedForViewUpdate(boolean z);

        void onFocusedChanged(boolean z);
    }

    void setFocused(boolean z);

    void setFocused(boolean z, boolean z2);

    void setOnFocusChangedListener(OnFocusChangedListener onFocusChangedListener);

    void setPreFocused(boolean z);
}
