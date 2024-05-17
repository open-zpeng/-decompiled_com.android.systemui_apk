package com.xiaopeng.speech.protocol.node.skill;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.SkillDialogEvent;
/* loaded from: classes23.dex */
public class SkillDialogNode_Processor implements ICommandProcessor {
    private SkillDialogNode mTarget;

    public SkillDialogNode_Processor(SkillDialogNode target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1594338748:
                if (event.equals(SkillDialogEvent.SKILL_SHOW_KEY_GUIDE)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -1248956104:
                if (event.equals(SkillDialogEvent.ROUTE_OPEN_WINDOW)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -764162793:
                if (event.equals(SkillDialogEvent.ROUTE_CLOSE_WINDOW)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -91367129:
                if (event.equals(SkillDialogEvent.AI_FORWARD_SCREEN_EVENT)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 2043108326:
                if (event.equals(SkillDialogEvent.SKILL_REFRESH_DATA)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 2110322600:
                if (event.equals(SkillDialogEvent.SKILL_SHOW_DOUBLE_GUIDE)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c == 0) {
            this.mTarget.onCloseWindow(event, data);
        } else if (c == 1) {
            this.mTarget.onOpenWindow(event, data);
        } else if (c == 2) {
            this.mTarget.onForwardScreenEvent(event, data);
        } else if (c == 3) {
            this.mTarget.onRefreshSkillData(event, data);
        } else if (c == 4) {
            this.mTarget.onShowDoubleGuide(event, data);
        } else if (c == 5) {
            this.mTarget.onShowKeyGuide(event, data);
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{SkillDialogEvent.ROUTE_CLOSE_WINDOW, SkillDialogEvent.ROUTE_OPEN_WINDOW, SkillDialogEvent.AI_FORWARD_SCREEN_EVENT, SkillDialogEvent.SKILL_REFRESH_DATA, SkillDialogEvent.SKILL_SHOW_DOUBLE_GUIDE, SkillDialogEvent.SKILL_SHOW_KEY_GUIDE};
    }
}
