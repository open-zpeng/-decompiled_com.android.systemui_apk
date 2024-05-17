package com.xiaopeng.systemui.ui.widget;

import android.content.res.Configuration;
import com.xiaopeng.systemui.controller.OsdController;
/* loaded from: classes24.dex */
public interface IOsdView {
    void dispatchConfigurationChanged(Configuration configuration);

    void showOsd(OsdController.OsdParams osdParams);

    void showOsd(boolean z);
}
