package com.xiaopeng.speech.protocol.node.command;

import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.protocol.event.CommandEvent;
import com.xiaopeng.speech.protocol.node.command.bean.CommandBean;
/* loaded from: classes23.dex */
public class CommandNode extends SpeechNode<CommandListener> {
    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CommandEvent.COMMAND_JOSN_POST)
    public void onJsonPost(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        CommandBean commandBean = CommandBean.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CommandListener) obj).postCommand(commandBean);
            }
        }
    }
}
