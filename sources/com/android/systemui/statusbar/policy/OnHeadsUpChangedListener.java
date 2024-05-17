package com.android.systemui.statusbar.policy;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
/* loaded from: classes21.dex */
public interface OnHeadsUpChangedListener {
    default void onHeadsUpPinnedModeChanged(boolean inPinnedMode) {
    }

    default void onHeadsUpPinned(NotificationEntry entry) {
    }

    default void onHeadsUpUnPinned(NotificationEntry entry) {
    }

    default void onHeadsUpStateChanged(NotificationEntry entry, boolean isHeadsUp) {
    }
}
