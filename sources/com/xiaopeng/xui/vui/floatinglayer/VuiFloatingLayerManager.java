package com.xiaopeng.xui.vui.floatinglayer;

import android.view.View;
import androidx.annotation.NonNull;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.utils.XLogUtils;
import com.xiaopeng.xui.utils.XViewLocationUtils;
import com.xiaopeng.xui.vui.VuiView;
/* loaded from: classes25.dex */
public class VuiFloatingLayerManager {
    private static IVuiFloatingLayer mVuiFloatingLayer;

    private VuiFloatingLayerManager() {
    }

    public static void show(@NonNull View targetView) {
        show(targetView, 0);
    }

    public static void show(@NonNull View targetView, boolean isCheckScroll) {
        show(targetView, 0, 0, 0, isCheckScroll);
    }

    public static void show(@NonNull View targetView, int type) {
        show(targetView, type, 0, 0);
    }

    public static void show(@NonNull View targetView, int centerOffsetX, int centerOffsetY) {
        show(targetView, 0, centerOffsetX, centerOffsetY);
    }

    public static void show(@NonNull View targetView, int type, int centerOffsetX, int centerOffsetY) {
        show(targetView, type, centerOffsetX, centerOffsetY, true);
    }

    public static void show(@NonNull final View targetView, final int type, final int centerOffsetX, final int centerOffsetY, boolean isCheckScroll) {
        if (type == 0 && (targetView instanceof VuiView)) {
            VuiView vuiView = (VuiView) targetView;
            if (vuiView.getVuiDisableHitEffect()) {
                XLogUtils.d("VuiFloatingManager", "DisableHitEffect type " + type + " view " + targetView);
                return;
            }
        }
        if (mVuiFloatingLayer == null) {
            mVuiFloatingLayer = new VuiFloatingLayer(Xui.getContext());
        }
        XLogUtils.d("VuiFloatingManager", "show==   isCheckScroll : " + isCheckScroll + " view : " + targetView + " x : " + centerOffsetX + " y : " + centerOffsetY + " type : " + type);
        if (isCheckScroll) {
            XViewLocationUtils.scrollByLocation(targetView, new XViewLocationUtils.OnCorrectionLocationListener() { // from class: com.xiaopeng.xui.vui.floatinglayer.-$$Lambda$VuiFloatingLayerManager$UreOefirr9dgokSDF5X4eo6PEbY
                @Override // com.xiaopeng.xui.utils.XViewLocationUtils.OnCorrectionLocationListener
                public final void onCorrectionLocationEnd(View view) {
                    VuiFloatingLayerManager.mVuiFloatingLayer.showFloatingLayer(targetView, type, centerOffsetX, centerOffsetY);
                }
            });
        } else {
            mVuiFloatingLayer.showFloatingLayer(targetView, type, centerOffsetX, centerOffsetY);
        }
    }

    public static void hide() {
        hide(0);
    }

    public static void hide(int type) {
        IVuiFloatingLayer iVuiFloatingLayer = mVuiFloatingLayer;
        if (iVuiFloatingLayer != null) {
            iVuiFloatingLayer.hideFloatingLayer(type);
        }
    }
}
