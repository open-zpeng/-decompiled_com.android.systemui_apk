package com.xiaopeng.speech.protocol.query.carcontrol;

import com.xiaopeng.speech.IQueryCaller;
/* loaded from: classes23.dex */
public interface ICarControlCaller extends IQueryCaller {
    int getAtmosphereBrightnessStatus();

    int getAtmosphereColorStatus();

    int getControlElectricCurtainSupport();

    int getControlLowSpeedAnalogSoundSupport();

    int getControlScissorDoorLeftCloseSupport();

    int getControlScissorDoorLeftOpenSupport();

    int getControlScissorDoorLeftRunningSupport();

    int getControlScissorDoorRightCloseSupport();

    int getControlScissorDoorRightOpenSupport();

    int getControlScissorDoorRightRunningSupport();

    int getControlSupportEnergyRecycleReason();

    int getControlXpedalSupport();

    int getDoorKeyValue();

    int getExtraTrunkStatus();

    int getGuiPageOpenState(String str);

    int getLegHeight();

    int getMirrorStatus();

    int getStatusChargePortControl(int i, int i2);

    int getSupportCloseTrunk();

    int getSupportOpenTrunk();

    int getSupportPsnSeat();

    int getSupportSeat();

    int getTrunkStatus();

    int getWindowStatus();

    int getWiperInterval();

    int isSteeringModeAdjustable();

    boolean isSupportAtmosphere();

    boolean isSupportCloseMirror();

    boolean isSupportControlChargePort(int i, int i2);

    boolean isSupportControlMirror();

    boolean isSupportDrivingMode();

    boolean isSupportEnergyRecycle();

    boolean isSupportUnlockTrunk();

    boolean isTirePressureNormal();

    default float[] getControlWindowsStateSupport() {
        return null;
    }

    default int getControlComfortableDrivingModeSupport() {
        return -1;
    }

    default int getControlLampSignalSupport() {
        return -1;
    }

    default int getNgpStatus() {
        return -1;
    }

    default int getVipChairStatus() {
        return -1;
    }

    default int getCapsuleCurrentMode() {
        return 0;
    }
}
