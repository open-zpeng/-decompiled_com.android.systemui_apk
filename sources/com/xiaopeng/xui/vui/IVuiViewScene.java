package com.xiaopeng.xui.vui;

import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.IVuiEngine;
import com.xiaopeng.vui.commons.IVuiSceneListener;
import java.util.List;
/* loaded from: classes25.dex */
public interface IVuiViewScene {
    default void setVuiSceneId(String sceneId) {
    }

    default void setVuiEngine(IVuiEngine vuiEngine) {
    }

    default void setVuiElementListener(IVuiElementListener listener) {
    }

    default void setCustomViewIdList(List<Integer> list) {
    }

    default void initVuiScene(String sceneId, IVuiEngine vuiEngine) {
    }

    default void initVuiScene(String sceneId, IVuiEngine vuiEngine, IVuiSceneListener listener) {
    }

    default int getVuiDisplayLocation() {
        return 0;
    }
}
