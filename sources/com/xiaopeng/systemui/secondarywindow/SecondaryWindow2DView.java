package com.xiaopeng.systemui.secondarywindow;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.WindowManager;
import com.android.systemui.R;
import com.xiaopeng.appstore.storeprovider.AssembleInfo;
import com.xiaopeng.appstore.storeprovider.AssembleResult;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.helper.WindowHelper;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.statusbar.StatusBarGlobal;
/* loaded from: classes24.dex */
public class SecondaryWindow2DView implements ISecondaryWindowView {
    private static final String TAG = "SecondaryWindow2DView";
    private SecondaryWindow mSecondaryWindow;
    private Context mContext = ContextUtils.getContext();
    private WindowManager mWindowManager = StatusBarGlobal.getInstance(this.mContext).getWindowManager();

    public SecondaryWindow2DView() {
        if (CarModelsManager.getFeature().isSecondaryWindowSupport()) {
            attachSecondaryWindow();
        }
    }

    private void attachSecondaryWindow() {
        Logger.d(TAG, "attachSecondaryWindow");
        this.mSecondaryWindow = (SecondaryWindow) View.inflate(this.mContext, R.layout.layout_secondary_window, null);
        WindowHelper.addSecondaryWindow(this.mWindowManager, this.mSecondaryWindow);
    }

    @Override // com.xiaopeng.systemui.secondarywindow.ISecondaryWindowView
    public void notifyDownloadInfo(AssembleInfo assembleInfo) {
        SecondaryWindow secondaryWindow = this.mSecondaryWindow;
        if (secondaryWindow != null) {
            secondaryWindow.notifyDownloadInfo(assembleInfo);
        }
    }

    @Override // com.xiaopeng.systemui.secondarywindow.ISecondaryWindowView
    public void dispatchConfigurationChanged(Configuration newConfig) {
        SecondaryWindow secondaryWindow = this.mSecondaryWindow;
        if (secondaryWindow != null) {
            secondaryWindow.dispatchConfigurationChanged(newConfig);
        }
    }

    @Override // com.xiaopeng.systemui.secondarywindow.ISecondaryWindowView
    public void onActivityChanged(String pkgName) {
        SecondaryWindow secondaryWindow = this.mSecondaryWindow;
        if (secondaryWindow != null) {
            secondaryWindow.onActivityChanged(pkgName);
        }
    }

    @Override // com.xiaopeng.systemui.secondarywindow.ISecondaryWindowView
    public void setPsnVolume(int volume) {
        SecondaryWindow secondaryWindow = this.mSecondaryWindow;
        if (secondaryWindow != null) {
            secondaryWindow.setPsnVolume(volume);
        }
    }

    @Override // com.xiaopeng.systemui.secondarywindow.ISecondaryWindowView
    public void notifyUninstallResult(String packageName, int returnCode) {
        SecondaryWindow secondaryWindow = this.mSecondaryWindow;
        if (secondaryWindow != null) {
            secondaryWindow.notifyUninstallResult(packageName, returnCode);
        }
    }

    @Override // com.xiaopeng.systemui.secondarywindow.ISecondaryWindowView
    public void setPsnBluetoothState(int state) {
        SecondaryWindow secondaryWindow = this.mSecondaryWindow;
        if (secondaryWindow != null) {
            secondaryWindow.setPsnBluetoothState(state);
        }
    }

    @Override // com.xiaopeng.systemui.secondarywindow.ISecondaryWindowView
    public void notifyDownloadResult(String pkgName, AssembleResult assembleResult) {
        SecondaryWindow secondaryWindow = this.mSecondaryWindow;
        if (secondaryWindow != null) {
            secondaryWindow.notifyDownloadResult(pkgName, assembleResult);
        }
    }
}
