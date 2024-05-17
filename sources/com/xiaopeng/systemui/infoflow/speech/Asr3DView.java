package com.xiaopeng.systemui.infoflow.speech;

import com.xiaopeng.systemui.Logger;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes24.dex */
public class Asr3DView extends Asr2DView {
    private static final String TAG = "Asr3DView";

    @Override // com.xiaopeng.systemui.infoflow.speech.Asr2DView, com.xiaopeng.systemui.infoflow.speech.IAsrView
    public void showAsr(boolean show) {
        if (this.mAsrContainer != null) {
            super.showAsr(show);
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("show", Boolean.valueOf(show));
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.Asr2DView, com.xiaopeng.systemui.infoflow.speech.IAsrView
    public void setRecommendData(String recommendData) {
        if (this.mAsrContainer != null) {
            super.setRecommendData(recommendData);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.Asr2DView, com.xiaopeng.systemui.infoflow.speech.IAsrView
    public void setAsrStatus(int status) {
        Logger.i(TAG, "setAsrStatus");
        if (this.mAsrContainer != null) {
            super.setAsrStatus(status);
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("status", Integer.valueOf(status));
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.Asr2DView, com.xiaopeng.systemui.infoflow.speech.IAsrView
    public void showAsrAnimation(int status) {
        if (this.mAsrContainer != null) {
            super.showAsrAnimation(status);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.Asr2DView, com.xiaopeng.systemui.infoflow.speech.IAsrView
    public void showVuiRecommendView(boolean hasAnimation) {
        if (this.mAsrContainer != null) {
            super.showVuiRecommendView(hasAnimation);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.Asr2DView, com.xiaopeng.systemui.infoflow.speech.IAsrView
    public void hideVuiRecommendView() {
        if (this.mAsrContainer != null) {
            super.hideVuiRecommendView();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.Asr2DView, com.xiaopeng.systemui.infoflow.speech.IAsrView
    public void setAsrText(String text) {
        StringBuilder sb = new StringBuilder();
        sb.append("setAsrText text:");
        sb.append(text);
        sb.append(" &mAsrContainer null");
        sb.append(this.mAsrContainer == null);
        Logger.i(TAG, sb.toString());
        if (this.mAsrContainer != null) {
            super.setAsrText(text);
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("text", text);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.Asr2DView, com.xiaopeng.systemui.infoflow.speech.IAsrView
    public void fadeOut() {
        if (this.mAsrContainer != null) {
            super.fadeOut();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.Asr2DView, com.xiaopeng.systemui.infoflow.speech.IAsrView
    public void clearAnimation() {
        if (this.mAsrContainer != null) {
            super.clearAnimation();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.Asr2DView, com.xiaopeng.systemui.infoflow.speech.IAsrView
    public void fadeIn(String text) {
        if (this.mAsrContainer != null) {
            super.fadeIn(text);
        }
    }
}
