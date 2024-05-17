package com.android.systemui.doze;

import androidx.annotation.VisibleForTesting;
import com.android.systemui.doze.DozeMachine;
import com.android.systemui.statusbar.phone.DozeParameters;
/* loaded from: classes21.dex */
public class DozeScreenStatePreventingAdapter extends DozeMachine.Service.Delegate {
    @VisibleForTesting
    DozeScreenStatePreventingAdapter(DozeMachine.Service inner) {
        super(inner);
    }

    @Override // com.android.systemui.doze.DozeMachine.Service.Delegate, com.android.systemui.doze.DozeMachine.Service
    public void setDozeScreenState(int state) {
        if (state == 3) {
            state = 2;
        } else if (state == 4) {
            state = 6;
        }
        super.setDozeScreenState(state);
    }

    public static DozeMachine.Service wrapIfNeeded(DozeMachine.Service inner, DozeParameters params) {
        return isNeeded(params) ? new DozeScreenStatePreventingAdapter(inner) : inner;
    }

    private static boolean isNeeded(DozeParameters params) {
        return !params.getDisplayStateSupported();
    }
}
