package com.xiaopeng.systemui.infoflow.speech;
/* loaded from: classes24.dex */
public interface IVoiceWavwView {
    public static final int EXTRA_TYPE_NAVI_POI = 1;
    public static final int EXTRA_TYPE_NAVI_ROUTE = 0;
    public static final int OTHER = 2;

    void showVoiceWaveAnim(int i, int i2);

    void stopVoiceWaveAnim(int i);
}
