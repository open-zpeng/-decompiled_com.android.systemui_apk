package com.android.systemui.qs.tiles;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRouter;
import android.support.v4.media.subtitle.Cea708CCParser;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.lifecycle.LifecycleOwner;
import com.android.internal.app.MediaRouteDialogPresenter;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.qs.DetailAdapter;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSDetailItems;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.statusbar.policy.NetworkController;
import com.xiaopeng.systemui.controller.CarController;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import javax.inject.Inject;
/* loaded from: classes21.dex */
public class CastTile extends QSTileImpl<QSTile.BooleanState> {
    private static final Intent CAST_SETTINGS = new Intent("android.settings.CAST_SETTINGS");
    private final ActivityStarter mActivityStarter;
    private final Callback mCallback;
    private final CastController mController;
    private final CastDetailAdapter mDetailAdapter;
    private Dialog mDialog;
    private final KeyguardMonitor mKeyguard;
    private final NetworkController mNetworkController;
    private final NetworkController.SignalCallback mSignalCallback;
    private boolean mWifiConnected;

    @Inject
    public CastTile(QSHost host, CastController castController, KeyguardMonitor keyguardMonitor, NetworkController networkController, ActivityStarter activityStarter) {
        super(host);
        this.mCallback = new Callback();
        this.mSignalCallback = new NetworkController.SignalCallback() { // from class: com.android.systemui.qs.tiles.CastTile.1
            @Override // com.android.systemui.statusbar.policy.NetworkController.SignalCallback
            public void setWifiIndicators(boolean enabled, NetworkController.IconState statusIcon, NetworkController.IconState qsIcon, boolean activityIn, boolean activityOut, String description, boolean isTransient, String statusLabel) {
                boolean enabledAndConnected = enabled && qsIcon.visible;
                if (enabledAndConnected != CastTile.this.mWifiConnected) {
                    CastTile.this.mWifiConnected = enabledAndConnected;
                    CastTile.this.refreshState();
                }
            }
        };
        this.mController = castController;
        this.mDetailAdapter = new CastDetailAdapter();
        this.mKeyguard = keyguardMonitor;
        this.mNetworkController = networkController;
        this.mActivityStarter = activityStarter;
        this.mController.observe((LifecycleOwner) this, (CastTile) this.mCallback);
        this.mKeyguard.observe((LifecycleOwner) this, (CastTile) this.mCallback);
        this.mNetworkController.observe((LifecycleOwner) this, (CastTile) this.mSignalCallback);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public DetailAdapter getDetailAdapter() {
        return this.mDetailAdapter;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public QSTile.BooleanState newTileState() {
        QSTile.BooleanState state = new QSTile.BooleanState();
        state.handlesLongClick = false;
        return state;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleSetListening(boolean listening) {
        if (DEBUG) {
            String str = this.TAG;
            Log.d(str, "handleSetListening " + listening);
        }
        if (!listening) {
            this.mController.setDiscovering(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleUserSwitch(int newUserId) {
        super.handleUserSwitch(newUserId);
        this.mController.setCurrentUserId(newUserId);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public Intent getLongClickIntent() {
        return new Intent("android.settings.CAST_SETTINGS");
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleSecondaryClick() {
        handleClick();
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleLongClick() {
        handleClick();
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleClick() {
        if (getState().state == 0) {
            return;
        }
        List<CastController.CastDevice> activeDevices = getActiveDevices();
        if (activeDevices.isEmpty() || (activeDevices.get(0).tag instanceof MediaRouter.RouteInfo)) {
            this.mActivityStarter.postQSRunnableDismissingKeyguard(new Runnable() { // from class: com.android.systemui.qs.tiles.-$$Lambda$CastTile$0TU5SvbFGUs5F0udF1tvlhHVObs
                @Override // java.lang.Runnable
                public final void run() {
                    CastTile.this.lambda$handleClick$0$CastTile();
                }
            });
        } else {
            this.mController.stopCasting(activeDevices.get(0));
        }
    }

    public /* synthetic */ void lambda$handleClick$0$CastTile() {
        showDetail(true);
    }

    private List<CastController.CastDevice> getActiveDevices() {
        ArrayList<CastController.CastDevice> activeDevices = new ArrayList<>();
        for (CastController.CastDevice device : this.mController.getCastDevices()) {
            if (device.state == 2 || device.state == 1) {
                activeDevices.add(device);
            }
        }
        return activeDevices;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void showDetail(boolean show) {
        this.mUiHandler.post(new Runnable() { // from class: com.android.systemui.qs.tiles.-$$Lambda$CastTile$WPXsuhhRJ1um-wt53q0kaFd3rzI
            @Override // java.lang.Runnable
            public final void run() {
                CastTile.this.lambda$showDetail$3$CastTile();
            }
        });
    }

    public /* synthetic */ void lambda$showDetail$3$CastTile() {
        this.mDialog = MediaRouteDialogPresenter.createDialog(this.mContext, 4, new View.OnClickListener() { // from class: com.android.systemui.qs.tiles.-$$Lambda$CastTile$4kXW6ECEqBpSUmuEtdBz8p9QY1w
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CastTile.this.lambda$showDetail$1$CastTile(view);
            }
        });
        this.mDialog.getWindow().setType(CarController.TYPE_CAR_CLTC_DRIVE_DISTANCE);
        SystemUIDialog.setShowForAllUsers(this.mDialog, true);
        SystemUIDialog.registerDismissListener(this.mDialog);
        SystemUIDialog.setWindowOnTop(this.mDialog);
        this.mUiHandler.post(new Runnable() { // from class: com.android.systemui.qs.tiles.-$$Lambda$CastTile$MhJepZXXVH2Vaj80AOmfpppL58s
            @Override // java.lang.Runnable
            public final void run() {
                CastTile.this.lambda$showDetail$2$CastTile();
            }
        });
        this.mHost.collapsePanels();
    }

    public /* synthetic */ void lambda$showDetail$1$CastTile(View v) {
        this.mDialog.dismiss();
        this.mActivityStarter.postStartActivityDismissingKeyguard(getLongClickIntent(), 0);
    }

    public /* synthetic */ void lambda$showDetail$2$CastTile() {
        this.mDialog.show();
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public CharSequence getTileLabel() {
        return this.mContext.getString(R.string.quick_settings_cast_title);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleUpdateState(QSTile.BooleanState state, Object arg) {
        state.label = this.mContext.getString(R.string.quick_settings_cast_title);
        state.contentDescription = state.label;
        state.value = false;
        List<CastController.CastDevice> devices = this.mController.getCastDevices();
        boolean connecting = false;
        Iterator<CastController.CastDevice> it = devices.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            CastController.CastDevice device = it.next();
            if (device.state == 2) {
                state.value = true;
                state.secondaryLabel = getDeviceName(device);
                state.contentDescription = ((Object) state.contentDescription) + "," + this.mContext.getString(R.string.accessibility_cast_name, state.label);
                connecting = false;
                break;
            } else if (device.state == 1) {
                connecting = true;
            }
        }
        if (connecting && !state.value) {
            state.secondaryLabel = this.mContext.getString(R.string.quick_settings_connecting);
        }
        state.icon = QSTileImpl.ResourceIcon.get(state.value ? R.drawable.ic_cast_connected : R.drawable.ic_cast);
        if (this.mWifiConnected || state.value) {
            state.state = state.value ? 2 : 1;
            if (!state.value) {
                state.secondaryLabel = "";
            }
            state.contentDescription = ((Object) state.contentDescription) + "," + this.mContext.getString(R.string.accessibility_quick_settings_open_details);
            state.expandedAccessibilityClassName = Button.class.getName();
        } else {
            state.state = 0;
            String noWifi = this.mContext.getString(R.string.quick_settings_cast_no_wifi);
            state.secondaryLabel = noWifi;
            state.contentDescription = ((Object) state.contentDescription) + ", " + this.mContext.getString(R.string.accessibility_quick_settings_not_available, noWifi);
        }
        this.mDetailAdapter.updateItems(devices);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public int getMetricsCategory() {
        return 114;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected String composeChangeAnnouncement() {
        if (!((QSTile.BooleanState) this.mState).value) {
            return this.mContext.getString(R.string.accessibility_casting_turned_off);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getDeviceName(CastController.CastDevice device) {
        return device.name != null ? device.name : this.mContext.getString(R.string.quick_settings_cast_device_default_name);
    }

    /* loaded from: classes21.dex */
    private final class Callback implements CastController.Callback, KeyguardMonitor.Callback {
        private Callback() {
        }

        @Override // com.android.systemui.statusbar.policy.CastController.Callback
        public void onCastDevicesChanged() {
            CastTile.this.refreshState();
        }

        @Override // com.android.systemui.statusbar.policy.KeyguardMonitor.Callback
        public void onKeyguardShowingChanged() {
            CastTile.this.refreshState();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public final class CastDetailAdapter implements DetailAdapter, QSDetailItems.Callback {
        private QSDetailItems mItems;
        private final LinkedHashMap<String, CastController.CastDevice> mVisibleOrder;

        private CastDetailAdapter() {
            this.mVisibleOrder = new LinkedHashMap<>();
        }

        @Override // com.android.systemui.plugins.qs.DetailAdapter
        public CharSequence getTitle() {
            return CastTile.this.mContext.getString(R.string.quick_settings_cast_title);
        }

        @Override // com.android.systemui.plugins.qs.DetailAdapter
        public Boolean getToggleState() {
            return null;
        }

        @Override // com.android.systemui.plugins.qs.DetailAdapter
        public Intent getSettingsIntent() {
            return CastTile.CAST_SETTINGS;
        }

        @Override // com.android.systemui.plugins.qs.DetailAdapter
        public void setToggleState(boolean state) {
        }

        @Override // com.android.systemui.plugins.qs.DetailAdapter
        public int getMetricsCategory() {
            return 151;
        }

        @Override // com.android.systemui.plugins.qs.DetailAdapter
        public View createDetailView(Context context, View convertView, ViewGroup parent) {
            this.mItems = QSDetailItems.convertOrInflate(context, convertView, parent);
            this.mItems.setTagSuffix("Cast");
            if (convertView == null) {
                if (CastTile.DEBUG) {
                    Log.d(CastTile.this.TAG, "addOnAttachStateChangeListener");
                }
                this.mItems.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: com.android.systemui.qs.tiles.CastTile.CastDetailAdapter.1
                    @Override // android.view.View.OnAttachStateChangeListener
                    public void onViewAttachedToWindow(View v) {
                        if (CastTile.DEBUG) {
                            Log.d(CastTile.this.TAG, "onViewAttachedToWindow");
                        }
                    }

                    @Override // android.view.View.OnAttachStateChangeListener
                    public void onViewDetachedFromWindow(View v) {
                        if (CastTile.DEBUG) {
                            Log.d(CastTile.this.TAG, "onViewDetachedFromWindow");
                        }
                        CastDetailAdapter.this.mVisibleOrder.clear();
                    }
                });
            }
            this.mItems.setEmptyState(R.drawable.ic_qs_cast_detail_empty, R.string.quick_settings_cast_detail_empty_text);
            this.mItems.setCallback(this);
            updateItems(CastTile.this.mController.getCastDevices());
            CastTile.this.mController.setDiscovering(true);
            return this.mItems;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateItems(List<CastController.CastDevice> devices) {
            if (this.mItems == null) {
                return;
            }
            QSDetailItems.Item[] items = null;
            if (devices != null && !devices.isEmpty()) {
                Iterator<CastController.CastDevice> it = devices.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    CastController.CastDevice device = it.next();
                    if (device.state == 2) {
                        QSDetailItems.Item item = new QSDetailItems.Item();
                        item.iconResId = R.drawable.ic_cast_connected;
                        item.line1 = CastTile.this.getDeviceName(device);
                        item.line2 = CastTile.this.mContext.getString(R.string.quick_settings_connected);
                        item.tag = device;
                        item.canDisconnect = true;
                        items = new QSDetailItems.Item[]{item};
                        break;
                    }
                }
                if (items == null) {
                    for (CastController.CastDevice device2 : devices) {
                        this.mVisibleOrder.put(device2.id, device2);
                    }
                    items = new QSDetailItems.Item[devices.size()];
                    int i = 0;
                    for (String id : this.mVisibleOrder.keySet()) {
                        CastController.CastDevice device3 = this.mVisibleOrder.get(id);
                        if (devices.contains(device3)) {
                            QSDetailItems.Item item2 = new QSDetailItems.Item();
                            item2.iconResId = R.drawable.ic_cast;
                            item2.line1 = CastTile.this.getDeviceName(device3);
                            if (device3.state == 1) {
                                item2.line2 = CastTile.this.mContext.getString(R.string.quick_settings_connecting);
                            }
                            item2.tag = device3;
                            items[i] = item2;
                            i++;
                        }
                    }
                }
            }
            this.mItems.setItems(items);
        }

        @Override // com.android.systemui.qs.QSDetailItems.Callback
        public void onDetailItemClick(QSDetailItems.Item item) {
            if (item != null && item.tag != null) {
                MetricsLogger.action(CastTile.this.mContext, (int) Cea708CCParser.Const.CODE_C1_DF5);
                CastController.CastDevice device = (CastController.CastDevice) item.tag;
                CastTile.this.mController.startCasting(device);
            }
        }

        @Override // com.android.systemui.qs.QSDetailItems.Callback
        public void onDetailItemDisconnect(QSDetailItems.Item item) {
            if (item != null && item.tag != null) {
                MetricsLogger.action(CastTile.this.mContext, 158);
                CastController.CastDevice device = (CastController.CastDevice) item.tag;
                CastTile.this.mController.stopCasting(device);
            }
        }
    }
}
