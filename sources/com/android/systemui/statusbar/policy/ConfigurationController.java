package com.android.systemui.statusbar.policy;

import android.content.res.Configuration;
/* loaded from: classes21.dex */
public interface ConfigurationController extends CallbackController<ConfigurationListener> {
    void notifyThemeChanged();

    void onConfigurationChanged(Configuration configuration);

    /* loaded from: classes21.dex */
    public interface ConfigurationListener {
        default void onConfigChanged(Configuration newConfig) {
        }

        default void onDensityOrFontScaleChanged() {
        }

        default void onOverlayChanged() {
        }

        default void onUiModeChanged() {
        }

        default void onThemeChanged() {
        }

        default void onLocaleListChanged() {
        }
    }
}
