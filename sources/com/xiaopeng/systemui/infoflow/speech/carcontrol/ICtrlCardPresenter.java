package com.xiaopeng.systemui.infoflow.speech.carcontrol;

import com.xiaopeng.systemui.infoflow.speech.ui.model.CtrlCardContent;
import com.xiaopeng.systemui.infoflow.speech.ui.widget.CtrlProgressView;
/* loaded from: classes24.dex */
public interface ICtrlCardPresenter extends CtrlProgressView.OnColorPickerChangeListener {
    void getCurrentValue();

    float getDriverTemp();

    boolean isSpeechCMD();

    void onDestroy();

    void onIcmBrightnessChanged(int i);

    void onScreenBrightnessChanged(int i);

    void playBgAnimation(int i, float f, float f2);

    void registerListener();

    void setATLBrightness(int i);

    void setAtlColor(int i);

    void setDriverTemp(float f, float f2);

    void setSeatHeatLevel(int i);

    void setSeatVentLevel(int i);

    void setWindLevel(float f, float f2);

    void unRegisterListener();

    void updateCtrlCardContent(CtrlCardContent ctrlCardContent);
}
