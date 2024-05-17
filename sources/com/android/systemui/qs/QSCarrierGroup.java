package com.android.systemui.qs;

import android.content.Context;
import android.content.Intent;
import android.telephony.SubscriptionManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.VisibleForTesting;
import com.android.keyguard.CarrierTextController;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.policy.NetworkController;
import javax.inject.Inject;
import javax.inject.Named;
/* loaded from: classes21.dex */
public class QSCarrierGroup extends LinearLayout implements CarrierTextController.CarrierTextCallback, NetworkController.SignalCallback, View.OnClickListener {
    private static final int SIM_SLOTS = 3;
    private static final String TAG = "QSCarrierGroup";
    private ActivityStarter mActivityStarter;
    private View[] mCarrierDividers;
    private QSCarrier[] mCarrierGroups;
    private CarrierTextController mCarrierTextController;
    private final CellSignalState[] mInfos;
    private boolean mListening;
    private final NetworkController mNetworkController;
    private TextView mNoSimTextView;

    @Inject
    public QSCarrierGroup(@Named("view_context") Context context, AttributeSet attrs, NetworkController networkController, ActivityStarter activityStarter) {
        super(context, attrs);
        this.mCarrierDividers = new View[2];
        this.mCarrierGroups = new QSCarrier[3];
        this.mInfos = new CellSignalState[3];
        this.mNetworkController = networkController;
        this.mActivityStarter = activityStarter;
    }

    @VisibleForTesting
    public QSCarrierGroup(Context context, AttributeSet attrs) {
        this(context, attrs, (NetworkController) Dependency.get(NetworkController.class), (ActivityStarter) Dependency.get(ActivityStarter.class));
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v.isVisibleToUser()) {
            this.mActivityStarter.postStartActivityDismissingKeyguard(new Intent("android.settings.WIRELESS_SETTINGS"), 0);
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mCarrierGroups[0] = (QSCarrier) findViewById(R.id.carrier1);
        this.mCarrierGroups[1] = (QSCarrier) findViewById(R.id.carrier2);
        this.mCarrierGroups[2] = (QSCarrier) findViewById(R.id.carrier3);
        this.mCarrierDividers[0] = findViewById(R.id.qs_carrier_divider1);
        this.mCarrierDividers[1] = findViewById(R.id.qs_carrier_divider2);
        this.mNoSimTextView = (TextView) findViewById(R.id.no_carrier_text);
        for (int i = 0; i < 3; i++) {
            this.mInfos[i] = new CellSignalState();
            this.mCarrierGroups[i].setOnClickListener(this);
        }
        this.mNoSimTextView.setOnClickListener(this);
        CharSequence separator = this.mContext.getString(17040227);
        this.mCarrierTextController = new CarrierTextController(this.mContext, separator, false, false);
        setImportantForAccessibility(1);
    }

    public void setListening(boolean listening) {
        if (listening == this.mListening) {
            return;
        }
        this.mListening = listening;
        updateListeners();
    }

    @Override // android.view.ViewGroup, android.view.View
    @VisibleForTesting
    public void onDetachedFromWindow() {
        setListening(false);
        super.onDetachedFromWindow();
    }

    private void updateListeners() {
        if (this.mListening) {
            if (this.mNetworkController.hasVoiceCallingFeature()) {
                this.mNetworkController.addCallback((NetworkController.SignalCallback) this);
            }
            this.mCarrierTextController.setListening(this);
            return;
        }
        this.mNetworkController.removeCallback((NetworkController.SignalCallback) this);
        this.mCarrierTextController.setListening(null);
    }

    private void handleUpdateState() {
        for (int i = 0; i < 3; i++) {
            this.mCarrierGroups[i].updateState(this.mInfos[i]);
        }
        int i2 = 0;
        this.mCarrierDividers[0].setVisibility((this.mInfos[0].visible && this.mInfos[1].visible) ? 0 : 8);
        View view = this.mCarrierDividers[1];
        if ((!this.mInfos[1].visible || !this.mInfos[2].visible) && (!this.mInfos[0].visible || !this.mInfos[2].visible)) {
            i2 = 8;
        }
        view.setVisibility(i2);
    }

    @VisibleForTesting
    protected int getSlotIndex(int subscriptionId) {
        return SubscriptionManager.getSlotIndex(subscriptionId);
    }

    @Override // com.android.keyguard.CarrierTextController.CarrierTextCallback
    public void updateCarrierInfo(CarrierTextController.CarrierTextCallbackInfo info) {
        this.mNoSimTextView.setVisibility(8);
        if (!info.airplaneMode && info.anySimReady) {
            boolean[] slotSeen = new boolean[3];
            if (info.listOfCarriers.length == info.subscriptionIds.length) {
                for (int i = 0; i < 3 && i < info.listOfCarriers.length; i++) {
                    int slot = getSlotIndex(info.subscriptionIds[i]);
                    if (slot >= 3) {
                        Log.w(TAG, "updateInfoCarrier - slot: " + slot);
                    } else if (slot == -1) {
                        Log.e(TAG, "Invalid SIM slot index for subscription: " + info.subscriptionIds[i]);
                    } else {
                        this.mInfos[slot].visible = true;
                        slotSeen[slot] = true;
                        this.mCarrierGroups[slot].setCarrierText(info.listOfCarriers[i].toString().trim());
                        this.mCarrierGroups[slot].setVisibility(0);
                    }
                }
                for (int i2 = 0; i2 < 3; i2++) {
                    if (!slotSeen[i2]) {
                        this.mInfos[i2].visible = false;
                        this.mCarrierGroups[i2].setVisibility(8);
                    }
                }
            } else {
                Log.e(TAG, "Carrier information arrays not of same length");
            }
        } else {
            for (int i3 = 0; i3 < 3; i3++) {
                this.mInfos[i3].visible = false;
                this.mCarrierGroups[i3].setCarrierText("");
                this.mCarrierGroups[i3].setVisibility(8);
            }
            this.mNoSimTextView.setText(info.carrierText);
            this.mNoSimTextView.setVisibility(0);
        }
        handleUpdateState();
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController.SignalCallback
    public void setMobileDataIndicators(NetworkController.IconState statusIcon, NetworkController.IconState qsIcon, int statusType, int qsType, boolean activityIn, boolean activityOut, CharSequence typeContentDescription, CharSequence typeContentDescriptionHtml, CharSequence description, boolean isWide, int subId, boolean roaming) {
        int slotIndex = getSlotIndex(subId);
        if (slotIndex >= 3) {
            Log.w(TAG, "setMobileDataIndicators - slot: " + slotIndex);
        } else if (slotIndex == -1) {
            Log.e(TAG, "Invalid SIM slot index for subscription: " + subId);
        } else {
            this.mInfos[slotIndex].visible = statusIcon.visible;
            this.mInfos[slotIndex].mobileSignalIconId = statusIcon.icon;
            this.mInfos[slotIndex].contentDescription = statusIcon.contentDescription;
            this.mInfos[slotIndex].typeContentDescription = typeContentDescription.toString();
            this.mInfos[slotIndex].roaming = roaming;
            handleUpdateState();
        }
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController.SignalCallback
    public void setNoSims(boolean hasNoSims, boolean simDetected) {
        if (hasNoSims) {
            for (int i = 0; i < 3; i++) {
                this.mInfos[i].visible = false;
            }
        }
        handleUpdateState();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public static final class CellSignalState {
        String contentDescription;
        int mobileSignalIconId;
        boolean roaming;
        String typeContentDescription;
        boolean visible;

        CellSignalState() {
        }
    }
}
