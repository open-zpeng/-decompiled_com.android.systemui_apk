package com.xiaopeng.systemui.infoflow;

import android.view.MotionEvent;
/* loaded from: classes24.dex */
public interface AvatarViewParentContainer {
    void notifyAvatarAction(int i, String str);

    void notifyAvatarMotionEvent(MotionEvent motionEvent);

    void setAvatarVisible(boolean z);
}
