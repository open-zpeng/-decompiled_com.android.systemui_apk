package com.xiaopeng.systemui.statusbar.MaskLayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.RemoteException;
import com.xiaopeng.lib.apirouter.ApiRouter;
import com.xiaopeng.lib.utils.SystemPropertyUtil;
import com.xiaopeng.lib.utils.ThreadUtils;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.app.XMaskLayer;
/* loaded from: classes24.dex */
public class WatermarkPresenter {
    private static final String ACTION_DIAGNOSTIC_LOGIN = "com.xiaopeng.broadcast.ACTION_DIAGNOSTIC_LOGIN";
    private static final String ACTION_DIAGNOSTIC_LOGOUT = "com.xiaopeng.broadcast.ACTION_DIAGNOSTIC_LOGOUT";
    private static final String TAG = "WatermarkPresenter";
    private boolean DiagnosticModeMarkShown;
    private boolean RepairModeMarkShown;
    private boolean hasSecondaryWindow;
    private String mAccountNumber;
    private BroadcastReceiver mBroadcastReceiver;
    private Context mContext;
    private IMaskLayer mDiagnosticModeMaskLayer;
    private IMaskLayer mRepairModeMaskLayer;
    private String mVINNumber;
    private WatermarkView mWatermarkView0;
    private WatermarkView mWatermarkView1;
    private XMaskLayer xMaskLayer0;
    private XMaskLayer xMaskLayer1;

    public IMaskLayer getDiagnosticModeMaskLayer() {
        if (this.mDiagnosticModeMaskLayer == null) {
            this.mDiagnosticModeMaskLayer = new DiagnosticModeMaskLayer();
        }
        return this.mDiagnosticModeMaskLayer;
    }

