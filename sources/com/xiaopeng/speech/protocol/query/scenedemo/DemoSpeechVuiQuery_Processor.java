package com.xiaopeng.speech.protocol.query.scenedemo;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.speech.SceneDemoSpeechVuiEvent;
/* loaded from: classes23.dex */
public class DemoSpeechVuiQuery_Processor implements IQueryProcessor {
    private DemoSpeechVuiQuery mTarget;

    public DemoSpeechVuiQuery_Processor(DemoSpeechVuiQuery target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1983407089:
                if (event.equals(SceneDemoSpeechVuiEvent.DEMO_VUI_ELEMENT_SCROLL_STATE)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -1834188428:
                if (event.equals(SceneDemoSpeechVuiEvent.DEMO_VUI_ELEMENT_ENABLED)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -1207439375:
                if (event.equals(SceneDemoSpeechVuiEvent.DEMO_VUI_XSLIDER_SETVALUE)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -1056370085:
                if (event.equals(SceneDemoSpeechVuiEvent.DEMO_VUI_ELEMENT_CHECKBOX_CHECKED)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case -787541084:
                if (event.equals(SceneDemoSpeechVuiEvent.DEMO_VUI_STATEFULBUTTON_CHECKED)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -448152039:
                if (event.equals(SceneDemoSpeechVuiEvent.DEMO_VUI_ELEMENT_RADIOBUTTON_CHECKED)) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case -315538214:
                if (event.equals(SceneDemoSpeechVuiEvent.DEMO_VUI_XSWITCH_CHECKED)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 59743647:
                if (event.equals(SceneDemoSpeechVuiEvent.DEMO_VUI_XTABLAYOUT_SELECTED)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1835176932:
                if (event.equals(SceneDemoSpeechVuiEvent.DEMO_VUI_STATEFULBUTTON_SETVALUE)) {
                    c = 4;
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
                return Boolean.valueOf(this.mTarget.isRadiobuttonChecked(event, data));
            default:
                return null;
        }
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{SceneDemoSpeechVuiEvent.DEMO_VUI_XSWITCH_CHECKED, SceneDemoSpeechVuiEvent.DEMO_VUI_XTABLAYOUT_SELECTED, SceneDemoSpeechVuiEvent.DEMO_VUI_XSLIDER_SETVALUE, SceneDemoSpeechVuiEvent.DEMO_VUI_STATEFULBUTTON_CHECKED, SceneDemoSpeechVuiEvent.DEMO_VUI_STATEFULBUTTON_SETVALUE, SceneDemoSpeechVuiEvent.DEMO_VUI_ELEMENT_ENABLED, SceneDemoSpeechVuiEvent.DEMO_VUI_ELEMENT_SCROLL_STATE, SceneDemoSpeechVuiEvent.DEMO_VUI_ELEMENT_CHECKBOX_CHECKED, SceneDemoSpeechVuiEvent.DEMO_VUI_ELEMENT_RADIOBUTTON_CHECKED};
    }
}
