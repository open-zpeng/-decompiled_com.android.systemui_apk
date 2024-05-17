package com.xiaopeng.vui.commons;

import android.view.View;
import com.xiaopeng.vui.commons.model.VuiElement;
import java.util.List;
/* loaded from: classes24.dex */
public interface IVuiElementBuilder {
    default List<VuiElement> build(int fatherElementId, View view) {
        return null;
    }

    default List<VuiElement> build(int fatherElementId, List<View> viewList) {
        return null;
    }
}
