package com.xiaopeng.speech.protocol.query.carcontrol;

import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.annotation.QueryAnnotation;
import com.xiaopeng.speech.protocol.event.query.QueryCarControlEvent;
/* loaded from: classes23.dex */
public class CarControlQuery extends SpeechQuery<ICarControlCaller> {
    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.IS_SUPPORT_CLOSE_MIRROR)
    public boolean isSupportCloseMirror(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).isSupportCloseMirror();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.GET_MIRROR_STATUS)
    public int getMirrorStatus(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getMirrorStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.GET_SUPPORT_OPEN_TRUNK)
    public int getSupportOpenTrunk(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getSupportOpenTrunk();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.GET_SUPPORT_CLOSE_TRUNK)
    public int getSupportCloseTrunk(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getSupportCloseTrunk();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.GET_WINDOW_STATUS)
    public int getWindowStatus(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getWindowStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.IS_SUPPORT_ENERGY_RECYCLE)
    public boolean isSupportEngryRecycle(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).isSupportEnergyRecycle();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.GET_SUPPORT_SEAT)
    public int getSupportSeat(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getSupportSeat();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.IS_SUPPORT_DRIVING_MODE)
    public boolean isSupportDrivingMode(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).isSupportDrivingMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.IS_SUPPORT_LITE_ATMOSPHERE)
    public boolean isSupportAtmosphere(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).isSupportAtmosphere();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.IS_TAIRPRESSURE_NORMAL)
    public boolean isTirePressureNormal(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).isTirePressureNormal();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.IS_SUPPORT_UNLOCK_TRUNK)
    public boolean isSupportUnlockTrunk(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).isSupportUnlockTrunk();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.IS_SUPPORT_CLOSE_L_CHARGE_PORT)
    public boolean isSupportCloseLeftChargePort(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).isSupportControlChargePort(0, 1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.IS_SUPPORT_CLOSE_R_CHARGE_PORT)
    public boolean isSupportCloseRightChargePort(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).isSupportControlChargePort(1, 1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.IS_SUPPORT_OPEN_L_CHARGE_PORT)
    public boolean isSupportOpenLeftChargePort(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).isSupportControlChargePort(0, 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.IS_SUPPORT_OPEN_R_CHARGE_PORT)
    public boolean isSupportOpenRightChargePort(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).isSupportControlChargePort(1, 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.IS_SUPPORT_CONTROL_MIRROR)
    public boolean isSupportControlMirror(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).isSupportControlMirror();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.CONTROL_LEG_HEIGHT_GET)
    public int getLegHeight(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getLegHeight();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.GET_STATUS_CLOSE_L_CHARGE_PORT)
    public int getStatusCloseLeftChargePort(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getStatusChargePortControl(0, 1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.GET_STATUS_CLOSE_R_CHARGE_PORT)
    public int getStatusCloseRightChargePort(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getStatusChargePortControl(1, 1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.GET_STATUS_OPEN_L_CHARGE_PORT)
    public int getStatusOpenLeftChargePort(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getStatusChargePortControl(0, 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.GET_STATUS_OPEN_R_CHARGE_PORT)
    public int getStatusOpenRightChargePort(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getStatusChargePortControl(1, 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.GET_STATUS_LIGHT_ATMOSPHERE_BRIGHTNESS)
    public int getAtmosphereBrightnessStatus(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getAtmosphereBrightnessStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.GET_STATUS_LIGHT_ATMOSPHERE_COLOR)
    public int getAtmosphereColorStatus(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getAtmosphereColorStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.IS_STEERING_MODE_ADJUSTABLE)
    public int isSteeringModeAdjustable(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).isSteeringModeAdjustable();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.GET_PAGE_OPEN_STATUS)
    public int getGuiPageOpenState(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getGuiPageOpenState(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.GET_CURR_WIPER_LEVEL)
    public int getWiperInterval(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getWiperInterval();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.GET_PSN_SUPPORT_SEAT)
    public int getSupportPsnSeat(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getSupportPsnSeat();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.GET_EXTRA_TRUNK_STATUS)
    public int getExtraTrunkStatus(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getExtraTrunkStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.GET_TRUNK_STATUS)
    public int getTrunkStatus(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getTrunkStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.GET_SET_SPEECH_WAKEUP_STATUS)
    public int getDoorKeyValue(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getDoorKeyValue();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.CONTROL_LOW_SPEED_ANALOG_SOUND_SUPPORT)
    public int getControlLowSpeedAnalogSoundSupport(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getControlLowSpeedAnalogSoundSupport();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.CONTROL_XPEDAL_SUPPORT)
    public int getControlXpedalSupport(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getControlXpedalSupport();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.CONTROL_SUPPORT_ENERGY_RECYCLE_REASON)
    public int getControlSupportEnergyRecycleReason(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getControlSupportEnergyRecycleReason();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.CONTROL_SCISSOR_DOOR_LEFT_OPEN_SUPPORT)
    public int getControlScissorDoorLeftOpenSupport(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getControlScissorDoorLeftOpenSupport();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.CONTROL_SCISSOR_DOOR_RIGHT_OPEN_SUPPORT)
    public int getControlScissorDoorCloseSupport(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getControlScissorDoorRightOpenSupport();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.CONTROL_SCISSOR_DOOR_LEFT_CLOSE_SUPPORT)
    public int getControlScissorDoorLeftCloseSupport(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getControlScissorDoorLeftCloseSupport();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.CONTROL_SCISSOR_DOOR_RIGHT_CLOSE_SUPPORT)
    public int getControlScissorDoorRightCloseSupport(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getControlScissorDoorRightCloseSupport();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.CONTROL_SCISSOR_DOOR_LEFT_RUNNING_SUPPORT)
    public int getControlScissorDoorLeftRunningSupport(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getControlScissorDoorLeftRunningSupport();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.CONTROL_SCISSOR_DOOR_RIGHT_RUNNING_SUPPORT)
    public int getControlScissorDoorRightRunningSupport(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getControlScissorDoorRightRunningSupport();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.CONTROL_ELECTRIC_CURTAIN_SUPPORT)
    public int getControlElectricCurtainSupport(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getControlElectricCurtainSupport();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.CONTROL_GET_WINDOWS_STATE_SUPPORT)
    public float[] getControlWindowsStateSupport(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getControlWindowsStateSupport();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.CONTROL_COMFORTABLE_DRIVING_MODE_SUPPORT)
    public int getControlComfortableDrivingModeSupport(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getControlComfortableDrivingModeSupport();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.CONTROL_LAMP_SIGNAL_SUPPORT)
    public int getControlLampSignalSupport(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getControlLampSignalSupport();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.XPU_NGP_STATUS)
    public int getNgpStatus(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getNgpStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.CONTROL_VIP_CHAIR_STATUS)
    public int getVipChairStatus(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getVipChairStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCarControlEvent.CONTROL_CAPSULE_MODE)
    public int getCapsuleCurrentMode(String event, String data) {
        return ((ICarControlCaller) this.mQueryCaller).getCapsuleCurrentMode();
    }
}
