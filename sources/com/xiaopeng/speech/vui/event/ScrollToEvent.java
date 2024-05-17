package com.xiaopeng.speech.vui.event;

import android.view.View;
import com.xiaopeng.vui.commons.model.VuiElement;
/* loaded from: classes.dex */
public class ScrollToEvent extends BaseEvent {
    @Override // com.xiaopeng.speech.vui.event.IVuiEvent
    public <T extends View> T run(T view, VuiElement vuiElement) {
        return view;
    }
}
