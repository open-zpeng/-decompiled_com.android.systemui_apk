package com.xiaopeng.speech.vui.event;

import android.view.View;
import com.xiaopeng.speech.vui.utils.LogUtils;
import com.xiaopeng.vui.commons.IVuiElement;
import com.xiaopeng.vui.commons.model.VuiElement;
/* loaded from: classes.dex */
public class ClickEvent extends BaseEvent {
    @Override // com.xiaopeng.speech.vui.event.IVuiEvent
    public <T extends View> T run(T view, VuiElement vuiElement) {
        if (view != null) {
            if (view instanceof IVuiElement) {
                ((IVuiElement) view).setPerformVuiAction(true);
            }
            boolean result = view.performClick();
            LogUtils.i("ClickEvent run :" + result);
            if (!result && (view instanceof IVuiElement)) {
                ((IVuiElement) view).setPerformVuiAction(false);
            }
        }
        return view;
    }
}