    public IMaskLayer getRepairModeMaskLayer() {
        if (this.mRepairModeMaskLayer == null) {
            this.mRepairModeMaskLayer = new RepairModeMaskLayer();
        }
        return this.mRepairModeMaskLayer;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class SingleHolder {
        private static final WatermarkPresenter sInstance = new WatermarkPresenter();

        private SingleHolder() {
        }
    }

    public static WatermarkPresenter getInstance() {
        return SingleHolder.sInstance;
    }

    private WatermarkPresenter() {
        this.RepairModeMarkShown = false;
        this.DiagnosticModeMarkShown = false;
        this.hasSecondaryWindow = CarModelsManager.getFeature().isSecondaryWindowSupport();
        Logger.d(TAG, "start");
        this.mContext = ContextUtils.getContext();
    }

    private void registerReceiver() {
        if (this.mBroadcastReceiver == null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_DIAGNOSTIC_LOGIN);
            filter.addAction(ACTION_DIAGNOSTIC_LOGOUT);
            this.mBroadcastReceiver = new BroadcastReceiver() { // from class: com.xiaopeng.systemui.statusbar.MaskLayer.WatermarkPresenter.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    if (intent == null) {
                        return;
                    }
                    Logger.d(WatermarkPresenter.TAG, "onReceive : " + intent.getAction());
                    String action = intent.getAction();
                    char c = 65535;
                    int hashCode = action.hashCode();
                    if (hashCode != -545160167) {
                        if (hashCode == 279910074 && action.equals(WatermarkPresenter.ACTION_DIAGNOSTIC_LOGOUT)) {
                            c = 1;
                        }
                    } else if (action.equals(WatermarkPresenter.ACTION_DIAGNOSTIC_LOGIN)) {
                        c = 0;
                    }
                    if (c == 0) {
                        WatermarkPresenter.this.setAccountNumber(intent.getStringExtra("account"));
                        WatermarkPresenter.this.lambda$updateLoginAccount$0$WatermarkPresenter();
                    } else if (c == 1) {
                        WatermarkPresenter.this.setAccountNumber("");
                        WatermarkPresenter.this.lambda$updateLoginAccount$0$WatermarkPresenter();
                    }
                }
            };
            this.mContext.registerReceiver(this.mBroadcastReceiver, filter);
        }
    }

    private void unregisterReceiver() {
        BroadcastReceiver broadcastReceiver = this.mBroadcastReceiver;
        if (broadcastReceiver != null) {
            this.mContext.unregisterReceiver(broadcastReceiver);
            this.mBroadcastReceiver = null;
        }
    }

    private void attachWatermark() {
        XMaskLayer.XMaskLayerBuilder builder = new XMaskLayer.XMaskLayerBuilder();
        this.mWatermarkView0 = new WatermarkView(this.mContext);
        this.xMaskLayer0 = builder.setContext(Xui.getContext()).setClickable(false).setStackWindow(true).setScreenId(0).setItemView(this.mWatermarkView0).create();
        if (this.hasSecondaryWindow) {
            this.mWatermarkView1 = new WatermarkView(this.mContext);
            this.xMaskLayer1 = builder.setContext(Xui.getContext()).setClickable(false).setStackWindow(true).setScreenId(1).setItemView(this.mWatermarkView1).create();
        }
        this.xMaskLayer0.show();
        if (this.hasSecondaryWindow) {
            this.xMaskLayer1.show();
        }
    }

    private void removeView() {
        WatermarkView watermarkView = this.mWatermarkView0;
        if (watermarkView != null && watermarkView.getParent() != null) {
            try {
                this.xMaskLayer0.cancel();
                if (this.hasSecondaryWindow) {
                    this.xMaskLayer1.cancel();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            this.xMaskLayer0 = null;
            this.xMaskLayer1 = null;
            this.mWatermarkView0 = null;
            this.mWatermarkView1 = null;
        }
    }

    private void updateLoginAccount() {
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.statusbar.MaskLayer.-$$Lambda$WatermarkPresenter$244Ov6rykVwrQ_ssxVuKE0y1FS0
            @Override // java.lang.Runnable
            public final void run() {
                WatermarkPresenter.this.lambda$updateLoginAccount$1$WatermarkPresenter();
            }
        });
    }

    public /* synthetic */ void lambda$updateLoginAccount$1$WatermarkPresenter() {
        Uri.Builder builder = new Uri.Builder();
        try {
            String[] result = {(String) ApiRouter.route(builder.authority("com.xiaopeng.diagnostic.DiagnoseService").path("getLoginAccount").build())};
            Logger.d(TAG, "getLoginAccount :" + result[0]);
            setAccountNumber(result[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.statusbar.MaskLayer.-$$Lambda$WatermarkPresenter$TLvXAxEg-Vd_X6BQnMEZcjQDgqI
            @Override // java.lang.Runnable
            public final void run() {
                WatermarkPresenter.this.lambda$updateLoginAccount$0$WatermarkPresenter();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAccountNumber(String number) {
        this.mAccountNumber = number;
    }

    private void updateVIN() {
        String result = "";
        try {
            result = SystemPropertyUtil.getVIN();
            Logger.d(TAG, "getVIN :" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mVINNumber = result;
    }

    public void updateDiagnosticMode(boolean on) {
        Logger.d(TAG, "Watermark, updateDiagnosticMode : " + on + " DiagnosticModeMarkShown : " + this.DiagnosticModeMarkShown);
        if (this.DiagnosticModeMarkShown != on) {
            this.DiagnosticModeMarkShown = on;
            updateWatermarkView();
        }
    }

    public void updateRepairMode(boolean on) {
        Logger.d(TAG, "Watermark, updateRepairMode :  = " + on + " RepairModeMarkShown : " + this.RepairModeMarkShown);
        if (this.RepairModeMarkShown != on) {
            this.RepairModeMarkShown = on;
            updateWatermarkView();
        }
    }

    public void updateWatermarkView() {
        WatermarkView watermarkView;
        if (this.RepairModeMarkShown || this.DiagnosticModeMarkShown) {
            WatermarkView watermarkView2 = this.mWatermarkView0;
            if (watermarkView2 == null || watermarkView2.getParent() == null) {
                attachWatermark();
                if (this.RepairModeMarkShown) {
                    registerReceiver();
                }
            }
            updateVIN();
            if (this.RepairModeMarkShown) {
                updateLoginAccount();
                lambda$updateLoginAccount$0$WatermarkPresenter();
                return;
            }
            this.mWatermarkView0.setDiagnosticMode(this.mVINNumber);
            if (this.hasSecondaryWindow && (watermarkView = this.mWatermarkView1) != null) {
                watermarkView.setDiagnosticMode(this.mVINNumber);
                return;
            }
            return;
        }
        removeView();
        unregisterReceiver();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updateAccountView */
    public void lambda$updateLoginAccount$0$WatermarkPresenter() {
        WatermarkView watermarkView;
        WatermarkView watermarkView2 = this.mWatermarkView0;
        if (watermarkView2 != null) {
            watermarkView2.setRepairMode(this.mVINNumber, this.mAccountNumber);
            if (this.hasSecondaryWindow && (watermarkView = this.mWatermarkView1) != null) {
                watermarkView.setRepairMode(this.mVINNumber, this.mAccountNumber);
            }
        }
    }
}
