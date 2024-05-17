package com.xiaopeng.speech.protocol.node.message;
/* loaded from: classes23.dex */
public abstract class AbsMessageListener implements MessageListener {
    @Override // com.xiaopeng.speech.protocol.node.message.MessageListener
    public void onCancel() {
    }

    @Override // com.xiaopeng.speech.protocol.node.message.MessageListener
    public void onPathChanged() {
    }

    @Override // com.xiaopeng.speech.protocol.node.message.MessageListener
    public void onParkingSelected(int index) {
    }

    @Override // com.xiaopeng.speech.protocol.node.message.MessageListener
    public void onAIMessage(int id) {
    }

    @Override // com.xiaopeng.speech.protocol.node.message.MessageListener
    public void onCommonAIMessage(String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.message.MessageListener
    public void onCommonSubmit() {
    }

    @Override // com.xiaopeng.speech.protocol.node.message.MessageListener
    public void onCommonCancel() {
    }

    @Override // com.xiaopeng.speech.protocol.node.message.MessageListener
    public void onAIMessageDisable() {
    }

    @Override // com.xiaopeng.speech.protocol.node.message.MessageListener
    public void onAIMessageDisableSevenDays() {
    }

    @Override // com.xiaopeng.speech.protocol.node.message.MessageListener
    public void onHotWordEngineOK(boolean isReady) {
    }
}
