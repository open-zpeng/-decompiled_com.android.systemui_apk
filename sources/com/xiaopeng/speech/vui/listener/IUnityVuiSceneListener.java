package com.xiaopeng.speech.vui.listener;

import com.xiaopeng.vui.commons.IVuiSceneListener;
/* loaded from: classes.dex */
public interface IUnityVuiSceneListener extends IVuiSceneListener {
    @Override // com.xiaopeng.vui.commons.IVuiSceneListener
    void onBuildScene();

    void onVuiEvent(String str);
}
