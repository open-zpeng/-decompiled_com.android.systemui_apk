package com.android.systemui.recents;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import com.android.systemui.SysUiServiceProvider;
import java.io.PrintWriter;
/* loaded from: classes21.dex */
interface RecentsImplementation {
    default void onStart(Context context, SysUiServiceProvider sysUiServiceProvider) {
    }

    default void onBootCompleted() {
    }

    default void onAppTransitionFinished() {
    }

    default void onConfigurationChanged(Configuration newConfig) {
    }

    default void preloadRecentApps() {
    }

    default void cancelPreloadRecentApps() {
    }

    default void showRecentApps(boolean triggeredFromAltTab) {
    }

    default void hideRecentApps(boolean triggeredFromAltTab, boolean triggeredFromHomeKey) {
    }

    default void toggleRecentApps() {
    }

    default void growRecents() {
    }

    default boolean splitPrimaryTask(int stackCreateMode, Rect initialBounds, int metricsDockAction) {
        return false;
    }

    default void dump(PrintWriter pw) {
    }
}
