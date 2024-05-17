package com.xiaopeng.speech.protocol.node.custom;

import com.xiaopeng.speech.INodeListener;
/* loaded from: classes23.dex */
public interface WakeupWordSettingListener extends INodeListener {
    default void onManualInput(String data) {
    }

    default void onSettingDone(String data) {
    }
}
