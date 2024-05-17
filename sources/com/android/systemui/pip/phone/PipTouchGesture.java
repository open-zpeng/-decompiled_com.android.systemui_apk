package com.android.systemui.pip.phone;
/* loaded from: classes21.dex */
public abstract class PipTouchGesture {
    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDown(PipTouchState touchState) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean onMove(PipTouchState touchState) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean onUp(PipTouchState touchState) {
        return false;
    }
}
