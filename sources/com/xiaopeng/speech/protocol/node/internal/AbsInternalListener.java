package com.xiaopeng.speech.protocol.node.internal;
/* loaded from: classes23.dex */
public abstract class AbsInternalListener implements InternalListener {
    @Override // com.xiaopeng.speech.protocol.node.internal.InternalListener
    public void onDmOutput(String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.internal.InternalListener
    public void onInputDmData(String event, String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.internal.InternalListener
    public void onLocalWakeupResult(String type, String word) {
    }

    @Override // com.xiaopeng.speech.protocol.node.internal.InternalListener
    public void onLocalWakeupResultWithChannel(String type, String word, String channel) {
    }

    @Override // com.xiaopeng.speech.protocol.node.internal.InternalListener
    public void onResourceUpdateFinish(String event, String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.internal.InternalListener
    public void onRebootComplete(String event, String data) {
    }
}
