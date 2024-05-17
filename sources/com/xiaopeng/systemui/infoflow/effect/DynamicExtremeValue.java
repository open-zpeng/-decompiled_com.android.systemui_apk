package com.xiaopeng.systemui.infoflow.effect;

import java.util.Iterator;
import java.util.LinkedList;
/* loaded from: classes24.dex */
public class DynamicExtremeValue {
    private float mAverageValue;
    private LinkedList<Float> mList = new LinkedList<>();
    private int mMaxSize;
    private float mMaxValue;
    private float mMinValue;

    public DynamicExtremeValue(int maxSize) {
        this.mMaxSize = maxSize;
    }

    public void addValue(float value) {
        while (this.mList.size() >= this.mMaxSize) {
            this.mList.removeFirst();
        }
        this.mList.add(Float.valueOf(value));
        updateValue();
    }

    private void updateValue() {
        float sum = 0.0f;
        float max = Float.MIN_VALUE;
        float min = Float.MAX_VALUE;
        Iterator<Float> it = this.mList.iterator();
        while (it.hasNext()) {
            Float f = it.next();
            sum += f.floatValue();
            max = Math.max(f.floatValue(), max);
            min = Math.min(f.floatValue(), min);
        }
        this.mMaxValue = max;
        this.mAverageValue = sum / this.mList.size();
        this.mMinValue = min;
    }

    public void setMaxSize(int maxSize) {
        int oldSize = this.mList.size();
        while (this.mList.size() > maxSize) {
            this.mList.removeFirst();
        }
        if (oldSize > maxSize) {
            updateValue();
        }
        this.mMaxSize = maxSize;
    }

    public float getAverageValue() {
        return this.mAverageValue;
    }

    public float getMaxValue() {
        return this.mMaxValue;
    }

    public float getMinValue() {
        return this.mMinValue;
    }
}
