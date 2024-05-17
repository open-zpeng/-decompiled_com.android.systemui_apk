package com.xiaopeng.libcarcontrol;
/* loaded from: classes23.dex */
public enum ChargePortState {
    OPENED,
    MIDDLE,
    CLOSED,
    FAULT,
    UNKNOWN;
    
    private static final int BCM_CHARGE_PORT_CLOSED = 2;
    private static final int BCM_CHARGE_PORT_FAULT = 3;
    private static final int BCM_CHARGE_PORT_MIDDLE = 1;
    private static final int BCM_CHARGE_PORT_OPEN = 0;
    private static final int BCM_CHARGE_PORT_UNKNOWN = -1;

    public static ChargePortState fromBcmValue(int value) {
        if (value == 0) {
            ChargePortState state = OPENED;
            return state;
        } else if (value == 1) {
            ChargePortState state2 = MIDDLE;
            return state2;
        } else if (value == 2) {
            ChargePortState state3 = CLOSED;
            return state3;
        } else if (value == 3) {
            ChargePortState state4 = FAULT;
            return state4;
        } else {
            ChargePortState state5 = UNKNOWN;
            return state5;
        }
    }
}
