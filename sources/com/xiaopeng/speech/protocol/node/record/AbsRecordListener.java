package com.xiaopeng.speech.protocol.node.record;

import com.xiaopeng.speech.protocol.node.record.bean.AsrResult;
import com.xiaopeng.speech.protocol.node.record.bean.RecordErrReason;
import com.xiaopeng.speech.protocol.node.record.bean.Volume;
/* loaded from: classes23.dex */
public abstract class AbsRecordListener implements RecordListener {
    @Override // com.xiaopeng.speech.protocol.node.record.RecordListener
    public void onAsrResult(AsrResult asrResult) {
    }

    @Override // com.xiaopeng.speech.protocol.node.record.RecordListener
    public void onRecordBegin() {
    }

    @Override // com.xiaopeng.speech.protocol.node.record.RecordListener
    public void onRecordEnd(boolean isStopRecord) {
    }

    @Override // com.xiaopeng.speech.protocol.node.record.RecordListener
    public void onRecordError(RecordErrReason recordReason) {
    }

    @Override // com.xiaopeng.speech.protocol.node.record.RecordListener
    public void onSpeechBegin() {
    }

    @Override // com.xiaopeng.speech.protocol.node.record.RecordListener
    public void onSpeechEnd() {
    }

    @Override // com.xiaopeng.speech.protocol.node.record.RecordListener
    public void onSpeechVolume(Volume volume) {
    }

    @Override // com.xiaopeng.speech.protocol.node.record.RecordListener
    public void onRecordMaxLength() {
    }
}
