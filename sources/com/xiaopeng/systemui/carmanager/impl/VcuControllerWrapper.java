package com.xiaopeng.systemui.carmanager.impl;

import com.xiaopeng.systemui.carmanager.controller.IVcuController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public class VcuControllerWrapper {
    private static VcuControllerWrapper mInstance;
    private IVcuController mVcuController;
    private List<Listener> mListenerList = new ArrayList();
    IVcuController.Callback mCallback = new IVcuController.Callback() { // from class: com.xiaopeng.systemui.carmanager.impl.VcuControllerWrapper.1
        @Override // com.xiaopeng.systemui.carmanager.controller.IVcuController.Callback
        public void onGearChanged(int gear) {
            for (Listener listener : VcuControllerWrapper.this.mListenerList) {
                listener.onGearChanged(gear);
            }
        }
    };

    /* loaded from: classes24.dex */
    public interface Listener {
        void onGearChanged(int i);
    }

    private VcuControllerWrapper() {
    }

    public static VcuControllerWrapper getInstance() {
        if (mInstance == null) {
            synchronized (VcuControllerWrapper.class) {
                if (mInstance == null) {
                    mInstance = new VcuControllerWrapper();
                }
            }
        }
        return mInstance;
    }

    public void setVcuController(IVcuController vcuController) {
        this.mVcuController = vcuController;
        IVcuController iVcuController = this.mVcuController;
        if (iVcuController != null) {
            iVcuController.registerCallback(this.mCallback);
        }
    }

    public void addListener(Listener listener) {
        this.mListenerList.add(listener);
    }
}
