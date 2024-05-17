package com.xiaopeng.systemui.infoflow.speech.carcontrol;

import android.util.Log;
/* loaded from: classes24.dex */
public class CtrlCardUtils {
    public static final int AUTO_FAN_ANIM_LEVEL = 5;
    public static final int AUTO_FAN_VALUE = 14;
    public static final int CARD_SEAT_GEAR_NUM = 4;
    public static final int CARD_SEAT_PROGRESS = 100;
    public static final int CARD_SEAT_PROGRESS_PER_STEP = 25;
    private static final String TAG = "CtrlCardUtils";
    public static final int TYPE_CARD_ICM_BRIGHTNESS = 17;
    public static final int TYPE_CARD_MOOD_LIGHTING_BRIGHTNESS = 11;
    public static final int TYPE_CARD_MOOD_LIGHTING_COLOR = 12;
    public static final int TYPE_CARD_SCREEN_BRIGHTNESS = 16;
    public static final int TYPE_CARD_SEAT_SETTING_HOT_DRIVER = 14;
    public static final int TYPE_CARD_SEAT_SETTING_HOT_PASSENGER = 13;
    public static final int TYPE_CARD_SEAT_SETTING_VENT = 15;
    public static final int TYPE_CARD_TEMP_RATING_BLOWING = 10;
    public static final int TYPE_CARD_TEMP_RATING_COLD = 8;
    public static final int TYPE_CARD_TEMP_RATING_GROUP_AC_TEMP = 5;
    public static final int TYPE_CARD_TEMP_RATING_GROUP_DRIVER = 6;
    public static final int TYPE_CARD_TEMP_RATING_GROUP_PASSENGER = 7;
    public static final int TYPE_CARD_TEMP_RATING_HOT = 9;
    public static final int TYPE_CARD_WIND_RATING_BLOWING = 4;
    public static final int TYPE_CARD_WIND_RATING_COLD = 2;
    public static final int TYPE_CARD_WIND_RATING_GROUP = 1;
    public static final int TYPE_CARD_WIND_RATING_HOT = 3;
    private static final CtrlCardUtils ourInstance = new CtrlCardUtils();
    private ProgressProperty progressProperty = new ProgressProperty();

    private CtrlCardUtils() {
    }

    public static CtrlCardUtils getInstance() {
        return ourInstance;
    }

    public static int transferSeatModeProgress(int progress) {
        return (progress / 25) * 25;
    }

    public int unit2ProgressValue(int type, float unitValue) {
        int result = -1;
        if (type != 2 && type != 3 && type != 4) {
            switch (type) {
                case 8:
                case 9:
                case 10:
                    if (unitValue >= 32.0f) {
                        unitValue = 32.0f;
                    }
                    if (unitValue - 18 >= 0.0f) {
                        int iResult = (int) ((unitValue - 18) * getProgressProperty(type).getPer());
                        result = iResult;
                        break;
                    } else {
                        result = 0;
                        break;
                    }
                case 11:
                    if (unitValue <= 10.0f) {
                        unitValue = 10.0f;
                    } else if (unitValue >= 100.0f) {
                        unitValue = 100.0f;
                    }
                    result = (int) unitValue;
                    break;
                case 12:
                    if (unitValue >= 20.0f) {
                        unitValue = 20.0f;
                    } else if (unitValue <= 1.0f) {
                        unitValue = 1.0f;
                    }
                    result = (int) (getProgressProperty(type).getPer() * unitValue);
                    break;
                case 13:
                case 14:
                case 15:
                    result = (int) (25.0f * unitValue);
                    break;
                case 16:
                case 17:
                    if (unitValue <= 0.0f) {
                        unitValue = 1.0f;
                    } else if (unitValue >= 100.0f) {
                        unitValue = 100.0f;
                    }
                    result = (int) unitValue;
                    break;
            }
        } else if (unitValue == 14.0f) {
            result = -1;
        } else {
            if (unitValue >= 10.0f) {
                unitValue = 10.0f;
            }
            result = (int) (getProgressProperty(type).getPer() * unitValue);
        }
        Log.d(TAG, "unit2ProgressValue() called with: type = [" + type + "], unitValue = [" + unitValue + "] result=" + result);
        return result;
    }

    public float progress2UnitValue(int type, int progress) {
        float result = -1.0f;
        if (type != 2 && type != 3 && type != 4) {
            switch (type) {
                case 8:
                case 9:
                case 10:
                    float fResult = progress / getProgressProperty(type).getPer();
                    int iResult = progress / getProgressProperty(type).getPer();
                    if (iResult == fResult) {
                        result = 18 + iResult;
                    } else if (iResult < fResult) {
                        result = 18 + iResult + 0.5f;
                    } else {
                        result = 18 + iResult;
                    }
                    if (result >= 32.0f) {
                        result = 32.0f;
                        break;
                    } else if (result <= 18.0f) {
                        result = 18.0f;
                        break;
                    }
                    break;
                case 11:
                    if (progress <= 10) {
                        progress = 10;
                    } else if (progress >= 100) {
                        progress = 100;
                    }
                    result = progress;
                    break;
                case 12:
                    result = progress / getProgressProperty(type).getPer();
                    if (result <= 1.0f) {
                        result = 1.0f;
                        break;
                    } else if (result >= 20.0f) {
                        result = 20.0f;
                        break;
                    }
                    break;
                case 13:
                case 14:
                case 15:
                    result = progress / 25;
                    if (result <= 0.0f) {
                        result = 0.0f;
                        break;
                    } else if (result >= 3.0f) {
                        result = 3.0f;
                        break;
                    }
                    break;
                case 16:
                case 17:
                    if (progress <= 0) {
                        progress = 1;
                    } else if (progress >= 100) {
                        progress = 100;
                    }
                    result = progress;
                    break;
            }
        } else {
            int intValue = progress / getProgressProperty(type).getPer();
            float flValue = progress / getProgressProperty(type).getPer();
            if (flValue > intValue) {
                result = intValue + 1;
            } else if (flValue == intValue) {
                result = intValue;
            }
            if (result >= 10.0f) {
                result = 10.0f;
            } else if (result == -1.0f) {
                result = 14.0f;
            } else if (result <= 0.0f) {
                result = 1.0f;
            }
        }
        Log.d(TAG, "progress2UnitValue() called with: type = [" + type + "], progress = [" + progress + "]result=" + result);
        return result;
    }

    public ProgressProperty getProgressProperty(int type) {
        this.progressProperty.setPer(1);
        this.progressProperty.setSlips(100);
        if (type != 2 && type != 3 && type != 4) {
            switch (type) {
                case 8:
                case 9:
                case 10:
                    this.progressProperty.setPer(5);
                    this.progressProperty.setSlips(14);
                    break;
                case 11:
                    this.progressProperty.setPer(1);
                    this.progressProperty.setSlips(100);
                    break;
                case 12:
                    this.progressProperty.setPer(5);
                    this.progressProperty.setSlips(20);
                    break;
                case 13:
                case 14:
                case 15:
                    this.progressProperty.setPer(1);
                    this.progressProperty.setSlips(100);
                    break;
            }
        } else {
            this.progressProperty.setPer(5);
            this.progressProperty.setSlips(10);
        }
        return this.progressProperty;
    }
}
