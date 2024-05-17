package com.xiaopeng.speech.protocol.node.charge;

import com.xiaopeng.speech.INodeListener;
/* loaded from: classes23.dex */
public interface ChargeListener extends INodeListener {
    void onModeFull();

    void onModePercent(int i);

    void onModePercentSupport(int i);

    void onModeSmartClose();

    void onModeSmartCloseSupport();

    void onModeSmartOff();

    void onModeSmartOffSupport();

    void onModeSmartOn();

    void onModeSmartOnSupport();

    void onPortOpen();

    void onPortOpenSupport();

    void onRestart();

    void onRestartSupport();

    void onStart();

    void onStartSupport();

    void onStop();

    void onStopSupport();

    void onUiClose();

    void onUiOpen();

    default void onChangeMileageMode(String standard) {
    }

    default void onChargeTrunkPower(boolean isOpen) {
    }

    default void setDischargeLimit(int target) {
    }
}
