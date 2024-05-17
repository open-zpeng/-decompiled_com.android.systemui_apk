package com.xiaopeng.vui.commons;

import android.view.View;
import com.xiaopeng.vui.commons.model.VuiEvent;
/* loaded from: classes24.dex */
public interface IVuiSceneListener {
    default void onVuiEvent(View view, VuiEvent event) {
    }

    default boolean onInterceptVuiEvent(View view, VuiEvent event) {
        return false;
    }

    default void onBuildScene() {
    }

    default void onVuiEvent(VuiEvent event) {
    }

    default void onVuiEventExecutioned() {
    }

    default void onVuiStateChanged() {
    }
}
