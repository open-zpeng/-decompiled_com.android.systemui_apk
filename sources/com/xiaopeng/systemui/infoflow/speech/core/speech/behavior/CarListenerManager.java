package com.xiaopeng.systemui.infoflow.speech.core.speech.behavior;

import com.xiaopeng.speech.common.util.SimpleCallbackList;
import com.xiaopeng.speech.protocol.bean.CardValue;
import com.xiaopeng.speech.protocol.node.carac.bean.ChangeValue;
/* loaded from: classes24.dex */
public class CarListenerManager {
    private SimpleCallbackList<ICarListener> mCallbacks = new SimpleCallbackList<>();

    public void addCallback(ICarListener behavior) {
        this.mCallbacks.addCallback(behavior);
    }

    public void removeCallback(ICarListener behavior) {
        this.mCallbacks.removeCallback(behavior);
    }

    public void onTempDriverUp(ChangeValue changeValue) {
        Object[] iCarListeners = this.mCallbacks.collectCallbacks();
        if (iCarListeners != null) {
            for (Object obj : iCarListeners) {
                ((ICarListener) obj).onTempDriverUp(changeValue);
            }
        }
    }

    public void onTempDriverDown(ChangeValue changeValue) {
        Object[] iCarListeners = this.mCallbacks.collectCallbacks();
        if (iCarListeners != null) {
            for (Object obj : iCarListeners) {
                ((ICarListener) obj).onTempDriverDown(changeValue);
            }
        }
    }

    public void showCtrlCard(int cardType, CardValue cardValue) {
        Object[] iCarListeners = this.mCallbacks.collectCallbacks();
        if (iCarListeners != null) {
            for (Object obj : iCarListeners) {
                ((ICarListener) obj).showCtrlCard(cardType, cardValue);
            }
        }
    }

    public void onIcmBrightnessChanged(int value) {
        Object[] iCarListeners = this.mCallbacks.collectCallbacks();
        if (iCarListeners != null) {
            for (Object obj : iCarListeners) {
                ((ICarListener) obj).onIcmBrightnessChanged(value);
            }
        }
    }

    public void onScreenBrightnessChanged(int value) {
        Object[] iCarListeners = this.mCallbacks.collectCallbacks();
        if (iCarListeners != null) {
            for (Object obj : iCarListeners) {
                ((ICarListener) obj).onScreenBrightnessChanged(value);
            }
        }
    }
}
