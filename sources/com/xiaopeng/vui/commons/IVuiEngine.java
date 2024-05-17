package com.xiaopeng.vui.commons;

import android.view.View;
import java.util.List;
/* loaded from: classes24.dex */
public interface IVuiEngine {
    void addVuiSceneListener(String str, View view, IVuiSceneListener iVuiSceneListener);

    void addVuiSceneListener(String str, View view, IVuiSceneListener iVuiSceneListener, IVuiElementChangedListener iVuiElementChangedListener);

    void buildScene(String str, View view);

    void buildScene(String str, View view, List<Integer> list, IVuiElementListener iVuiElementListener);

    void buildScene(String str, List<View> list);

    void buildScene(String str, List<View> list, List<Integer> list2, IVuiElementListener iVuiElementListener);

    void enterScene(String str);

    void exitScene(String str);

    void removeVuiSceneListener(String str);

    void updateElementAttribute(String str, View view);

    void updateScene(String str, View view);

    void updateScene(String str, View view, List<Integer> list, IVuiElementListener iVuiElementListener);

    void updateScene(String str, List<View> list);

    void updateScene(String str, List<View> list, List<Integer> list2, IVuiElementListener iVuiElementListener);
}
