package com.xiaopeng.systemui.viewmodel.signal;

import com.xiaopeng.systemui.viewmodel.IViewModel;
/* loaded from: classes24.dex */
public interface ISignalViewModel extends IViewModel {
    int getWifiLevel();

    int getWifiState();

    boolean isWifiConnected();
}
