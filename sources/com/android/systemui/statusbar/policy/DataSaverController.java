package com.android.systemui.statusbar.policy;
/* loaded from: classes21.dex */
public interface DataSaverController extends CallbackController<Listener> {

    /* loaded from: classes21.dex */
    public interface Listener {
        void onDataSaverChanged(boolean z);
    }

    boolean isDataSaverEnabled();

    void setDataSaverEnabled(boolean z);
}
