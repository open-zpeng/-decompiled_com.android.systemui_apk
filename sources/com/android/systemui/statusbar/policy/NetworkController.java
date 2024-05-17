package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.content.Intent;
import android.telephony.SubscriptionInfo;
import com.android.settingslib.net.DataUsageController;
import com.android.settingslib.wifi.AccessPoint;
import com.android.systemui.DemoMode;
import java.util.List;
/* loaded from: classes21.dex */
public interface NetworkController extends CallbackController<SignalCallback>, DemoMode {

    /* loaded from: classes21.dex */
    public interface AccessPointController {

        /* loaded from: classes21.dex */
        public interface AccessPointCallback {
            void onAccessPointsChanged(List<AccessPoint> list);

            void onSettingsActivityTriggered(Intent intent);
        }

        void addAccessPointCallback(AccessPointCallback accessPointCallback);

        boolean canConfigWifi();

        boolean connect(AccessPoint accessPoint);

        int getIcon(AccessPoint accessPoint);

        void removeAccessPointCallback(AccessPointCallback accessPointCallback);

        void scanForAccessPoints();
    }

    /* loaded from: classes21.dex */
    public interface EmergencyListener {
        void setEmergencyCallsOnly(boolean z);
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    void addCallback(SignalCallback signalCallback);

    void addEmergencyListener(EmergencyListener emergencyListener);

    AccessPointController getAccessPointController();

    DataSaverController getDataSaverController();

    DataUsageController getMobileDataController();

    String getMobileDataNetworkName();

    int getNumberSubscriptions();

    boolean hasEmergencyCryptKeeperText();

    boolean hasMobileDataFeature();

    boolean hasVoiceCallingFeature();

    boolean isRadioOn();

    @Override // com.android.systemui.statusbar.policy.CallbackController
    void removeCallback(SignalCallback signalCallback);

    void removeEmergencyListener(EmergencyListener emergencyListener);

    void setWifiEnabled(boolean z);

    /* loaded from: classes21.dex */
    public interface SignalCallback {
        default void setWifiIndicators(boolean enabled, IconState statusIcon, IconState qsIcon, boolean activityIn, boolean activityOut, String description, boolean isTransient, String statusLabel) {
        }

        default void setMobileDataIndicators(IconState statusIcon, IconState qsIcon, int statusType, int qsType, boolean activityIn, boolean activityOut, CharSequence typeContentDescription, CharSequence typeContentDescriptionHtml, CharSequence description, boolean isWide, int subId, boolean roaming) {
        }

        default void setSubs(List<SubscriptionInfo> subs) {
        }

        default void setNoSims(boolean show, boolean simDetected) {
        }

        default void setEthernetIndicators(IconState icon) {
        }

        default void setIsAirplaneMode(IconState icon) {
        }

        default void setMobileDataEnabled(boolean enabled) {
        }
    }

    /* loaded from: classes21.dex */
    public static class IconState {
        public final String contentDescription;
        public final int icon;
        public final boolean visible;

        public IconState(boolean visible, int icon, String contentDescription) {
            this.visible = visible;
            this.icon = icon;
            this.contentDescription = contentDescription;
        }

        public IconState(boolean visible, int icon, int contentDescription, Context context) {
            this(visible, icon, context.getString(contentDescription));
        }
    }
}
