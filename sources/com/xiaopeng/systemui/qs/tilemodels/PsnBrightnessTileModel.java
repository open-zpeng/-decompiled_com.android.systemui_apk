package com.xiaopeng.systemui.qs.tilemodels;

import android.util.Log;
import com.xiaopeng.systemui.controller.BrightnessController;
/* loaded from: classes24.dex */
public class PsnBrightnessTileModel extends ContentProviderTileModel {
    public static final String TAG = "PsnBrightnessTile";

    public PsnBrightnessTileModel(String tileSpec) {
        super(tileSpec);
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.ContentProviderTileModel, com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public void click(int value) {
        Log.d("PsnBrightnessTile", "xptile psn_brightness ui:" + value);
        if (value == 0) {
            value = 1;
        }
        BrightnessController.getInstance(this.mContext).setPsnScreenBrightness((int) ((value * 255.0f) / 100.0f));
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.ContentProviderTileModel, com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public int getCurrentState() {
        int state = BrightnessController.getInstance(this.mContext).getPsnScreenBrightness();
        return (int) ((state * 100.0f) / 255.0f);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public int convertState(int value) {
        return (int) ((value * 100.0f) / 255.0f);
    }
}
