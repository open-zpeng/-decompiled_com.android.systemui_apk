package com.xiaopeng.speech.protocol.node.camera;

import com.xiaopeng.speech.protocol.bean.ChangeValue;
/* loaded from: classes23.dex */
public abstract class AbsCameraListener implements CameraListener {
    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onOverallOn(boolean isTTS) {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onRearTake(boolean isTTS) {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onRearRecord(boolean isTTS) {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onFrontTake() {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onFrontRecord() {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onLeftTake() {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onLeftRecord() {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onRightTake() {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onRightRecord() {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onLeftOn() {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onRightOn() {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onRearOn() {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onRearOnNew() {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onFrontOn() {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onRearOff() {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onCarcorderTake() {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onCarcorderLock() {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onCarcorderRecord() {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onTopOff(boolean isTTS) {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onTopOn(boolean isTTS) {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onTopTake() {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onTopRecord() {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onTopRecordEnd() {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onTopRotateLeft(ChangeValue changeValue, boolean isTTS) {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onTopRotateRight(ChangeValue changeValue, boolean isTTS) {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onTopRotateFront(boolean isTTS) {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onTopRotateRear(boolean isTTS) {
    }

    @Override // com.xiaopeng.speech.protocol.node.camera.CameraListener
    public void onTransparentChassisCMD(String data) {
    }
}
