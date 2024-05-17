package com.xiaopeng.lib.framework.moduleinterface.carcontroller.v2extend;

import com.xiaopeng.lib.framework.moduleinterface.carcontroller.AbstractEventMsg;
/* loaded from: classes23.dex */
public interface IAvmController {

    /* loaded from: classes23.dex */
    public static class V2FrontRadarDataEventMsg extends AbstractEventMsg<float[]> {
    }

    /* loaded from: classes23.dex */
    public static class V2TailRadarDataEventMsg extends AbstractEventMsg<float[]> {
    }

    int getOverlayWorkSt() throws Exception;

    void setOverlayWorkSt(int i) throws Exception;
}
