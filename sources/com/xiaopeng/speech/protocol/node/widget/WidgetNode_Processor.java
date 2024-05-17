package com.xiaopeng.speech.protocol.node.widget;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.WidgetEvent;
/* loaded from: classes23.dex */
public class WidgetNode_Processor implements ICommandProcessor {
    private WidgetNode mTarget;

    public WidgetNode_Processor(WidgetNode target) {
        this.mTarget = target;
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        if (((event.hashCode() == -691162900 && event.equals(WidgetEvent.AC_WIDGET_ON)) ? (char) 0 : (char) 65535) == 0) {
            this.mTarget.onAcWidgetOn(event, data);
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{WidgetEvent.AC_WIDGET_ON};
    }
}
