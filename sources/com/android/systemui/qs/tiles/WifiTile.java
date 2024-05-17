package com.android.systemui.qs.tiles;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import androidx.lifecycle.Lifecycle;
import com.alibaba.fastjson.asm.Opcodes;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.wifi.AccessPoint;
import com.android.systemui.R;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.qs.DetailAdapter;
import com.android.systemui.plugins.qs.QSIconView;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.AlphaControlledSignalTileView;
import com.android.systemui.qs.QSDetailItems;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.statusbar.policy.NetworkController;
import java.util.List;
import javax.inject.Inject;
/* loaded from: classes21.dex */
public class WifiTile extends QSTileImpl<QSTile.SignalState> {
    private static final Intent WIFI_SETTINGS = new Intent("android.settings.WIFI_SETTINGS");
    private final ActivityStarter mActivityStarter;
    protected final NetworkController mController;
    private final WifiDetailAdapter mDetailAdapter;
    private boolean mExpectDisabled;
    protected final WifiSignalCallback mSignalCallback;
    private final QSTile.SignalState mStateBeforeClick;
    private final NetworkController.AccessPointController mWifiController;

    @Inject
    public WifiTile(QSHost host, NetworkController networkController, ActivityStarter activityStarter) {
        super(host);
        this.mStateBeforeClick = newTileState();
        this.mSignalCallback = new WifiSignalCallback();
        this.mController = networkController;
        this.mWifiController = this.mController.getAccessPointController();
        this.mDetailAdapter = (WifiDetailAdapter) createDetailAdapter();
        this.mActivityStarter = activityStarter;
        this.mController.observe(getLifecycle(), (Lifecycle) this.mSignalCallback);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public QSTile.SignalState newTileState() {
        return new QSTile.SignalState();
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleSetListening(boolean listening) {
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public void setDetailListening(boolean listening) {
        if (listening) {
            this.mWifiController.addAccessPointCallback(this.mDetailAdapter);
        } else {
            this.mWifiController.removeAccessPointCallback(this.mDetailAdapter);
        }
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public DetailAdapter getDetailAdapter() {
        return this.mDetailAdapter;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected DetailAdapter createDetailAdapter() {
        return new WifiDetailAdapter();
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public QSIconView createTileView(Context context) {
        return new AlphaControlledSignalTileView(context);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public Intent getLongClickIntent() {
        return WIFI_SETTINGS;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleClick() {
        ((QSTile.SignalState) this.mState).copyTo(this.mStateBeforeClick);
        boolean wifiEnabled = ((QSTile.SignalState) this.mState).value;
        refreshState(wifiEnabled ? null : ARG_SHOW_TRANSIENT_ENABLING);
        this.mController.setWifiEnabled(!wifiEnabled);
        this.mExpectDisabled = wifiEnabled;
        if (this.mExpectDisabled) {
            this.mHandler.postDelayed(new Runnable() { // from class: com.android.systemui.qs.tiles.-$$Lambda$WifiTile$FBMX-zj483F7uFPAUwutmnquiRU
                @Override // java.lang.Runnable
                public final void run() {
                    WifiTile.this.lambda$handleClick$0$WifiTile();
                }
            }, 350L);
        }
    }

    public /* synthetic */ void lambda$handleClick$0$WifiTile() {
        if (this.mExpectDisabled) {
            this.mExpectDisabled = false;
            refreshState();
        }
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleSecondaryClick() {
        if (!this.mWifiController.canConfigWifi()) {
            this.mActivityStarter.postStartActivityDismissingKeyguard(new Intent("android.settings.WIFI_SETTINGS"), 0);
            return;
        }
        showDetail(true);
        if (!((QSTile.SignalState) this.mState).value) {
            this.mController.setWifiEnabled(true);
        }
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public CharSequence getTileLabel() {
        return this.mContext.getString(R.string.quick_settings_wifi_label);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleUpdateState(QSTile.SignalState state, Object arg) {
        if (DEBUG) {
            String str = this.TAG;
            Log.d(str, "handleUpdateState arg=" + arg);
        }
        CallbackInfo cb = this.mSignalCallback.mInfo;
        if (this.mExpectDisabled) {
            if (cb.enabled) {
                return;
            }
            this.mExpectDisabled = false;
        }
        boolean transientEnabling = arg == ARG_SHOW_TRANSIENT_ENABLING;
        boolean wifiConnected = cb.enabled && cb.wifiSignalIconId > 0 && cb.ssid != null;
        boolean wifiNotConnected = cb.wifiSignalIconId > 0 && cb.ssid == null;
        boolean enabledChanging = state.value != cb.enabled;
        if (enabledChanging) {
            this.mDetailAdapter.setItemsVisible(cb.enabled);
            fireToggleStateChanged(cb.enabled);
        }
        if (state.slash == null) {
            state.slash = new QSTile.SlashState();
            state.slash.rotation = 6.0f;
        }
        state.slash.isSlashed = false;
        boolean isTransient = transientEnabling || cb.isTransient;
        state.secondaryLabel = getSecondaryLabel(isTransient, cb.statusLabel);
        state.state = 2;
        state.dualTarget = true;
        state.value = transientEnabling || cb.enabled;
        state.activityIn = cb.enabled && cb.activityIn;
        state.activityOut = cb.enabled && cb.activityOut;
        StringBuffer minimalContentDescription = new StringBuffer();
        Resources r = this.mContext.getResources();
        if (isTransient) {
            state.icon = QSTileImpl.ResourceIcon.get(17302820);
            state.label = r.getString(R.string.quick_settings_wifi_label);
        } else if (!state.value) {
            state.slash.isSlashed = true;
            state.state = 1;
            state.icon = QSTileImpl.ResourceIcon.get(17302852);
            state.label = r.getString(R.string.quick_settings_wifi_label);
        } else if (wifiConnected) {
            state.icon = QSTileImpl.ResourceIcon.get(cb.wifiSignalIconId);
            state.label = removeDoubleQuotes(cb.ssid);
        } else if (wifiNotConnected) {
            state.icon = QSTileImpl.ResourceIcon.get(R.drawable.ic_qs_wifi_disconnected);
            state.label = r.getString(R.string.quick_settings_wifi_label);
        } else {
            state.icon = QSTileImpl.ResourceIcon.get(17302852);
            state.label = r.getString(R.string.quick_settings_wifi_label);
        }
        minimalContentDescription.append(this.mContext.getString(R.string.quick_settings_wifi_label));
        minimalContentDescription.append(",");
        if (state.value && wifiConnected) {
            minimalContentDescription.append(cb.wifiSignalContentDescription);
            minimalContentDescription.append(",");
            minimalContentDescription.append(removeDoubleQuotes(cb.ssid));
            if (!TextUtils.isEmpty(state.secondaryLabel)) {
                minimalContentDescription.append(",");
                minimalContentDescription.append(state.secondaryLabel);
            }
        }
        state.contentDescription = minimalContentDescription.toString();
        state.dualLabelContentDescription = r.getString(R.string.accessibility_quick_settings_open_settings, getTileLabel());
        state.expandedAccessibilityClassName = Switch.class.getName();
    }

    private CharSequence getSecondaryLabel(boolean isTransient, String statusLabel) {
        if (isTransient) {
            return this.mContext.getString(R.string.quick_settings_wifi_secondary_label_transient);
        }
        return statusLabel;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public int getMetricsCategory() {
        return Opcodes.IAND;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected boolean shouldAnnouncementBeDelayed() {
        return this.mStateBeforeClick.value == ((QSTile.SignalState) this.mState).value;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected String composeChangeAnnouncement() {
        if (((QSTile.SignalState) this.mState).value) {
            return this.mContext.getString(R.string.accessibility_quick_settings_wifi_changed_on);
        }
        return this.mContext.getString(R.string.accessibility_quick_settings_wifi_changed_off);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public boolean isAvailable() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.wifi");
    }

    private static String removeDoubleQuotes(String string) {
        if (string == null) {
            return null;
        }
        int length = string.length();
        if (length > 1 && string.charAt(0) == '\"' && string.charAt(length - 1) == '\"') {
            return string.substring(1, length - 1);
        }
        return string;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes21.dex */
    public static final class CallbackInfo {
        boolean activityIn;
        boolean activityOut;
        boolean connected;
        boolean enabled;
        boolean isTransient;
        String ssid;
        public String statusLabel;
        String wifiSignalContentDescription;
        int wifiSignalIconId;

        protected CallbackInfo() {
        }

        public String toString() {
            return "CallbackInfo[enabled=" + this.enabled + ",connected=" + this.connected + ",wifiSignalIconId=" + this.wifiSignalIconId + ",ssid=" + this.ssid + ",activityIn=" + this.activityIn + ",activityOut=" + this.activityOut + ",wifiSignalContentDescription=" + this.wifiSignalContentDescription + ",isTransient=" + this.isTransient + ']';
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes21.dex */
    public final class WifiSignalCallback implements NetworkController.SignalCallback {
        final CallbackInfo mInfo = new CallbackInfo();

        protected WifiSignalCallback() {
        }

        @Override // com.android.systemui.statusbar.policy.NetworkController.SignalCallback
        public void setWifiIndicators(boolean enabled, NetworkController.IconState statusIcon, NetworkController.IconState qsIcon, boolean activityIn, boolean activityOut, String description, boolean isTransient, String statusLabel) {
            if (WifiTile.DEBUG) {
                String str = WifiTile.this.TAG;
                Log.d(str, "onWifiSignalChanged enabled=" + enabled);
            }
            CallbackInfo callbackInfo = this.mInfo;
            callbackInfo.enabled = enabled;
            callbackInfo.connected = qsIcon.visible;
            this.mInfo.wifiSignalIconId = qsIcon.icon;
            CallbackInfo callbackInfo2 = this.mInfo;
            callbackInfo2.ssid = description;
            callbackInfo2.activityIn = activityIn;
            callbackInfo2.activityOut = activityOut;
            callbackInfo2.wifiSignalContentDescription = qsIcon.contentDescription;
            CallbackInfo callbackInfo3 = this.mInfo;
            callbackInfo3.isTransient = isTransient;
            callbackInfo3.statusLabel = statusLabel;
            if (WifiTile.this.isShowingDetail()) {
                WifiTile.this.mDetailAdapter.updateItems();
            }
            WifiTile.this.refreshState();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes21.dex */
    public class WifiDetailAdapter implements DetailAdapter, NetworkController.AccessPointController.AccessPointCallback, QSDetailItems.Callback {
        private AccessPoint[] mAccessPoints;
        private QSDetailItems mItems;

        protected WifiDetailAdapter() {
        }

        @Override // com.android.systemui.plugins.qs.DetailAdapter
        public CharSequence getTitle() {
            return WifiTile.this.mContext.getString(R.string.quick_settings_wifi_label);
        }

        @Override // com.android.systemui.plugins.qs.DetailAdapter
        public Intent getSettingsIntent() {
            return WifiTile.WIFI_SETTINGS;
        }

        @Override // com.android.systemui.plugins.qs.DetailAdapter
        public Boolean getToggleState() {
            return Boolean.valueOf(((QSTile.SignalState) WifiTile.this.mState).value);
        }

        @Override // com.android.systemui.plugins.qs.DetailAdapter
        public void setToggleState(boolean state) {
            if (WifiTile.DEBUG) {
                String str = WifiTile.this.TAG;
                Log.d(str, "setToggleState " + state);
            }
            MetricsLogger.action(WifiTile.this.mContext, 153, state);
            WifiTile.this.mController.setWifiEnabled(state);
        }

        @Override // com.android.systemui.plugins.qs.DetailAdapter
        public int getMetricsCategory() {
            return 152;
        }

        @Override // com.android.systemui.plugins.qs.DetailAdapter
        public View createDetailView(Context context, View convertView, ViewGroup parent) {
            if (WifiTile.DEBUG) {
                String str = WifiTile.this.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("createDetailView convertView=");
                sb.append(convertView != null);
                Log.d(str, sb.toString());
            }
            this.mAccessPoints = null;
            this.mItems = QSDetailItems.convertOrInflate(context, convertView, parent);
            this.mItems.setTagSuffix("Wifi");
            this.mItems.setCallback(this);
            WifiTile.this.mWifiController.scanForAccessPoints();
            setItemsVisible(((QSTile.SignalState) WifiTile.this.mState).value);
            return this.mItems;
        }

        @Override // com.android.systemui.statusbar.policy.NetworkController.AccessPointController.AccessPointCallback
        public void onAccessPointsChanged(List<AccessPoint> accessPoints) {
            this.mAccessPoints = (AccessPoint[]) accessPoints.toArray(new AccessPoint[accessPoints.size()]);
            filterUnreachableAPs();
            updateItems();
        }

        private void filterUnreachableAPs() {
            int numReachable = 0;
            for (AccessPoint ap : this.mAccessPoints) {
                if (ap.isReachable()) {
                    numReachable++;
                }
            }
            if (numReachable != this.mAccessPoints.length) {
                AccessPoint[] unfiltered = this.mAccessPoints;
                this.mAccessPoints = new AccessPoint[numReachable];
                int i = 0;
                for (AccessPoint ap2 : unfiltered) {
                    if (ap2.isReachable()) {
                        this.mAccessPoints[i] = ap2;
                        i++;
                    }
                }
            }
        }

        @Override // com.android.systemui.statusbar.policy.NetworkController.AccessPointController.AccessPointCallback
        public void onSettingsActivityTriggered(Intent settingsIntent) {
            WifiTile.this.mActivityStarter.postStartActivityDismissingKeyguard(settingsIntent, 0);
        }

        @Override // com.android.systemui.qs.QSDetailItems.Callback
        public void onDetailItemClick(QSDetailItems.Item item) {
            if (item == null || item.tag == null) {
                return;
            }
            AccessPoint ap = (AccessPoint) item.tag;
            if (!ap.isActive() && WifiTile.this.mWifiController.connect(ap)) {
                WifiTile.this.mHost.collapsePanels();
            }
            WifiTile.this.showDetail(false);
        }

        @Override // com.android.systemui.qs.QSDetailItems.Callback
        public void onDetailItemDisconnect(QSDetailItems.Item item) {
        }

        public void setItemsVisible(boolean visible) {
            QSDetailItems qSDetailItems = this.mItems;
            if (qSDetailItems == null) {
                return;
            }
            qSDetailItems.setItemsVisible(visible);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateItems() {
            int i;
            if (this.mItems == null) {
                return;
            }
            AccessPoint[] accessPointArr = this.mAccessPoints;
            if ((accessPointArr != null && accessPointArr.length > 0) || !WifiTile.this.mSignalCallback.mInfo.enabled) {
                WifiTile.this.fireScanStateChanged(false);
            } else {
                WifiTile.this.fireScanStateChanged(true);
            }
            if (!WifiTile.this.mSignalCallback.mInfo.enabled) {
                this.mItems.setEmptyState(17302852, R.string.wifi_is_off);
                this.mItems.setItems(null);
                return;
            }
            this.mItems.setEmptyState(17302852, R.string.quick_settings_wifi_detail_empty_text);
            QSDetailItems.Item[] items = null;
            AccessPoint[] accessPointArr2 = this.mAccessPoints;
            if (accessPointArr2 != null) {
                items = new QSDetailItems.Item[accessPointArr2.length];
                int i2 = 0;
                while (true) {
                    AccessPoint[] accessPointArr3 = this.mAccessPoints;
                    if (i2 >= accessPointArr3.length) {
                        break;
                    }
                    AccessPoint ap = accessPointArr3[i2];
                    QSDetailItems.Item item = new QSDetailItems.Item();
                    item.tag = ap;
                    item.iconResId = WifiTile.this.mWifiController.getIcon(ap);
                    item.line1 = ap.getSsid();
                    item.line2 = ap.isActive() ? ap.getSummary() : null;
                    if (ap.getSecurity() != 0) {
                        i = R.drawable.qs_ic_wifi_lock;
                    } else {
                        i = -1;
                    }
                    item.icon2 = i;
                    items[i2] = item;
                    i2++;
                }
            }
            this.mItems.setItems(items);
        }
    }
}
