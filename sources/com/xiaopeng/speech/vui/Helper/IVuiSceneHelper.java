package com.xiaopeng.speech.vui.Helper;

import android.text.TextUtils;
import android.view.View;
import com.xiaopeng.speech.vui.VuiEngine;
import com.xiaopeng.speech.vui.constants.Foo;
import com.xiaopeng.vui.commons.IVuiSceneListener;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public interface IVuiSceneHelper extends IVuiSceneListener {
    List<View> getBuildViews();

    String getSceneId();

    @Override // com.xiaopeng.vui.commons.IVuiSceneListener
    default void onBuildScene() {
        if (!isCustomBuildScene()) {
            buildScene();
        }
    }

    default boolean isMainScene() {
        return true;
    }

    default List<String> getSubSceneList() {
        return new ArrayList();
    }

    default void buildScene() {
        if (TextUtils.isEmpty(getSceneId())) {
            return;
        }
        VuiEngine.getInstance(Foo.getContext()).buildScene(getSceneId(), getBuildViews(), getSubSceneList(), isMainScene());
    }

    default boolean isCustomBuildScene() {
        return false;
    }
}
