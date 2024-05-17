package com.xiaopeng.speech.protocol.node.scene;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.VuiEvent;
/* loaded from: classes23.dex */
public class SceneNode_Processor implements ICommandProcessor {
    private SceneNode mTarget;

    public SceneNode_Processor(SceneNode target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1722601438:
                if (event.equals(VuiEvent.SCENE_VUI_DISABLE)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -1546876663:
                if (event.equals(VuiEvent.SCENE_VUI_ENABLE)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 720644575:
                if (event.equals(VuiEvent.SCENE_DM_START)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 828241388:
                if (event.equals(VuiEvent.SCENE_CONTROL)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1045003449:
                if (event.equals("scene.rebuild")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 1301293464:
                if (event.equals(VuiEvent.SCENE_DM_END)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c == 0) {
            this.mTarget.onSceneEvent(event, data);
        } else if (c == 1) {
            this.mTarget.onDMStart(event, data);
        } else if (c == 2) {
            this.mTarget.onDMEnd(event, data);
        } else if (c == 3) {
            this.mTarget.onVuiEnable(event, data);
        } else if (c == 4) {
            this.mTarget.onVuiDisable(event, data);
        } else if (c == 5) {
            this.mTarget.onRebuild(event, data);
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{VuiEvent.SCENE_CONTROL, VuiEvent.SCENE_DM_START, VuiEvent.SCENE_DM_END, VuiEvent.SCENE_VUI_ENABLE, VuiEvent.SCENE_VUI_DISABLE, "scene.rebuild"};
    }
}
