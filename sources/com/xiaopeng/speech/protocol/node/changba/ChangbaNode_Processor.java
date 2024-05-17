package com.xiaopeng.speech.protocol.node.changba;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.ChangbaEvent;
/* loaded from: classes23.dex */
public class ChangbaNode_Processor implements ICommandProcessor {
    private ChangbaNode mTarget;

    public ChangbaNode_Processor(ChangbaNode target) {
        this.mTarget = target;
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        if (((event.hashCode() == -2072002904 && event.equals(ChangbaEvent.MUSIC_CHANGBA_SEARCH)) ? (char) 0 : (char) 65535) == 0) {
            this.mTarget.onMusicChangbaSearch(event, data);
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{ChangbaEvent.MUSIC_CHANGBA_SEARCH};
    }
}
