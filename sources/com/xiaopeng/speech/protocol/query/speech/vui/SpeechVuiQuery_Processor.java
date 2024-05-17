package com.xiaopeng.speech.protocol.query.speech.vui;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.speech.SpeechVuiEvent;
/* loaded from: classes23.dex */
public class SpeechVuiQuery_Processor implements IQueryProcessor {
    private SpeechVuiQuery mTarget;

    public SpeechVuiQuery_Processor(SpeechVuiQuery target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -2145092778:
                if (event.equals(SpeechVuiEvent.VUI_XTABLAYOUT_SELECTED)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -1914744341:
                if (event.equals(SpeechVuiEvent.VUI_ELEMENT_ENABLED)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -1689433816:
                if (event.equals(SpeechVuiEvent.VUI_XSLIDER_SETVALUE)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -1559106607:
                if (event.equals(SpeechVuiEvent.VUI_XSWITCH_CHECKED)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -1526015973:
                if (event.equals(SpeechVuiEvent.VUI_STATEFULBUTTON_CHECKED)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -1065770734:
                if (event.equals(SpeechVuiEvent.VUI_ELEMENT_CHECKBOX_CHECKED)) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case -235119162:
                if (event.equals(SpeechVuiEvent.VUI_ELEMENT_SCROLL_STATE)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -162510757:
                if (event.equals(SpeechVuiEvent.VUI_STATEFULBUTTON_SETVALUE)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 561567344:
                if (event.equals(SpeechVuiEvent.VUI_SWITCH_OPENED)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 1029533589:
                if (event.equals(SpeechVuiEvent.VUI_REBUILD_SCENE)) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case 1554959696:
                if (event.equals(SpeechVuiEvent.VUI_ELEMENT_RADIOBUTTON_CHECKED)) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case 1920063277:
                if (event.equals(SpeechVuiEvent.VUI_SCROLLVIEW_CHILD_VIEW_VISIBLE)) {
                    c = '\n';
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
                return Boolean.valueOf(this.mTarget.isVuiSwitchOpened());
            case '\b':
                return Boolean.valueOf(this.mTarget.isCheckBoxChecked(event, data));
            case '\t':
                return Boolean.valueOf(this.mTarget.isRadiobuttonChecked(event, data));
            case '\n':
                return this.mTarget.isViewVisibleByScrollView(event, data);
            case 11:
                return Boolean.valueOf(this.mTarget.rebuildScene(event, data));
            default:
                return null;
        }
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{SpeechVuiEvent.VUI_XSWITCH_CHECKED, SpeechVuiEvent.VUI_XTABLAYOUT_SELECTED, SpeechVuiEvent.VUI_XSLIDER_SETVALUE, SpeechVuiEvent.VUI_STATEFULBUTTON_CHECKED, SpeechVuiEvent.VUI_STATEFULBUTTON_SETVALUE, SpeechVuiEvent.VUI_ELEMENT_ENABLED, SpeechVuiEvent.VUI_ELEMENT_SCROLL_STATE, SpeechVuiEvent.VUI_SWITCH_OPENED, SpeechVuiEvent.VUI_ELEMENT_CHECKBOX_CHECKED, SpeechVuiEvent.VUI_ELEMENT_RADIOBUTTON_CHECKED, SpeechVuiEvent.VUI_SCROLLVIEW_CHILD_VIEW_VISIBLE, SpeechVuiEvent.VUI_REBUILD_SCENE};
    }
}
