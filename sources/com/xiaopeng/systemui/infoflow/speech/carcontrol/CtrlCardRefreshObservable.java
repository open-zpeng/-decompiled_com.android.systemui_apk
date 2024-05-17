package com.xiaopeng.systemui.infoflow.speech.carcontrol;

import java.util.Observable;
/* loaded from: classes24.dex */
public class CtrlCardRefreshObservable extends Observable {
    private static final CtrlCardRefreshObservable ourInstance = new CtrlCardRefreshObservable();

    private CtrlCardRefreshObservable() {
    }

    public static CtrlCardRefreshObservable getInstance() {
        return ourInstance;
    }

    @Override // java.util.Observable
    public void notifyObservers() {
        if (!ourInstance.hasChanged()) {
            ourInstance.setChanged();
        }
        super.notifyObservers();
    }
}
