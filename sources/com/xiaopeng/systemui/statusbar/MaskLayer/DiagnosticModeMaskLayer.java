package com.xiaopeng.systemui.statusbar.MaskLayer;

import com.xiaopeng.systemui.Logger;
/* loaded from: classes24.dex */
public class DiagnosticModeMaskLayer implements IMaskLayer {
    private static final String TAG = "DiagnosticModeMaskLayer";

    @Override // com.xiaopeng.systemui.statusbar.MaskLayer.IMaskLayer
    public void updateView(boolean on) {
        Logger.d(TAG, "on = " + on);
        WatermarkPresenter.getInstance().updateDiagnosticMode(on);
    }
}
