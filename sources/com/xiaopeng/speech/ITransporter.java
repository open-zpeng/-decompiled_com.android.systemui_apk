package com.xiaopeng.speech;

import com.xiaopeng.speech.actor.Actor;
/* loaded from: classes23.dex */
public interface ITransporter {

    /* loaded from: classes23.dex */
    public interface Receiver {
        void onCall(Actor actor);
    }

    /* loaded from: classes23.dex */
    public interface Sender {
        void send(Actor actor);
    }
}
