package com.xiaopeng.lib.framework.moduleinterface.aiassistantmodule;
/* loaded from: classes23.dex */
public interface IInteractiveMsgService {
    void close();

    IInteractiveMsgBuilder interactiveMsgBuilder();

    void sendMessage(IInteractiveMsg iInteractiveMsg);

    void shutup(String str);

    void speak(String str, String str2);

    /* loaded from: classes23.dex */
    public static class InteractiveMsgEvent {
        public final IInteractiveMsg msg;

        public InteractiveMsgEvent(IInteractiveMsg msg) {
            this.msg = msg;
        }
    }

    /* loaded from: classes23.dex */
    public static class SpeakEndEvent {
        public static final int END_STATE_ERROR = 1;
        public static final int END_STATE_SUCCESS = 0;
        public final int endState;
        public final String id;

        public SpeakEndEvent(String id, int endState) {
            this.id = id;
            this.endState = endState;
        }
    }
}
