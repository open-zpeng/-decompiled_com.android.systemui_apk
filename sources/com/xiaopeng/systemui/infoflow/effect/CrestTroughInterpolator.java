package com.xiaopeng.systemui.infoflow.effect;

import androidx.annotation.NonNull;
import java.util.Iterator;
import java.util.LinkedList;
/* loaded from: classes24.dex */
public class CrestTroughInterpolator implements EffectInterpolator {
    private float lastSize;
    private boolean lastUp;
    private float min = 1000.0f;
    private float max = -1000.0f;
    private LinkedList<Listener> mListeners = new LinkedList<>();

    /* loaded from: classes24.dex */
    public interface Listener {
        void onCrest(float f);

        void onTrough(float f);
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.EffectInterpolator
    public float getMaxValue() {
        return this.max;
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.EffectInterpolator
    public float getMinValue() {
        return this.min;
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.EffectInterpolator
    public float getValue() {
        return this.lastSize;
    }

    private void performTrough(float value) {
        Iterator<Listener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            Listener listener = it.next();
            listener.onTrough(value);
        }
    }

    private void performCrest(float value) {
        Iterator<Listener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            Listener listener = it.next();
            listener.onCrest(value);
        }
    }

    public void addListener(@NonNull Listener listener) {
        this.mListeners.add(listener);
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.EffectInterpolator
    public void update(float size) {
        float f = this.lastSize;
        if (size - f > 0.0f) {
            if (!this.lastUp) {
                this.min = Math.min(f, this.min);
                performTrough(this.lastSize);
            }
            this.lastSize = size;
            this.lastUp = true;
            return;
        }
        if (this.lastUp) {
            this.max = Math.max(f, this.max);
            performCrest(this.lastSize);
        }
        this.lastSize = size;
        this.lastUp = false;
    }
}
