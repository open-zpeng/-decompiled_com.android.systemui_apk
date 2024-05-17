package com.xiaopeng.speech.protocol.query.personalCenter;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.speech.PersonalCenterVuiEvent;
/* loaded from: classes23.dex */
public class PersonalCenterVuiQuery_Processor implements IQueryProcessor {
    private PersonalCenterVuiQuery mTarget;

    public PersonalCenterVuiQuery_Processor(PersonalCenterVuiQuery target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1676441979:
                if (event.equals(PersonalCenterVuiEvent.VUI_ELEMENT_SCROLL_STATE)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -1377418726:
                if (event.equals(PersonalCenterVuiEvent.VUI_STATEFULBUTTON_SETVALUE)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -1003325425:
                if (event.equals(PersonalCenterVuiEvent.VUI_ELEMENT_RADIOBUTTON_CHECKED)) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case -435247663:
                if (event.equals(PersonalCenterVuiEvent.VUI_ELEMENT_CHECKBOX_CHECKED)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 102757397:
                if (event.equals(PersonalCenterVuiEvent.VUI_XTABLAYOUT_SELECTED)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 384983952:
                if (event.equals(PersonalCenterVuiEvent.VUI_XSWITCH_CHECKED)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1183902695:
                if (event.equals(PersonalCenterVuiEvent.VUI_XSLIDER_SETVALUE)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1448450986:
                if (event.equals(PersonalCenterVuiEvent.VUI_ELEMENT_ENABLED)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 1843237338:
                if (event.equals(PersonalCenterVuiEvent.VUI_STATEFULBUTTON_CHECKED)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return Boolean.valueOf(this.mTarget.isSwitchChecked(event, data));
            case 1:
                return Boolean.valueOf(this.mTarget.isTableLayoutSelected(event, data));
            case 2:
                return Integer.valueOf(this.mTarget.getXSliderIndex(event, data));
            case 3:
                return Boolean.valueOf(this.mTarget.isStatefulButtonChecked(event, data));
            case 4:
                return this.mTarget.getStatefulButtonValue(event, data);
            case 5:
                return Boolean.valueOf(this.mTarget.isElementEnabled(event, data));
            case 6:
                return Boolean.valueOf(this.mTarget.isElementCanScrolled(event, data));
            case 7:
                return Boolean.valueOf(this.mTarget.isCheckBoxChecked(event, data));
            case '\b':
                return Boolean.valueOf(this.mTarget.isRadioButtonChecked(event, data));
            default:
                return null;
        }
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{PersonalCenterVuiEvent.VUI_XSWITCH_CHECKED, PersonalCenterVuiEvent.VUI_XTABLAYOUT_SELECTED, PersonalCenterVuiEvent.VUI_XSLIDER_SETVALUE, PersonalCenterVuiEvent.VUI_STATEFULBUTTON_CHECKED, PersonalCenterVuiEvent.VUI_STATEFULBUTTON_SETVALUE, PersonalCenterVuiEvent.VUI_ELEMENT_ENABLED, PersonalCenterVuiEvent.VUI_ELEMENT_SCROLL_STATE, PersonalCenterVuiEvent.VUI_ELEMENT_CHECKBOX_CHECKED, PersonalCenterVuiEvent.VUI_ELEMENT_RADIOBUTTON_CHECKED};
    }
}
