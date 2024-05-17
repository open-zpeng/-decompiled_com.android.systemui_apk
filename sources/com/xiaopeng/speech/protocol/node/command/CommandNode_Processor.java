package com.xiaopeng.speech.protocol.node.command;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.CommandEvent;
/* loaded from: classes23.dex */
public class CommandNode_Processor implements ICommandProcessor {
    private CommandNode mTarget;

    public CommandNode_Processor(CommandNode target) {
        this.mTarget = target;
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        if (((event.hashCode() == 380439127 && event.equals(CommandEvent.COMMAND_JOSN_POST)) ? (char) 0 : (char) 65535) == 0) {
            this.mTarget.onJsonPost(event, data);
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{CommandEvent.COMMAND_JOSN_POST};
    }
}
