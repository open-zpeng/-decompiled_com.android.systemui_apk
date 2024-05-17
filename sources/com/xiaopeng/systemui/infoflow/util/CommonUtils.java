package com.xiaopeng.systemui.infoflow.util;

import android.text.TextUtils;
import com.xiaopeng.speech.protocol.bean.base.ButtonBean;
import com.xiaopeng.vui.commons.VuiElementType;
import com.xiaopeng.vui.commons.VuiFeedbackType;
import com.xiaopeng.vui.commons.VuiPriority;
/* loaded from: classes24.dex */
public class CommonUtils {
    public static VuiPriority getViewLeveByPriority(int priority) {
        if (priority == 0) {
            VuiPriority viewLeveEnum = VuiPriority.LEVEL1;
            return viewLeveEnum;
        } else if (priority == 1) {
            VuiPriority viewLeveEnum2 = VuiPriority.LEVEL2;
            return viewLeveEnum2;
        } else if (priority == 2) {
            VuiPriority viewLeveEnum3 = VuiPriority.LEVEL3;
            return viewLeveEnum3;
        } else if (priority == 3) {
            VuiPriority viewLeveEnum4 = VuiPriority.LEVEL4;
            return viewLeveEnum4;
        } else {
            VuiPriority viewLeveEnum5 = VuiPriority.LEVEL3;
            return viewLeveEnum5;
        }
    }

    public static VuiFeedbackType getFeedbackType(int value) {
        if (value == 2) {
            VuiFeedbackType vuiFeedbackType = VuiFeedbackType.TTS;
            return vuiFeedbackType;
        }
        VuiFeedbackType vuiFeedbackType2 = VuiFeedbackType.SOUND;
        return vuiFeedbackType2;
    }

    public static VuiElementType getElementType(int value) {
        switch (value) {
            case 1:
                VuiElementType elementType = VuiElementType.BUTTON;
                return elementType;
            case 2:
                VuiElementType elementType2 = VuiElementType.LISTVIEW;
                return elementType2;
            case 3:
                VuiElementType elementType3 = VuiElementType.CHECKBOX;
                return elementType3;
            case 4:
                VuiElementType elementType4 = VuiElementType.RADIOBUTTON;
                return elementType4;
            case 5:
                VuiElementType elementType5 = VuiElementType.RADIOGROUP;
                return elementType5;
            case 6:
                VuiElementType elementType6 = VuiElementType.GROUP;
                return elementType6;
            case 7:
                VuiElementType elementType7 = VuiElementType.EDITTEXT;
                return elementType7;
            case 8:
                VuiElementType elementType8 = VuiElementType.PROGRESSBAR;
                return elementType8;
            case 9:
                VuiElementType elementType9 = VuiElementType.SEEKBAR;
                return elementType9;
            case 10:
                VuiElementType elementType10 = VuiElementType.TABHOST;
                return elementType10;
            case 11:
                VuiElementType elementType11 = VuiElementType.SEARCHVIEW;
                return elementType11;
            case 12:
                VuiElementType elementType12 = VuiElementType.RATINGBAR;
                return elementType12;
            case 13:
                VuiElementType elementType13 = VuiElementType.IMAGEBUTTON;
                return elementType13;
            case 14:
                VuiElementType elementType14 = VuiElementType.IMAGEVIEW;
                return elementType14;
            case 15:
                VuiElementType elementType15 = VuiElementType.SCROLLVIEW;
                return elementType15;
            case 16:
                VuiElementType elementType16 = VuiElementType.TEXTVIEW;
                return elementType16;
            case 17:
                VuiElementType elementType17 = VuiElementType.RECYCLEVIEW;
                return elementType17;
            case 18:
                VuiElementType elementType18 = VuiElementType.SWITCH;
                return elementType18;
            case 19:
                VuiElementType elementType19 = VuiElementType.CUSTOM;
                return elementType19;
            case 20:
                VuiElementType elementType20 = VuiElementType.XSLIDER;
                return elementType20;
            case 21:
                VuiElementType elementType21 = VuiElementType.XTABLAYOUT;
                return elementType21;
            case 22:
                VuiElementType elementType22 = VuiElementType.STATEFULBUTTON;
                return elementType22;
            default:
                VuiElementType elementType23 = VuiElementType.UNKNOWN;
                return elementType23;
        }
    }

    public static String getRecommendText(ButtonBean buttonBean) {
        String strRecommend = buttonBean.showText;
        if (TextUtils.isEmpty(strRecommend)) {
            return buttonBean.text;
        }
        return strRecommend;
    }
}
