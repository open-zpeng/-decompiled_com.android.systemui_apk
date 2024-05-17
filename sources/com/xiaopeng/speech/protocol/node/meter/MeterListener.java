package com.xiaopeng.speech.protocol.node.meter;

import com.xiaopeng.speech.INodeListener;
/* loaded from: classes23.dex */
public interface MeterListener extends INodeListener {
    default void setLeftCard(int index) {
    }

    default void setRightCard(int index) {
    }

    default void onDashboardLightsStatus() {
    }
}
