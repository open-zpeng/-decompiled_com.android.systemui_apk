package com.xiaopeng.systemui.infoflow.message.view;

import android.view.KeyEvent;
/* loaded from: classes24.dex */
public interface IXFocusView {
    boolean isXShown();

    void performFocusNavigation(KeyEvent keyEvent);

    void setXFocused(boolean z);
}
