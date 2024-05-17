package com.xiaopeng.speech.vui.listener;

import com.xiaopeng.speech.vui.observer.XpVuiElementChangedObserver;
import com.xiaopeng.vui.commons.IVuiSceneListener;
import com.xiaopeng.vui.commons.model.VuiEvent;
/* loaded from: classes.dex */
public interface IXpVuiSceneListener extends IVuiSceneListener {
    @Override // com.xiaopeng.vui.commons.IVuiSceneListener
    void onBuildScene();

    void onExcuteCommand(String str);

    void onInitCompleted(XpVuiElementChangedObserver xpVuiElementChangedObserver);

    @Override // com.xiaopeng.vui.commons.IVuiSceneListener
    void onVuiEvent(VuiEvent vuiEvent);
}
