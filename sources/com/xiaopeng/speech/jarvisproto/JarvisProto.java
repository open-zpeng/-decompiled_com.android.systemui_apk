package com.xiaopeng.speech.jarvisproto;

import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
/* loaded from: classes23.dex */
public abstract class JarvisProto {
    public abstract String getEvent();

    public abstract String getJsonData();

    public String toString() {
        return getClass().getSimpleName() + NavigationBarInflaterView.KEY_IMAGE_DELIM + getJsonData();
    }
}
