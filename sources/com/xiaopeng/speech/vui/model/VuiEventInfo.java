package com.xiaopeng.speech.vui.model;

import android.view.View;
/* loaded from: classes.dex */
public class VuiEventInfo {
    public View hitView;
    public String sceneId;

    public VuiEventInfo(View view, String sceneId) {
        this.hitView = view;
        this.sceneId = sceneId;
    }
}
