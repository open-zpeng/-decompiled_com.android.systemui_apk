package com.android.systemui.statusbar.phone;
/* loaded from: classes21.dex */
public interface PanelExpansionListener {
    void onPanelExpansionChanged(float f, boolean z);

    default void onQsExpansionChanged(float expansion) {
    }
}
