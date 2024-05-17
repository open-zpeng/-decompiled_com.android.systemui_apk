package com.xiaopeng.speech.vui.listener;

import android.view.View;
import com.xiaopeng.vui.commons.model.VuiElement;
import java.util.List;
/* loaded from: classes.dex */
public interface IXpVuiElementChanged {
    default void onVuiElementChanged(String sceneId, View view) {
    }

    default void onVuiElementChanged(String sceneId, View view, List<VuiElement> elementList) {
    }

    default void onVuiElementChanged(String sceneId, View view, String[] vuiLabels, int curState) {
    }
}
