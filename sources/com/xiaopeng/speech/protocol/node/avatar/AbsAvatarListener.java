package com.xiaopeng.speech.protocol.node.avatar;

import com.xiaopeng.speech.jarvisproto.DMListening;
import com.xiaopeng.speech.jarvisproto.DMRecognized;
/* loaded from: classes23.dex */
public abstract class AbsAvatarListener implements AvatarListener {
    @Override // com.xiaopeng.speech.protocol.node.avatar.AvatarListener
    public void onSilence(DMRecognized dmRecognized) {
    }

    @Override // com.xiaopeng.speech.protocol.node.avatar.AvatarListener
    public void onListening(DMListening dmListening) {
    }

    @Override // com.xiaopeng.speech.protocol.node.avatar.AvatarListener
    public void onSpeaking() {
    }

    @Override // com.xiaopeng.speech.protocol.node.avatar.AvatarListener
    public void onStandby() {
    }

    @Override // com.xiaopeng.speech.protocol.node.avatar.AvatarListener
    public void onUnderstanding() {
    }

    @Override // com.xiaopeng.speech.protocol.node.avatar.AvatarListener
    public void onAvatarExpression(String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.avatar.AvatarListener
    public void onWidgetCustom(String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.avatar.AvatarListener
    public void onWidgetText(String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.avatar.AvatarListener
    public void onWidgetList(String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.avatar.AvatarListener
    public void onWidgetMedia(String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.avatar.AvatarListener
    public void onWidgetCard(String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.avatar.AvatarListener
    public void onWidgetSearch(String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.avatar.AvatarListener
    public void onAvatarWakerupEnable(String reason) {
    }

    @Override // com.xiaopeng.speech.protocol.node.avatar.AvatarListener
    public void onAvatarWakerupDisable(String reason) {
    }
}
