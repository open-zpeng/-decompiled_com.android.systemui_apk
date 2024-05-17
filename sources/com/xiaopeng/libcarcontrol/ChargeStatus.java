package com.xiaopeng.libcarcontrol;

import android.util.Log;
/* loaded from: classes23.dex */
public enum ChargeStatus {
    Prepare,
    Appointment,
    Charging,
    ChargeError,
    ChargeDone,
    Discharging,
    DischargeDone,
    DischargeError,
    FullyChargedD21,
    ChargerRemovedD21,
    WrongOpD21,
    ChargeStoppingD21,
    ChargeErrorD21;

    public static ChargeStatus fromVcuChargeStatus(int value) {
        switch (value) {
            case 0:
                ChargeStatus chargeStatus = Prepare;
                return chargeStatus;
            case 1:
                ChargeStatus chargeStatus2 = Appointment;
                return chargeStatus2;
            case 2:
                ChargeStatus chargeStatus3 = Charging;
                return chargeStatus3;
            case 3:
                ChargeStatus chargeStatus4 = ChargeError;
                return chargeStatus4;
            case 4:
                ChargeStatus chargeStatus5 = ChargeDone;
                return chargeStatus5;
            case 5:
                ChargeStatus chargeStatus6 = Discharging;
                return chargeStatus6;
            case 6:
                ChargeStatus chargeStatus7 = DischargeDone;
                return chargeStatus7;
            case 7:
                ChargeStatus chargeStatus8 = DischargeError;
                return chargeStatus8;
            default:
                switch (value) {
                    case 16:
                        ChargeStatus chargeStatus9 = FullyChargedD21;
                        return chargeStatus9;
                    case 17:
                        ChargeStatus chargeStatus10 = ChargerRemovedD21;
                        return chargeStatus10;
                    case 18:
                        ChargeStatus chargeStatus11 = WrongOpD21;
                        return chargeStatus11;
                    case 19:
                        ChargeStatus chargeStatus12 = ChargeStoppingD21;
                        return chargeStatus12;
                    case 20:
                        ChargeStatus chargeStatus13 = ChargeErrorD21;
                        return chargeStatus13;
                    default:
                        Log.d("ChargeStatus", "Unknown charge status, set as prepare status");
                        ChargeStatus chargeStatus14 = Prepare;
                        return chargeStatus14;
                }
        }
    }
}
