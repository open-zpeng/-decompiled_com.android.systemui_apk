package com.android.systemui.statusbar.policy;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.net.Uri;
import android.service.notification.Condition;
import android.service.notification.ZenModeConfig;
/* loaded from: classes21.dex */
public interface ZenModeController extends CallbackController<Callback> {
    boolean areNotificationsHiddenInShade();

    ZenModeConfig getConfig();

    NotificationManager.Policy getConsolidatedPolicy();

    int getCurrentUser();

    ComponentName getEffectsSuppressor();

    ZenModeConfig.ZenRule getManualRule();

    long getNextAlarm();

    int getZen();

    boolean isCountdownConditionSupported();

    boolean isVolumeRestricted();

    boolean isZenAvailable();

    void setZen(int i, Uri uri, String str);

    /* loaded from: classes21.dex */
    public interface Callback {
        default void onZenChanged(int zen) {
        }

        default void onConditionsChanged(Condition[] conditions) {
        }

        default void onNextAlarmChanged() {
        }

        default void onZenAvailableChanged(boolean available) {
        }

        default void onEffectsSupressorChanged() {
        }

        default void onManualRuleChanged(ZenModeConfig.ZenRule rule) {
        }

        default void onConfigChanged(ZenModeConfig config) {
        }

        default void onConsolidatedPolicyChanged(NotificationManager.Policy policy) {
        }
    }
}
